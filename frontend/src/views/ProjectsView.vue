<script setup>
  import { useProjectStore } from '../stores/project';
  import { storeToRefs } from 'pinia';
  import { NCard, NEllipsis, NRow, NCol, NEmpty, NIcon } from 'naive-ui';
  import {
    AlbumsOutline as ProjectsIcon
  } from "@vicons/ionicons5";

  import NewProjectButton from '../components/atomic-naive-ui/NewProjectButton.vue';

  const projectStore = useProjectStore();
  const { projects } = storeToRefs(projectStore);
</script>

<template>
  <div style="padding-left: 25px; width: 75%;">
    <h1>Projects</h1>
    <NewProjectButton />
    <br>
    <div class="noProjectsPlaceholder" v-if="projects.length == 0">
      <n-empty size="huge" description="You can't find anything">
        <template #icon>
          <n-icon>
            <ProjectsIcon />
          </n-icon>
        </template>
        No Projects Found
      </n-empty>
    </div>
    <div v-else>
      <n-row :gutter="[10, 10]">
        <div v-for="project in projects">
          <n-col :span="100">
            <RouterLink :to="`/projects/${project.id}`" style="text-decoration: none">
              <n-card :title="project.name" embedded hoverable style="width: 300px; height: 250px;">
                <n-ellipsis line-clamp="3">
                  {{ project.description }}
                  <template #tooltip>
                    <div style="text-align: center; max-width: 300px; max-height: 100px;">
                      {{ project.description }}
                    </div>
                  </template>
                </n-ellipsis>
                <template #footer>
                  <p style="font-style: italic;">creation time: {{ new Date(project.creationTime).toLocaleString() }}</p>
                </template>
              </n-card>
            </RouterLink>
          </n-col>
        </div>
      </n-row>
    </div>
  </div>
</template>

<style>
  .noProjectsPlaceholder {
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    min-height: 50vh;
  }
</style>
