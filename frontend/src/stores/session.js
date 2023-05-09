import { defineStore } from "pinia";
import Cookies from "js-cookie";
import { computed } from "vue";
import { useRouter } from 'vue-router';
import { useFetchAgent } from "./fetchAgent";

export const useSessionStore = defineStore("session", () => {
  const fetchAgent = useFetchAgent();

  const login = async (loginEmail, loginPassword) => {
    const postAuthenticationResponse = await fetchAgent.postAuthentication(loginEmail, loginPassword);
    console.log(postAuthenticationResponse)
    if (postAuthenticationResponse.isSuccessful) {
      setEmail(loginEmail);
      localStorage.setItem("jwt", postAuthenticationResponse.data)
      return { isLoginSuccessful: true, message: "Logged in with email: " + loginEmail };
    } else {
      deleteEmail();
      return { isLoginSuccessful: false, message: postAuthenticationResponse.data };
    }
  }

  const logout = async () => {
    console.log(localStorage.getItem("jwt"))
    localStorage.removeItem("jwt");
    deleteEmail();
    
    window.location.reload(true);
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
