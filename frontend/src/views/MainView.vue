<script setup>
  import { onMounted } from 'vue';
  import { storeToRefs } from 'pinia';
  import { useUserStore } from '../stores/user.js';
  import { useSessionStore } from '../stores/session';
  import { NSpace, NLayout, NLayoutHeader} from 'naive-ui';

  import SidebarMenuVue from '../components/atomic-naive-ui/SidebarMenu.vue';

  const sessionStore = useSessionStore();
  const userStore = useUserStore();
  const { user } = storeToRefs(userStore);

  onMounted( async () => {
    userStore.setEmail(sessionStore.email);
    await userStore.updateUserByEmail();
  });
</script>

<template>
  <n-space vertical>
    <n-layout>
      <n-layout-header>Hello {{user.name}}! After login this should always be visible.</n-layout-header>
      <n-layout has-sider>
        <SidebarMenuVue />
        <RouterView />
      </n-layout>
    </n-layout>
  </n-space>
</template>
