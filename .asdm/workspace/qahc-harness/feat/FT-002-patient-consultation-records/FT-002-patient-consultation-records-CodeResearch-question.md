# FT-002 代码调研报告 · 后端（qa-service-question）

> 调研对象：`server/qa-service-question/`（Spring Boot 3.5.7）
> 调研目的：核对 FT-002 复用的 `GET /api/questions?patientId=` 接口契约与数据模型
> 调研方式：只读代码/文档搜索，未修改任何文件

---

## 1. 服务现状

**结论：该服务目前仅处于「基础框架（Scaffold）」阶段，尚无任何业务实现。**

- 启动类 `QaServiceQuestionApplication.java` 为空壳，无 `@Bean`/配置类。
- `pom.xml` 仅含 `spring-boot-starter-web` / `starter-test` / testcontainers / junit；**无 spring-boot-starter-data-jpa、无 MySQL 驱动、无 validation、无对 qa-service-user 的依赖**。
- `application.properties` 仅两行：`spring.application.name` + `server.port=8081`，无数据源/JPA 配置。
- 源码目录仅启动类一个 `.java` 文件，**无任何 Entity / DTO / Controller / Service / Repository**。

> 架构文档标记其为「开发中」，但代码实际未落地。FT-002 的接口契约目前只存在于规划文档。

---

## 2. Question 相关代码

**结论：当前不存在任何 Question 的 Java 业务代码。** 唯一「定义」在文档中：
- `server/docs/data-models.md` 规划 `Question` 实体（第 255–355 行）、`Answer` 实体（第 359–415 行）。
- `server/docs/arch.md` 规划 `QuestionController`（第 392 行）、`Question` 实体（第 411 行），均尚未创建。

---

## 3. 状态枚举

数据模型文档规划 `QuestionStatus`：`PENDING(0)` / `ANSWERED(1)` / `CLOSED(2)`。

**差异**：FT-002 期望字符串 `"pending"` / `"answered"`（仅两个值）；后端规划为序数值枚举 0/1/2 且含第三个 `CLOSED`。接口契约需约定序列化形式与 `CLOSED` 是否纳入患者问诊记录展示。

---

## 4. 查询接口 `GET /api/questions`

**结论：尚未实现，仅停留在架构文档规划片段。**

- 代码无 `api/questions` 实现。
- 架构文档中相关问题查询：`GET /api/doctors/{id}/questions`（医生维度）、`GET /api/questions?doctorId={id}&status=pending`（医生查看）。
- **FT-002 需要的 `GET /api/questions?patientId=` 在架构文档无直接对应规划**，最接近的是数据模型文档的 `QuestionRepository.findByPatientId(Long)` 与分页重载。

**规划落点建议**：在 `QuestionController` 新增 `GET /api/questions?patientId={id}`（可附带 `status`/`page`/`size`），由 `QuestionService` → `QuestionRepository.findByPatientId` 实现，返回 DTO 列表。

---

## 5. 字段契约核对（FT-002 期望 vs 后端规划）

| FT-002 期望字段 | 后端规划 | 差异/映射 |
|------|------|------|
| `id` (string `q001`) | `Question.id` BIGINT | 需约定序列化格式 |
| `patientId` (string) | `Question.patient_id` BIGINT | 类型不一致，需 DTO 转换 |
| `doctorId` (string) | `Question.doctor_id` BIGINT | 类型不一致 |
| **`doctorName`** | 无字段，需 join `User.real_name` | ⚠️ 关键：DB 只存 `doctor_id`，姓名在 `t_user.real_name` |
| **`question`** | `Question.title` + `Question.content` | ⚠️ 命名差异：无 `question` 字段，需 DTO 映射 `content` |
| `submitTime` | `Question.created_at` DATETIME | ⚠️ 命名差异 |
| `status` | `Question.status` TINYINT + 枚举 | 字符串 vs 序数(0/1/2)，含 `CLOSED` |
| `answer` | `Answer.content`（跨表 1:1） | ⚠️ 跨表差异，无则 null |
| `answerTime` | `Answer.created_at` | ⚠️ 命名差异 |

**前端 mock 单条示例**（`data/question-list.json`）：
```json
{ "id":"q001","patientId":"patient001","patientName":"赵明","doctorId":"doc001",
  "doctorName":"张伟医生","question":"最近总是感觉胸闷气短...",
  "submitTime":"2025-11-02T09:30:00","status":"answered",
  "answer":"根据您的描述,可能是...","answerTime":"2025-11-02T09:45:00" }
```

---

## 6. 依赖关系：doctorName 由谁提供

- `qa-service-question/pom.xml` **未引入对 qa-service-user 的依赖**（无 OpenFeign/RestTemplate 封装）。
- 架构文档（第 7.1 节）规划 qa-service-question 调用 `GET /api/users/{patientId}` 验证患者，但代码未实现、且无依赖。
- **契约结论**：FT-002 响应里的 `doctorName` 应由 qa-service-question 在**后端聚合后一并返回**，前端不应自行拼接；聚合方式（同库 join `t_user` 还是跨服务 REST）由 FT-001/后端选型决定。

---

## 7. 关键发现与待确认（供接口设计章节）

### 契约要点（建议 PRD 标注）
1. 接口 `GET /api/questions`，必填 `patientId`，可选 `status`/`page`/`size`。
2. 响应 DTO 含：`id`/`patientId`/`doctorId`/`doctorName`/`question`/`submitTime`/`status`/`answer`/`answerTime`。
3. 字段映射：`question←content`、`submitTime←created_at`、`answer←Answer.content`、`answerTime←Answer.created_at`、`doctorName←User.real_name`。
4. id 类型统一序列化策略；时间统一 ISO 8601；未回答项 `answer`/`answerTime` 为 `null`。

### 需 FT-001 / 后端交付或确认
1. 端点 `GET /api/questions?patientId=` 当前未实现，需按落点实现 Controller/Service/Repository/DTO。
2. `Question`/`Answer` 实体与 `QuestionStatus` 枚举需落地为真实代码。
3. `doctorName` 聚合方式（join vs 跨服务）确认——直接影响是否依赖 user 服务上线。
4. 是否对「患者问诊记录」暴露 `closed` 状态，还是仅 `pending`/`answered`。
5. `patientId` 类型对齐（字符串 `patient001` vs 数字），否则过滤失效。

---

## 8. 调研来源文件

- `server/qa-service-question/pom.xml`
- `server/qa-service-question/src/main/java/com/leansofx/qaservicequestion/QaServiceQuestionApplication.java`
- `server/qa-service-question/src/main/resources/application.properties`
- `server/docs/arch.md`
- `server/docs/data-models.md`
- `web/qa-web/src/data/question-list.json`

---

*由 QA Healthcare 驾驭工程工具集（qahc-harness）Code Research 产出。*
