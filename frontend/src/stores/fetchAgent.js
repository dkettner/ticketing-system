import { defineStore } from "pinia";
import axios from 'axios';

import { useSessionStore } from "./session";

export const useFetchAgent = defineStore("fetchAgent", () => {
  const sessionStore = useSessionStore();

  const getBearerToken = () => {
    return "Bearer " + localStorage.getItem("jwt");
  }

  const getConfig = () => {
    return {
      headers: { Authorization: getBearerToken() }
    }
  };
  
  // authentication
  const authenticationPath = "/authentications";

  const postAuthentication = async (loginEmail, loginPassword) => {
    try {
      const response = await axios.post(authenticationPath, {email: loginEmail, password: loginPassword});
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      // await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  // memberships
  const membershipsPath = "/memberships";

  const getMembershipsByEmail = async (email) => {
    try {
      const response = await axios.get(membershipsPath + '?email=' + email, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const getMembershipsByProjectId = async (projectId) => {
    try {
      const response = await axios.get(membershipsPath + '?project-id=' + projectId, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const postMembership = async (postMembershipData) => {
    try {
      const response = await axios.post(membershipsPath, postMembershipData, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  const deleteMembershipById = async (id) => {
    try {
      const response = await axios.delete(membershipsPath + "/" + id, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const putMembershipState = async (membershipId, patchMembershipStateData) => {
    let response;
    try {
      response = await axios.put(membershipsPath + '/' + membershipId + '/state', patchMembershipStateData, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  const putMembershipRole = async (membershipId, patchMembershipRoleData) => {
    let response;
    try {
      response = await axios.put(membershipsPath + '/' + membershipId + '/role', patchMembershipRoleData, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  // phases
  const phasesPath = "/phases";

  const getPhasesByProjectId = async (projectId) => {
    try {
      const response = await axios.get(phasesPath + '?project-id=' + projectId, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const postPhase = async (postPhaseData) => {
    try {
      const response = await axios.post(phasesPath, postPhaseData, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.phase.data };
    }
  }

  const patchPhaseNameById = async (phaseId, patchPhaseNameData) => {
    try {
      const response = await axios.put(phasesPath + '/' + phaseId + '/name', patchPhaseNameData, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  const deletePhaseById = async (id) => {
    try {
      const response = await axios.delete(phasesPath + "/" + id, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }


  // projects
  const projectsPath = "/projects";

  const postProject = async (postProjectData) => {
    try {
      const response = await axios.post(projectsPath, postProjectData, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const getProjectById = async (id) => {
    try {
      const response = await axios.get(projectsPath + "/" + id,  getConfig());
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
        projects.push(await axios.get(projectsPath + "/" + projectIds[index],  getConfig()
));
      }
      return { isSuccessful: true, data: projects };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const patchProjectById = async (projectId, patchProjectData) => {
    try {
      const response = await axios.patch(projectsPath + '/' + projectId, patchProjectData, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  const deleteProjectById = async (id) => {
    try {
      const response = await axios.delete(projectsPath + "/" + id, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }
  

  // tickets
  const ticketsPath = "/tickets";

  const postTicket = async (postTicketData) => {
    try {
      const response = await axios.post(ticketsPath, postTicketData, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const getTicketById = async (id) => {
    try {
      const response = await axios.get(ticketsPath + "/" + id, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const getTicketsByProjectId = async (projectId) => {
    try {
      const response = await axios.get(ticketsPath + '?project-id=' + projectId, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const patchTicket = async (ticketId, patchTicketData) => {
    try {
      const response = await axios.patch(ticketsPath + '/' + ticketId, patchTicketData, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const deleteTicketById = async (id) => {
    try {
      const response = await axios.delete(ticketsPath + "/" + id, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }


  // users
  const usersPath = "/users";

  const getUserById = async (id) => {
    try {
      const response = await axios.get(usersPath + '/' + id, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }

  const getUserByEmail = async (email) => {
    try {
      const response = await axios.get(usersPath + '?email=' + email, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  const deleteUserById = async (id) => {
    try {
      const response = await axios.delete(usersPath + "/" + id, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error.response.data };
    }
  }

  const patchUserById = async (userId, patchUserData) => {
    try {
      const response = await axios.patch(usersPath + '/' + userId, patchUserData, getConfig());
      return { isSuccessful: true, data: response.data };
    } catch (error) {
      await handleError(error);
      return { isSuccessful: false, data: error };
    }
  }


  async function handleError(error) {
    console.log(error)
    if (error.response.status == 401) {
      await sessionStore.logout();
    }
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
    getTicketById,
    getTicketsByProjectId,
    patchTicket,
    deleteTicketById,

    getUserById,
    getUserByEmail,
    patchUserById,
    deleteUserById
  };
});
