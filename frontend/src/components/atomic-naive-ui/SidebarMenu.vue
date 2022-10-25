<template>
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
import { ref } from "vue";
import { NSpace, NLayout, NLayoutSider, NMenu} from "naive-ui";

const collapsed = ref(false);
const menuOptions = [
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
</script>