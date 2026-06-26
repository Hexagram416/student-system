<template>
  <div class="score-input-container">
    <el-card>
      <template #header>
        <span>成绩录入</span>
      </template>

      <!-- Step 1: Select offering -->
      <el-form :inline="true">
        <el-form-item label="选择开课">
          <el-select
            v-model="selectedOfferingId"
            placeholder="请选择开课"
            filterable
            clearable
            style="width: 360px"
            @change="onOfferingChange"
          >
            <el-option
              v-for="item in offerings"
              :key="item.id"
              :label="`${item.courseName || ''} - ${item.teacherName || ''} (${item.semester || ''})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- Step 2 & 3: Score input table (visible after offering selected) -->
      <div v-if="selectedOfferingId && students.length > 0" style="margin-top: 20px">
        <el-table :data="students" border stripe style="width: 100%">
          <el-table-column prop="studentName" label="学生姓名" width="150" />
          <el-table-column prop="studentNo" label="学号" width="150" />
          <el-table-column label="平时成绩" width="180" align="center">
            <template #default="{ row, $index }">
              <el-input-number
                v-model="row.regularScore"
                :min="0"
                :max="100"
                :precision="1"
                size="small"
                style="width: 140px"
              />
            </template>
          </el-table-column>
          <el-table-column label="考试成绩" width="180" align="center">
            <template #default="{ row, $index }">
              <el-input-number
                v-model="row.examScore"
                :min="0"
                :max="100"
                :precision="1"
                size="small"
                style="width: 140px"
              />
            </template>
          </el-table-column>
        </el-table>

        <div style="margin-top: 20px; text-align: right">
          <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
            提交所有成绩
          </el-button>
        </div>
      </div>

      <div v-else-if="selectedOfferingId && students.length === 0" style="margin-top: 20px; color: #999">
        该开课暂无选课学生
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const offerings = ref([])
const selectedOfferingId = ref(null)
const students = ref([])
const submitting = ref(false)

const fetchOfferings = async () => {
  try {
    const res = await request.get('/course-offerings')
    if (res.code === 200) {
      const data = res.data.records || res.data.list || res.data || []
      offerings.value = Array.isArray(data) ? data : []
    } else {
      ElMessage.error(res.message || '获取开课列表失败')
    }
  } catch (err) {
    ElMessage.error('获取开课列表失败')
  }
}

const onOfferingChange = async (offeringId) => {
  students.value = []
  if (!offeringId) return
  try {
    const res = await request.get(`/enrollments/offering/${offeringId}`)
    if (res.code === 200) {
      const data = res.data.records || res.data.list || res.data || []
      students.value = (Array.isArray(data) ? data : []).map((item) => ({
        studentId: item.studentId || item.student?.id,
        studentName: item.studentName || item.student?.name || '',
        studentNo: item.studentNo || item.student?.studentNo || '',
        regularScore: null,
        examScore: null
      }))
    } else {
      ElMessage.error(res.message || '获取学生列表失败')
    }
  } catch (err) {
    ElMessage.error('获取学生列表失败')
  }
}

const handleSubmit = async () => {
  // Validate
  const hasInvalid = students.value.some((s) => {
    const r = s.regularScore
    const e = s.examScore
    return r == null || e == null || r < 0 || r > 100 || e < 0 || e > 100
  })
  if (hasInvalid) {
    ElMessage.warning('请确保所有学生的成绩都填写完整且在 0-100 之间')
    return
  }

  submitting.value = true
  try {
    const scores = students.value.map((s) => ({
      studentId: s.studentId,
      regularScore: s.regularScore,
      examScore: s.examScore
    }))
    const res = await request.post('/scores', {
      offeringId: selectedOfferingId.value,
      scores
    })
    if (res.code === 200) {
      ElMessage.success('成绩提交成功')
      students.value = []
      selectedOfferingId.value = null
    } else {
      ElMessage.error(res.message || '成绩提交失败')
    }
  } catch (err) {
    ElMessage.error('成绩提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchOfferings()
})
</script>

<style scoped>
.score-input-container { padding: 20px; }
</style>
