# QA Healthcare 数据模型文档

## 概述

本文档定义了 QA Healthcare 医疗问答系统的核心数据模型，包含用户、问题、统计等模块的实体设计。

**数据模型版本：** v1.0  
**数据库类型：** 关系型数据库（MySQL/PostgreSQL）  
**ORM 框架：** Spring Data JPA  
**设计原则：** 第三范式（3NF），适当冗余优化查询性能

---

## 一、核心实体关系图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           实体关系图（ER Diagram）                            │
└─────────────────────────────────────────────────────────────────────────────┘

    ┌──────────────┐         ┌──────────────┐         ┌──────────────┐
    │    User      │         │   Question   │         │    Answer    │
    │    用户表     │◄───────►│    问题表     │◄───────►│    回答表     │
    ├──────────────┤    1:N  ├──────────────┤   1:1   ├──────────────┤
    │ PK id        │         │ PK id        │         │ PK id        │
    │ username     │         │ FK patient_id│         │ FK question_ │
    │ password     │         │ FK doctor_id │         │    id        │
    │ real_name    │         │ title        │         │ content      │
    │ role         │         │ content      │         │ created_at   │
    │ status       │         │ status       │         │ updated_at   │
    │ created_at   │         │ created_at   │         └──────────────┘
    │ updated_at   │         │ updated_at   │
    └──────────────┘         └──────────────┘
           │
           │ 1:N
           ▼
    ┌──────────────┐
    │  UserProfile │
    │  用户资料表   │
    ├──────────────┤
    │ PK id        │
    │ FK user_id   │
    │ avatar       │
    │ phone        │
    │ email        │
    │ department   │
    │ title        │
    │ specialties  │
    │ experience   │
    └──────────────┘

    ┌──────────────┐         ┌──────────────┐
    │  Department  │◄───────►│ DoctorDept   │
    │   科室表      │   1:N   │ 医生科室关联  │
    ├──────────────┤         ├──────────────┤
    │ PK id        │         │ PK id        │
    │ name         │         │ FK doctor_id │
    │ description  │         │ FK dept_id   │
    │ status       │         └──────────────┘
    └──────────────┘
```

---

## 二、实体详细定义

### 2.1 用户模块

#### 2.1.1 User（用户表）

存储系统所有用户的基本信息，包括患者和医生。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名，登录账号 |
| password | VARCHAR(255) | NOT NULL | 加密后的密码 |
| real_name | VARCHAR(50) | NOT NULL | 真实姓名 |
| role | TINYINT | NOT NULL, DEFAULT 0 | 角色：0-患者，1-医生，2-管理员 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：0-禁用，1-启用，2-锁定 |
| email | VARCHAR(100) | NULL, UNIQUE | 邮箱地址 |
| phone | VARCHAR(20) | NULL | 手机号码 |
| last_login_time | DATETIME | NULL | 最后登录时间 |
| last_login_ip | VARCHAR(50) | NULL | 最后登录IP |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 逻辑删除标志：0-未删除，1-已删除 |

**索引设计：**
```sql
CREATE INDEX idx_username ON user(username);
CREATE INDEX idx_role_status ON user(role, status);
CREATE INDEX idx_created_at ON user(created_at);
```

**Java 实体类：**
```java
@Entity
@Table(name = "t_user")
@SQLDelete(sql = "UPDATE t_user SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted = 0")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "real_name", nullable = false, length = 50)
    private String realName;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.PATIENT;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;
    
    @Column(name = "email", unique = true, length = 100)
    private String email;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    
    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    // 关联关系
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;
    
    @OneToMany(mappedBy = "patient")
    private List<Question> questionsAsPatient;
    
    @OneToMany(mappedBy = "doctor")
    private List<Question> questionsAsDoctor;
    
    // 枚举定义
    public enum UserRole {
        PATIENT,    // 患者
        DOCTOR,     // 医生
        ADMIN       // 管理员
    }
    
    public enum UserStatus {
        DISABLED,   // 禁用
        ACTIVE,     // 启用
        LOCKED      // 锁定
    }
}
```

---

#### 2.1.2 UserProfile（用户资料表）

存储用户的详细资料信息，特别是医生的专业信息。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| user_id | BIGINT | NOT NULL, FK, UNIQUE | 用户ID，外键关联user表 |
| avatar | VARCHAR(500) | NULL | 头像URL |
| gender | TINYINT | NULL | 性别：0-未知，1-男，2-女 |
| birthday | DATE | NULL | 出生日期 |
| address | VARCHAR(255) | NULL | 地址 |
| department | VARCHAR(100) | NULL | 所属科室（医生） |
| title | VARCHAR(50) | NULL | 职称（医生）：主任医师/副主任医师等 |
| experience | VARCHAR(255) | NULL | 临床经验描述 |
| specialties | VARCHAR(500) | NULL | 专长领域，JSON数组格式 |
| introduction | TEXT | NULL | 个人简介 |
| is_online | TINYINT | NOT NULL, DEFAULT 0 | 是否在线（医生）：0-离线，1-在线 |
| created_at | DATETIME | NOT NULL | 创建时间 |
| updated_at | DATETIME | NOT NULL | 更新时间 |

**Java 实体类：**
```java
@Entity
@Table(name = "t_user_profile")
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "avatar", length = 500)
    private String avatar;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "gender")
    private Gender gender;
    
    @Column(name = "birthday")
    private LocalDate birthday;
    
    @Column(name = "address", length = 255)
    private String address;
    
    @Column(name = "department", length = 100)
    private String department;
    
    @Column(name = "title", length = 50)
    private String title;
    
    @Column(name = "experience", length = 255)
    private String experience;
    
    @Column(name = "specialties", length = 500)
    private String specialties; // JSON格式存储
    
    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;
    
    @Column(name = "is_online", nullable = false)
    private Boolean isOnline = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum Gender {
        UNKNOWN, MALE, FEMALE
    }
}
```

---

### 2.2 问答模块

#### 2.2.1 Question（问题表）

存储患者提交的医疗问题。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| patient_id | BIGINT | NOT NULL, FK | 患者ID，外键关联user表 |
| doctor_id | BIGINT | NULL, FK | 指定医生ID，外键关联user表 |
| title | VARCHAR(200) | NOT NULL | 问题标题 |
| content | TEXT | NOT NULL | 问题详细内容 |
| category | VARCHAR(50) | NULL | 问题分类 |
| status | TINYINT | NOT NULL, DEFAULT 0 | 状态：0-待回复，1-已回复，2-已关闭 |
| priority | TINYINT | NOT NULL, DEFAULT 0 | 优先级：0-普通，1-紧急，2-特急 |
| view_count | INT | NOT NULL, DEFAULT 0 | 浏览次数 |
| is_anonymous | TINYINT | NOT NULL, DEFAULT 0 | 是否匿名：0-否，1-是 |
| created_at | DATETIME | NOT NULL | 创建时间 |
| updated_at | DATETIME | NOT NULL | 更新时间 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 逻辑删除标志 |

**索引设计：**
```sql
CREATE INDEX idx_patient_id ON question(patient_id);
CREATE INDEX idx_doctor_id ON question(doctor_id);
CREATE INDEX idx_status ON question(status);
CREATE INDEX idx_created_at ON question(created_at);
CREATE INDEX idx_patient_status ON question(patient_id, status);
```

**Java 实体类：**
```java
@Entity
@Table(name = "t_question")
@SQLDelete(sql = "UPDATE t_question SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted = 0")
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "category", length = 50)
    private String category;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private QuestionStatus status = QuestionStatus.PENDING;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "priority", nullable = false)
    private Priority priority = Priority.NORMAL;
    
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;
    
    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    // 关联关系
    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL)
    private Answer answer;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<QuestionImage> images;
    
    public enum QuestionStatus {
        PENDING,    // 待回复
        ANSWERED,   // 已回复
        CLOSED      // 已关闭
    }
    
    public enum Priority {
        NORMAL,     // 普通
        URGENT,     // 紧急
        CRITICAL    // 特急
    }
}
```

---

#### 2.2.2 Answer（回答表）

存储医生对问题的回答。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| question_id | BIGINT | NOT NULL, FK, UNIQUE | 问题ID，外键关联question表 |
| doctor_id | BIGINT | NOT NULL, FK | 回答医生ID |
| content | TEXT | NOT NULL | 回答内容 |
| is_voice | TINYINT | NOT NULL, DEFAULT 0 | 是否语音回答：0-文字，1-语音 |
| voice_url | VARCHAR(500) | NULL | 语音文件URL |
| voice_duration | INT | NULL | 语音时长（秒） |
| helpful_count | INT | NOT NULL, DEFAULT 0 | 有用数 |
| created_at | DATETIME | NOT NULL | 创建时间 |
| updated_at | DATETIME | NOT NULL | 更新时间 |

**Java 实体类：**
```java
@Entity
@Table(name = "t_answer")
public class Answer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    private Question question;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;
    
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "is_voice", nullable = false)
    private Boolean isVoice = false;
    
    @Column(name = "voice_url", length = 500)
    private String voiceUrl;
    
    @Column(name = "voice_duration")
    private Integer voiceDuration;
    
    @Column(name = "helpful_count", nullable = false)
    private Integer helpfulCount = 0;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

---

#### 2.2.3 QuestionImage（问题图片表）

存储问题的配图。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| question_id | BIGINT | NOT NULL, FK | 问题ID |
| image_url | VARCHAR(500) | NOT NULL | 图片URL |
| sort_order | INT | NOT NULL, DEFAULT 0 | 排序顺序 |
| created_at | DATETIME | NOT NULL | 创建时间 |

**Java 实体类：**
```java
@Entity
@Table(name = "t_question_image")
public class QuestionImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;
    
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

---

### 2.3 科室模块

#### 2.3.1 Department（科室表）

存储医院科室信息。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| name | VARCHAR(50) | NOT NULL, UNIQUE | 科室名称 |
| code | VARCHAR(20) | NOT NULL, UNIQUE | 科室编码 |
| description | VARCHAR(500) | NULL | 科室描述 |
| icon | VARCHAR(255) | NULL | 科室图标 |
| sort_order | INT | NOT NULL, DEFAULT 0 | 排序顺序 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：0-禁用，1-启用 |
| created_at | DATETIME | NOT NULL | 创建时间 |
| updated_at | DATETIME | NOT NULL | 更新时间 |

**Java 实体类：**
```java
@Entity
@Table(name = "t_department")
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
    
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "icon", length = 255)
    private String icon;
    
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum Status {
        DISABLED, ACTIVE
    }
}
```

---

#### 2.3.2 DoctorDepartment（医生科室关联表）

医生与科室的多对多关联表。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| doctor_id | BIGINT | NOT NULL, FK | 医生ID |
| department_id | BIGINT | NOT NULL, FK | 科室ID |
| is_primary | TINYINT | NOT NULL, DEFAULT 0 | 是否主要科室：0-否，1-是 |
| created_at | DATETIME | NOT NULL | 创建时间 |

**Java 实体类：**
```java
@Entity
@Table(name = "t_doctor_department", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "department_id"}))
public class DoctorDepartment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;
    
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

---

### 2.4 统计模块

#### 2.4.1 DailyStatistics（每日统计表）

存储系统每日的统计数据。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| stat_date | DATE | NOT NULL, UNIQUE | 统计日期 |
| new_user_count | INT | NOT NULL, DEFAULT 0 | 新增用户数 |
| new_question_count | INT | NOT NULL, DEFAULT 0 | 新增问题数 |
| new_answer_count | INT | NOT NULL, DEFAULT 0 | 新增回答数 |
| active_doctor_count | INT | NOT NULL, DEFAULT 0 | 活跃医生数 |
| active_patient_count | INT | NOT NULL, DEFAULT 0 | 活跃患者数 |
| total_user_count | INT | NOT NULL, DEFAULT 0 | 总用户数 |
| total_question_count | INT | NOT NULL, DEFAULT 0 | 总问题数 |
| created_at | DATETIME | NOT NULL | 创建时间 |
| updated_at | DATETIME | NOT NULL | 更新时间 |

**Java 实体类：**
```java
@Entity
@Table(name = "t_daily_statistics")
public class DailyStatistics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "stat_date", nullable = false, unique = true)
    private LocalDate statDate;
    
    @Column(name = "new_user_count", nullable = false)
    private Integer newUserCount = 0;
    
    @Column(name = "new_question_count", nullable = false)
    private Integer newQuestionCount = 0;
    
    @Column(name = "new_answer_count", nullable = false)
    private Integer newAnswerCount = 0;
    
    @Column(name = "active_doctor_count", nullable = false)
    private Integer activeDoctorCount = 0;
    
    @Column(name = "active_patient_count", nullable = false)
    private Integer activePatientCount = 0;
    
    @Column(name = "total_user_count", nullable = false)
    private Integer totalUserCount = 0;
    
    @Column(name = "total_question_count", nullable = false)
    private Integer totalQuestionCount = 0;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

---

## 三、数据字典

### 3.1 用户角色（UserRole）

| 值 | 名称 | 说明 |
|----|------|------|
| 0 | PATIENT | 患者用户 |
| 1 | DOCTOR | 医生用户 |
| 2 | ADMIN | 管理员 |

### 3.2 用户状态（UserStatus）

| 值 | 名称 | 说明 |
|----|------|------|
| 0 | DISABLED | 禁用，无法登录 |
| 1 | ACTIVE | 正常启用 |
| 2 | LOCKED | 锁定，临时限制登录 |

### 3.3 问题状态（QuestionStatus）

| 值 | 名称 | 说明 |
|----|------|------|
| 0 | PENDING | 待回复 |
| 1 | ANSWERED | 已回复 |
| 2 | CLOSED | 已关闭 |

### 3.4 优先级（Priority）

| 值 | 名称 | 说明 |
|----|------|------|
| 0 | NORMAL | 普通 |
| 1 | URGENT | 紧急 |
| 2 | CRITICAL | 特急 |

### 3.5 性别（Gender）

| 值 | 名称 | 说明 |
|----|------|------|
| 0 | UNKNOWN | 未知 |
| 1 | MALE | 男 |
| 2 | FEMALE | 女 |

---

## 四、数据库建表脚本

```sql
-- 用户表
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0-患者，1-医生，2-管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用，2-锁定',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_role_status (role, status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户资料表
CREATE TABLE t_user_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    avatar VARCHAR(500) COMMENT '头像URL',
    gender TINYINT COMMENT '性别：0-未知，1-男，2-女',
    birthday DATE COMMENT '出生日期',
    address VARCHAR(255) COMMENT '地址',
    department VARCHAR(100) COMMENT '所属科室',
    title VARCHAR(50) COMMENT '职称',
    experience VARCHAR(255) COMMENT '临床经验',
    specialties VARCHAR(500) COMMENT '专长领域，JSON格式',
    introduction TEXT COMMENT '个人简介',
    is_online TINYINT NOT NULL DEFAULT 0 COMMENT '是否在线：0-离线，1-在线',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户资料表';

-- 问题表
CREATE TABLE t_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    doctor_id BIGINT COMMENT '指定医生ID',
    title VARCHAR(200) NOT NULL COMMENT '问题标题',
    content TEXT NOT NULL COMMENT '问题内容',
    category VARCHAR(50) COMMENT '问题分类',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待回复，1-已回复，2-已关闭',
    priority TINYINT NOT NULL DEFAULT 0 COMMENT '优先级：0-普通，1-紧急，2-特急',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    is_anonymous TINYINT NOT NULL DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    FOREIGN KEY (patient_id) REFERENCES t_user(id),
    FOREIGN KEY (doctor_id) REFERENCES t_user(id),
    INDEX idx_patient_id (patient_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题表';

-- 回答表
CREATE TABLE t_answer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_id BIGINT NOT NULL UNIQUE COMMENT '问题ID',
    doctor_id BIGINT NOT NULL COMMENT '回答医生ID',
    content TEXT NOT NULL COMMENT '回答内容',
    is_voice TINYINT NOT NULL DEFAULT 0 COMMENT '是否语音：0-文字，1-语音',
    voice_url VARCHAR(500) COMMENT '语音URL',
    voice_duration INT COMMENT '语音时长（秒）',
    helpful_count INT NOT NULL DEFAULT 0 COMMENT '有用数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES t_question(id),
    FOREIGN KEY (doctor_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答表';

-- 科室表
CREATE TABLE t_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '科室名称',
    code VARCHAR(20) NOT NULL UNIQUE COMMENT '科室编码',
    description VARCHAR(500) COMMENT '科室描述',
    icon VARCHAR(255) COMMENT '科室图标',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室表';

-- 医生科室关联表
CREATE TABLE t_doctor_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    department_id BIGINT NOT NULL COMMENT '科室ID',
    is_primary TINYINT NOT NULL DEFAULT 0 COMMENT '是否主要科室：0-否，1-是',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_doctor_dept (doctor_id, department_id),
    FOREIGN KEY (doctor_id) REFERENCES t_user(id),
    FOREIGN KEY (department_id) REFERENCES t_department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生科室关联表';

-- 每日统计表
CREATE TABLE t_daily_statistics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    stat_date DATE NOT NULL UNIQUE COMMENT '统计日期',
    new_user_count INT NOT NULL DEFAULT 0 COMMENT '新增用户数',
    new_question_count INT NOT NULL DEFAULT 0 COMMENT '新增问题数',
    new_answer_count INT NOT NULL DEFAULT 0 COMMENT '新增回答数',
    active_doctor_count INT NOT NULL DEFAULT 0 COMMENT '活跃医生数',
    active_patient_count INT NOT NULL DEFAULT 0 COMMENT '活跃患者数',
    total_user_count INT NOT NULL DEFAULT 0 COMMENT '总用户数',
    total_question_count INT NOT NULL DEFAULT 0 COMMENT '总问题数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日统计表';
```

---

## 五、Repository 接口设计

### 5.1 UserRepository

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 根据用户名查询
    Optional<User> findByUsername(String username);
    
    // 根据邮箱查询
    Optional<User> findByEmail(String email);
    
    // 根据角色和状态查询
    List<User> findByRoleAndStatus(UserRole role, UserStatus status);
    
    // 检查用户名是否存在
    boolean existsByUsername(String username);
    
    // 分页查询医生列表
    Page<User> findByRole(UserRole role, Pageable pageable);
    
    // 模糊查询用户名
    List<User> findByUsernameContaining(String username);
}
```

### 5.2 QuestionRepository

```java
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    // 根据患者ID查询
    List<Question> findByPatientId(Long patientId);
    
    // 根据医生ID查询
    List<Question> findByDoctorId(Long doctorId);
    
    // 根据状态查询
    List<Question> findByStatusOrderByCreatedAtDesc(QuestionStatus status);
    
    // 分页查询患者的问题
    Page<Question> findByPatientId(Long patientId, Pageable pageable);
    
    // 查询医生的待回复问题
    List<Question> findByDoctorIdAndStatus(Long doctorId, QuestionStatus status);
    
    // 统计某状态的问题数量
    long countByStatus(QuestionStatus status);
}
```

---

## 六、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2025-11-03 | 初始版本，定义核心数据模型 |

---

## 附录

### A. 命名规范对照表

| 类型 | Java 类名 | 数据库表名 | 说明 |
|------|-----------|------------|------|
| 用户 | User | t_user | 基础用户表 |
| 用户资料 | UserProfile | t_user_profile | 扩展信息 |
| 问题 | Question | t_question | 问诊问题 |
| 回答 | Answer | t_answer | 医生回答 |
| 科室 | Department | t_department | 医院科室 |
| 医生科室关联 | DoctorDepartment | t_doctor_department | 多对多关联 |
| 每日统计 | DailyStatistics | t_daily_statistics | 统计报表 |

### B. 字段命名规范

- 主键：`id`
- 外键：`{表名}_id`，如 `user_id`, `question_id`
- 创建时间：`created_at`
- 更新时间：`updated_at`
- 逻辑删除：`deleted`
- 状态字段：`status`
- 类型字段：`type`, `role`, `category` 等具体含义命名
