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
        collapse-mode="width"
        :collapsed-width="64"
        :width="240"
        show-trigger
        :collapsed="collapsed"
        @collapse="collapsed = true"
        @expand="collapsed = false"
      >
        <n-menu
          :collapsed="collapsed"
          :collapsed-width="64"
          :options="menuOptions"
        />
      </n-layout-sider>
      <n-layout>
        <span>Content</span>
      </n-layout>
    </n-layout>
  </n-space>
</template>

<script setup>
  import { ref, h } from "vue";
  import { NSpace, NLayout, NLayoutSider, NMenu, NModal } from "naive-ui";
  import NewProjectForm from "./NewProjectForm.vue";

  const activateProjectForm = ref(false);
  const collapsed = ref(false);
  const menuOptions = [
    {
      label: () => h(
        "div",
        {
          onClick: () => {
            activateProjectForm.value = true;
          },
        },
        "+ New Project"
      ),
      key: "addnewproject"
    },
    {
      label: "Project 0",
      key: "project0"
    },
    {
      label: "Project 1",
      key: "project1"
    },
    {
      label: "Project 2",
      key: "project2",
    }
  ];

  function handleProjectCreationCancelled() {
    activateProjectForm.value = false;
  }
  function handleProjectDataCollected(newProjectName, newProjectDescription) {
    console.log("new project name: " + newProjectName);
    console.log("new project description: " + newProjectDescription);
    activateProjectForm.value = false;
  }
</script>
