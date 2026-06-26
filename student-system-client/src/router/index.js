import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '../utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', noAuth: true }
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/system/Users.vue'),
        meta: { title: '用户管理', roles: ['ADMIN'] }
      },
      {
        path: 'students',
        name: 'Students',
        component: () => import('../views/student/Students.vue'),
        meta: { title: '学生管理', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'courses',
        name: 'Courses',
        component: () => import('../views/course/Courses.vue'),
        meta: { title: '课程管理', roles: ['ADMIN'] }
      },
      {
        path: 'course-offerings',
        name: 'CourseOfferings',
        component: () => import('../views/course/CourseOfferings.vue'),
        meta: { title: '开课管理' }
      },
      {
        path: 'enrollments',
        name: 'Enrollments',
        component: () => import('../views/course/Enrollments.vue'),
        meta: { title: '我的选课', roles: ['STUDENT'] }
      },
      {
        path: 'scores',
        name: 'ScoreInput',
        component: () => import('../views/score/ScoreInput.vue'),
        meta: { title: '成绩录入', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'my-scores',
        name: 'MyScores',
        component: () => import('../views/score/MyScores.vue'),
        meta: { title: '我的成绩', roles: ['STUDENT'] }
      },
      {
        path: 'attendance',
        name: 'AttendanceRecord',
        component: () => import('../views/attendance/AttendanceRecord.vue'),
        meta: { title: '考勤记录', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'my-attendance',
        name: 'MyAttendance',
        component: () => import('../views/attendance/MyAttendance.vue'),
        meta: { title: '我的考勤', roles: ['STUDENT'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 学生管理系统` : '学生管理系统'

  if (to.meta.noAuth) {
    next()
    return
  }

  if (!getToken()) {
    next('/login')
    return
  }

  next()
})

export default router
