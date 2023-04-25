<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { NSpace, NButton, NDataTable } from 'naive-ui';
import { useUserStore } from '../stores/user';
import { useMembershipStore } from '../stores/membership';
import { useProjectStore } from '../stores/project';
import { usePhaseStore } from '../stores/phase';
import { useTicketStore } from '../stores/ticket';
import { useFetchAgent } from '../stores/fetchAgent';
import { useNotification } from 'naive-ui';
import { storeToRefs } from 'pinia';

const notificationAgent = useNotification();
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

const columns = [
  {
    title: 'Title',
    key: 'title',
    sorter: 'default'
  },
  {
    title: 'Description',
    key: 'description'
  },
  {
    title: 'Project',
    key: 'projectName',
    filterOptions: projectFilterOptions(),
    filter (value, row) {
      return ~row.projectName.indexOf(value)
    },
    sorter: 'default'
  },
  {
    title: 'Phase',
    key: 'phaseName'
  },
  {
    title: 'Due',
    key: 'dueTime'
  },
  {
    title: 'Assignees',
    key: 'assigneeNames'
  }
]

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
    filterOptions.value.push({ label: projectName, value: projectName});
  }
  return filterOptions.value;
}

const tableHeight = computed(() => Math.floor((screen.height * 70)/100));

onMounted(async () => {
  await updateAll();
  compileTableData();
})

</script>

<template>
  <div style="padding-left: 25px; width:85vw;">
    <h1>Tickets</h1>
    <n-space vertical :size="20">
      <n-space justify="end">
      </n-space>
      <n-data-table ref="table" :style="{ height: `${tableHeight}px` }"  :columns="columns" :data="ticketData" :single-line="false" :bordered="false" flex-height/>
    </n-space>
  </div>
</template>
