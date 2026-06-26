<template>
  <div class="attendance-record-container">
    <el-card>
      <template #header>
        <span>考勤记录</span>
      </template>

      <!-- Selectors -->
      <el-form :inline="true">
        <el-form-item label="选择开课">
          <el-select
            v-model="selectedOfferingId"
            placeholder="请选择开课"
            filterable
            clearable
            style="width: 320px"
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
        <el-form-item label="日期">
          <el-date-picker
            v-model="attendDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="周次">
          <el-input-number v-model="weekNumber" :min="1" :max="20" placeholder="周次" />
        </el-form-item>
      </el-form>

      <!-- Attendance table -->
      <div v-if="selectedOfferingId && students.length > 0" style="margin-top: 20px">
        <el-table :data="students" border stripe style="width: 100%">
          <el-table-column prop="studentName" label="学生姓名" width="120" />
          <el-table-column prop="studentNo" label="学号" width="140" />
          <el-table-column label="考勤状态" width="180" align="center">
            <template #default="{ row, $index }">
              <el-select v-model="row.attendanceStatus" placeholder="请选择" size="small" style="width: 140px">
                <el-option label="出勤" value="出勤" />
                <el-option label="迟到" value="迟到" />
                <el-option label="早退" value="早退" />
                <el-option label="旷课" value="旷课" />
                <el-option label="请假" value="请假" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="备注" min-width="200">
            <template #default="{ row, $index }">
              <el-input v-model="row.remark" placeholder="备注（可选）" size="small" />
            </template>
          </el-table-column>
        </el-table>

        <div style="margin-top: 20px; text-align: right">
          <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
            提交考勤
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
const attendDate = ref('')
const weekNumber = ref(1)
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
        attendanceStatus: '出勤',
        remark: ''
      }))
    } else {
      ElMessage.error(res.message || '获取学生列表失败')
    }
  } catch (err) {
    ElMessage.error('获取学生列表失败')
  }
}

const handleSubmit = async () => {
  if (!attendDate.value) {
    ElMessage.warning('请选择日期')
    return
  }
  if (!weekNumber.value) {
    ElMessage.warning('请填写周次')
    return
  }

  submitting.value = true
  try {
    const records = students.value.map((s) => ({
      studentId: s.studentId,
      offeringId: selectedOfferingId.value,
      attendDate: attendDate.value,
      weekNumber: weekNumber.value,
      status: s.attendanceStatus,
      remark: s.remark || ''
    }))
    const res = await request.post('/attendance', records)
    if (res.code === 200) {
      ElMessage.success('考勤提交成功')
      students.value = []
      selectedOfferingId.value = null
      attendDate.value = ''
      weekNumber.value = 1
    } else {
      ElMessage.error(res.message || '考勤提交失败')
    }
  } catch (err) {
    ElMessage.error('考勤提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchOfferings()
})
</script>

<style scoped>
.attendance-record-container { padding: 20px; }
</style>
