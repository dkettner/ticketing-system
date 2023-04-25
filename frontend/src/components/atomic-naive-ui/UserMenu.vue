<template>
  <n-dropdown placement="bottom-end" :options="options">
    <n-avatar :style="{ backgroundColor: '#888888' }" round :size="48">{{ Array.from(user.name)[0] }}</n-avatar>
  </n-dropdown>

  <n-modal v-model:show=activateEditUserForm :trap-focus="false">
    <EditUserForm @closeInvitationsForm="handleCloseInvitationsForm" />
  </n-modal>
</template>

<script setup>
import { useUserStore } from '../../stores/user';
import { useSessionStore } from '../../stores/session';
import { h, ref } from "vue";
import { NIcon, NDropdown, NAvatar, NModal } from "naive-ui";
import {
  PersonCircleOutline as UserIcon,
  LogOutOutline as LogoutIcon
} from "@vicons/ionicons5";
import { storeToRefs } from 'pinia';
import EditUserForm from './EditUserForm.vue';

const userStore = useUserStore();
const { user } = storeToRefs(userStore);
const sessionStorage = useSessionStore();
const activateEditUserForm = ref(false);

function handleActivateEditUserForm() {
  activateEditUserForm.value = true;
}

function handleDeactivateEditUserForm() {
  activateEditUserForm.value = false;
}


const renderIcon = (icon) => {
  return () => {
    return h(NIcon, null, {
      default: () => h(icon)
    });
  };
};


const options = [
  {
    label: () => h(
      "div", { onClick: () => handleActivateEditUserForm() }, "Profile"
    ),
    key: "profile",
    icon: renderIcon(UserIcon)
  },
  {
    label: () => h(
      "div", { onClick: () => sessionStorage.logout() }, "Logout"
    ),
    key: "logout",
    icon: renderIcon(LogoutIcon)
  }
]

</script>
