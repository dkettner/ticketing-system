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
          Please choose a project or create a new one.
        </n-card>
      </n-layout>
      <n-layout v-else>
        {{projects[currentSelectedProjectId]}}
      </n-layout>
    </n-layout>
  </n-space>
</template>

<script setup>
  import { ref, h, onMounted } from "vue";
  import { NSpace, NLayout, NLayoutSider, NMenu, NModal, NCard, NButton, useNotification } from "naive-ui";
  import axios from "axios";
  import NewProjectForm from "./NewProjectForm.vue";

  const collapsed = ref(false);
  const activateProjectForm = ref(false);

  const projects = ref({});  
  const menuOptions = ref([]);
  const currentSelectedProjectId = ref(null);

  const notificationAgent = useNotification();

  onMounted( async () => {
    updateProjects();
  });

  function handleNewProjectButtonClicked() {
    activateProjectForm.value = true;
  }
  function handleProjectCreationCancelled() {
    activateProjectForm.value = false;
  }
  async function handleProjectDataCollected(newProjectName, newProjectDescription) {
    activateProjectForm.value = false;

    const defaultCreatorId = "a9cac26a-943c-494c-ba68-99af078ab24f";
    const defaultMemberIds = [];
    await postProject(newProjectName, newProjectDescription, defaultCreatorId, defaultMemberIds);
    await updateProjects();
  }
  function updateProjectMenu() {
    const newMenuOptions = [];
    for (let key in projects.value) {
      newMenuOptions.push(buildMenuOptionEntry(projects.value[key].name, key))
    }
    menuOptions.value = newMenuOptions;
  }
  function buildMenuOptionEntry(projectName, projectId) {
    return ( {
      label: () => h(
        "div",
        { onClick: () => { currentSelectedProjectId.value = projectId; } },
        projectName
      ),
      key: "project" + projectId
    });
  }
  function sendNotification(_title, _content) {
    notificationAgent.create({
      title: _title,
      content: _content
    });
  }
  async function updateProjects() {
    const result = await axios.get(`http://localhost:8080/projects`);
    if (result.status == 200) {
      const projectArray = result.data;

      projects.value = {};
      for (let index in projectArray) {
        projects.value[projectArray[index].id] = projectArray[index];
      }

      updateProjectMenu();
    } else {
      sendNotification("Error", "Failed to get projects from backend.");
    }
  }
  async function postProject(name, description, creatorId, memberIds) {    
    const result = await axios.post(`http://localhost:8080/projects`, { name: name, description: description, creatorId: creatorId, memberIds: memberIds});
    if (result.status == 201) {
      sendNotification(
        "Success", 
        "Created Project with name: " + name
      );
    } else {
      sendNotification(
        "Error", 
        "Failed to create project with name: " + name
      );
    }
  }
</script>

<style>

</style>