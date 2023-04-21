<script setup>
  import { ref, defineEmits } from "vue";
  import { useTicketStore } from "../../stores/ticket";
  import { NForm, NButton, NGi, NInput, NFormItemGi, NGrid, useNotification } from "naive-ui";

  const props = defineProps(['projectId']);
  const ticketStore = useTicketStore();
  const notificationAgent = useNotification();
  const emit = defineEmits(['closeNewTicketFormSuccesful', 'closeNewTicketFormUnsuccesful']);
  const formRef = ref(null);
  const ticketPostData = ref({
    projectId: props.projectId,
    title: null,
    description: null,
    dueTime: null,
    assigneeIds: []
  });
  const rules =  {
    title: {
      required: true,
      trigger: ["blur", "input"],
      message: "Please input a title"
    },
    description: {
      required: true,
      trigger: ["blur", "input"],
      message: "Please input description"
    }
  };

  async function handleCreateButtonClick(e) {
    e.preventDefault();
    formRef.value?.validate(async (errors) => {
      if (!errors) {
        const result = await ticketStore.postTicket(ticketPostData.value);
        if (result.isPostSuccessful) {
          emit('closeNewTicketFormSuccesful');
          ticketPostData.value.projectId = null;
          ticketPostData.value.title = null;
          ticketPostData.value.description = null;
          ticketPostData.value.dueTime = null;
          ticketPostData.value.assigneeIds = [];
        } else {
          sendNotification("Error", result.message)
        }
      } else {
        console.log(errors);
        console.log("could not create new project");
      }
    });
  };
  function handleCancelButtonClick(e) {
    emit('closeNewTicketFormUnsuccesful');
    ticketPostData.value.projectId = null;
    ticketPostData.value.title = null;
    ticketPostData.value.description = null;
    ticketPostData.value.dueTime = null;
    ticketPostData.value.assigneeIds = [];
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
    :model="ticketPostData"
    :rules="rules"
    :size="medium"
    label-placement="top"
    style="min-width: 300px; width: 40%; max-width: 500px; background-color: #EEEEEE; padding: 20px; border-radius: 5px;"
  >
    <n-grid :span="24" :x-gap="24" :cols ="1">
      <n-form-item-gi :span="24" label="Title" path="title">
        <n-input style="border-radius: 5px;" v-model:value="ticketPostData.title" placeholder="Title" />
      </n-form-item-gi>
      <n-form-item-gi :span="24" label="Description" path="description">
        <n-input
          style="border-radius: 5px;"
          v-model:value="ticketPostData.description"
          placeholder="Description"
          type="textarea"
          :autosize="{
            minRows: 5,
            maxRows: 10
          }"
        />
      </n-form-item-gi>

      <n-gi :span="24">
        <div style="display: flex; justify-content: flex-end">
          <n-button style="border-radius: 5px;" type="error" @click="handleCancelButtonClick">
            Cancel
          </n-button>
          &nbsp;
          <n-button style="border-radius: 5px;" type="primary" @click="handleCreateButtonClick">
            Create New Ticket
          </n-button>
        </div>
      </n-gi>
    </n-grid>
  </n-form>
</template>
