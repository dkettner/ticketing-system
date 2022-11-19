<script setup>
  import SidebarMenuVue from '../components/atomic-naive-ui/SidebarMenu.vue';
  import { NNotificationProvider } from 'naive-ui';
  import { storeToRefs } from 'pinia';
  import { useUserStore } from '../stores/user.js'
import { onMounted } from 'vue';
  
  const userStore = useUserStore()
  const { user } = storeToRefs(userStore);

  onMounted( async () => {
    userStore.updateUserByEmail()
  });
</script>

<template>
  <h1 style="padding-left: 25px;">Dashboard</h1>
  <h1>Hello {{user.name}}!</h1>
  <n-notification-provider>
    <SidebarMenuVue />
  </n-notification-provider>
</template>
