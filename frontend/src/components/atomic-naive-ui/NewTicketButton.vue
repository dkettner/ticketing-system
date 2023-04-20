<script setup>
  import { ref, defineProps } from "vue";
  import { NButton, NModal } from 'naive-ui';
  
  import NewTicketForm from "./NewTicketForm.vue";

  const props = defineProps(['projectId']);
  const activateNewTicketForm = ref(false);

  function handleNewTicketButtonClicked() {
    activateNewTicketForm.value = true;
  }
  function handleCloseNewTicketForm() {
    activateNewTicketForm.value = false;
  }
</script>

<template>
    <n-modal v-model:show=activateNewTicketForm :trap-focus="false">
      <NewTicketForm
      :projectId="props.projectId"
        @closeNewTicketForm="handleCloseNewTicketForm" 
      />
    </n-modal>

  <n-button @click="handleNewTicketButtonClicked" type="primary" block primary strong style="max-width: 125px; border-radius: 5px;">
    + New Ticket
  </n-button>
</template>
