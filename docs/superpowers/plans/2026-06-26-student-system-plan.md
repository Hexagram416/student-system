# Student Management System - Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a complete student management system with Spring Boot backend + Vue 3 frontend + MySQL.

**Architecture:** Monorepo — `student-system-server/` (Spring Boot 3 + MyBatis-Plus) and `student-system-client/` (Vue 3 + Element Plus). JWT auth, RBAC (ADMIN/TEACHER/STUDENT).

**Tech Stack:** Spring Boot 3.1, Vue 3, MySQL 8.0, MyBatis-Plus 3.5, Element Plus, JJWT 0.11, Lombok

## Global Constraints

- Java 17, Spring Boot 3.x, Vue 3.x, MySQL 8.0, MyBatis-Plus 3.5+
- JWT with 24h expiration, BCrypt password encoding
- Element Plus for UI, CORS enabled for localhost:5173
- Single Maven module, no microservices
- 5 modules: system, student, course, score, attendance
- 10 database tables per design doc

---

## Phase 1: Backend Foundation

### Task 1: Project scaffolding (pom.xml + Application + config + schema + seed)

**Files:** Create `pom.xml`, `StudentSystemApplication.java`, `application.yml`, `schema.sql`, `init_data.sql`

- [ ] Step 1: Create Maven POM with Spring Boot, MyBatis-Plus, MySQL, JJWT, Lombok deps
- [ ] Step 2: Create `@SpringBootApplication` entry class
- [ ] Step 3: Create `application.yml` (port 8080, datasource, JWT config)
- [ ] Step 4: Create `schema.sql` (10 tables: sys_user, department, major, class, teacher, student, course, course_offering, enrollment, score, attendance, status_change)
- [ ] Step 5: Create `init_data.sql` with sample data
- [ ] Step 6: Commit

### Task 2: Common classes (Result, BusinessException, GlobalExceptionHandler)

**Files:** Create `common/Result.java`, `common/BusinessException.java`, `common/GlobalExceptionHandler.java`

- [ ] Step 1: Write `Result<T>` unified response `{code, message, data}`
- [ ] Step 2: Write `BusinessException extends RuntimeException`
- [ ] Step 3: Write `GlobalExceptionHandler` with `@RestControllerAdvice`
- [ ] Step 4: Verify compilation
- [ ] Step 5: Commit

### Task 3: Security (JWT + Spring Security)

**Files:** Create `security/JwtTokenProvider.java`, `security/JwtAuthenticationFilter.java`, `security/UserDetailsServiceImpl.java`, `config/SecurityConfig.java`, `config/CorsConfig.java`

- [ ] Step 1: Write `JwtTokenProvider` (generate/validate JWT, extract username/role)
- [ ] Step 2: Write `JwtAuthenticationFilter extends OncePerRequestFilter`
- [ ] Step 3: Write `UserDetailsServiceImpl` (load from sys_user table)
- [ ] Step 4: Write `SecurityConfig` (disable CSRF, stateless session, permit `/api/auth/**`)
- [ ] Step 5: Write `CorsConfig` (allow localhost:5173)
- [ ] Step 6: Commit

### Task 4: Entity classes (12 entities)

**Files:** Create all entities under `entity/` package

- [ ] `SysUser.java` — @TableName("sys_user"), fields: id, username, password, realName, role, phone, email, status, lastLoginTime, createdAt, updatedAt
- [ ] `Department.java` — id, deptNo, deptName, deptHead
- [ ] `Major.java` — id, majorNo, majorName, deptId
- [ ] `Class.java` — id, classNo, className, majorId, grade (note: "class" is reserved, use @TableName("class"))
- [ ] `Teacher.java` — id, teacherNo, name, gender, title, phone, email, deptId, userId
- [ ] `Student.java` — id, studentNo, name, gender, birthDate, idCard, phone, address, enrollmentYear, politicalStatus, classId, userId
- [ ] `Course.java` — id, courseNo, courseName, credit, totalHours, courseType, deptId, description
- [ ] `CourseOffering.java` — id, courseId, teacherId, semester, maxCapacity, currentCount, schedule, classroom, status
- [ ] `Enrollment.java` — id, studentId, offeringId, enrollDate, status
- [ ] `Score.java` — id, enrollmentId, regularScore, examScore, totalScore, gradePoint, status, submitTime, updateTime
- [ ] `Attendance.java` — id, studentId, offeringId, attendDate, weekNumber, status, remark
- [ ] `StatusChange.java` — id, studentId, changeType, reason, changeDate, status

All use Lombok `@Data`, `@TableName`, MyBatis-Plus `@TableId(type = IdType.AUTO)`.

- [ ] Step 1: Create all entity files
- [ ] Step 2: Verify compilation
- [ ] Step 3: Commit

### Task 5: Mapper interfaces (12 mappers)

**Files:** Create all mappers under `mapper/` package

Each extends `BaseMapper<Entity>`. Create: `UserMapper`, `StudentMapper`, `TeacherMapper`, `DepartmentMapper`, `MajorMapper`, `ClassMapper`, `CourseMapper`, `CourseOfferingMapper`, `EnrollmentMapper`, `ScoreMapper`, `AttendanceMapper`, `StatusChangeMapper`.

Add `@Mapper` annotation on each. Add custom query methods where needed:
- `EnrollmentMapper`: `selectByStudentIdAndSemester(Long studentId, String semester)`
- `ScoreMapper`: `selectByOfferingId(Long offeringId)`

- [ ] Step 1: Create all mapper interfaces
- [ ] Step 2: Verify compilation
- [ ] Step 3: Commit

### Task 6: Service layer

**Files:** Create service interfaces and implementations under `service/` and `service/impl/`

Services to create:
- `AuthService` / `AuthServiceImpl` — login(token generation), getCurrentUser
- `UserService` / `UserServiceImpl` — CRUD + pagination + search for sys_user
- `StudentService` / `StudentServiceImpl` — CRUD + pagination + search for student
- `CourseService` / `CourseServiceImpl` — CRUD for course
- `CourseOfferingService` / `CourseOfferingServiceImpl` — CRUD + list available
- `EnrollmentService` / `EnrollmentServiceImpl` — enroll (validate not duplicate, check capacity), drop, list by student
- `ScoreService` / `ScoreServiceImpl` — input scores (validate 0-100, calc total = regular*0.4 + exam*0.6), query by student, stats by offering
- `AttendanceService` / `AttendanceServiceImpl` — record, query by student/offering
- `DepartmentService` / `DepartmentServiceImpl` — CRUD
- `ClassService` / `ClassServiceImpl` — CRUD

- [ ] Step 1: Create all service interfaces
- [ ] Step 2: Create all service implementations
- [ ] Step 3: Verify compilation
- [ ] Step 4: Commit

### Task 7: DTOs

**Files:** Create under `dto/` package

- `LoginRequest.java` — username, password
- `LoginResponse.java` — token, username, realName, role
- `UserDTO.java` — for create/update requests
- `StudentDTO.java` — for create/update requests
- `CourseOfferingDTO.java` — with courseName, teacherName
- `EnrollmentDTO.java` — studentId, offeringId, studentName, courseName
- `ScoreInputDTO.java` — offeringId, List<ScoreItem> (studentId, regularScore, examScore)
- `ScoreStatsDTO.java` — totalCount, averageScore, passRate, excellentRate
- `AttendanceDTO.java` — studentId, offeringId, attendDate, weekNumber, status, remark

- [ ] Step 1: Create all DTO classes
- [ ] Step 2: Verify compilation
- [ ] Step 3: Commit

### Task 8: Controllers

**Files:** Create under `controller/` package

- `AuthController.java` — POST /api/auth/login, GET /api/auth/me
- `UserController.java` — GET/POST/PUT/DELETE /api/users (ADMIN only)
- `StudentController.java` — GET/POST/PUT/DELETE /api/students
- `DepartmentController.java` — GET/POST/PUT/DELETE /api/departments
- `ClassController.java` — GET/POST/PUT/DELETE /api/classes
- `CourseController.java` — GET/POST/PUT/DELETE /api/courses
- `CourseOfferingController.java` — GET/POST /api/course-offerings
- `EnrollmentController.java` — POST /api/enrollments (enroll), DELETE /api/enrollments/{id} (drop), GET /api/enrollments/my
- `ScoreController.java` — POST /api/scores (input), GET /api/scores/student/{id}, GET /api/scores/stats/{offeringId}
- `AttendanceController.java` — POST /api/attendance, GET /api/attendance/student/{id}, GET /api/attendance/offering/{id}

Use `@PreAuthorize` for role checks:
- `hasRole('ADMIN')` for user CRUD
- `hasAnyRole('ADMIN', 'TEACHER')` for score input
- `hasAnyRole('ADMIN', 'STUDENT')` for enrollment

- [ ] Step 1: Create all controller classes
- [ ] Step 2: Verify compilation, test with `mvn spring-boot:run`
- [ ] Step 3: Commit

---

## Phase 2: Frontend

### Task 9: Vue 3 project scaffolding

**Files:** Create `student-system-client/` with Vite + Vue 3 + Element Plus

- [ ] Step 1: `npm create vite@latest student-system-client -- --template vue`
- [ ] Step 2: `npm install element-plus vue-router@4 axios`
- [ ] Step 3: Configure `vite.config.js` with proxy `/api` → `http://localhost:8080`
- [ ] Step 4: Configure `src/main.js` with Element Plus, Router
- [ ] Step 5: Verify `npm run dev` starts on port 5173
- [ ] Step 6: Commit

### Task 10: Auth utils and HTTP client

**Files:** Create `src/utils/auth.js`, `src/utils/request.js`

- [ ] `auth.js`: `getToken()`, `setToken(token)`, `removeToken()`, `getUser()`, `setUser(user)`
- [ ] `request.js`: Axios instance with baseURL `/api`, request interceptor (attach JWT), response interceptor (handle 401 redirect to login)

### Task 11: Router with guards

**Files:** Create `src/router/index.js`

Routes:
- `/login` — Login page (public)
- `/` — Layout with sidebar (protected)
  - `/dashboard` — Dashboard home
  - `/students` — Student list (ADMIN, TEACHER)
  - `/courses` — Course management (ADMIN)
  - `/course-offerings` — Course offerings (all)
  - `/enrollments` — My enrollments (STUDENT)
  - `/scores` — Score input (TEACHER) / query (STUDENT)
  - `/attendance` — Attendance (TEACHER: record, STUDENT: view)
  - `/users` — User management (ADMIN)

Navigation guard: check token exists, redirect to `/login` if not.

### Task 12: Layout and Login page

**Files:** Create `src/App.vue`, `src/views/Login.vue`, `src/views/Layout.vue`

- `App.vue`: `<router-view />`
- `Login.vue`: Username/password form, call `/api/auth/login`, store token + user, redirect to `/`
- `Layout.vue`: Sidebar (el-menu) + top bar + `<router-view />`, sidebar items based on role

### Task 13: Core CRUD pages

Create all view pages with Element Plus tables/forms:

- `views/system/Users.vue` — User list (el-table + pagination) + create/edit dialog (el-dialog + el-form)
- `views/student/Students.vue` — Student list (search by name/no/class) + create/edit dialog
- `views/course/Courses.vue` — Course list + create/edit
- `views/course/CourseOfferings.vue` — Offering list with course name, teacher name, status
- `views/course/Enrollments.vue` — My enrollments + available courses to select

### Task 14: Score and Attendance pages

- `views/score/ScoreInput.vue` — Select offering → load enrolled students → input regular+exam scores → submit
- `views/score/MyScores.vue` — View own scores by semester
- `views/attendance/AttendanceRecord.vue` — Select offering → load students → mark attendance status → save
- `views/attendance/MyAttendance.vue` — View own attendance records

---

## Phase 3: Test & Verify

### Task 15: Build and test

- [ ] Step 1: Start MySQL, run schema.sql + init_data.sql
- [ ] Step 2: Start backend: `mvn spring-boot:run`
- [ ] Step 3: Start frontend: `npm run dev`
- [ ] Step 4: Test login (admin/admin, teacher/teacher, student/student)
- [ ] Step 5: Test each module: student CRUD, course CRUD, enrollment, score input, attendance
- [ ] Step 6: Verify role-based access (student can't access user management)
- [ ] Step 7: Commit final working state
