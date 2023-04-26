<script setup>
import { ref, onMounted, computed, h } from 'vue';
import { NSpace, NButton, NDataTable, NModal } from 'naive-ui';
import { useUserStore } from '../stores/user';
import { useMembershipStore } from '../stores/membership';
import { useProjectStore } from '../stores/project';
import { usePhaseStore } from '../stores/phase';
import { useTicketStore } from '../stores/ticket';
import { useFetchAgent } from '../stores/fetchAgent';
import { storeToRefs } from 'pinia';
import { RouterLink } from 'vue-router';

import EditTicketForm from '../components/atomic-naive-ui/EditTicketForm.vue';

const fetchAgent = useFetchAgent();
const userStore = useUserStore();
const { user } = storeToRefs(userStore);
const membershipStore = useMembershipStore();
const { memberships } = storeToRefs(membershipStore);
const projectStore = useProjectStore();
const { projects } = storeToRefs(projectStore);
const phaseStore = usePhaseStore();
const { phases } = storeToRefs(phaseStore);
const ticketStore = useTicketStore();
const { tickets } = storeToRefs(ticketStore);
const tableRef = ref(null);
const ticketData = ref([]);
const selectedTicketId = ref(null);
const activateEditTicketForm = ref(false);
const projectIdOfSelectedTicketId = ref(null);

const columns =  [
  {
    title: 'Title',
    key: 'title',
    sorter: 'default',
    render(row) {
      return h(
        "div",
        { 
          onClick: () => openEditTicketForm(row.key, row.projectId), 
          style: { 'cursor': 'pointer' }
        },
        { default: () => row.title }
      );
    }
  },
  {
    title: 'Description',
    key: 'description'
  },
  {
    title: 'Project',
    key: 'projectName',
    filterOptions: projectFilterOptions(),
    filter(value, row) {
      return ~row.projectName.indexOf(value)
    },
    sorter: 'default',
    render(row) {
      return h(
        RouterLink,
        { 
          to: { name: "projectDetails", params: { id: row.projectId } },
          style: { 'text-decoration': 'none' }  
        },
        { default: () => row.projectName }
      );
    }
  },
  {
    title: 'Phase',
    key: 'phaseName'
  },
  {
    title: 'Due',
    key: 'dueTime',
    sorter: (row1, row2) => nullableDateSorter(row1.dueTime, row2.dueTime)
  },
  {
    title: 'Assignees',
    key: 'assigneeNames',
    filterOptions: [
      {
        label: 'Assigned To Me',
        value: 'myUserId'
      },
      {
        label: 'Unassigned',
        value: 'unassigned'
      }
    ],
    filter (value, row) {
      if (value == 'myUserId') {
        return row.assigneeIds.includes(user.value.id);
      }

      return row.assigneeIds == "";
    }
  }
]

function openEditTicketForm(ticketId, projectId) {
  selectedTicketId.value = ticketId;
  projectIdOfSelectedTicketId.value = projectId;
  activateEditTicketForm.value = true;
}
function handleCloseEditTicketForm() {
  activateEditTicketForm.value = false;
  selectedTicketId.value = null;
  projectIdOfSelectedTicketId.value = null;
}

function nullableDateSorter(dateAlpha, dateOmega) {
  if (dateAlpha == null) {
    return 1;
  }
  if (dateOmega == null) {
    return -1;
  }
  return new Date(dateAlpha) <= new Date(dateOmega) ? -1 : 1
}

async function updateAll() {
  await membershipStore.updateMembershipsByEmail();
  await projectStore.updateProjectsByAcceptedMemberships();
  for (let project of projects.value) {
    await phaseStore.updatePhasesByProjectId(project.id);
    await ticketStore.updateTicketsByProjectId(project.id);
  }
}
async function compileTableData() {
  const newData = ref([]);
  for (let ticket of tickets.value) {
    const project = projects.value.find(project => project.id == ticket.projectId);
    const phase = phases.value.find(phase => phase.id == ticket.phaseId);
    const assignees = ref([]);
    for (let assigneeId of ticket.assigneeIds) {
      const response = await fetchAgent.getUserById(assigneeId);
      if (response.isSuccessful) {
        assignees.value.push({ userId: response.data.id, userName: response.data.name })
      }
    }

    const assigneeNames = assignees.value.map(assignee => assignee.userName);

    newData.value.push({
      key: ticket.id,
      title: ticket.title,
      description: ticket.description,
      dueTime: ticket.dueTime == null ? null : new Date(ticket.dueTime).toLocaleString(),
      projectId: project.id,
      projectName: project.name,
      phaseName: phase.name,
      assigneeNames: assigneeNames.toString(),
      assigneeIds: assignees.value.map(assignee => assignee.userId)
    });
  }

  ticketData.value = newData.value;
};

function projectFilterOptions() {
  const filterOptions = ref([])
  const projectNames = projects.value.map(project => project.name);
  for (let projectName of projectNames) {
    filterOptions.value.push({ label: projectName, value: projectName });
  }
  return filterOptions.value;
}
function reloadPage() {
  window.location.reload(true);
}

const tableHeight = computed(() => Math.floor((screen.height * 70) / 100));

onMounted(async () => {
  await updateAll();
  compileTableData();
})

</script>

<template>
  <div style="background-color: #ffffff; width: 100%;">
    <div style="padding-left: 25px; width:85vw;">
      <h1>Tickets</h1>
      <n-modal v-model:show=activateEditTicketForm :trap-focus="false">
      <EditTicketForm :ticketId="selectedTicketId" :projectId="projectIdOfSelectedTicketId" @closeEditTicketForm="handleCloseEditTicketForm"
        @updateTickets="reloadPage" />
    </n-modal>
      <n-space vertical :size="20">
        <n-space justify="end">
        </n-space>
        <n-data-table ref="table" :style="{ height: `${tableHeight}px` }" :columns="columns" :data="ticketData"
          :single-line="false" :bordered="false" flex-height />
      </n-space>
    </div>
  </div>
</template>
