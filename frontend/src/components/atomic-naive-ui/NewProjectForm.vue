<template>
  <n-form
    ref="formRef"
    :model="model"
    :rules="rules"
    :size="medium"
    label-placement="top"
    style="min-width: 300px; width: 40%; max-width: 500px; background-color: #EEEEEE; padding: 20px;"
  >
    <n-grid :span="24" :x-gap="24" :cols ="1">
      <n-form-item-gi :span="24" label="Project Name" path="projectName">
        <n-input v-model:value="model.projectName" placeholder="Project Name" />
      </n-form-item-gi>
      <n-form-item-gi :span="24" label="Description" path="projectDescription">
        <n-input
          v-model:value="model.projectDescription"
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
          <n-button type="error" @click="handleCancelButtonClick">
            Cancel
          </n-button>
          &nbsp;
          <n-button type="primary" @click="handleCreateButtonClick">
            Create New Project
          </n-button>
        </div>
      </n-gi>
    </n-grid>
  </n-form>
</template>

<script setup>
  import { ref, defineEmits } from "vue";
  import { NForm, NButton, NGi, NInput, NFormItemGi, NGrid} from "naive-ui";

  const emit = defineEmits(['projectDataCollected', 'projectCreationCancelled']);
  const formRef = ref(null);
  const model = ref({
    projectName: null,
    projectDescription: null
  });
  const rules =  {
    projectName: {
      required: true,
      trigger: ["blur", "input"],
      message: "Please input project name"
    },
    projectDescription: {
      required: true,
      trigger: ["blur", "input"],
      message: "Please input description"
    }
  };

  function handleCreateButtonClick(e) {
    e.preventDefault();
    formRef.value?.validate((errors) => {
      if (!errors) {
        emit('projectDataCollected', model.value.projectName, model.value.projectDescription);
        model.value.projectName = null;
        model.value.projectDescription = null;
      } else {
        console.log(errors);
        console.log("could not create new project");
      }
    });
    
  };
  function handleCancelButtonClick(e) {
    model.value.projectName = null;
    model.value.projectDescription = null;
    emit('projectCreationCancelled');
  };
</script>
