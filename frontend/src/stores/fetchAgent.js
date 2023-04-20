import { defineStore } from "pinia";
import axios from 'axios';
import { useNotification } from "naive-ui";

import { useSessionStore } from "./session";

export const useFetchAgent = defineStore("fetchAgent", () => {
  const sessionStore = useSessionStore();
  const notificationAgent = useNotification();

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
      return { isSuccessful: false, data: error };
    }
  }


  async function handleError(error) {
    if (error.response.status == 400 || error.response.status == 401) {
      await sessionStore.logout();
    }

    console.log(error);
  }


  return {
    postAuthentication,
    getMembershipsByEmail,
    getPhasesByProjectId,
    postProject,
    deleteProjectById,
    getProjectById,
    getMultipleProjectsByIds,
    getTicketsByProjectId,
    postTicket,
    patchTicket,
    getUserById,
    getUserByEmail
  };
});
