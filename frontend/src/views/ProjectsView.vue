<script setup>
  import { useProjectStore } from '../stores/project';
  import { useMembershipStore } from '../stores/membership';
  import { storeToRefs } from 'pinia';
  import { NCard, NEllipsis, NRow, NCol } from 'naive-ui';

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
    <n-row :gutter="[10,10]">
      <div v-for="project in projects">
        <n-col :span="100">
          <RouterLink :to="`/projects/${project.id}`" style="text-decoration: none">
          <n-card :title="project.name" embedded hoverable style="width: 40vh; height: 25vh;">
            <n-ellipsis line-clamp="3" >
              {{project.description}}
              <template #tooltip>
                <div style="text-align: center; width: 40vh;">
                  {{project.description}}
                </div>
              </template>
            </n-ellipsis>
            <template #footer>
              <p style="font-style: italic;">created at: {{project.creationTime}}</p>
            </template>
          </n-card>
        </RouterLink>
        </n-col>
      </div>
    </n-row>
  </div>
</template>
