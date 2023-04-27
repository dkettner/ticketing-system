<script setup>
  import { ref, defineEmits } from "vue";
  import { useProjectStore } from "../../stores/project";
  import { NForm, NButton, NGi, NInput, NFormItemGi, NGrid, useNotification } from "naive-ui";

  const projectStore = useProjectStore();
  const notificationAgent = useNotification();
  const emit = defineEmits(['closeProjectForm']);
  const formRef = ref(null);
  const projectPostData = ref({
    name: null,
    description: null
  });
  const rules =  {
    name: {
      required: true,
      trigger: ["blur", "input"],
      message: "Please input project name"
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
        const result = await projectStore.postNewProject(projectPostData.value);
        if (result.isPostSuccessful) {
          sendNotification(
            "Success", 
            "Created a new project with name: " + projectPostData.value.name
          );
          emit('closeProjectForm');
          projectPostData.value.name = null;
          projectPostData.value.description = null;
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
    emit('closeProjectForm');
    projectPostData.value.name = null;
    projectPostData.value.description = null;
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
    :model="projectPostData"
    :rules="rules"
    :size="medium"
    label-placement="top"
    style="min-width: 300px; width: 50%; max-width: 600px; background-color: #fdfdfd; padding: 25px; border-radius: 5px;"
  >
    <n-grid :span="24" :x-gap="24" :cols ="1">
      <n-gi :span="24">
        <div style="font-size: 1.5em; font-weight: bold; padding-top: 10px; padding-bottom: 40px;">
          Create Project
        </div>
      </n-gi>

      <n-form-item-gi :span="24" label="Project Name" path="name">
        <n-input style="border-radius: 5px;" v-model:value="projectPostData.name" placeholder="Project Name" />
      </n-form-item-gi>
      <n-form-item-gi :span="24" label="Description" path="description">
        <n-input
          style="border-radius: 5px;"
          v-model:value="projectPostData.description"
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
          <n-button type="error" block error strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;" @click="handleCancelButtonClick">
            Cancel
          </n-button>
          &nbsp;&nbsp;
          <n-button type="primary" block primary strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;" @click="handleCreateButtonClick">
            Create Project
          </n-button>
        </div>
      </n-gi>
    </n-grid>
  </n-form>
</template>
