# FT-001 代码调研 · qa-web（前端应用）

> 调研模块：`web/qa-web/`
> 调研日期：2026-07-14
> 关联特性：[FT-001-patient-identity-verification-PRD.md](./FT-001-patient-identity-verification-PRD.md)

---

## 1. 代码库概述

- **技术栈**：Vue 3.5 + TypeScript 5.5 + Vite 5.4 + Ant Design Vue 4 + axios + dayjs + vue-router + Pinia 风格 `reactive` store。
- **HTTP 层**：`api/request.ts` 以 axios 创建实例，`baseURL: '/api'`，响应拦截器直接返回 `response.data`。
- **现有 API 模块**：`api/modules/doctor.ts`（封装 `/doctors` 系列）；`api/types.ts` 定义 `Doctor` 与 `ApiResponse<T>`。**无 patient / question API 模块**。
- **全局状态**：`store/index.ts` 以 `reactive` 持有 `doctors/patients/questions` 三大本地 mock 数组，全部来自 `src/data/*.json`；`currentPatient` 为当前登录患者。
- **页面**：`views/Consultation.vue` 实现「身份验证表单 → 患者门户 → 切换用户 → 提交问题」全流程，当前**全部走本地 store mock**。

---

## 2. 现有实现分析（对照 FT-001 需求）

FT-001 在前端需将 `Consultation.vue` / `store` 从本地 mock 改为调用真实后端 `verify` 与历史接口。

当前问题点：
- ❌ `Consultation.vue` 的 `verifyPatient()` 调用 `store.verifyPatient(name, birthday)`（本地 mock，生成 `id: 'patient'+Date.now()`，**非 `PAT-` 业务键**）。
- ❌ `myQuestions` 由 `store.getQuestionsByPatient(currentPatient.value.id)` 从**本地 questions** 过滤。
- ❌ `store` 无异步后端调用；`Patient` 接口无 `businessKey` 字段。
- ❌ `api/` 下无 patient / question 调用封装。

---

## 3. 关键发现

1. **需新增 API 模块**：`api/modules/patient.ts`（`verify(name,birthday)` → `POST /api/patients/verify`；`getHistory(businessKey)` → `GET /api/questions?patientId=`）。注意历史接口挂在 question 服务，**但前端按业务键调用即可，无需区分服务**（路由由代理层决定）。
2. **store 需改造为异步**：`verifyPatient` / `getQuestionsByPatient` 改为 `async`，调用后端并返回 `PatientDTO`；`Patient` 接口新增 `businessKey` 字段，建议 `id` 即 `businessKey`（与后端主键一致）。
3. **🚨 代理层路径不一致（关键阻塞）**：
   - **Vite dev 代理**（`vite.config.ts`）当前仅代理 `/api/doctors` → `http://localhost:8080`。新增的 `/api/patients/*` 和 `/api/questions*` **不会被代理**，需补充：
     - `'/api/patients': { target: 'http://localhost:8080', changeOrigin: true }`
     - `'/api/questions': { target: 'http://localhost:8081', changeOrigin: true }`
   - **Nginx 生产代理**（`nginx.conf`）`location /api/user/` 与 `/api/question/` 均使用 `proxy_pass http://...:8080/;`（带尾斜杠），会把 `/api/user/xxx` 重写为 `/xxx`，**剥离 `/api` 前缀**。而后端 Controller 映射为 `/api/patients`、`/api/questions`、`/api/doctors`。因此：
     - 现有 `/api/doctors` 在 Nginx 下也**无法正确路由**（会落入 SPA fallback），属既有缺陷；
     - 新增患者/问题接口在 Nginx 下同样会失败。
   - **修复建议**：Nginx 的 `proxy_pass` 去掉尾斜杠（即 `proxy_pass http://qa-service-user:8080;`），使原始 `/api/...` 路径被原样转发；并按前缀拆分：`/api/patients/`、`/api/doctors/` → user 服务，`/api/questions/` → question 服务。
4. **验证成功反馈**：`Consultation.vue` 当前用 `existingPatientCount` 判断是否「首次/欢迎回来」。改造后应依据后端返回值（命中已有档案 vs 新建）来提示，避免前端重复计算。
5. **切换用户语义**：`logoutPatient()` 仅清空 `currentPatient`，档案保留在后端，符合 PRD 场景四；改造后无需改动语义，仅清空前端状态即可。

---

## 4. 缺失项（开发阶段需补齐）

| 缺失项 | 说明 | 建议位置 |
| ------ | ---- | -------- |
| `api/modules/patient.ts` | `verify()` + `getHistory(businessKey)` | `web/qa-web/src/api/modules/patient.ts` |
| `Patient` 类型补充 `businessKey` | 对齐后端 `PatientDTO` | `store/index.ts` 或 `api/types.ts` |
| `store.verifyPatient` 异步化 | 调用后端 verify，返回带 `businessKey` 的 Patient | `store/index.ts` |
| `store.getQuestionsByPatient` 异步化 | 调用 `getHistory(businessKey)` | `store/index.ts` |
| `Consultation.vue` 改造 | `verifyPatient()` 改 `async/await`；成功提示依据后端命中标志 | `views/Consultation.vue` |
| **Vite 代理补充** | 增加 `/api/patients`、`/api/questions` 代理 | `vite.config.ts` |
| **Nginx 代理修复** | `proxy_pass` 去尾斜杠以保留 `/api` 前缀；按前缀分流 | `nginx.conf` |

---

## 5. 待确认问题

1. **历史问诊接口的归属**：前端是否统一走 `api/modules/patient.ts` 的 `getHistory`（内部访问 question 服务），还是独立建 `api/modules/question.ts`？建议前者（对患者门户更内聚）。
2. **Nginx 代理修复范围**：是否一并修复既有 `/api/doctors` 的路由缺陷（去掉 `proxy_pass` 尾斜杠）？建议在 FT-001 内一并修复，避免生产环境患者/问题接口不可用。
3. **加载态/错误态**：验证与历史请求改为异步后，是否需补充 loading/错误提示（如网络失败回退）？建议在 `Consultation.vue` 增加 `message.error` 兜底。

---

*由 qahc-harness-design-details（调研阶段）产出。*
