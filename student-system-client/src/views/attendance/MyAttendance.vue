<template>
  <div class="my-attendance-container">
    <el-card>
      <template #header>
        <span>我的考勤</span>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="courseName" label="课程名称" width="180" show-overflow-tooltip />
        <el-table-column prop="attendDate" label="日期" width="130" />
        <el-table-column prop="weekNumber" label="周次" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="attendanceStatusTag(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

// NOTE: In a real app, studentId comes from user context (store/auth). Hardcoded to 1 for now.
const studentId = ref(1)

const tableData = ref([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get(`/attendance/student/${studentId.value}`)
    if (res.code === 200) {
      const data = res.data.records || res.data.list || res.data || []
      tableData.value = Array.isArray(data) ? data : []
    } else {
      ElMessage.error(res.message || '获取考勤记录失败')
    }
  } catch (err) {
    ElMessage.error('获取考勤记录失败')
  } finally {
    loading.value = false
  }
}

const attendanceStatusTag = (status) => {
  const map = {
    '出勤': 'success',
    '迟到': 'warning',
    '早退': 'warning',
    '旷课': 'danger',
    '请假': 'info'
  }
  return map[status] || ''
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.my-attendance-container { padding: 20px; }
</style>
