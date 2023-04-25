<script setup>
  import { ref, h } from "vue";
  import { RouterLink, useRoute } from "vue-router";
  import { NLayoutSider, NMenu, NScrollbar, NIcon } from "naive-ui";
  import {
    GridOutline as DashboarIcon,
    DocumentTextOutline as TicketsIcon,
    AlbumsOutline as ProjectsIcon
  } from "@vicons/ionicons5";

  const location = useRoute();
  const selectedKey = ref(location.name);
  const collapsed = ref(false);
  const menuOptions = [
    {
      label: () => h(
        RouterLink,
        { to: { name: "projects" } },
        { default: () => "Projects" }
      ),
      key: "projects",
      icon: renderIcon(ProjectsIcon)
    },
    {
      label: () => h(
        RouterLink,
        { to: { name: "tickets" } },
        { default: () => "Tickets" }
      ),
      key: "tickets",
      icon: renderIcon(TicketsIcon)
    }
  ];

  function renderIcon(icon) {
    return () => h(NIcon, null, { default: () => h(icon) });
  }
</script>

<template>
  <n-layout-sider
    bordered
    collapse-mode="width"
    :collapsed-width="64"
    :width="170"
    
    show-trigger
    :collapsed="collapsed"
    @collapse="collapsed = true"
    @expand="collapsed = false"
  >
    <div style="padding-right: 10px; height: calc(100vh - 80px)">
      <n-scrollbar style="max-height: 80vh;">
        <n-menu
          :value="selectedKey"
          :collapsed="collapsed"
          :collapsed-width="54"
          :collapsed-icon-size="20"
          :options="menuOptions"
          v-model:value="selectedKey"
        />
      </n-scrollbar>
    </div>
  </n-layout-sider>
</template>

<style>
</style>
