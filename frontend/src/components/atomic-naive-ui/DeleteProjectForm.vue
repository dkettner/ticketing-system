<script setup>
  import { useRouter } from 'vue-router';
  import { ref, defineEmits, defineProps } from "vue";
  import { useProjectStore } from "../../stores/project";
  import { NForm, NButton, NGi, NInput, NFormItemGi, NGrid, useNotification } from "naive-ui";

  const router = useRouter();
  const projectStore = useProjectStore();
  const notificationAgent = useNotification();
  const emit = defineEmits(['closeProjectForm']);
  const formRef = ref(null);
  const projectProp = defineProps(['project']);

  async function handleDeleteButtonClick(e) {
    e.preventDefault();
    formRef.value?.validate(async (errors) => {
      if (!errors) {
        const result = await projectStore.deleteProjectById(projectProp.project.id);
        if (result.isDeleteSuccessful) {
          sendNotification(
            "Success", 
            "Deleted project: " + projectProp.project.name
          );
          emit('closeProjectForm');
          router.push('/projects/overview');
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
      <n-gi :span="24">Do you really want to delete this project?</n-gi>
      <n-gi> &nbsp; </n-gi>
      <n-gi :span="24">
        <div style="display: flex; justify-content: flex-end">
          <n-button style="border-radius: 5px;" type="strong secondary" @click="handleCancelButtonClick">
            Cancel
          </n-button>
          &nbsp;
          <n-button style="border-radius: 5px;" type="error" @click="handleDeleteButtonClick">
            Delete project
          </n-button>
        </div>
      </n-gi>
    </n-grid>
  </n-form>
</template>