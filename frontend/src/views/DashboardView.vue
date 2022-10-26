<script setup>
  import SidebarMenuVue from '../components/atomic-naive-ui/SidebarMenu.vue';
  import { NNotificationProvider } from 'naive-ui';
</script>

<template>
  <h1 style="padding-left: 25px;">Dashboard</h1>
  <n-notification-provider>
    <SidebarMenuVue />
  </n-notification-provider>
</template>
