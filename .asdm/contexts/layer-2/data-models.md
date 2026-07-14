# 数据模型（L2 层）

## 概述

QA Healthcare 领域模型围绕**用户（患者/医生/管理员）、问题、回答、科室、统计**展开。

- **当前存储（Phase 1）**：后端服务以内存 JSON（`ConcurrentHashMap`，`@PostConstruct` 加载）持久化，重启即丢失，仅用于开发与演示。
- **规划存储（Phase 2）**：MySQL 8 + Spring Data JPA（规范化表，逻辑删除、时间戳、索引齐全，见 `server/docs/data-models.md` 建表脚本）。

> 以下实体字段以**规划 JPA 实体**为权威定义；同时标注前端 `web/qa-web/src/data/*.json` 与后端 `DoctorUserDTO` 的实际形态（id 为字符串，如 `doctor-001`）。

## 实体关系图（Mermaid）

```mermaid
erDiagram
    USER ||--o| USER_PROFILE : "has"
    USER ||--o{ QUESTION : "patient/doctor"
    QUESTION ||--o| ANSWER : "answered by"
    QUESTION ||--o{ QUESTION_IMAGE : "has"
    USER ||--o{ DOCTOR_DEPARTMENT : "doctor"
    DEPARTMENT ||--o{ DOCTOR_DEPARTMENT : "contains"
    DAILY_STATISTICS ||--o| USER : "counts"

    USER {
        bigint id PK
        string username UK
        string password
        string real_name
        int role
        int status
        string email UK
        datetime created_at
        datetime updated_at
        bool deleted
    }
    USER_PROFILE {
        bigint id PK
        bigint user_id FK
        string avatar
        int gender
        date birthday
        string department
        string title
        string specialties
        bool is_online
    }
    QUESTION {
        bigint id PK
        bigint patient_id FK
        bigint doctor_id FK
        string title
        text content
        int status
        int priority
        int view_count
        bool is_anonymous
    }
    ANSWER {
        bigint id PK
        bigint question_id FK UK
        bigint doctor_id FK
        text content
        bool is_voice
        int helpful_count
    }
    DEPARTMENT {
        bigint id PK
        string name UK
        string code UK
        int status
    }
    DOCTOR_DEPARTMENT {
        bigint id PK
        bigint doctor_id FK
        bigint department_id FK
        bool is_primary
    }
    DAILY_STATISTICS {
        bigint id PK
        date stat_date UK
        int new_user_count
        int new_question_count
        int active_doctor_count
    }
```

## 核心实体

### User（用户表 t_user）
系统所有用户（患者/医生/管理员）基础信息。

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 登录账号 |
| password | VARCHAR(255) | NOT NULL | 加密密码 |
| real_name | VARCHAR(50) | NOT NULL | 真实姓名 |
| role | TINYINT | DEFAULT 0 | 0-患者 1-医生 2-管理员 |
| status | TINYINT | DEFAULT 1 | 0-禁用 1-启用 2-锁定 |
| email | VARCHAR(100) | UNIQUE | 邮箱 |
| phone | VARCHAR(20) | NULL | 手机 |
| created_at / updated_at | DATETIME | NOT NULL | 时间戳 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

### UserProfile（用户资料表 t_user_profile）
医生专业信息（职称、科室、专长、在线状态）。

### Question（问题表 t_question）
患者提交的医疗问题。`patient_id`/`doctor_id` 均外键关联 user；`status`：0-待回复 1-已回复 2-已关闭；`priority`：0-普通 1-紧急 2-特急。

### Answer（回答表 t_answer）
医生对问题的回答，`question_id` 唯一（一对一）；支持文字/`is_voice` 语音回答与 `helpful_count` 有用数。

### Department / DoctorDepartment
科室表与医生-科室多对多关联（含 `is_primary` 主科室标志）。

### DailyStatistics（每日统计表 t_daily_statistics）
按 `stat_date` 聚合的新增用户/问题/回答、活跃医生/患者、累计总量。

## 数据字典（枚举）

| 枚举 | 值 | 说明 |
|------|----|------|
| UserRole | 0/1/2 | PATIENT / DOCTOR / ADMIN |
| UserStatus | 0/1/2 | DISABLED / ACTIVE / LOCKED |
| QuestionStatus | 0/1/2 | PENDING / ANSWERED / CLOSED |
| Priority | 0/1/2 | NORMAL / URGENT / CRITICAL |
| Gender | 0/1/2 | UNKNOWN / MALE / FEMALE |

## 实际数据形态（运行态）

- 前端 `data/doctor-user-list.json`、后端 `DoctorUserDTO`：医生以 **字符串 id**（如 `doctor-001`）+ `username/name/title/department/avatar/experience/specialties[]/isActive` 表示。
- 前端 `data/patient-user.json`、`data/question-list.json`：患者与问题列表，字段对应上述实体。
- 后端 Repository 已定义 `DoctorUserRepository extends JpaRepository<DoctorUser, String>`（id 为字符串），印证运行态以字符串主键为主。

## 数据访问模式

```java
public interface DoctorUserRepository extends JpaRepository<DoctorUser, String> {
    Optional<DoctorUser> findByUsername(String username);
    List<DoctorUser> findByIsActiveTrue();
}
```

- Repository 继承 `JpaRepository<X, ID>` + `JpaSpecificationExecutor`；方法名查询 / `@Query` / 分页 `Page<X>`。
- 索引：`user(username, role_status, created_at)`、`question(patient_id, doctor_id, status, created_at)`。

---

*由 QA Healthcare 上下文构建工具集维护。*
