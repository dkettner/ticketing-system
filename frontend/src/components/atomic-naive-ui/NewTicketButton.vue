<script setup>
  import { ref, defineProps, defineEmits } from "vue";
  import { NButton, NModal } from 'naive-ui';
  
  import NewTicketForm from "./NewTicketForm.vue";

  const emit = defineEmits(['ticketCreated']);
  const props = defineProps(['projectId']);
  const activateNewTicketForm = ref(false);
  
  function handleNewTicketButtonClicked() {
    activateNewTicketForm.value = true;
  }
  function handleCloseNewTicketForm() {
    activateNewTicketForm.value = false;
  }
  function handleCloseNewTicketFormSuccesful() {
    activateNewTicketForm.value = false;
    emit('ticketCreated');
  }
</script>

<template>
    <n-modal v-model:show=activateNewTicketForm :trap-focus="false">
      <NewTicketForm
      :projectId="props.projectId"
        @closeNewTicketFormUnsuccesful="handleCloseNewTicketForm" @closeNewTicketFormSuccesful="handleCloseNewTicketFormSuccesful" 
      />
    </n-modal>

  <n-button @click="handleNewTicketButtonClicked" type="primary" block primary strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">
    + New Ticket
  </n-button>
</template>
