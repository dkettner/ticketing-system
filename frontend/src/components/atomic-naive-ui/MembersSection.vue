<template>
    <n-avatar-group style="padding-left: 15px;" v-if="membersWithUserData.value !== 'undefined'" :options="membersWithUserData" :size="48" :max="8">
    <template #avatar="{ option: { id, name, email, role, state } }">
      <n-tooltip>
        <template #trigger>
          <n-avatar :style="{backgroundColor: '#3E6E6E'}">{{ Array.from(name)[0] }}</n-avatar>
        </template>
        <div style="display: flex;">
          <div>
            {{ name }}
          </div>
          &nbsp;
          <div style="font-style: italic;" v-if="role == 'ADMIN'">
            (Admin)
          </div>
          &nbsp;
          <div  v-if="state == 'OPEN'">
            &#8594; pending
          </div>
        </div>
      </n-tooltip>
    </template>
    <template #rest="{ options: restOptions, rest }">
      <n-dropdown :options="createDropdownOptions(restOptions)" placement="top">
        <n-avatar>+{{ rest }}</n-avatar>
      </n-dropdown>
    </template>
  </n-avatar-group>
</template>

<script setup>
  import { NAvatar, NTooltip, NAvatarGroup, NDropdown } from 'naive-ui';
  import { useFetchAgent } from '../../stores/fetchAgent';
  import { defineProps, onMounted, ref } from 'vue';

  const fetchAgent = useFetchAgent();
  const props = defineProps(['projectId']);
  const membersWithUserData = ref([]);
  const projectMemberships = ref([]);

  const updateProjectMembers = async () => {
    const response = await fetchAgent.getMembershipsByProjectId(props.projectId);
    if (response.isSuccessful) {
      projectMemberships.value = response.data.map((membership) => ({ userId: membership.userId, role: membership.role, state:membership.state}));

      let tempMembers = ref([]);
      for (let membership of projectMemberships.value) {
        const getUserResponse = await fetchAgent.getUserById(membership.userId);
        if (getUserResponse.isSuccessful) {
          let user = getUserResponse.data
          tempMembers.value.push({id: user.id, name: user.name, email: user.email, role: membership.role, state: membership.state});
        }
      }

      membersWithUserData.value = [];
      for (let member of tempMembers.value) {
        membersWithUserData.value.push(member);
      }
    }
  }

  onMounted(async () => {
    await updateProjectMembers();

  });

  const createDropdownOptions =  (options) => options.map((option) => ({
    key: option.id,
    label: option.name
  }))

</script>