<script setup>
  import { ref, defineProps, defineEmits } from "vue";
  import { NButton, NModal } from 'naive-ui';

  import LeaveProjectForm from "./LeaveProjectForm.vue";

  const props = defineProps(['membershipId']);
  const emit = defineEmits(['updateMembers'])
  const activateDeleteForm = ref(false);

  function handleLeaveProjectButtonClicked() {
    activateDeleteForm.value = true;
  }
  function handleCloseLeaveProjectForm() {
    activateDeleteForm.value = false;
    emit('updateMembers');
  }
</script>

<template>

    <n-modal v-model:show=activateDeleteForm :trap-focus="false">
      <LeaveProjectForm
        :membershipId="props.membershipId"
        @closeLeaveProjectForm="handleCloseLeaveProjectForm" 
      />
    </n-modal>

  <n-button @click="handleLeaveProjectButtonClicked" type="error" block error strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">
    Leave Project
  </n-button>
</template>
