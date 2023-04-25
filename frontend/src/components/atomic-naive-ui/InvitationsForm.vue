<script setup>
  import { defineEmits, onMounted, computed } from "vue";
  import { useFetchAgent } from "../../stores/fetchAgent";
  import { NButton, useNotification, NCard } from "naive-ui";
  import { useMembershipStore } from "../../stores/membership";
  import { storeToRefs } from "pinia";
  import { useProjectStore } from "../../stores/project";

  const emit = defineEmits(['updateProjects', 'closeInvitationsForm']);
  const fetchAgent = useFetchAgent();
  const notificationAgent = useNotification();
  const membershipStore = useMembershipStore();
  const { memberships } = storeToRefs(membershipStore);
  const unaccceptedMemberships = computed(() => memberships.value.filter(membership => membership.state == 'OPEN'));
  const projectStore = useProjectStore();
  const { projects } = storeToRefs(projectStore);

  async function handleAcceptInvitation(membershipId) {
    const response = await fetchAgent.putMembershipState(membershipId, {state: 'ACCEPTED'});
    if (response.isSuccessful) {
      await membershipStore.updateMembershipsByEmail();
      await projectStore.updateProjectsByAcceptedMemberships();
      emit('updateProjects');
      if (unaccceptedMemberships.length < 1) {
        emit('closeInvitationsForm');
      }
    } else {
      sendNotification("Error", response.data);
    }
  }
  async function handleDeclineInvitation(membershipId) {
    const response = await fetchAgent.deleteMembershipById(membershipId);
    if (response.isSuccessful) {
      await membershipStore.updateMembershipsByEmail();
      if (unaccceptedMemberships.length < 1) {
        emit('closeInvitationsForm');
      }
    } else {
      sendNotification("Error", response.data);
    }
  }

  function handleCloseButtonClick(e) {
    emit('closeInvitationsForm');
  }
  function sendNotification(_title, _content) {
    notificationAgent.create({
      title: _title,
      content: _content
    });
  }
</script>

<template>
  <n-card style=" max-width: 1000px; border-radius: 5px; background-color: #fdfdfd;" title="Invitations" :bordered="false" size="huge" role="dialog" aria-modal="true">
    <div v-for="invitation in unaccceptedMemberships">
      <div style="display: flex; justify-content: space-around; font-size: 1.2em; padding-bottom: 10px;">
        
        <div style="display: flex;">
          <div style="margin-top: 10px; font-weight: bold;">
            Project ID:
          </div>
          &nbsp;
          <div style="margin-top: 10px">
            {{ invitation.projectId }}
          </div>
        </div>

        <div style="display: flex;">
          <div style="margin-top: 10px; font-weight: bold;">
            Role:
          </div>
          &nbsp;
          <div style="margin-top: 10px; min-width: 67px;">
            {{ invitation.role }}
          </div>
        </div>

        <div style="display: flex;">
          <div style="margin-top: 6px;">
            <n-button @click="handleAcceptInvitation(invitation.id)" type="primary" block strong primary
              style="border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">
              Accept
            </n-button>
          </div>
          &nbsp;&nbsp;&nbsp;
          <div style="margin-top: 6px;">
            <n-button @click="handleDeclineInvitation(invitation.id)" type="error" block strong primary
              style="border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">
              Decline
            </n-button>
          </div>
        </div>

      </div>
    </div>
  </n-card>
</template>

<style>
  .inputEmail:focus {
    outline: none;
  }
</style>
