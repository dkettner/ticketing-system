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

    <n-modal v-model:show=activateProjectForm :trap-focus="false">
      <NewProjectForm 
        @closeProjectForm="handleCloseProjectForm" 
      />
    </n-modal>

  <n-button @click="handleNewProjectButtonClicked" type="primary" block primary strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;;">
    + New Project
  </n-button>
</template>
