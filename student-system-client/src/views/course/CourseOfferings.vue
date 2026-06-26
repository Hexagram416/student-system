<template>
  <div class="offerings-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>开课管理</span>
          <el-button type="primary" @click="handleAdd">新增开课</el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input
          v-model="semester"
          placeholder="搜索学期（如 2024-2025-1）"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%; margin-top: 20px">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="courseName" label="课程名称" width="160" show-overflow-tooltip />
        <el-table-column prop="teacherName" label="教师" width="100" />
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column prop="maxCapacity" label="最大容量" width="100" align="center" />
        <el-table-column prop="currentCount" label="已选人数" width="100" align="center" />
        <el-table-column prop="schedule" label="上课时间" width="150" show-overflow-tooltip />
        <el-table-column prop="classroom" label="教室" width="120" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="offeringStatusTag(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="550px"
      :close-on-click-modal="false"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程ID" prop="courseId">
          <el-input-number v-model="form.courseId" :min="1" style="width: 100%" placeholder="请输入课程ID" />
        </el-form-item>
        <el-form-item label="教师ID" prop="teacherId">
          <el-input-number v-model="form.teacherId" :min="1" style="width: 100%" placeholder="请输入教师ID" />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-input v-model="form.semester" placeholder="请输入学期，如 2024-2025-1" />
        </el-form-item>
        <el-form-item label="最大容量" prop="maxCapacity">
          <el-input-number v-model="form.maxCapacity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="上课时间" prop="schedule">
          <el-input v-model="form.schedule" placeholder="请输入上课时间" />
        </el-form-item>
        <el-form-item label="教室" prop="classroom">
          <el-input v-model="form.classroom" placeholder="请输入教室" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="开放选课" value="OPEN" />
            <el-option label="已关闭" value="CLOSED" />
            <el-option label="已结课" value="ENDED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

const tableData = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const semester = ref('')

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/course-offerings', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value, semester: semester.value }
    })
    if (res.code === 200) {
      tableData.value = res.data.records || res.data.list || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取开课列表失败')
    }
  } catch (err) {
    ElMessage.error('获取开课列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchData()
}

const handleReset = () => {
  semester.value = ''
  pageNum.value = 1
  fetchData()
}

const offeringStatusTag = (status) => {
  const map = { OPEN: 'success', CLOSED: 'danger', ENDED: 'info' }
  return map[status] || ''
}

// ---------- dialog ----------
const dialogVisible = ref(false)
const dialogTitle = computed(() => (form.id ? '编辑开课' : '新增开课'))
const formRef = ref(null)
const submitting = ref(false)

const initialForm = () => ({
  id: null,
  courseId: null,
  teacherId: null,
  semester: '',
  maxCapacity: 60,
  schedule: '',
  classroom: '',
  status: 'OPEN'
})

const form = reactive(initialForm())

const rules = {
  courseId: [{ required: true, message: '请输入课程ID', trigger: 'blur' }],
  teacherId: [{ required: true, message: '请输入教师ID', trigger: 'blur' }],
  semester: [{ required: true, message: '请输入学期', trigger: 'blur' }],
  maxCapacity: [{ required: true, message: '请输入最大容量', trigger: 'blur' }],
  schedule: [{ required: true, message: '请输入上课时间', trigger: 'blur' }],
  classroom: [{ required: true, message: '请输入教室', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const resetForm = () => {
  if (formRef.value) formRef.value.resetFields()
  Object.assign(form, initialForm())
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, {
    id: row.id,
    courseId: row.courseId,
    teacherId: row.teacherId,
    semester: row.semester,
    maxCapacity: row.maxCapacity,
    schedule: row.schedule || '',
    classroom: row.classroom || '',
    status: row.status
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const payload = { ...form }
      const res = form.id
        ? await request.put(`/course-offerings/${form.id}`, payload)
        : await request.post('/course-offerings', payload)
      if (res.code === 200) {
        ElMessage.success(form.id ? '编辑成功' : '新增成功')
        dialogVisible.value = false
        fetchData()
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    } catch (err) {
      ElMessage.error('操作失败')
    } finally {
      submitting.value = false
    }
  })
}

// ---------- delete ----------
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除该开课记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const res = await request.delete(`/course-offerings/${row.id}`)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          fetchData()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (err) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.offerings-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-bar { display: flex; gap: 10px; align-items: center; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
