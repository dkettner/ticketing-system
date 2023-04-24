<script setup>
import { NSpace, NNotificationProvider } from 'naive-ui';
import SignInForm from '../components/atomic-naive-ui/SignInForm.vue';
</script>

<template>
  <div class="signIn">
    <div>
      <n-notification-provider>
        <div style="width: 300px;">
          <SignInForm />
        </div>
      </n-notification-provider>
    </div>
  </div>
</template>

<style>
.signIn {
  position: absolute;
  width: 280px;
  margin-left: -150px;
  margin-top: -280px;
  left: 50%;
  top: 50%;
}
</style>
