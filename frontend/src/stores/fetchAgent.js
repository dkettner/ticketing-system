import { defineStore } from "pinia";
import axios from 'axios';
import { useNotification } from "naive-ui";

import { useSessionStore } from "./session";

export const useFetchAgent = defineStore("fetchAgent", () => {
  const sessionStore = useSessionStore();

  const backendBaseURL = "https://localhost:8080";
  
  // authentication
  const authenticationPath = backendBaseURL + "/authentication";


  // memberships
  const membershipsPath = backendBaseURL + "/memberships";

  const getMembershipsByEmail = async (email) => {
    try {
      const response = await axios.get(membershipsPath + '?email=' + email, {withCredentials: true});
      return response.data;
    } catch (error) {
      await handleError(error);
      return null; // TODO: useful return value?
    }
  }

  // phases
  const phasesPath = backendBaseURL + "/phases";


  // projects
  const projectsPath = backendBaseURL + "/projects";

  const postProject = async (postProjectData) => {
    try {
      const response = await axios.post(projectsPath, postProjectData, {withCredentials: true});
      return response.data;
    } catch (error) {
      await handleError(error);
      return null; // TODO: useful return value?
    }
  }

  const getProjectById = async (id) => {
    try {
      const response = await axios.get(projectsPath + "/" + id,  {withCredentials: true});
      return response.data;
    } catch (error) {
      await handleError(error);
      return null; // TODO: useful return value?
    }
  }

  const getMultipleProjectsByIds = async (projectIds) => {
    const projects = [];
    for (let index in projectIds) {
      projects.push(await getProjectById(projectIds[index]));
    }
    return projects;
  }
  

  // tickets
  const ticketsPath = backendBaseURL + "/tickets";


  // users
  const usersPath = backendBaseURL + "/users";

  const getUserById = async (id) => {
    try {
      const response = await axios.get(usersPath + '/' + id, {withCredentials: true});
      return response.data;
    } catch (error) {
      await handleError(error);
      return null; // TODO: useful return value?
    }
  }

  const getUserByEmail = async (email) => {
    try {
      const response = await axios.get(usersPath + '?email=' + email, {withCredentials: true});
      return response.data;
    } catch (error) {
      await handleError(error);
      return null; // TODO: useful return value?
    }
  }


  async function handleError(error) {
    if (error.response.status == 400 || error.response.status == 401) {
      await sessionStore.logout();
    }

    console.log(error);

    const notificationAgent = useNotification();
    notificationAgent.create({
      title: "Error",
      content: error.response.data
    });
  }


  return {
    getMembershipsByEmail,
    postProject,
    getProjectById,
    getMultipleProjectsByIds,
    getUserById,
    getUserByEmail
  };
});
