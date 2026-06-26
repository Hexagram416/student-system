<template>
  <div class="dashboard">
    <h3 style="margin-bottom:20px">欢迎使用学生管理系统</h3>
    <el-row :gutter="20">
      <el-col :span="8" v-for="item in menuItems" :key="item.path" style="margin-bottom:20px">
        <el-card shadow="hover" class="dash-card" @click="$router.push(item.path)">
          <div class="card-body">
            <div class="card-icon">{{ item.icon }}</div>
            <div class="card-title">{{ item.title }}</div>
            <div class="card-desc">{{ item.desc }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getRole } from '../../utils/auth'

const role = getRole()

const allItems = [
  { path: '/students', icon: '🎓', title: '学生管理', desc: '查看和管理学生信息', roles: ['ADMIN', 'TEACHER'] },
  { path: '/courses', icon: '📚', title: '课程管理', desc: '维护课程基础信息', roles: ['ADMIN'] },
  { path: '/course-offerings', icon: '📋', title: '开课管理', desc: '管理每学期开课安排', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { path: '/enrollments', icon: '📝', title: '我的选课', desc: '选课和退课操作', roles: ['STUDENT'] },
  { path: '/scores', icon: '📈', title: '成绩录入', desc: '录入学生成绩', roles: ['ADMIN', 'TEACHER'] },
  { path: '/my-scores', icon: '📄', title: '我的成绩', desc: '查看个人成绩单', roles: ['STUDENT'] },
  { path: '/attendance', icon: '📅', title: '考勤记录', desc: '记录学生出勤情况', roles: ['ADMIN', 'TEACHER'] },
  { path: '/my-attendance', icon: '🕐', title: '我的考勤', desc: '查看个人考勤记录', roles: ['STUDENT'] },
  { path: '/users', icon: '👤', title: '用户管理', desc: '管理系统用户账号', roles: ['ADMIN'] },
]

const menuItems = computed(() => allItems.filter(item => item.roles.includes(role)))
</script>

<style scoped>
.dashboard { padding: 20px; }
.dash-card { cursor: pointer; transition: transform .2s; }
.dash-card:hover { transform: translateY(-4px); }
.card-body { text-align: center; padding: 10px 0; }
.card-icon { font-size: 48px; margin-bottom: 12px; }
.card-title { font-size: 16px; font-weight: bold; color: #303133; margin-bottom: 6px; }
.card-desc { font-size: 13px; color: #909399; }
</style>
