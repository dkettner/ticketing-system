import { defineStore } from "pinia";
import axios from 'axios';
import Cookies from "js-cookie";
import { computed } from "vue";
import { useRouter } from 'vue-router';

export const useSessionStore = defineStore("session", () => {
  const login = async (loginEmail, loginPassword) => {
    try {
      const postAuthenticationResponse = 
        await axios.post('/authentication', { email: loginEmail, password: loginPassword });
      
      setEmail(loginEmail);
      return { isLoginSuccessful: true, message: "Logged in with email: " + loginEmail};
    } catch(error) {
      console.log(error);
      return { isLoginSuccessful: false, message: error.message};
    }
  }
  const logout = async () => {
    // TODO: delete jwt in cookies -> delete call on backend

    deleteEmail();
    const router = useRouter();
    router.go();
  };
  function isLoggedIn() {
    return (Cookies.get("email") !== undefined);
  };

  const email = computed(() => {
    return Cookies.get("email");
  })
  const setEmail = (newEmail) => {
    document.cookie = "email=" + newEmail + "; max-age=43200; path=/"
  }

  const deleteEmail = () => {
    Cookies.remove("email");
  }

  return {
    login,
    logout,
    email,
    isLoggedIn
  };
});
