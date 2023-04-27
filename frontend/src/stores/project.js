import { ref } from "vue";
import { defineStore } from "pinia";
import { useFetchAgent } from "./fetchAgent";
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
      return { isPostSuccessful: false, message: postProjectResponse.data.response.data };
    }
  }

  const deleteProjectById = async (id) => {
    const deleteProjectResponse = await fetchAgent.deleteProjectById(id);
    if (deleteProjectResponse.isSuccessful) {
      await updateProjectsByAcceptedMemberships();
      return { isDeleteSuccessful: true, message: "Deleted project with id: " + id };
    } else {
      return { isPostSuccessful: false, message: deleteProjectResponse.data.response.data };
    }
  }

  const updateProjectsByAcceptedMemberships = async () => {
    const projectIds = membershipStore.getAcceptedMembershipsProjectIds();

    const newProjects = [];
    for (let index in projectIds) {
      const getProjectResponse = await fetchAgent.getProjectById(projectIds[index]);
      if (getProjectResponse.isSuccessful) {
        newProjects.push(getProjectResponse.data);
      } else {
        console.log(getProjectResponse.data.response.data);
      }
    }

    projects.value = [];
    for (let i in newProjects) {
      projects.value.push(newProjects[i]);
    }
    return;
  }

  return {
    projects,
    postNewProject,
    deleteProjectById,
    updateProjectsByAcceptedMemberships
  };
});
