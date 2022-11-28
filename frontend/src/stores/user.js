import { ref } from "vue";
import { defineStore } from "pinia";
import { useFetchAgent } from "./fetchAgent";
import { useNotification } from "naive-ui";

export const useUserStore = defineStore("user", () => {
  const user = ref({
    id: "",
    email: "", 
    name: ""
  });

  const fetchAgent = useFetchAgent();
  const notificationAgent = useNotification();

  const setEmail = (email) => {
    user.value.email = email;
  }

  const updateUserById = async (id = user.value.id) => {
    const getUserResponse = await fetchAgent.getUserById(id);
    if (getUserResponse.isSuccessful) {
      user.value.id = getUserResponse.data.id;
      user.value.email = getUserResponse.data.email;
      user.value.name = getUserResponse.data.name;
    } else {
      notificationAgent.create({
        title: "Error",
        content: getUserResponse.data.response.data
      });
    }
  }
  const updateUserByEmail = async (email = user.value.email) => {
    const getUserResponse = await fetchAgent.getUserByEmail(email);
    if (getUserResponse.isSuccessful) {
      user.value.id = getUserResponse.data.id;
      user.value.email = getUserResponse.data.email;
      user.value.name = getUserResponse.data.name;
    } else {
      notificationAgent.create({
        title: "Error",
        content: getUserResponse.data.response.data
      });
    }
  }

  return {
    user,
    setEmail,
    updateUserById,
    updateUserByEmail
  };
});
