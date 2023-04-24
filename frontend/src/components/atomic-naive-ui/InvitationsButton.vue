
<script setup>
  import { ref, defineEmits } from "vue";
  import { NButton, NModal } from 'naive-ui';
  
  import InvitationsForm from "./InvitationsForm.vue";

  const emit = defineEmits(['updateProjects']);
  const activateInvitationsForm = ref(false);
  
  function handleInvitationsButtonClicked() {
    activateInvitationsForm.value = true;
  }
  function handleCloseInvitationsForm() {
    activateInvitationsForm.value = false;
  }
  function handleUpdateProjects() {
    emit('updateProjects');
  }
</script>

<template>
    <n-modal v-model:show=activateInvitationsForm :trap-focus="false">
      <InvitationsForm
        @closeInvitationsForm="handleCloseInvitationsForm" 
        @updateProjects="handleUpdateProjects" 
      />
    </n-modal>

  <n-button @click="handleInvitationsButtonClicked" type="info" block primary strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">
    View Invitations
  </n-button>
</template>
