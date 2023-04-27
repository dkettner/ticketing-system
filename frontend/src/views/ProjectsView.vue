<script setup>
import { useProjectStore } from '../stores/project';
import { storeToRefs } from 'pinia';
import { NCard, NEllipsis, NRow, NCol, NEmpty, NIcon } from 'naive-ui';
import { AlbumsOutline as ProjectsIcon } from "@vicons/ionicons5";

import NewProjectButton from '../components/atomic-naive-ui/NewProjectButton.vue';
import InvitationsButton from '../components/atomic-naive-ui/InvitationsButton.vue';
import { useMembershipStore } from '../stores/membership';

const projectStore = useProjectStore();
const { projects } = storeToRefs(projectStore);
const membershipStore = useMembershipStore();
const { memberships } = storeToRefs(membershipStore);

function invitationsExist() {
  let unacceptedMemberships = memberships.value.filter(membership => membership.state == 'OPEN');
  return unacceptedMemberships !== undefined && unacceptedMemberships.length > 0;
};

async function updateProjects() {
  await projectStore.updateProjectsByAcceptedMemberships();
}

</script>

<template>
  <div style="padding-left: 25px; width: 100%;">
    <h1>Projects</h1>
    <div style="display: flex;">
      <NewProjectButton />
      <div v-if="invitationsExist()" style="margin-left: 15px;">
        <InvitationsButton @updateProjects="updateProjects()"/>
      </div>
    </div>

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
              <n-card :title="project.name" hoverable style="width: 300px; height: 250px; background-color: #f6f6f6; border-radius: 5px;">
                <n-ellipsis line-clamp="3">
                  {{ project.description }}
                  <template #tooltip>
                    <div style="text-align: center; max-width: 300px; max-height: 200px;">
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
  min-height: 60vh;
}
</style>
