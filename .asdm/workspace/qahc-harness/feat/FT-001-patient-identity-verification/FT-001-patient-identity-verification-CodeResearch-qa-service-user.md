# FT-001 代码调研 · qa-service-user（后端用户服务）

> 调研模块：`server/qa-service-user/`
> 调研日期：2026-07-14
> 关联特性：[FT-001-patient-identity-verification-PRD.md](./FT-001-patient-identity-verification-PRD.md)

---

## 1. 代码库概述

- **技术栈**：Spring Boot 3.5.7 + Java 17 + Spring Data JPA 3.x + Maven，端口 `8080`。
- **分层架构**：`Controller → Service/impl → Repository → Entity`，DTO 做 Entity↔对外数据转换。
- **当前业务代码**：
  - `entity/DoctorUser.java`：`@Entity` `@Table("doctor_user")`，**字符串主键** `id`（length 20）。
  - `repository/DoctorUserRepository.java`：`extends JpaRepository<DoctorUser, String>`，含 `findByUsername`、`findByIsActiveTrue`、`existsByUsername` 方法名查询。
  - `service/DoctorUserService.java` + `service/impl/DoctorUserServiceImpl.java`：标准接口/实现，含 `toDTO`/`toEntity`。
  - `dto/DoctorUserDTO.java`：对外 DTO。
  - `controller/DoctorUserController.java`：`@RequestMapping("/api/doctors")`，提供医生 CRUD。
  - `controller/TestController.java`：`/api/test/cors` 连通性测试。
  - `config/CorsConfig.java`：`WebMvcConfigurer` 实现，允许 `*` origin + credentials=true，注册 `/**` 的 `CorsConfigurationSource`。
- **持久化配置**（`application.properties`）：
  - `spring.datasource.url=jdbc:mysql://localhost:3307/healthcare`（root/root）。
  - `spring.jpa.hibernate.ddl-auto=update`（**表结构由 JPA 自动维护**）。
  - `spring.web.cors.allow-credentials=false`（与 `CorsConfig` 中 `true` 存在配置层面不一致，详见 §4）。
- **数据初始化**：`src/main/resources/db/init-doctor-data.sql` 仅灌入 `doctor_user` 表；**无患者相关 SQL**。

---

## 2. 现有实现分析（对照 FT-001 需求）

FT-001 在 user 服务需新增：**患者实体 + `POST /api/patients/verify` + 患者档案查询**，按「姓名+生日」匹配/新建并生成 `PAT-` 业务键。

当前 user 服务：
- ✅ 已有完整的 `Controller/Service/Repository/Entity/DTO` 分层范式，可直接套用（新建 `PatientController`、`PatientService/Impl`、`PatientRepository`、`Patient`、`PatientDTO`）。
- ❌ 不存在任何患者（Patient）实体、Repository 或 Controller。
- ❌ 无业务键生成逻辑（`PAT-` 时间戳/随机串）。
- ❌ 无「姓名归一化 + 生日精确匹配」的去重查询。

---

## 3. 关键发现

1. **字符串主键范式可复用**：`DoctorUser.id` 为字符串。患者业务键 `PAT-20260714-8F3K` 可直接作为患者实体的 `id`（字符串），无需单独业务键列；建议以业务键即主键，并保留 `name`/`birthday`/`normalizedName` 等列。
2. **去重查询落地方式**：在 `PatientRepository` 中新增方法名查询 `Optional<Patient> findByNormalizedNameAndBirthday(String normalizedName, String birthday)`，并配合唯一索引（见 §4 缺失项）保证并发安全。
3. **业务键唯一性**：`PAT-` 业务键需保证全局唯一。由于 `id` 即主键，JPA 保存时主键冲突会被数据库拒绝；建议在生成器中加入随机串/纳秒时间戳以降低碰撞。
4. **ddl-auto=update 的影响**：患者表将由 JPA 在应用启动时自动建表，**无需手写建表脚本**即可运行；但 PRD §1.3 提及「配套 MySQL 建表脚本」。建议二选一：① 依赖 `ddl-auto=update` 自动建表（最快），② 额外提供 `init-patient-data.sql` 仅用于可选种子/说明。需与 PRD 决策一致（见 §5 待确认）。
5. **与前端的契约**：前端 `Patient` 接口当前含 `phone`/`gender`，但验证表单仅收集 `name`+`birthday`。后端 Patient 实体建议只持久化 `name`、`birthday`、`normalizedName`、`businessKey(id)`、时间戳，不强制 `phone`/`gender`（与弱认证、仅姓名+生日验证的目标一致）。

---

## 4. 缺失项（开发阶段需补齐）

| 缺失项 | 说明 | 建议位置 |
| ------ | ---- | -------- |
| `Patient` 实体 | 字段：`id`(业务键 PK, String)、`name`、`normalizedName`、`birthday`(YYYY-MM-DD)、`createdAt`/`updatedAt` | `entity/Patient.java` |
| `PatientRepository` | `JpaRepository<Patient,String>` + `findByNormalizedNameAndBirthday`；建议加 `@Table` 唯一约束 `(normalized_name, birthday)` | `repository/PatientRepository.java` |
| `PatientService` / `impl` | `verify(name,birthday)`：归一化→查重→命中返回/未命中新建+生成业务键；`getByBusinessKey(id)` | `service/`、`service/impl/` |
| `PatientDTO` | 对外结构：`businessKey`、`name`、`birthday` | `dto/PatientDTO.java` |
| `PatientController` | `POST /api/patients/verify`（入参 name+birthday，返回 PatientDTO）、`GET /api/patients/{businessKey}` | `controller/PatientController.java` |
| 业务键生成器 | `PAT-` + `yyyyMMdd` + 随机/纳秒串，保证唯一 | `util/BusinessKeyGenerator.java` 或 Service 内 |
| CORS 配置一致性修复 | `application.properties` 的 `allow-credentials=false` 与 `CorsConfig` 的 `true` 冲突；若前端带凭证需统一为 `true`（同 `CorsConfig`） | 二选一统一 |

---

## 5. 待确认问题

1. **业务键是否即实体主键**？建议是（与 `DoctorUser` 字符串主键范式一致，简化去重与查询）。
2. **患者表是否手写建表脚本**？还是依赖 `ddl-auto=update` 自动建表（推荐，与现有 doctor 表由 JPA 管理一致）？
3. **CORS 凭证**：前端是否需携带凭证（cookie/session）？当前 `CorsConfig` 允许 credentials，但 `application.properties` 设为 false；是否统一为 `true`？
4. **患者档案是否需要 `phone`/`gender` 字段**？验证流程不收集这两项，建议后端不持久化，避免与前端 mock 结构耦合。

---

*由 qahc-harness-design-details（调研阶段）产出。*
