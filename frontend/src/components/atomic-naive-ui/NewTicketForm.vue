<script setup>
import { ref, defineEmits, onMounted } from "vue";
import { useTicketStore } from "../../stores/ticket";
import { NForm, NButton, NGi, NInput, NFormItemGi, NGrid, NDatePicker, NTransfer, useNotification } from "naive-ui";
import { useFetchAgent } from "../../stores/fetchAgent";

const props = defineProps(['projectId']);
const ticketStore = useTicketStore();
const fetchAgent = useFetchAgent();
const notificationAgent = useNotification();
const emit = defineEmits(['closeNewTicketFormSuccesful', 'closeNewTicketFormUnsuccesful']);
const formRef = ref(null);
const ticketPostData = ref({
  projectId: props.projectId,
  title: null,
  description: null,
  dueTime: null,
  assigneeIds: null
});
const rules = {
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
const projectMembers = ref([])

const getProjectMembers = async () => {
  const response = await fetchAgent.getMembershipsByProjectId(props.projectId);
  if (response.isSuccessful) {
    const acceptedMemberships = response.data.filter(membership => membership.state == 'ACCEPTED');
    projectMembers.value = [];
    for (let membership of acceptedMemberships) {
      const getUserResponse = await fetchAgent.getUserById(membership.userId);
      if (getUserResponse.isSuccessful) {
        projectMembers.value.push({ label: getUserResponse.data.name, value: getUserResponse.data.id });
      }
    }
  }
}

async function handleCreateButtonClick(e) {
  e.preventDefault();
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      if (ticketPostData.value.dueTime < 1 || ticketPostData.value.dueTime == null) {
        ticketPostData.value.dueTime = null;
      } else {
        ticketPostData.value.dueTime = new Date(ticketPostData.value.dueTime);
      }
      if (ticketPostData.value.assigneeIds == null) {
        ticketPostData.value.assigneeIds = [];
      }
      
      const result = await ticketStore.postTicket(ticketPostData.value);
      if (result.isPostSuccessful) {
        console.log(result)
        emit('closeNewTicketFormSuccesful');
        ticketPostData.value.projectId = null;
        ticketPostData.value.title = null;
        ticketPostData.value.description = null;
        ticketPostData.value.dueTime = null;
        ticketPostData.value.assigneeIds = null;
        //assignees.value = [];
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
  //assignees.value = [];
};
function sendNotification(_title, _content) {
  notificationAgent.create({
    title: _title,
    content: _content
  });
}

onMounted(async () => {
  await getProjectMembers();
})
</script>

<template>
  <n-form ref="formRef" :model="ticketPostData" :rules="rules" :size="medium" label-placement="top"
    style="min-width: 300px; width: 50%; max-width: 600px; background-color: #fdfdfd; padding: 25px; border-radius: 5px;">
    <n-grid :span="24" :x-gap="24" :cols="1">

      <n-gi :span="24">
        <div style="font-size: 1.5em; font-weight: bold; padding-top: 10px; padding-bottom: 40px;">
          Create Ticket
        </div>
      </n-gi>

      <n-form-item-gi :span="24" label="Title" path="title">
        <n-input style="border-radius: 5px;" v-model:value="ticketPostData.title" placeholder="Title" />
      </n-form-item-gi>
      <n-form-item-gi :span="24" label="Description" path="description">
        <n-input style="border-radius: 5px;" v-model:value="ticketPostData.description" placeholder="Description"
          type="textarea" :autosize="{
              minRows: 5,
              maxRows: 10
            }" />
      </n-form-item-gi>
      <n-form-item-gi :span="12" label="Due" path="dueTime">
        <n-date-picker clearable v-model:value="ticketPostData.dueTime" type="datetime" />
      </n-form-item-gi>
      <n-form-item-gi label="Assignees" path="assigneeIds">
        <n-transfer size="large" virtual-scroll ref="transfer" v-model:value="ticketPostData.assigneeIds" :options="projectMembers" />
      </n-form-item-gi>

      <n-gi :span="24">
        <div style="display: flex; justify-content: flex-end">
          <n-button type="error" block error strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;" @click="handleCancelButtonClick">
            Cancel
          </n-button>
          &nbsp;&nbsp;
          <n-button type="primary" block primary strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;" @click="handleCreateButtonClick">
            Create Ticket
          </n-button>
        </div>
      </n-gi>
    </n-grid>
  </n-form>
</template>
