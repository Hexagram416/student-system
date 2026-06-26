<template>
  <div class="enrollments-container">
    <el-row :gutter="20">
      <!-- Available Course Offerings -->
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>可选课程</span>
              <el-input
                v-model="availableKeyword"
                placeholder="搜索课程或教师"
                clearable
                style="width: 220px"
                @keyup.enter="fetchAvailableOfferings"
              />
            </div>
          </template>
          <el-table :data="availableOfferings" border stripe v-loading="availableLoading" style="width: 100%">
            <el-table-column prop="courseName" label="课程名称" width="140" show-overflow-tooltip />
            <el-table-column prop="teacherName" label="教师" width="100" />
            <el-table-column prop="semester" label="学期" width="120" />
            <el-table-column prop="schedule" label="上课时间" width="130" show-overflow-tooltip />
            <el-table-column label="容量" width="110" align="center">
              <template #default="{ row }">
                {{ row.currentCount || 0 }} / {{ row.maxCapacity }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center" fixed="right">
              <template #default="{ row }">
                <el-button
                  type="primary"
                  size="small"
                  :disabled="(row.currentCount || 0) >= row.maxCapacity"
                  @click="handleEnroll(row)"
                >
                  选课
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- My Enrollments -->
      <el-col :span="10">
        <el-card>
          <template #header>
            <span>我的选课</span>
          </template>
          <el-table :data="myEnrollments" border stripe v-loading="myLoading" style="width: 100%">
            <el-table-column prop="courseName" label="课程名称" width="120" show-overflow-tooltip />
            <el-table-column prop="teacherName" label="教师" width="80" />
            <el-table-column prop="semester" label="学期" width="110" />
            <el-table-column prop="enrollDate" label="选课日期" width="110" />
            <el-table-column label="操作" width="80" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="handleDrop(row)">退课</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

// NOTE: In a real app, studentId comes from user context (store/auth). Hardcoded to 1 for now.
const studentId = ref(1)

const availableOfferings = ref([])
const availableLoading = ref(false)
const availableKeyword = ref('')

const myEnrollments = ref([])
const myLoading = ref(false)

const fetchAvailableOfferings = async () => {
  availableLoading.value = true
  try {
    const res = await request.get('/course-offerings', {
      params: { keyword: availableKeyword.value, status: 'OPEN' }
    })
    if (res.code === 200) {
      const data = res.data.records || res.data.list || res.data || []
      availableOfferings.value = Array.isArray(data) ? data : []
    } else {
      ElMessage.error(res.message || '获取可选课程失败')
    }
  } catch (err) {
    ElMessage.error('获取可选课程失败')
  } finally {
    availableLoading.value = false
  }
}

const fetchMyEnrollments = async () => {
  myLoading.value = true
  try {
    const res = await request.get('/enrollments/my', {
      params: { studentId: studentId.value }
    })
    if (res.code === 200) {
      const data = res.data.records || res.data.list || res.data || []
      myEnrollments.value = Array.isArray(data) ? data : []
    } else {
      ElMessage.error(res.message || '获取我的选课失败')
    }
  } catch (err) {
    ElMessage.error('获取我的选课失败')
  } finally {
    myLoading.value = false
  }
}

const handleEnroll = async (row) => {
  try {
    const res = await request.post('/enrollments', {
      studentId: studentId.value,
      offeringId: row.id
    })
    if (res.code === 200) {
      ElMessage.success('选课成功')
      fetchAvailableOfferings()
      fetchMyEnrollments()
    } else {
      ElMessage.error(res.message || '选课失败')
    }
  } catch (err) {
    ElMessage.error('选课失败')
  }
}

const handleDrop = (row) => {
  ElMessageBox.confirm(`确认退选该课程吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const res = await request.delete(`/enrollments/${row.id}`)
        if (res.code === 200) {
          ElMessage.success('退课成功')
          fetchAvailableOfferings()
          fetchMyEnrollments()
        } else {
          ElMessage.error(res.message || '退课失败')
        }
      } catch (err) {
        ElMessage.error('退课失败')
      }
    })
    .catch(() => {})
}

onMounted(() => {
  fetchAvailableOfferings()
  fetchMyEnrollments()
})
</script>

<style scoped>
.enrollments-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
