import { ref } from "vue";
import { defineStore } from "pinia";
import { useFetchAgent } from "./fetchAgent";m
import { useMembershipStore } from "./membership";

export const useProjectStore = defineStore("project", () => {
  const fetchAgent = useFetchAgent();
  const projects = ref([]);
  const membershipStore = useMembershipStore();

  const postNewProject = async (postProjectData) => {
    const postProjectResponse = await fetchAgent.postProject(postProjectData);
    if (postProjectResponse.isSuccessful) {
      await membershipStore.updateMembershipsByEmail();
      await updateProjectsByAcceptedMemberships();
      return { isPostSuccessful: true, message: "Created a new project with name: " + postProjectData.name };
    } else {
      return { isPostSuccessful: false, message: error.response.data };
    }
  }

  const updateProjectsByAcceptedMemberships = async () => {
    try {
      const projectIds = membershipStore.getAcceptedMembershipsProjectIds();

      projects.value = [];
      for (let index in projectIds) {
        const getProjectResponse = await axios.get(projectsPath + '/' + projectIds[index], {withCredentials: true});
        projects.value.push(getProjectResponse.data);
      }
    } catch(error) {
      console.log(error);

      if (error.response.status == 401) {
        await sessionStore.logout();
      }
    }
  }

  return {
    projects,
    postNewProject,
    updateProjectsByAcceptedMemberships
  };
});
