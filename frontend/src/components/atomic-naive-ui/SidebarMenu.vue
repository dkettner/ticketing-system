<template>
  <div class="projectform">
  <n-modal v-model:show=activateProjectForm :trap-focus="false">
    <NewProjectForm 
      @projectCreationCancelled="handleProjectCreationCancelled" 
      @projectDataCollected ="handleProjectDataCollected"
    />
  </n-modal>
  </div>
  <n-space vertical>
    <n-layout has-sider>
      <n-layout-sider
        bordered
        collapse-mode="transform"
        :collapsed-width="10"
        :width="200"
        show-trigger
        :collapsed="collapsed"
        @collapse="collapsed = true"
        @expand="collapsed = false"
      >
        <div style="padding-left: 10px; padding-right: 10px;">
          <n-button @click="handleNewProjectButtonClicked" type="primary" block primary strong>
            + New Project
          </n-button>
          <n-menu
            :collapsed="collapsed"
            :collapsed-width="10"
            :options="menuOptions"
          />
        </div>
      </n-layout-sider>
      <n-layout v-if="!currentSelectedProjectId" content-style="padding-top: 50px; padding-left: 100px;">
        <n-card style="width: 330px; background-color: whitesmoke;">
          {{defaultContentString}}
        </n-card>
      </n-layout>
      <n-layout v-else>
        {{currentSelectedProjectId}}
      </n-layout>
    </n-layout>
  </n-space>
</template>

<script setup>
  import { ref, h, onMounted } from "vue";
  import { NSpace, NLayout, NLayoutSider, NMenu, NModal, NCard, NButton, useNotification } from "naive-ui";
  import axios from "axios";
  import NewProjectForm from "./NewProjectForm.vue";

  const notification = useNotification();
  const defaultContentString = "Please choose a project or create a new one.";
  const defaultCreatorId = "a9cac26a-943c-494c-ba68-99af078ab24f";
  const currentProjectsIdsWithNames = ref([]);
  const getProjectsResult = ref(null);
  const currentSelectedProjectId = ref(null);
  const activateProjectForm = ref(false);
  const collapsed = ref(false);
  const menuOptions = ref([
    {
      label: () => h(
        "div",
        {
          onClick: () => {
            currentSelectedProjectId.value = "Project 0 plus details";
          },
        },
        "Project 0"
      ),
      key: "project0"
    },
    {
      label: () => h(
        "div",
        {
          onClick: () => {
            currentSelectedProjectId.value = "Project 1 plus details";
          },
        },
        "Project 1"
      ),
      key: "project1"
    },
    {
      label: () => h(
        "div",
        {
          onClick: () => {
            currentSelectedProjectId.value = "Project 2 plus details";
          },
        },
        "Project 2"
      ),
      key: "project2"
    }
  ]);

  onMounted( async () => {
    getProjectsResult.value = await axios.get(`http://localhost:8080/projects`);
  });


  function handleNewProjectButtonClicked() {
    activateProjectForm.value = true;
  }
  function handleProjectCreationCancelled() {
    activateProjectForm.value = false;
  }
  function handleProjectDataCollected(newProjectName, newProjectDescription) {
    postProject(newProjectName, newProjectDescription, defaultCreatorId, [])
    menuOptions.value.push({label: newProjectName, key: newProjectName});
    activateProjectForm.value = false;
    updateProjects();
    sendNotification(newProjectName);
  }
  function sendNotification(message) {
    notification.create({
      title: 'New Project added',
      content: message
    });
  }
  async function updateProjects() {
    const result = await axios.get(`http://localhost:8080/projects`);
    getProjectsResult.value = result;
    console.log(result);
  }
  async function postProject(name, descrption, creatorId, memberIds) {
    const jsonProjectDto = JSON.stringify({ name: name, descrption: descrption, creatorId: creatorId, memberIds: memberIds});
    const result = await axios.post(`http://localhost:8080/projects`, { name: name, descrption: descrption, creatorId: creatorId, memberIds: memberIds});
    updateProjects();
  }
</script>

<style>

</style>