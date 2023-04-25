<script setup>
import { onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useUserStore } from '../stores/user.js';
import { useSessionStore } from '../stores/session';
import { useProjectStore } from '../stores/project';
import { useMembershipStore } from '../stores/membership';
import { usePhaseStore } from '../stores/phase';
import { NSpace, NLayout, NLayoutHeader, NNotificationProvider, NDivider } from 'naive-ui';

import SidebarMenuVue from '../components/atomic-naive-ui/SidebarMenu.vue';
import UserMenu from '../components/atomic-naive-ui/UserMenu.vue';

const sessionStore = useSessionStore();
const membershipStore = useMembershipStore();
const projectStore = useProjectStore();
const phasestore = usePhaseStore();
const userStore = useUserStore();
const { projects } = storeToRefs(projectStore)
const { user } = storeToRefs(userStore);

onMounted(async () => {
  userStore.setEmail(sessionStore.email);
  await userStore.updateUserByEmail();
  await membershipStore.updateMembershipsByEmail();
  await projectStore.updateProjectsByAcceptedMemberships();
  const projectIds = projects.value.map(element => element.id);
  projectIds.forEach(async (projectId) => await phasestore.updatePhasesByProjectId(projectId))
});
</script>

<template>
  <div>
  <div style="display: flex; justify-content: space-between; border-bottom: 1px solid #F0F0F0;">
    <div style="display: flex;">
      <img src="../assets/logo_ticketing.png" alt="logo" style="height: 68px;">
    </div>
    <div style="padding: 10px 20px 5px 5px;">
      <UserMenu />
    </div>
  </div>
  <div>
    <n-notification-provider>
      <n-space vertical>
        <n-layout>
          <n-layout has-sider>
            <SidebarMenuVue />
            <RouterView />
          </n-layout>
        </n-layout>
      </n-space>
    </n-notification-provider>
  </div>
</div>
</template>
