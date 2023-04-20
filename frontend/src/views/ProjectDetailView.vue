<script setup>
  import { onBeforeUpdate, onMounted, onUpdated, ref, computed, reactive } from 'vue';
  import { storeToRefs } from 'pinia';
  import { useProjectStore } from '../stores/project';
  import { usePhaseStore } from '../stores/phase';
  import { useTicketStore } from '../stores/ticktet';
  import { useRoute } from 'vue-router';
  import { NCard, NCol } from 'naive-ui';
  import  draggable  from 'vuedraggable';

  import DeleteProjectButton from '../components/atomic-naive-ui/DeleteProjectButton.vue';

  const route = useRoute();
  const projectStore = useProjectStore();
  const { projects } = storeToRefs(projectStore);
  const project = ref(projects.value.find(element => element.id == route.params.id));
  const phasestore = usePhaseStore();
  const { phases } = storeToRefs(phasestore);
  const projectPhases = reactive(sortPhases(phases.value.filter(element => element.projectId == route.params.id)));
  const ticketStore = useTicketStore();
  const { tickets } = storeToRefs(ticketStore);
  const projectTickets = ref([]);
  const getTicketsByPhaseId = (phaseId) => projectTickets.value.filter(ticket => ticket.phaseId == phaseId);
  
  async function updateLocalTickets() {
    await ticketStore.updateTicketsByProjectId(route.params.id);
    projectTickets.value = ticketStore.getTicketsByProjectId(route.params.id);
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
      <div style="padding-left: 40px; padding-right: 30px; display: block; font-size: 2em; margin-block-start: 0.67__qem; margin-block-end: 0.67em; margin-inline-start: 0; margin-inline-end: 0; font-weight: bold">
        {{ project.name }}
      </div>
      <DeleteProjectButton :project="project" />
    </div>
    <div style="padding-left: 45px;">{{ project.description }}</div>
    <br>
    
    <div style="padding-left: 45px;">creation time: {{ project.creationTime }}</div>

    <draggable :list="projectPhases" tag="ul">
      <template #item ="{ element: phase }">
        <li>{{ phase }}</li>
      </template>
    </draggable>


  </div>
  
</template>