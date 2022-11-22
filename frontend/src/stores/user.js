import { ref } from "vue";
import { defineStore } from "pinia";
import axios from 'axios';
import { useSessionStore } from "./session";

export const useUserStore = defineStore("user", () => {
  const usersPath = "/users";
  const user = ref({
    id: "",
    email: "", 
    name: ""
  });

  const setEmail = (email) => {
    user.value.email = email;
  }

  const updateUserById = async (id = user.value.id) => {
    try {
      const getUserResponse = await axios.get(usersPath + '/' + id);
      user.value.id = getUserResponse.data.id;
      user.value.email = getUserResponse.data.email;
      user.value.name = getUserResponse.data.name;
    } catch(error) {
      console.log(error);
      
      if (error.response.status == 401) {
        const sessionStore = useSessionStore();
        await sessionStore.logout();
      }
    }
  }
  const updateUserByEmail = async (email = user.value.email) => {
    try {
      const getUserResponse = await axios.get(usersPath + '?email=' + email, {withCredentials: true});
      user.value.id = getUserResponse.data.id;
      user.value.email = getUserResponse.data.email;
      user.value.name = getUserResponse.data.name;
    } catch(error) {
      console.log(error);

      if (error.response.status == 401) {
        const sessionStore = useSessionStore();
        await sessionStore.logout();
      }
    }
  }

  return {
    user,
    setEmail,
    updateUserById,
    updateUserByEmail
  };
});
