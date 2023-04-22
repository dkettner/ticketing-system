<script setup>
  import { useRouter } from 'vue-router';
  import { ref, defineEmits, defineProps } from "vue";
  import { NForm, NButton, NGi, NInput, NFormItemGi, NGrid, useNotification } from "naive-ui";
  import { useMembershipStore } from '../../stores/membership';
import { useFetchAgent } from '../../stores/fetchAgent';

  const router = useRouter();
  const fetchAgent = useFetchAgent();
  const membershipStore = useMembershipStore();
  const notificationAgent = useNotification();
  const emit = defineEmits(['closeDeleteMembershipForm']);
  const formRef = ref(null);
  const props = defineProps(['membershipId']);

  async function handleDeleteButtonClick(e) {
    e.preventDefault();
    formRef.value?.validate(async (errors) => {
      if (!errors) {
        const result = await fetchAgent.deleteMembershipById(props.membershipId)
        if (result.isSuccessful) {
          emit('closeDeleteMembershipForm');
        } else {
          sendNotification("Error", result.data)
        }
      } else {
        console.log(errors);
      }
    });
  };
  function handleCancelButtonClick(e) {
    emit('closeDeleteMembershipForm');
  };
  function sendNotification(_title, _content) {
    notificationAgent.create({
      title: _title,
      content: _content
    });
  }
</script>

<template>
  <n-form
    ref="formRef"
    :size="medium"
    label-placement="top"
    style="min-width: 300px; width: 40%; max-width: 500px; background-color: #EEEEEE; padding: 20px; border-radius: 5px;"
  >
    <n-grid :span="24" :x-gap="24" :cols ="1">
      <n-gi :span="24">Do you really want to delete this membership?</n-gi>
      <n-gi> &nbsp; </n-gi>
      <n-gi :span="24">
        <div style="display: flex; justify-content: flex-end">
          <n-button style="border-radius: 5px;" type="strong secondary" @click="handleCancelButtonClick">
            Cancel
          </n-button>
          &nbsp;
          <n-button style="border-radius: 5px;" type="error" @click="handleDeleteButtonClick">
            Delete membership
          </n-button>
        </div>
      </n-gi>
    </n-grid>
  </n-form>
</template>
