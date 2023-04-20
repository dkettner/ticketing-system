import { ref } from "vue";
import { defineStore } from "pinia";
import { useFetchAgent } from "./fetchAgent";

export const useTicketStore = defineStore("ticket", () => {
  const tickets = ref([]);
  const projectIds = new Set([]);

  const fetchAgent = useFetchAgent();

  const addProjectId = (newProjectId) => {
    projectIds.add(newProjectId);
  }

  const getTicketsByProjectId = async (projectId) => {
    return tickets.value.filter(element => element.projectId == projectId);
  }

  const updateTicketsByProjectId = async (projectId) => {
    const getTicketsResponse = await fetchAgent.getTicketsByProjectId(projectId);
    if (getTicketsResponse.isSuccessful) {
        projectIds.add(projectId);
        tickets.value = tickets.value.filter(element => element.projectId != projectId);
        for (let index in getTicketsResponse.data) {
            tickets.value.push(getTicketsResponse.data[index]);
        }
      return { isSuccessful: true, data: getTicketsResponse.data };
    } else {
      return { isSuccessful: false, data: getTicketsResponse.data.response.data };
    }
  }

  const postTicket = async (postTicketData) => {
    const postTicketResponse = await fetchAgent.postTicket(postTicketData);
    if (postTicketResponse.isSuccessful) {
      await updateTicketsByProjectId();
      return { isPostSuccessful: true, message: "Created a new project with name: " + postProjectData.name };
    } else {
      return { isPostSuccessful: false, message: postTicketResponse.data.response.data };
    }
  }

  const patchTicket = async (ticketId, patchTicketData) => {
    const patchTicketResponse = await fetchAgent.patchTicket(ticketId, patchTicketData);
  }

  const updateTicketPosition = async (ticketId, newPhaseId) => {
    const ticketPostData = ref({
        title: null,
        description: null,
        dueTime: null,
        phaseId: newPhaseId,
        assigneeIds: null
      });
    await patchTicket(ticketId, ticketPostData.value);
  }

  return {
    tickets,
    addProjectId,
    updateTicketsByProjectId,
    postTicket,
    patchTicket,
    updateTicketPosition,
    getTicketsByProjectId
  };
});
