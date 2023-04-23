<script setup>
import { ref, defineEmits, onMounted, computed } from "vue";
import { useFetchAgent } from "../../stores/fetchAgent";
import { NForm, NButton, NGi, NInput, NFormItemGi, NGrid, useNotification, NCard, NDivider, NFormItem } from "naive-ui";
import { useRoute } from "vue-router";

const props = defineProps(['project']);
const emit = defineEmits(['updateProject', 'closeEditProjectForm']);
const fetchAgent = useFetchAgent();
const notificationAgent = useNotification();
const projectName = ref(props.project.name)
const projectDescription = ref(props.project.description)
const isNameSubmitButtonDisabled = computed(() => projectName.value == undefined || projectName.value.length == 0)
const isDescriptionSubmitButtonDisabled = computed(() => projectDescription.value == undefined || projectDescription.value.length == 0)

async function handleSubmitNewProjectName() {
  const response = await fetchAgent.patchProjectById(props.project.id, {name: projectName.value, description: null})
  if (response.isSuccessful) {
    emit('updateProject');
  } else {
    sendNotification("Error", response.data);
  }
}

async function handleSubmitNewProjectDescription() {
  const response = await fetchAgent.patchProjectById(props.project.id, {name: null, description: projectDescription.value})
  if (response.isSuccessful) {
    emit('updateProject');
  } else {
    sendNotification("Error", response.data);
  }
}

function handleCloseButtonClick(e) {
  emit('closeEditProjectForm');
}
function sendNotification(_title, _content) {
  notificationAgent.create({
    title: _title,
    content: _content
  });
}
</script>

<template>
  <n-card style="width: 50%; max-width: 1000px;" title="Edit Project" :bordered="false" size="huge" role="dialog"
    aria-modal="true">
    <div>

      <div style="display: flex; font-size: 1.2em;">
        <div style="width: 95%; margin-top: 10px;">
          <n-form-item label="Edit Name" >
            <n-input v-model:value="projectName" />
            <n-button @click="handleSubmitNewProjectName" :disabled="isNameSubmitButtonDisabled" style="margin-left: 8px;">Submit</n-button>
          </n-form-item>
        </div>
      </div>

      <div style="display: flex; font-size: 1.2em;">
        <div style="width: 95%; margin-top: 10px;">
          <n-form-item label="Edit Description" >
            <n-input :autosize="{
                minRows: 5,
                maxRows: 5
              }" type="textarea" v-model:value="projectDescription" />
            <n-button @click="handleSubmitNewProjectDescription" :disabled="isDescriptionSubmitButtonDisabled" style="margin-left: 8px; margin-bottom: 91px;">Submit</n-button>
          </n-form-item>
        </div>
      </div>

      <n-divider />

      <div>
        add phases options here
      </div>

    </div>
  </n-card>
</template>
