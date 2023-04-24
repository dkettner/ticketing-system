<script setup>
  import { ref, defineProps, defineEmits } from "vue";
  import { NButton, NModal } from 'naive-ui';
  
  import EditProjectForm from "./EditProjectForm.vue";

  const projectProps = defineProps(['project']);
  const emit = defineEmits(['updateProject', 'updatePhasesAndTickets'])
  const activateEditForm = ref(false);

  function handleEditProjectButtonClicked() {
    activateEditForm.value = true;
  }
  function handleCloseEditProjectForm() {
    activateEditForm.value = false;
  }
</script>

<template>

    <n-modal v-model:show=activateEditForm :trap-focus="false">
      <EditProjectForm
        :project="projectProps.project"
        @closeEditProjectForm="handleCloseEditProjectForm"
        @updateProject="emit('updateProject')"
        @updatePhasesAndTickets="emit('updatePhasesAndTickets')"
      />
    </n-modal>

  <n-button @click="handleEditProjectButtonClicked" block secondary strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">
    Edit
  </n-button>
</template>
