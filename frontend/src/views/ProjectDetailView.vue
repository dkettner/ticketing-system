<script setup>
import { onBeforeUpdate, onMounted, onUpdated, ref, computed, reactive, ReactiveEffect, onBeforeMount } from 'vue';
import { storeToRefs } from 'pinia';
import { useProjectStore } from '../stores/project';
import { usePhaseStore } from '../stores/phase';
import { useTicketStore } from '../stores/ticket';
import { useMembershipStore } from '../stores/membership';
import { useRoute } from 'vue-router';
import { NCard, NDivider, NModal } from 'naive-ui';
import { useFetchAgent } from '../stores/fetchAgent';
import draggable from 'vuedraggable';

import EditProjectButton from '../components/atomic-naive-ui/EditProjectButton.vue';
import DeleteProjectButton from '../components/atomic-naive-ui/DeleteProjectButton.vue';
import NewTicketButton from '../components/atomic-naive-ui/NewTicketButton.vue';
import MembersSection from '../components/atomic-naive-ui/MembersSection.vue';
import EditTicketForm from '../components/atomic-naive-ui/EditTicketForm.vue';

const route = useRoute();
const fetchAgent = useFetchAgent();
const projectStore = useProjectStore();
const { projects } = storeToRefs(projectStore);
const project = computed(() => projects.value.find(element => element.id == route.params.id));
const phasestore = usePhaseStore();
const { phases } = storeToRefs(phasestore);
const ticketStore = useTicketStore();
const { tickets } = storeToRefs(ticketStore);
const membershipStore = useMembershipStore();
const { memberships } = storeToRefs(membershipStore);
const arrayOfPhases = ref([]);
const activateEditTicketForm = ref(false);
const selectedTicketId = ref(null);

function handleTicketClick(ticketId) {
  selectedTicketId.value = ticketId
  activateEditTicketForm.value = true;
}
function handleCloseEditTicketForm() {
  activateEditTicketForm.value = false;
  selectedTicketId.value = null;
}

async function updateTicketPhase(item) {
  if (item.added !== undefined) {
    let newPhaseIdOfTicket = arrayOfPhases.value.find(phase => phase.tickets.includes(item.added.element)).id
    await ticketStore.updateTicketPosition(route.params.id, item.added.element.id, newPhaseIdOfTicket);
    await updateLocalTickets();
  }
}
async function updateArrayOfPhases() {
  await phasestore.updatePhasesByProjectId(route.params.id);
  updateLocalTickets();
}
async function updateLocalTickets() {
  await ticketStore.updateTicketsByProjectId(route.params.id);
  arrayOfPhases.value = [];
  let tempPhases = sortPhases(phases.value.filter(phase => phase.projectId == route.params.id));
  for (let phase of tempPhases) {
    let ticketsOfCurrentPhase = tickets.value.filter(ticket => ticket.phaseId == phase.id);
    arrayOfPhases.value.push({ id: phase.id, name: phase.name, tickets: ticketsOfCurrentPhase });
  }
}

function sortPhases(givenPhases) {
  if (!Array.isArray(givenPhases) || !givenPhases.length) {
    return [];
  }
  const sortedPhases = [];
  let phase = givenPhases.find(element => element.previousPhaseId == null);
  sortedPhases.push(phase);
  while (phase.nextPhaseId != null) {
    phase = givenPhases.find(element => element.id == phase.nextPhaseId)
    sortedPhases.push(phase);
  }
  return sortedPhases;
}

function amIAnAdminOfThisProject() {
  let myProjectMembership = memberships.value.find(membership => membership.projectId == route.params.id);
  return myProjectMembership.role == "ADMIN";
}

onMounted(async () => {
  await projectStore.updateProjectsByAcceptedMemberships();
  await phasestore.updatePhasesByProjectId(route.params.id);
  await updateLocalTickets();
});
</script>

<template>
  <div style="width: 100%; padding-left: 25px; overflow-wrap: break-word;">
    <div style="padding-top: 10px; display: flex; width: 100%; max-width: calc(100% - 20px);">
      <div
        style="padding-top: 5px; font-size: 2em; margin-block-start: 0.67__qem; margin-block-end: 0.67em; margin-inline-start: 0; margin-inline-end: 0; font-weight: bold">
        {{ project.name }}
      </div>
      <div style="margin-top: 10px; margin-left: 30px;">
        <EditProjectButton v-if="amIAnAdminOfThisProject()" @updatePhasesAndTickets="updateArrayOfPhases"
          @updateProject="projectStore.updateProjectsByAcceptedMemberships()" :project="project" />
      </div>
      <div style="margin-top: 10px; padding-left: 12px;">
        <DeleteProjectButton v-if="amIAnAdminOfThisProject()" :project="project" />
      </div>
    </div>
    <div style="display: flex;">
      <div style="height: 200px;  width: 90%; margin-left: 2px;">
        <div style="height: 160px; font-size: 1.2em; margin-right: 35px;">{{ project.description }}</div>
        <br />
        <div style="font-style: italic; font-size: 1.1em;">creation time: {{ new
          Date(project.creationTime).toLocaleString() }}</div>
      </div>
      <div
        style="height: 200px;  width: 40%; max-width: calc(40% - 80px); border-left: 1px solid #F0F0F0; padding-left: 40px; padding-right: 20px;">
        <div style="font-weight: bold; font-size: 1.4em;">
          Members
        </div>
        <br />
        <div style="margin-top: 5px;">
          <MembersSection :project-id="route.params.id" />
        </div>
      </div>
    </div>
    <n-divider />
    <div>
      <NewTicketButton @ticketCreated="updateLocalTickets" :project-id="route.params.id" />
    </div>
    <br />

    <n-modal v-model:show=activateEditTicketForm :trap-focus="false">
      <EditTicketForm :ticketId="selectedTicketId" :projectId="route.params.id" @closeEditTicketForm="handleCloseEditTicketForm"
        @updateTickets="updateLocalTickets" />
    </n-modal>
    <div class="kanban">
      <div class="column" style="" v-for="phase in arrayOfPhases">
        <div class="columnHeader">
          <h4 style="display: flex; justify-content: center; ">{{ phase.name }}</h4>
        </div>
        <draggable style="min-height: 100%;" class="list-group" :list="phase.tickets" @change="updateTicketPhase" group="phase.id" itemKey="id">
          <template #item="{ element: ticket }">
            <n-card @click="handleTicketClick(ticket.id)"
              style="border-radius: 5px; margin-bottom: 8px; white-space:normal; cursor: pointer;" :bordered="true" size="small"
              hoverable>{{ ticket.title }}</n-card>
          </template>
        </draggable>
      </div>
    </div>
  </div>
</template>

<style>
.kanban {
  display: flex;
  width: 100%;
  max-width: calc(100% - 20px);
  overflow: auto;
  white-space: nowrap;
}

.column {
  display: inline-block;
  flex: 0 0 220px;
  min-height: 380px;
  padding: 10px;
  margin-right: 5px;
  margin-bottom: 10px;
  border-radius: 5px;
  background-color: #ebebeb
}

.columnHeader {
  padding: 8px;
  margin-bottom: 12px;
  background-color: #A8B8D0;
  border-radius: 5px;
  box-shadow: 2px 2px 3px rgb(153, 153, 153);
}
</style>
