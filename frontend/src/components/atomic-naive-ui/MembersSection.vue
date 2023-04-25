<template>
  <div>
    <div>
      <n-avatar-group style="padding-left: 5px;" v-if="membersWithUserData.value !== 'undefined'"
        :options="membersWithUserData" :size="48" :max="5">
        <template #avatar="{ option: { id, membershipId, name, email, role, state } }">
          <n-tooltip>
            <template #trigger>
              <n-avatar :style="{ backgroundColor: '#3E6E6E' }">{{ Array.from(name)[0] }}</n-avatar>
            </template>
            <div style="display: flex;">
              <div>
                {{ name }}
              </div>
              <div style="font-style: italic;" v-if="role == 'ADMIN'">
                &nbsp;(Admin)
              </div>
              <div v-if="state == 'OPEN'">
                &nbsp;&#8594; pending
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
    </div>
    <br />
    <div style="display: flex; margin-top: 10px;">
      <div v-if="amIAnAdminOfThisProject()" style=" padding-right: 5px;">
        <EditMembersButton @updateMembers="updateProjectMembers()" :projectMembers="membersWithUserData" />
      </div>
      &nbsp;&nbsp;
      <div>
        <LeaveProjectButton :membershipId="findMyMembershipOfThisProject().id" />
      </div>
    </div>
  </div>
</template>

<script setup>
  import { NAvatar, NTooltip, NAvatarGroup, NDropdown } from 'naive-ui';
  import { useFetchAgent } from '../../stores/fetchAgent';
  import { defineProps, onMounted, ref } from 'vue';
  import EditMembersButton from './EditMembersButton.vue';
  import { storeToRefs } from 'pinia';
  import { useMembershipStore } from '../../stores/membership';
  import { useRoute } from 'vue-router';
  import LeaveProjectButton from './LeaveProjectButton.vue';

  const fetchAgent = useFetchAgent();
  const route = useRoute();
  const props = defineProps(['projectId']);
  const membersWithUserData = ref([]);
  const projectMemberships = ref([]);
  const membershipStore = useMembershipStore();
  const { memberships } = storeToRefs(membershipStore);

  function amIAnAdminOfThisProject() {
    return findMyMembershipOfThisProject().role == "ADMIN";
  }

  function findMyMembershipOfThisProject() {
    return memberships.value.find(membership => membership.projectId == route.params.id);
  }

  const updateProjectMembers = async () => {
    const response = await fetchAgent.getMembershipsByProjectId(props.projectId);
    if (response.isSuccessful) {
      projectMemberships.value = response.data.map((membership) => ({ membershipId: membership.id, userId: membership.userId, role: membership.role, state: membership.state }));
      let tempMembers = ref([]);
      for (let membership of projectMemberships.value) {
        const getUserResponse = await fetchAgent.getUserById(membership.userId);
        if (getUserResponse.isSuccessful) {
          let user = getUserResponse.data;
          tempMembers.value.push({ id: user.id, membershipId: membership.membershipId, name: user.name, email: user.email, role: membership.role, state: membership.state });
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

  const createDropdownOptions = (options) => options.map((option) => ({
    key: option.id,
    label: option.name
  }))
</script>
