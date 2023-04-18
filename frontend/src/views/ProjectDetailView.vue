<script setup>
  import { onBeforeUpdate, onMounted, onUpdated, ref } from 'vue';
  import { storeToRefs } from 'pinia';
  import { useProjectStore } from '../stores/project';
  import { useRoute } from 'vue-router';

  import DeleteProjectButton from '../components/atomic-naive-ui/DeleteProjectButton.vue';

  const projectStore = useProjectStore();
  const route = useRoute();
  const { projects } = storeToRefs(projectStore);
  const project = ref(projects.value.find(element => element.id == route.params.id));

  

  onMounted(async () => {
    await projectStore.updateProjectsByAcceptedMemberships();
  });

  onBeforeUpdate(async () => {
    await projectStore.updateProjectsByAcceptedMemberships();
  });


</script>

<template>
  <div>
    <div style="display: flex; justify-content: space-between;">
      <div style="padding-left: 40px; padding-right: 30px; display: block; font-size: 2em; margin-block-start: 0.67__qem; margin-block-end: 0.67em; margin-inline-start: 0; margin-inline-end: 0; font-weight: bold">
        {{ project.name }}
      </div>
      <DeleteProjectButton :project="project" />
    </div>
    <div style="padding-left: 45px;">{{ project.description }}</div>
    <br>
    <div style="padding-left: 45px;">creation time: {{ project.creationTime }}</div>
  </div>
</template>