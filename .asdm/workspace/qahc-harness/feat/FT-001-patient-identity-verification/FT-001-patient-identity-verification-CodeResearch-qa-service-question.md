# FT-001 代码调研 · qa-service-question（后端问题服务）

> 调研模块：`server/qa-service-question/`
> 调研日期：2026-07-14
> 关联特性：[FT-001-patient-identity-verification-PRD.md](./FT-001-patient-identity-verification-PRD.md)

---

## 1. 代码库概述

- **技术栈**：Spring Boot 3.5.7 + Java 17 + Maven，端口 `8081`。
- **当前代码状态**：**仅骨架**。
  - `src/main/java/.../QaServiceQuestionApplication.java`：空 `SpringBootApplication`，无任何 `@RestController`/`@Entity`/`@Repository`。
  - `src/test/...`：仅 `ApplicationTests` + Testcontainers 占位配置（`TestcontainersConfiguration`），无业务测试。
  - `src/main/resources/application.properties`：**仅 2 行**（`spring.application.name`、`server.port=8081`），**未配置任何 MySQL 数据源 / JPA / CORS**。
- 对比 user 服务：question 服务目前**完全没有持久化与 API 能力**，是 FT-001 缺口最大的模块。

---

## 2. 现有实现分析（对照 FT-001 需求）

FT-001 在 question 服务需新增：`GET /api/questions?patientId=<业务键>` 用于前端历史问诊展示。

当前 question 服务：
- ❌ 无任何 Controller、Entity、Repository、Service。
- ❌ 无数据源配置，无法连接 `healthcare` MySQL。
- ❌ 无问诊记录持久化（现有问诊数据仅存在于**前端 mock** `web/qa-web/src/data/question-list.json`）。

---

## 3. 关键发现

1. **需从零搭建完整后端栈**：question 服务要落地历史查询，必须新建 `Question` 实体、`QuestionRepository`、`QuestionService/Impl`、`QuestionController`，并补齐 `application.properties` 的数据源/JPA/CORS 配置（可参照 user 服务）。
2. **数据来源缺口**：前端 `question-list.json` 已含 `patientId` 字段（与 FT-001 业务键语义对应），但后端无对应表。需决策：是否将 question 数据迁至 MySQL（与 FT-001「直接对接 MySQL」决策一致），并配套种子数据；还是仅新增查询接口、数据暂以内存/其它方式提供。
3. **`patientId` 即业务键**：前端历史查询使用 `GET /api/questions?patientId=<业务键>`，后端 `Question.patientId` 应存储 user 服务生成的 `PAT-` 业务键（字符串），便于两服务通过业务键关联，无需跨库 Join。
4. **服务间耦合**：本特性「历史问诊」为前端分别调用 user（验证）+ question（历史），question 服务**无需调用** user 服务，仅在自身库按 `patientId` 过滤即可，降低耦合。

---

## 4. 缺失项（开发阶段需补齐）

| 缺失项 | 说明 | 建议位置 |
| ------ | ---- | -------- |
| 数据源/JPA/CORS 配置 | 复制 user 服务的 MySQL(`localhost:3307/healthcare`)+`ddl-auto=update`+CORS 配置 | `application.properties` |
| `Question` 实体 | 字段对齐前端 `question-list.json`：`id`、`patientId`(业务键)、`patientName`、`doctorId`、`doctorName`、`question`、`submitTime`、`status`、`answer`、`answerTime` | `entity/Question.java` |
| `QuestionRepository` | `JpaRepository<Question,String>` + `findByPatientId(String patientId)` | `repository/QuestionRepository.java` |
| `QuestionService` / `impl` | `getByPatientId(patientId)` 返回列表 | `service/`、`service/impl/` |
| `QuestionDTO` | 对外结构（同实体字段，状态用枚举/字符串） | `dto/QuestionDTO.java` |
| `QuestionController` | `GET /api/questions?patientId=` 按患者查询历史 | `controller/QuestionController.java` |
| 问诊种子数据（可选） | 将 `question-list.json` 迁移/同步为 MySQL 种子，保证历史可查 | `db/init-question-data.sql` |

---

## 5. 待确认问题

1. **是否在本特性内将问诊数据迁至 MySQL**？还是 question 服务仅新增空查询接口、数据后续特性补齐（当前前端历史是 mock）？
2. **`status` 取值**：前端用 `'pending' | 'answered'`，后端是否沿用该字符串枚举（与 user 服务的 DTO 风格一致）？
3. **跨服务数据一致性**：question 服务的 `patientId` 是否严格依赖 user 服务生成的 `PAT-` 业务键？是否需要 user 服务提供「业务键是否存在」的校验接口（当前设计不需要，确认即可）。

---

*由 qahc-harness-design-details（调研阶段）产出。*
