import { ref } from "vue";
import { defineStore } from "pinia";
import { useFetchAgent } from "./fetchAgent";

export const usePhaseStore = defineStore("phase", () => {
  const phases = ref([]);
  const projectIds = new Set([]);

  const fetchAgent = useFetchAgent();

  const addProjectId = (newProjectId) => {
    projectIds.add(newProjectId);
  }

  const getPhasesByProjectId = async (projectId) => {
    return phases.value.filter(element => element.projectId == projectId);
  }

  const updatePhasesByProjectId = async (projectId) => {
    const getPhasesResponse = await fetchAgent.getPhasesByProjectId(projectId);
    if (getPhasesResponse.isSuccessful) {
        projectIds.add(projectId);
        phases.value = phases.value.filter(element => element.projectId != projectId)
        for (let index in getPhasesResponse.data) {
            phases.value.push(getPhasesResponse.data[index]);
        }
      return { isSuccessful: true, data: getPhasesResponse.data };
    } else {
      return { isSuccessful: false, data: getPhasesResponse.data.response.data };
    }
  }

  return {
    phases,
    addProjectId,
    updatePhasesByProjectId,
    getPhasesByProjectId
  };
});
