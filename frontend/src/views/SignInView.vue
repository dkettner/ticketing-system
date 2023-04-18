<script setup>
  import { NSpace, NNotificationProvider } from 'naive-ui';
  import SignInForm from '../components/atomic-naive-ui/SignInForm.vue';
</script>

<template>
  <n-space justify="center" style="transform: translateY(+50%);">
    <img src="../assets/ticketsystem-logo.png">
    <n-notification-provider>
      <SignInForm />
    </n-notification-provider>
  </n-space>
</template>
