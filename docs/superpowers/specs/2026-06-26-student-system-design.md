# Student Management System - Design Spec

**Date:** 2026-06-26
**Status:** Approved

## Overview

Build a simple but complete Student Management System based on the existing design document (`学生管理系统设计文档.md`). The system covers 5 modules with core functionality, using Spring Boot 3 + Vue 3 + MySQL 8.0 + MyBatis-Plus.

## Scope

### Modules (5 total, simplified core features each)

| Module | Core Features |
|--------|-------------|
| System Management | User CRUD, role-based access control (ADMIN/TEACHER/STUDENT) |
| Student Info | Student profile CRUD, multi-condition search |
| Course Management | Course CRUD, course offering management, enrollment (select/drop) |
| Score Management | Score input (manual), score query (by student/teacher), basic stats |
| Attendance | Attendance record, query by student/course, basic stats |

### Technical Decisions

| Decision | Choice |
|----------|--------|
| Database | MySQL 8.0 (external, must be installed) |
| Auth | JWT + Spring Security + BCrypt |
| Roles | ADMIN, TEACHER, STUDENT (RBAC enforced at API level) |
| Simplifications | No optimistic locking, no Redis, no Excel import, no course conflict detection, no GPA calculation |

## Architecture

### Backend: Spring Boot 3

```
student-system-server/
├── pom.xml
├── src/main/java/com/studentsystem/
│   ├── StudentSystemApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── CorsConfig.java
│   ├── security/
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── UserDetailsServiceImpl.java
│   ├── common/
│   │   ├── Result.java
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
│   ├── controller/   (8 controllers)
│   ├── service/      (interfaces + impl/)
│   ├── mapper/       (MyBatis-Plus Mapper interfaces)
│   ├── entity/       (JPA/MP entities)
│   └── dto/          (request/response DTOs)
└── src/main/resources/
    ├── application.yml
    └── db/
        ├── schema.sql
        └── init_data.sql
```

### Frontend: Vue 3 + Element Plus

```
student-system-client/
├── package.json
├── vite.config.js
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── router/index.js
│   ├── utils/request.js, auth.js
│   ├── views/ (Login, Layout, dashboard, system, student, course, score, attendance)
│   └── components/
```

### Database: 10 tables

sys_user, department, major, class, student, teacher, course, course_offering, enrollment, score, attendance, status_change

### API Design (RESTful)

All endpoints under `/api/`:

- `POST /api/auth/login` — Login
- `GET /api/auth/me` — Current user info
- `GET/POST/PUT/DELETE /api/users` — User CRUD (ADMIN only)
- `GET/POST/PUT/DELETE /api/students` — Student CRUD
- `GET/POST/PUT/DELETE /api/courses` — Course CRUD
- `GET/POST /api/course-offerings` — Course offering list/create
- `POST /api/enrollments` — Enroll in a course
- `DELETE /api/enrollments/{id}` — Drop a course
- `GET /api/enrollments/my` — My enrollments (STUDENT)
- `GET/POST /api/scores` — Score query/input
- `GET /api/scores/stats/{offeringId}` — Score statistics
- `GET/POST /api/attendance` — Attendance query/record
- `GET/POST/PUT/DELETE /api/departments` — Department CRUD
- `GET/POST/PUT/DELETE /api/classes` — Class CRUD

### Role Permissions

| Role | Access |
|------|--------|
| ADMIN | All endpoints |
| TEACHER | Score input/query for own courses, attendance record/query, profile |
| STUDENT | Enrollment, own scores, own attendance, profile |

## Implementation Notes

- Single Maven module, no microservices
- No file upload (no Excel import)
- Front-end uses Element Plus for UI components
- API responses use unified `Result<T>` wrapper: `{code, message, data}`
- Passwords encrypted with BCrypt
- JWT token expires in 24h
- CORS enabled for localhost:5173 (Vite dev server)

## What's NOT Included (from full design doc)

- Optimistic locking for course enrollment
- Redis caching
- Schedule conflict detection
- Prerequisite course checking
- Excel batch import/export
- GPA calculation (simplified score only)
- Status change (学籍异动) management
- System operation logs
- Data visualization charts (ECharts)
- WebSocket notifications
- Microservices
