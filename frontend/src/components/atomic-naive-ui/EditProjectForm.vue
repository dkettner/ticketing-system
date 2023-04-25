<script setup>
import { ref, defineEmits, onMounted, computed } from "vue";
import { useFetchAgent } from "../../stores/fetchAgent";
import { NForm, NButton, NGi, NInput, NFormItemGi, NGrid, useNotification, NCard, NDivider, NFormItem } from "naive-ui";
import { useRoute } from "vue-router";
import { usePhaseStore } from "../../stores/phase";
import { storeToRefs } from "pinia";

const props = defineProps(['project']);
const emit = defineEmits(['updateProject', 'updatePhasesAndTickets', 'closeEditProjectForm']);
const fetchAgent = useFetchAgent();
const notificationAgent = useNotification();
const route = useRoute();
const projectName = ref(props.project.name)
const projectDescription = ref(props.project.description)
const isNameSubmitButtonDisabled = computed(() => projectName.value == undefined || projectName.value.length == 0)
const isDescriptionSubmitButtonDisabled = computed(() => projectDescription.value == undefined || projectDescription.value.length == 0)
const phasestore = usePhaseStore();
const { phases } = storeToRefs(phasestore);
const phasesOfProject = computed(() => sortPhases(phases.value.filter(phase => phase.projectId == route.params.id)));
const newPhaseName = ref('');

async function handleDeletePhase(phaseId) {
  const response = await fetchAgent.deletePhaseById(phaseId);
  if (response.isSuccessful) {
    emit('updatePhasesAndTickets');
  } else {
    sendNotification("Error", response.data);
  }
}
async function handlePatchPhaseName(phaseId, phaseName) {
  const response = await fetchAgent.patchPhaseNameById(phaseId, {name: phaseName});
  if (response.isSuccessful) {
    emit('updatePhasesAndTickets');
  } else {
    sendNotification("Error", response.data);
  }
}
async function handleAddPhase() {
  const projectPhases = sortPhases(phases.value.filter(phase => phase.projectId == route.params.id));
  const firstPhaseOfProjectId = projectPhases[projectPhases.length -1].id;
  const response = await fetchAgent.postPhase({projectId: route.params.id, name: newPhaseName.value, previousPhaseId: firstPhaseOfProjectId});
  if (response.isSuccessful) {
    emit('updatePhasesAndTickets');
    newPhaseName.value = '';
  } else {
    sendNotification("Error", response.data);
  }
}


function isPhaseButtonDisabled(phaseName) {
  return phaseName == undefined || phaseName.length <1;
} 
function isPhaseDeleteButtonDisabled() {
  return phases.value.filter(phase => phase.projectId == route.params.id).length < 2;
} 
async function handleSubmitNewProjectName() {
  const response = await fetchAgent.patchProjectById(props.project.id, { name: projectName.value, description: null })
  if (response.isSuccessful) {
    emit('updateProject');
  } else {
    sendNotification("Error", response.data);
  }
}

async function handleSubmitNewProjectDescription() {
  const response = await fetchAgent.patchProjectById(props.project.id, { name: null, description: projectDescription.value })
  if (response.isSuccessful) {
    emit('updateProject');
  } else {
    sendNotification("Error", response.data);
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
function handleCloseButtonClick(e) {
  emit('closeEditProjectForm');
}
function sendNotification(_title, _content) {
  notificationAgent.create({
    title: _title,
    content: _content
  });
}

onMounted(async () => {
  await phasestore.updatePhasesByProjectId(route.params.id);
});
</script>

<template>
  <n-card style="width: 50%; max-width: 1000px; background-color: #fdfdfd; border-radius: 5px;" title="Edit Project" :bordered="false" size="huge" role="dialog"
    aria-modal="true">
    <div>

      <div style="display: flex; font-size: 1.2em;">
        <div style="width: 95%;">
          <n-form-item label="Edit Name">
            <n-input v-model:value="projectName" />
            <n-button @click="handleSubmitNewProjectName" :disabled="isNameSubmitButtonDisabled" block secondary strong
              style="max-width: 70px; margin-left: 10px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">Change</n-button>
          </n-form-item>
        </div>
      </div>

      <div style="display: flex; font-size: 1.2em;">
        <div style="width: 95%; border-bottom: 2px solid #f0f0f0;">
          <n-form-item label="Edit Description">
            <n-input :autosize="{
                minRows: 3,
                maxRows: 3
              }" type="textarea" v-model:value="projectDescription" />
            <n-button @click="handleSubmitNewProjectDescription" :disabled="isDescriptionSubmitButtonDisabled" block
              secondary strong
              style="max-width: 70px; margin-left: 10px; margin-bottom: 46px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">Change</n-button>
          </n-form-item>
        </div>
      </div>

      <br />
      <div style="font-size: 1.3em; font-weight: bold; ">
        Phases
      </div>

      <div style="padding-left: 10px;" v-for="(phase, index) in phasesOfProject">

        <div style="width: 400px; margin-top: 18px; display: flex; font-size: 1.2em;">
          <div>{{ index + 1 }}</div>
          <div>:</div>
          <div>&nbsp;</div>
          <div>&nbsp;</div>
          <n-input v-model:value="phase.name" />
          <n-button @click="handlePatchPhaseName(phase.id, phase.name)" block secondary strong :disabled="isPhaseButtonDisabled(phase.name)"
            style="margin-left: 15px; max-width: 70px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">
            Change
          </n-button>
          <n-button @click="handleDeletePhase(phase.id)" type="error" block strong :disabled="isPhaseDeleteButtonDisabled()"
            style="margin-left: 15px; max-width: 40px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">
            X
          </n-button>
        </div>
      </div>

      <div style="padding-left: 10px;">
        <div style="width: 400px; margin-top: 18px; display: flex; font-size: 1.2em;">
          <div>{{ phasesOfProject.length + 1 }}</div>
          <div>:</div>
          <div>&nbsp;</div>
          <div>&nbsp;</div>
          <n-input v-model:value="newPhaseName" />
          <n-button @click="handleAddPhase" type="primary" block primary strong :disabled="isPhaseButtonDisabled(newPhaseName)"
            style="margin-left: 15px; margin-right: 55px; max-width: 70px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">
            Add
          </n-button>
        </div>
      </div>

    </div>
  </n-card>
</template>
