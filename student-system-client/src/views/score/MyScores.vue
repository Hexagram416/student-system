<template>
  <div class="my-scores-container">
    <el-card>
      <template #header>
        <span>我的成绩</span>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="courseName" label="课程名称" width="180" show-overflow-tooltip />
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column prop="regularScore" label="平时成绩" width="100" align="center" />
        <el-table-column prop="examScore" label="考试成绩" width="100" align="center" />
        <el-table-column prop="totalScore" label="总评成绩" width="100" align="center" />
        <el-table-column prop="gradePoint" label="绩点" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="scoreStatusTag(row.status)" size="small">
              {{ row.status || '-' }}
            </el-tag>
          </template>
        </el-table-column>
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
    const res = await request.get(`/scores/student/${studentId.value}`)
    if (res.code === 200) {
      const data = res.data.records || res.data.list || res.data || []
      tableData.value = Array.isArray(data) ? data : []
    } else {
      ElMessage.error(res.message || '获取成绩失败')
    }
  } catch (err) {
    ElMessage.error('获取成绩失败')
  } finally {
    loading.value = false
  }
}

const scoreStatusTag = (status) => {
  if (!status) return ''
  if (status === '已发布' || status === 'PUBLISHED') return 'success'
  if (status === '暂存' || status === 'DRAFT') return 'warning'
  return 'info'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.my-scores-container { padding: 20px; }
</style>
