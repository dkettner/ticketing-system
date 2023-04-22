<script setup>
  import { ref, defineProps, defineEmits } from "vue";
  import { NButton, NModal } from 'naive-ui';
  
  import EditMembersForm from "./EditMembersForm.vue";

  const emit = defineEmits(['updateMembers']);
  const props = defineProps(['projectMembers']);
  const activateEditMembersForm = ref(false);
  
  function handleEditMembersButtonClicked() {
    activateEditMembersForm.value = true;
  }
  function handleCloseEditMembersForm() {
    activateEditMembersForm.value = false;
  }
  function handleUpdateMembers() {
    emit('updateMembers');
  }
</script>

<template>
    <n-modal v-model:show=activateEditMembersForm :trap-focus="false">
      <EditMembersForm
      :projectMembers="props.projectMembers"
        @closeEditMembersForm="handleCloseEditMembersForm" 
        @updateMembers="handleUpdateMembers" 
      />
    </n-modal>

  <n-button @click="handleEditMembersButtonClicked" block secondary strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">
    Edit Members
  </n-button>
</template>
