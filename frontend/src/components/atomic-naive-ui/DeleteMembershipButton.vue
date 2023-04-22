<script setup>
  import { ref, defineProps, defineEmits } from "vue";
  import { NButton, NModal } from 'naive-ui';

  import DeleteMemberShipForm from './DeleteMembershipForm.vue'

  const props = defineProps(['membershipId']);
  const emit = defineEmits(['updateMembers'])
  const activateDeleteForm = ref(false);

  function handleDeleteMembershipButtonClicked() {
    activateDeleteForm.value = true;
  }
  function handleCloseDeleteMembershipForm() {
    activateDeleteForm.value = false;
    emit('updateMembers');
  }
</script>

<template>

    <n-modal v-model:show=activateDeleteForm :trap-focus="false">
      <DeleteMemberShipForm 
        :membershipId="props.membershipId"
        @closeDeleteMembershipForm="handleCloseDeleteMembershipForm" 
      />
    </n-modal>

  <n-button @click="handleDeleteMembershipButtonClicked" type="error" block error strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">
    X
  </n-button>
</template>
