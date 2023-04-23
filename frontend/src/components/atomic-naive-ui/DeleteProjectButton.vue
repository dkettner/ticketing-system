<script setup>
  import { ref, defineProps } from "vue";
  import { NButton, NModal, NNotificationProvider } from 'naive-ui';
  
  import DeleteProjectForm from './DeleteProjectForm.vue';

  const projectProps = defineProps(['project']);
  const activateDeleteForm = ref(false);

  function handleDeleteProjectButtonClicked() {
    activateDeleteForm.value = true;
  }
  function handleCloseDeleteProjectForm() {
    activateDeleteForm.value = false;
  }
</script>

<template>

    <n-modal v-model:show=activateDeleteForm :trap-focus="false">
      <DeleteProjectForm 
      :project="projectProps.project"
        @closeProjectForm="handleCloseDeleteProjectForm" 
      />
    </n-modal>

  <n-button @click="handleDeleteProjectButtonClicked" type="error" block error strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">
    Delete
  </n-button>
</template>
