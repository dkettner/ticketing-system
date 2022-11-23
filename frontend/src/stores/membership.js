import { ref } from "vue";
import { defineStore } from "pinia";
import axios from 'axios';
import { useSessionStore } from "./session";

export const useMembershipStore = defineStore("membership", () => {
  const membershipsPath = "/memberships";
  const memberships = ref([]);
  const sessionStore = useSessionStore();

  const updateMembershipsByEmail = async (email = sessionStore.email) => {
    try {
      const getMembershipsResponse = await axios.get(membershipsPath + '?email=' + email, {withCredentials: true});
      memberships.value = getMembershipsResponse.data;
    } catch(error) {
      console.log(error);

      if (error.response.status == 401) {
        await sessionStore.logout();
      }
    }
  }

  function getAcceptedMembershipsProjectIds() {
    return memberships
        .value
        .filter(membership => membership.state === 'ACCEPTED')
        .map(membership => membership.projectId);
  }
  function getOpenMembershipsProjectIds() {
    return memberships
        .value
        .filter(membership => {membership.state === "OPEN"})
        .map(membership => membership.projectId);
  }

  return {
    memberships,
    updateMembershipsByEmail,
    getAcceptedMembershipsProjectIds,
    getOpenMembershipsProjectIds
  };
});
