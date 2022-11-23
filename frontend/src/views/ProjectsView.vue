<script setup>
  import { useProjectStore } from '../stores/project';
  import { useMembershipStore } from '../stores/membership';
  import { storeToRefs } from 'pinia';
  import { NCard } from 'naive-ui';

  const membershipStore = useMembershipStore();
  const memberships = storeToRefs(membershipStore);
  const projectStore = useProjectStore();
  const { projects } = storeToRefs(projectStore);
</script>

<template>
  <div>
    <h1 style="padding-left: 25px;">Imagine all your projects right here.</h1>
    <div v-for="project in projects" style="padding-left: 25px;">
      <n-card :title="project.name" embedded hoverable style="max-width: 400px; ">
        {{project.description}}
        <p style="font-style: italic;">created at: {{project.creationTime}}</p>
      </n-card>
    </div>
  </div>
</template>
