import { ref } from "vue";
import { defineStore } from "pinia";
import axios from 'axios';
import { useSessionStore } from "./session";

export const useMembershipStore = defineStore("membership", () => {
  const membershipsPath = "/memberships";
  const memberships = ref({});
  const sessionStore = useSessionStore();

  const updateMembershipsByEmail = async (email = sessionStore.email) => {
    try {
      const getMembershipsResponse = await axios.get(membershipsPath + '?email=' + email, {withCredentials: true});
      const membershipsArray = getMembershipsResponse.data;

      memberships.value = {};
      for (let index in membershipsArray) {
        memberships.value[membershipsArray[index].id] = membershipsArray[index];
      }
    } catch(error) {
      console.log(error);

      if (error.response.status == 401) {
        await sessionStore.logout();
      }
    }
  }

  return {
    memberships,
    updateMembershipsByEmail
  };
});