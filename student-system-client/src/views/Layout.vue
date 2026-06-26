<template>
  <el-container style="height:100%">
    <el-aside width="220px" style="background:#304156">
      <div class="logo">学生管理系统</div>
      <el-menu :default-active="activeMenu" background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF"
        router>
        <el-menu-item index="/dashboard">
          <span>📊 首页</span>
        </el-menu-item>

        <template v-if="hasRole('ADMIN')">
          <el-menu-item index="/users">
            <span>👤 用户管理</span>
          </el-menu-item>
        </template>

        <template v-if="hasRole('ADMIN','TEACHER')">
          <el-menu-item index="/students">
            <span>🎓 学生管理</span>
          </el-menu-item>
        </template>

        <template v-if="hasRole('ADMIN')">
          <el-menu-item index="/courses">
            <span>📚 课程管理</span>
          </el-menu-item>
        </template>

        <el-menu-item index="/course-offerings">
          <span>📋 开课管理</span>
        </el-menu-item>

        <template v-if="hasRole('STUDENT')">
          <el-menu-item index="/enrollments">
            <span>📝 我的选课</span>
          </el-menu-item>
        </template>

        <template v-if="hasRole('ADMIN','TEACHER')">
          <el-menu-item index="/scores">
            <span>📈 成绩录入</span>
          </el-menu-item>
        </template>

        <template v-if="hasRole('STUDENT')">
          <el-menu-item index="/my-scores">
            <span>📄 我的成绩</span>
          </el-menu-item>
        </template>

        <template v-if="hasRole('ADMIN','TEACHER')">
          <el-menu-item index="/attendance">
            <span>📅 考勤记录</span>
          </el-menu-item>
        </template>

        <template v-if="hasRole('STUDENT')">
          <el-menu-item index="/my-attendance">
            <span>🕐 我的考勤</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="border-bottom:1px solid #e6e6e6; display:flex; align-items:center; justify-content:space-between">
        <div style="font-size:16px; font-weight:bold">{{ $route.meta.title }}</div>
        <div>
          <span style="margin-right:12px; color:#666">{{ user?.realName }}（{{ roleLabel }}）</span>
          <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUser, getRole, logout } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const user = computed(() => getUser())

const activeMenu = computed(() => route.path)

const roleLabels = { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }
const roleLabel = computed(() => roleLabels[getRole()] || '')

function hasRole(...roles) {
  return roles.includes(getRole())
}

function handleLogout() {
  logout()
}
</script>

<style scoped>
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background: #263445;
}
.el-aside {
  overflow-x: hidden;
}
.el-main {
  background: #f0f2f5;
}
</style>
