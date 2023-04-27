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
      tickets.value = tickets.value.filter(element => element.projectId != projectId);
      return { isSuccessful: false, data: getTicketsResponse.data.response.data };
    }
  }

  const postTicket = async (postTicketData) => {
    const postTicketResponse = await fetchAgent.postTicket(postTicketData);
    if (postTicketResponse.isSuccessful) {
      await updateTicketsByProjectId(postTicketData.projectId);
      return { isPostSuccessful: true, message: "Created a new project with title: " + postTicketData.title };
    } else {
      return { isPostSuccessful: false, message: postTicketResponse.data.response.data };
    }
  }

  const patchTicket = async (projectId, ticketId, patchTicketData) => {
    const patchTicketResponse = await fetchAgent.patchTicket(ticketId, patchTicketData);
    if (patchTicketResponse.isSuccessful) {
      await updateTicketsByProjectId(projectId);
      return { isPostSuccessful: true, message: "updated ticket with id:" + ticketId};
    } else {
      return { isPostSuccessful: false, message: patchTicketResponse.data.response.data };
    }
  }

  const updateTicketPosition = async (projectId, ticketId, newPhaseId) => {
    const ticketPostData = ref({
        title: null,
        description: null,
        dueTime: null,
        phaseId: newPhaseId,
        assigneeIds: null
      });
    await patchTicket(projectId, ticketId, ticketPostData.value);
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
