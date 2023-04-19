<script setup>
  import { onBeforeUpdate, onMounted, onUpdated, ref, computed } from 'vue';
  import { storeToRefs } from 'pinia';
  import { useProjectStore } from '../stores/project';
  import { usePhaseStore } from '../stores/phase';
  import { useRoute } from 'vue-router';
  import { NCard, NCol } from 'naive-ui';

  import DeleteProjectButton from '../components/atomic-naive-ui/DeleteProjectButton.vue';

  const route = useRoute();
  const projectStore = useProjectStore();
  const { projects } = storeToRefs(projectStore);
  const project = ref(projects.value.find(element => element.id == route.params.id));
  const phasestore = usePhaseStore();
  const { phases } = storeToRefs(phasestore);
  const projectPhases = computed(() => sortPhases(phases.value.filter(element => element.projectId == route.params.id)));

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
    <li v-for="phase in projectPhases">
      {{ phase.name }}
    </li>

  </div>
  
  
  
</template>