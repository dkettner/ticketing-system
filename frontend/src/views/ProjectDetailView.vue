<script setup>
  import { onBeforeUpdate, onMounted, onUpdated, ref, computed, reactive, ReactiveEffect, onBeforeMount } from 'vue';
  import { storeToRefs } from 'pinia';
  import { useProjectStore } from '../stores/project';
  import { usePhaseStore } from '../stores/phase';
  import { useTicketStore } from '../stores/ticket';
  import { useRoute } from 'vue-router';
  import { NCard, NCol } from 'naive-ui';
  import  draggable  from 'vuedraggable';

  import DeleteProjectButton from '../components/atomic-naive-ui/DeleteProjectButton.vue';
  import NewTicketButton from '../components/atomic-naive-ui/NewTicketButton.vue';

  const route = useRoute();
  const projectStore = useProjectStore();
  const { projects } = storeToRefs(projectStore);
  const project = ref(projects.value.find(element => element.id == route.params.id));
  const phasestore = usePhaseStore();
  const { phases } = storeToRefs(phasestore);
  const ticketStore = useTicketStore();
  const { tickets } = storeToRefs(ticketStore);
  const arrayOfPhases = ref([]);

  async function updateTicketPhase (item) {
    if (item.added !== undefined) {
      let newPhaseIdOfTicket = arrayOfPhases.value.find(phase => phase.tickets.includes(item.added.element)).id
      await ticketStore.updateTicketPosition(route.params.id, item.added.element.id, newPhaseIdOfTicket);
      await updateLocalTickets();
    }
  }
  async function updateLocalTickets() {
    await ticketStore.updateTicketsByProjectId(route.params.id);
    arrayOfPhases.value = [];
    let tempPhases = sortPhases(phases.value.filter(phase => phase.projectId == route.params.id));
    for (let phase of tempPhases) {
      let ticketsOfCurrentPhase = tickets.value.filter(ticket => ticket.phaseId == phase.id);
      arrayOfPhases.value.push({id: phase.id, name: phase.name, tickets: ticketsOfCurrentPhase});
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

  onMounted(async () => {
    await projectStore.updateProjectsByAcceptedMemberships();
    await phasestore.updatePhasesByProjectId(route.params.id);
    await updateLocalTickets();
  });
</script>

<template>
  <div>
    <div style="display: flex; justify-content: space-between;">
      <div style="padding-left: 20px; padding-right: 30px; display: block; font-size: 2em; margin-block-start: 0.67__qem; margin-block-end: 0.67em; margin-inline-start: 0; margin-inline-end: 0; font-weight: bold">
        {{ project.name }}
      </div>
      <DeleteProjectButton :project="project" />
    </div>
    <div style="padding-left: 25px;">{{ project.description }}</div>
    <br/>
    <div style="padding-left: 25px;">creation time: {{ project.creationTime }}</div>
    <br/>
    <div style="padding-left: 25px;">
      <NewTicketButton @ticketCreated="updateLocalTickets" :project-id="route.params.id"/>
    </div>
    <br/>
    <div class="kanban" style="padding-left: 25px;">
      <div class="column" style="background-color:whitesmoke;" v-for="phase in arrayOfPhases">
        <h4 style="display: flex; justify-content: center;">{{ phase.name }}</h4>
        <draggable class="list-group" :list="phase.tickets" @change="updateTicketPhase" group="phase.id" itemKey="id">
          <template #item="{ element: ticket }">
            <n-card style="border-radius: 5px;" size="small" hoverable embedded>{{ ticket.title }}</n-card>
          </template>
        </draggable>
      </div>
    </div>
  </div>
  
</template>

<style>
  .kanban:after {
    content: "";
    display: table;
    clear: both;
  }

  .column {
    float: left;
    width: 200px;
    padding: 10px;
    height: 300px;
    border: 1px solid black;
    margin-right: 5px;
    border-radius: 5px;
  }
</style>
