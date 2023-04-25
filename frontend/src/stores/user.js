import { ref } from "vue";
import { defineStore } from "pinia";
import { useFetchAgent } from "./fetchAgent";
import { useSessionStore } from "./session";

export const useUserStore = defineStore("user", () => {
  const user = ref({
    id: "",
    email: "", 
    name: ""
  });

  const fetchAgent = useFetchAgent();
  const sessionStore = useSessionStore();

  const setEmail = (email) => {
    user.value.email = email;
  }

  const patchUserById = async (id, userPatchData) => {
    const patchUserResponse = await fetchAgent.patchUserById(id, userPatchData);
    if (patchUserResponse.isSuccessful) {
      await updateUserById();
      return { isSuccessful: true, data: patchUserResponse.data };
    } else {
      return { isSuccessful: false, data: patchUserResponse.data.response.data };
    }
  }

  const updateUserById = async (id = user.value.id) => {
    const getUserResponse = await fetchAgent.getUserById(id);
    if (getUserResponse.isSuccessful) {
      user.value.id = getUserResponse.data.id;
      user.value.email = getUserResponse.data.email;
      user.value.name = getUserResponse.data.name;
      return { isSuccessful: true, data: getUserResponse.data };
    } else {
      return { isSuccessful: false, data: getUserResponse.data };
    }
  }
  const updateUserByEmail = async (email = user.value.email) => {
    const getUserResponse = await fetchAgent.getUserByEmail(email);
    if (getUserResponse.isSuccessful) {
      user.value.id = getUserResponse.data.id;
      user.value.email = getUserResponse.data.email;
      user.value.name = getUserResponse.data.name;
      return { isSuccessful: true, data: getUserResponse.data };
    } else {
      return { isSuccessful: false, data: getUserResponse.data.response.data };
    }
  }
  const deleteUserById = async (id) => {
    const deleteUserResponse = await fetchAgent.deleteUserById(id);
    if (deleteUserResponse.isSuccessful) {
      user.value.id = null;
      user.value.email = null;
      user.value.name = null;
      await sessionStore.logout();
      return { isSuccessful: true }; // should not be needed;
    } else {
      return { isSuccessful: false, data: getUserResponse.data.response.data };
    }
  }

  return {
    user,
    setEmail,
    updateUserById,
    updateUserByEmail,
    patchUserById,
    deleteUserById
  };
});
