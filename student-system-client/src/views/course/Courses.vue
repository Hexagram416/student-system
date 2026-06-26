<template>
  <div class="courses-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
          <el-button type="primary" @click="handleAdd">新增课程</el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索课程名称或课程编号"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%; margin-top: 20px">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="courseNo" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="180" show-overflow-tooltip />
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <el-table-column prop="totalHours" label="总学时" width="90" align="center" />
        <el-table-column prop="courseType" label="课程类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="courseTypeTag(row.courseType)" size="small">{{ row.courseType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deptId" label="院系ID" width="100" align="center" />
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="课程编号" prop="courseNo">
          <el-input v-model="form.courseNo" placeholder="请输入课程编号" />
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="form.credit" :min="0" :max="20" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="总学时" prop="totalHours">
          <el-input-number v-model="form.totalHours" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="课程类型" prop="courseType">
          <el-select v-model="form.courseType" placeholder="请选择课程类型" style="width: 100%">
            <el-option label="必修" value="必修" />
            <el-option label="选修" value="选修" />
            <el-option label="公选" value="公选" />
          </el-select>
        </el-form-item>
        <el-form-item label="院系ID" prop="deptId">
          <el-input-number v-model="form.deptId" :min="1" style="width: 100%" placeholder="请输入院系ID" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入课程描述" />
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
const keyword = ref('')

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/courses', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value }
    })
    if (res.code === 200) {
      tableData.value = res.data.records || res.data.list || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取课程列表失败')
    }
  } catch (err) {
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchData()
}

const handleReset = () => {
  keyword.value = ''
  pageNum.value = 1
  fetchData()
}

const courseTypeTag = (type) => {
  const map = { '必修': 'danger', '选修': 'warning', '公选': 'info' }
  return map[type] || ''
}

// ---------- dialog ----------
const dialogVisible = ref(false)
const dialogTitle = computed(() => (form.id ? '编辑课程' : '新增课程'))
const formRef = ref(null)
const submitting = ref(false)

const initialForm = () => ({
  id: null,
  courseNo: '',
  courseName: '',
  credit: 0,
  totalHours: 0,
  courseType: '必修',
  deptId: null,
  description: ''
})

const form = reactive(initialForm())

const rules = {
  courseNo: [{ required: true, message: '请输入课程编号', trigger: 'blur' }],
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'blur' }],
  totalHours: [{ required: true, message: '请输入总学时', trigger: 'blur' }],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
  deptId: [{ required: true, message: '请输入院系ID', trigger: 'blur' }]
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
    courseNo: row.courseNo,
    courseName: row.courseName,
    credit: row.credit,
    totalHours: row.totalHours,
    courseType: row.courseType,
    deptId: row.deptId,
    description: row.description || ''
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
        ? await request.put(`/courses/${form.id}`, payload)
        : await request.post('/courses', payload)
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
  ElMessageBox.confirm(`确认删除课程「${row.courseName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const res = await request.delete(`/courses/${row.id}`)
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
.courses-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-bar { display: flex; gap: 10px; align-items: center; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
