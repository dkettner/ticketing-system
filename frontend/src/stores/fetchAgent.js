import { defineStore } from "pinia";
import axios from 'axios';
import { useNotification } from "naive-ui";

import { useSessionStore } from "./session";

export const useFetchAgent = defineStore("fetchAgent", () => {
  const sessionStore = useSessionStore();
  

  const backendBaseURL = "https://localhost:8080";
  
  // authentication
  const authenticationPath = backendBaseURL + "/authentication";

  const postAuthentication = async (loginEmail, loginPassword) => {
    try {
      const response = await axios.post(authenticationPath, {email: loginEmail, password: loginPassword}, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  // memberships
  const membershipsPath = backendBaseURL + "/memberships";

  const getMembershipsByEmail = async (email) => {
    try {
      const response = await axios.get(membershipsPath + '?email=' + email, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const getMembershipsByProjectId = async (projectId) => {
    try {
      const response = await axios.get(membershipsPath + '?project-id=' + projectId, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const postMembership = async (postMembershipData) => {
    try {
      const response = await axios.post(membershipsPath, postMembershipData, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  const deleteMembershipById = async (id) => {
    try {
      const response = await axios.delete(membershipsPath + "/" + id, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const putMembershipState = async (membershipId, patchMembershipStateData) => {
    let response;
    try {
      response = await axios.put(membershipsPath + '/' + membershipId + '/state', patchMembershipStateData, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  const putMembershipRole = async (membershipId, patchMembershipRoleData) => {
    let response;
    try {
      response = await axios.put(membershipsPath + '/' + membershipId + '/role', patchMembershipRoleData, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  // phases
  const phasesPath = backendBaseURL + "/phases";

  const getPhasesByProjectId = async (projectId) => {
    try {
      const response = await axios.get(phasesPath + '?project-id=' + projectId, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const postPhase = async (postPhaseData) => {
    try {
      const response = await axios.post(phasesPath, postPhaseData, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.phase.data };
    }
  }

  const patchPhaseNameById = async (phaseId, patchPhaseNameData) => {
    try {
      const response = await axios.patch(phasesPath + '/' + phaseId + '/name', patchPhaseNameData, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  const deletePhaseById = async (id) => {
    try {
      const response = await axios.delete(phasesPath + "/" + id, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }


  // projects
  const projectsPath = backendBaseURL + "/projects";

  const postProject = async (postProjectData) => {
    try {
      const response = await axios.post(projectsPath, postProjectData, {withCredentials: true,   headers: {
        'Content-Type': 'application/json; charset=UTF-8'
      }});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const getProjectById = async (id) => {
    try {
      const response = await axios.get(projectsPath + "/" + id,  {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const getMultipleProjectsByIds = async (projectIds) => {
    try {
      const projects = [];
      for (let index in projectIds) {
        projects.push(await axios.get(projectsPath + "/" + projectIds[index],  {withCredentials: true}));
      }
      return { isSuccessful: true, data: projects };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const patchProjectById = async (projectId, patchProjectData) => {
    try {
      const response = await axios.patch(projectsPath + '/' + projectId, patchProjectData, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  const deleteProjectById = async (id) => {
    try {
      const response = await axios.delete(projectsPath + "/" + id, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }
  

  // tickets
  const ticketsPath = backendBaseURL + "/tickets";

  const postTicket = async (postTicketData) => {
    try {
      const response = await axios.post(ticketsPath, postTicketData, {withCredentials: true,   headers: {
        'Content-Type': 'application/json; charset=UTF-8'
      }});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const getTicketsByProjectId = async (projectId) => {
    try {
      const response = await axios.get(ticketsPath + '?project-id=' + projectId, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const patchTicket = async (ticketId, patchTicketData) => {
    try {
      const response = await axios.patch(ticketsPath + '/' + ticketId, patchTicketData, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const deleteTicketById = async (id) => {
    try {
      const response = await axios.delete(ticketsPath + "/" + id, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }


  // users
  const usersPath = backendBaseURL + "/users";

  const getUserById = async (id) => {
    try {
      const response = await axios.get(usersPath + '/' + id, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const getUserByEmail = async (email) => {
    try {
      const response = await axios.get(usersPath + '?email=' + email, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  const deleteUserById = async (id) => {
    try {
      const response = await axios.delete(usersPath + "/" + id, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const patchUserById = async (userId, patchUserData) => {
    try {
      const response = await axios.patch(usersPath + '/' + userId, patchUserData, {withCredentials: true});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }


  async function handleError(error) {
    if (error.response.status == 401) {
      await sessionStore.logout();
    }

    console.log(error);
  }


  return {
    postAuthentication,

    postMembership,
    getMembershipsByEmail,
    getMembershipsByProjectId,
    putMembershipRole,
    putMembershipState,
    deleteMembershipById,
    
    postPhase,
    getPhasesByProjectId,
    patchPhaseNameById,
    deletePhaseById,

    postProject,
    getProjectById,
    getMultipleProjectsByIds,
    patchProjectById,
    deleteProjectById,

    postTicket,
    getTicketsByProjectId,
    patchTicket,
    deleteTicketById,

    getUserById,
    getUserByEmail,
    patchUserById,
    deleteUserById
  };
});
