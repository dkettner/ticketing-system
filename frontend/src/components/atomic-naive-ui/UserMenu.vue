<template>
  <n-dropdown placement="bottom-end" :options="options">
    <n-avatar round :size="48">{{ Array.from(user.name)[0] }}</n-avatar>
  </n-dropdown>
</template>

<script setup>
  import { useUserStore } from '../../stores/user';
  import { useSessionStore } from '../../stores/session';
  import { h } from "vue";
  import { NIcon, NDropdown, NAvatar } from "naive-ui";
  import {
    PersonCircleOutline as UserIcon,
    Pencil as EditIcon,
    LogOutOutline as LogoutIcon
  } from "@vicons/ionicons5";
  import { storeToRefs } from 'pinia';

  const userStore = useUserStore();
  const { user } = storeToRefs(userStore);
  const sessionStorage = useSessionStore();

  const renderIcon = (icon) => {
    return () => {
      return h(NIcon, null, {
        default: () => h(icon)
      });
    };
  };
  

  const options = [
        {
          label: "Profile",
          key: "profile",
          icon: renderIcon(UserIcon)
        },
        {
          label: "Edit Profile",
          key: "editProfile",
          icon: renderIcon(EditIcon)
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
