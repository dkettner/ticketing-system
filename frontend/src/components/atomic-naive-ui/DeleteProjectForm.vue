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
    style="min-width: 300px; width: 40%; max-width: 500px; background-color: #fdfdfd; padding: 20px; border-radius: 5px;"
  >
    <n-grid :span="24" :x-gap="24" :cols ="1">
      <n-gi :span="24">Do you really want to delete this project?</n-gi>
      <n-gi> &nbsp; </n-gi>
      <n-gi :span="24">
        <div style="display: flex; justify-content: flex-end">
          <n-button block secondary strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;" @click="handleCancelButtonClick">
            Cancel
          </n-button>
          &nbsp;&nbsp;
          <n-button type="error" block error strong style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;" @click="handleDeleteButtonClick">
            Delete project
          </n-button>
        </div>
      </n-gi>
    </n-grid>
  </n-form>
</template>
