<script setup>
  import { ref } from "vue";
  import { NButton, NModal, NNotificationProvider } from 'naive-ui';
  
  import NewProjectForm from './NewProjectForm.vue';

  const activateProjectForm = ref(false);

  function handleNewProjectButtonClicked() {
    activateProjectForm.value = true;
  }
  function handleCloseProjectForm() {
    activateProjectForm.value = false;
  }
</script>

<template>
  <n-notification-provider>
    <n-modal v-model:show=activateProjectForm :trap-focus="false">
      <NewProjectForm 
        @closeProjectForm="handleCloseProjectForm" 
      />
    </n-modal>
  </n-notification-provider>
  <n-button @click="handleNewProjectButtonClicked" type="primary" block primary strong style="max-width: 125px">
    + New Project
  </n-button>
</template>
