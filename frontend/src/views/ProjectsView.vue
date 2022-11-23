<script setup>
  import { useProjectStore } from '../stores/project';
  import { useMembershipStore } from '../stores/membership';
  import { storeToRefs } from 'pinia';
  import { NCard } from 'naive-ui';

  import NewProjectButton from '../components/atomic-naive-ui/NewProjectButton.vue';

  const membershipStore = useMembershipStore();
  const memberships = storeToRefs(membershipStore);
  const projectStore = useProjectStore();
  const { projects } = storeToRefs(projectStore);
</script>

<template>
  <div style="padding-left: 25px;">
    <h1>Projects</h1>
    <NewProjectButton />
    <br>
    <div v-for="project in projects">
      <n-card :title="project.name" embedded hoverable style="max-width: 400px; ">
        {{project.description}}
        <p style="font-style: italic;">created at: {{project.creationTime}}</p>
      </n-card>
    </div>
  </div>
</template>
