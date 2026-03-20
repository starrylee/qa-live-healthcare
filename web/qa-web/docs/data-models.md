# QA Healthcare 前端数据模型文档

## 概述

本文档定义了 QA Healthcare 前端项目的数据模型，基于 TypeScript 接口实现，用于支撑医疗问诊平台的业务逻辑。

**文档版本：** v1.0  
**技术栈：** TypeScript + Vue 3 Reactive  
**存储方式：** 内存存储（基于 JSON 文件加载）  
**设计原则：** 类型安全、响应式数据流、组件化共享

---

## 一、数据模型关系图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           前端数据模型关系图                                   │
└─────────────────────────────────────────────────────────────────────────────┘

    ┌──────────────┐         ┌──────────────┐         ┌──────────────┐
    │    Doctor    │         │   Question   │         │   Patient    │
    │    医生模型   │◄───────►│    问题模型   │◄───────►│    患者模型   │
    ├──────────────┤   1:N   ├──────────────┤   N:1   ├──────────────┤
    │ id: string   │         │ id: string   │         │ id: string   │
    │ username     │         │ patientId    │         │ name         │
    │ password     │         │ doctorId     │         │ birthday     │
    │ name         │         │ question     │         │ phone        │
    │ title        │         │ status       │         │ gender       │
    │ department   │         │ answer       │         └──────────────┘
    │ isActive     │         └──────────────┘
    └──────────────┘

    ┌─────────────────────────────────────────────────────────────────────────┐
    │                          Store State（全局状态）                         │
    ├─────────────────────────────────────────────────────────────────────────┤
    │  doctors: Doctor[]              - 医生列表                               │
    │  patients: Patient[]            - 患者列表                               │
    │  questions: Question[]          - 问题列表                               │
    │  currentDoctor: Doctor | null   - 当前登录医生                           │
    │  currentPatient: Patient | null - 当前登录患者                           │
    └─────────────────────────────────────────────────────────────────────────┘
```

---

## 二、核心数据模型

### 2.1 Doctor（医生模型）

医生用户数据模型，包含基本信息和专业资质。

#### TypeScript 接口定义

```typescript
export interface Doctor {
  /** 医生唯一标识 */
  id: string;
  /** 登录用户名 */
  username: string;
  /** 登录密码（明文存储，仅用于演示） */
  password: string;
  /** 医生姓名 */
  name: string;
  /** 职称 */
  title: string;
  /** 所属科室 */
  department: string;
  /** 头像图片URL */
  avatar: string;
  /** 临床经验描述 */
  experience: string;
  /** 专长领域数组 */
  specialties: string[];
  /** 是否在线/活跃 */
  isActive: boolean;
}
```

#### 字段详细说明

| 字段名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| `id` | `string` | 是 | 医生唯一标识符 | `"doc001"` |
| `username` | `string` | 是 | 登录用户名 | `"dr-zhang-wei"` |
| `password` | `string` | 是 | 登录密码 | `"123456"` |
| `name` | `string` | 是 | 医生姓名 | `"张伟医生"` |
| `title` | `string` | 是 | 职称 | `"主任医师"`、`"副主任医师"` |
| `department` | `string` | 是 | 所属科室 | `"心内科"`、`"儿科"` |
| `avatar` | `string` | 是 | 头像图片URL | `"https://.../photo.jpg"` |
| `experience` | `string` | 是 | 临床经验 | `"15年临床经验"` |
| `specialties` | `string[]` | 是 | 专长领域数组 | `["高血压", "冠心病"]` |
| `isActive` | `boolean` | 是 | 是否在线 | `true`、`false` |

#### 示例数据

```typescript
const doctor: Doctor = {
  id: "doc001",
  username: "dr-zhang-wei",
  password: "123456",
  name: "张伟医生",
  title: "主任医师",
  department: "心内科",
  avatar: "https://images.pexels.com/photos/5215024/...",
  experience: "15年临床经验",
  specialties: ["高血压", "冠心病", "心律失常"],
  isActive: true
};
```

---

### 2.2 Patient（患者模型）

患者用户数据模型，用于患者身份验证和信息展示。

#### TypeScript 接口定义

```typescript
export interface Patient {
  /** 患者唯一标识 */
  id: string;
  /** 患者姓名 */
  name: string;
  /** 出生日期（YYYY-MM-DD格式） */
  birthday: string;
  /** 手机号码（脱敏显示） */
  phone: string;
  /** 性别 */
  gender: string;
}
```

#### 字段详细说明

| 字段名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| `id` | `string` | 是 | 患者唯一标识符 | `"patient001"` |
| `name` | `string` | 是 | 患者姓名 | `"赵明"` |
| `birthday` | `string` | 是 | 出生日期 | `"1985-03-15"` |
| `phone` | `string` | 是 | 手机号码（脱敏） | `"138****1234"` |
| `gender` | `string` | 是 | 性别 | `"男"`、`"女"` |

#### 示例数据

```typescript
const patient: Patient = {
  id: "patient001",
  name: "赵明",
  birthday: "1985-03-15",
  phone: "138****1234",
  gender: "男"
};
```

---

### 2.3 Question（问题模型）

问诊问题数据模型，记录患者与医生之间的问答内容。

#### TypeScript 接口定义

```typescript
export interface Question {
  /** 问题唯一标识 */
  id: string;
  /** 提问患者ID */
  patientId: string;
  /** 提问患者姓名 */
  patientName: string;
  /** 指定医生ID */
  doctorId: string;
  /** 指定医生姓名 */
  doctorName: string;
  /** 问题内容 */
  question: string;
  /** 提交时间（ISO 8601格式） */
  submitTime: string;
  /** 问题状态 */
  status: 'pending' | 'answered';
  /** 回答内容 */
  answer: string | null;
  /** 回答时间（ISO 8601格式） */
  answerTime: string | null;
}
```

#### 字段详细说明

| 字段名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| `id` | `string` | 是 | 问题唯一标识符 | `"q001"` |
| `patientId` | `string` | 是 | 提问患者ID | `"patient001"` |
| `patientName` | `string` | 是 | 提问患者姓名 | `"赵明"` |
| `doctorId` | `string` | 是 | 指定医生ID | `"doc001"` |
| `doctorName` | `string` | 是 | 指定医生姓名 | `"张伟医生"` |
| `question` | `string` | 是 | 问题内容 | `"最近总是感觉胸闷气短..."` |
| `submitTime` | `string` | 是 | 提交时间 | `"2025-11-02T09:30:00"` |
| `status` | `'pending' \| 'answered'` | 是 | 问题状态 | `"pending"`、`"answered"` |
| `answer` | `string \| null` | 是 | 回答内容 | `"根据您的描述..."` 或 `null` |
| `answerTime` | `string \| null` | 是 | 回答时间 | `"2025-11-02T09:45:00"` 或 `null` |

#### 状态说明

| 状态值 | 说明 | 场景 |
|--------|------|------|
| `pending` | 待回复 | 患者已提交问题，医生尚未回答 |
| `answered` | 已回复 | 医生已回答问题 |

#### 示例数据

```typescript
// 待回复问题
const pendingQuestion: Question = {
  id: "q002",
  patientId: "patient002",
  patientName: "孙丽",
  doctorId: "doc002",
  doctorName: "李娜医生",
  question: "孩子5岁,最近总是咳嗽,晚上更严重,需要吃什么药?",
  submitTime: "2025-11-02T10:15:00",
  status: "pending",
  answer: null,
  answerTime: null
};

// 已回复问题
const answeredQuestion: Question = {
  id: "q001",
  patientId: "patient001",
  patientName: "赵明",
  doctorId: "doc001",
  doctorName: "张伟医生",
  question: "最近总是感觉胸闷气短,特别是爬楼梯的时候,这是什么原因?",
  submitTime: "2025-11-02T09:30:00",
  status: "answered",
  answer: "根据您的描述,可能是心脏功能问题。建议您做个心电图和心脏彩超检查...",
  answerTime: "2025-11-02T09:45:00"
};
```

---

### 2.4 State（全局状态）

Vue Reactive 全局状态对象，管理应用的所有数据状态。

#### TypeScript 接口定义

```typescript
interface State {
  /** 医生列表 */
  doctors: Doctor[];
  /** 患者列表 */
  patients: Patient[];
  /** 问题列表 */
  questions: Question[];
  /** 当前登录医生 */
  currentDoctor: Doctor | null;
  /** 当前登录患者 */
  currentPatient: Patient | null;
}
```

#### 字段详细说明

| 字段名 | 类型 | 初始值 | 说明 |
|--------|------|--------|------|
| `doctors` | `Doctor[]` | `[]` | 所有医生数据列表 |
| `patients` | `Patient[]` | `[]` | 所有患者数据列表 |
| `questions` | `Question[]` | `[]` | 所有问题数据列表 |
| `currentDoctor` | `Doctor \| null` | `null` | 当前登录的医生对象 |
| `currentPatient` | `Patient \| null` | `null` | 当前登录的患者对象 |

#### 状态初始化

```typescript
const state = reactive<State>({
  doctors: doctorData as Doctor[],      // 从 JSON 文件加载
  patients: patientData as Patient[],    // 从 JSON 文件加载
  questions: questionData as Question[], // 从 JSON 文件加载
  currentDoctor: null,                   // 初始未登录
  currentPatient: null,                  // 初始未登录
});
```

---

## 三、数据操作 API

### 3.1 Store 对象方法

Store 对象提供了对数据状态的统一操作方法。

#### 认证相关方法

```typescript
/**
 * 医生登录
 * @param username - 用户名
 * @param password - 密码
 * @returns 登录成功返回 Doctor 对象，失败返回 null
 */
loginDoctor(username: string, password: string): Doctor | null

/**
 * 医生登出
 */
logoutDoctor(): void

/**
 * 患者身份验证/注册
 * @param name - 患者姓名
 * @param birthday - 出生日期
 * @returns Patient 对象（已存在则返回已有，否则创建新患者）
 */
verifyPatient(name: string, birthday: string): Patient

/**
 * 患者登出
 */
logoutPatient(): void
```

#### 查询方法

```typescript
/**
 * 根据医生ID获取问题列表
 * @param doctorId - 医生ID
 * @returns 该医生的所有问题列表
 */
getQuestionsByDoctor(doctorId: string): Question[]

/**
 * 根据患者ID获取问题列表
 * @param patientId - 患者ID
 * @returns 该患者的所有问题列表
 */
getQuestionsByPatient(patientId: string): Question[]

/**
 * 根据用户名获取医生信息
 * @param username - 用户名
 * @returns Doctor 对象或 undefined
 */
getDoctorByUsername(username: string): Doctor | undefined

/**
 * 获取在线医生列表
 * @returns isActive 为 true 的医生列表
 */
getActiveDoctors(): Doctor[]

/**
 * 获取统计数据
 * @returns 包含各项统计指标的对象
 */
getStatistics(): {
  totalDoctors: number;      // 医生总数
  totalQuestions: number;    // 问题总数
  activeSessions: number;    // 待响应问题数
  totalSessions: number;     // 在线医生数
}
```

#### 数据修改方法

```typescript
/**
 * 添加新问题
 * @param question - 问题数据（不含 id、submitTime、status、answer、answerTime）
 * @returns 创建成功的 Question 对象
 */
addQuestion(
  question: Omit<Question, 'id' | 'submitTime' | 'status' | 'answer' | 'answerTime'>
): Question

/**
 * 回答问题
 * @param questionId - 问题ID
 * @param answer - 回答内容
 */
answerQuestion(questionId: string, answer: string): void

/**
 * 标记问题为已解答（口述解答）
 * @param questionId - 问题ID
 */
markQuestionAsAnswered(questionId: string): void
```

---

## 四、数据文件结构

### 4.1 数据文件位置

```
src/
└── data/
    ├── doctor-user-list.json    # 医生初始数据
    ├── patient-user.json        # 患者初始数据
    └── question-list.json       # 问题初始数据
```

### 4.2 doctor-user-list.json

```json
[
  {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "password": "123456",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "avatar": "https://images.pexels.com/photos/...",
    "experience": "15年临床经验",
    "specialties": ["高血压", "冠心病", "心律失常"],
    "isActive": true
  }
]
```

### 4.3 patient-user.json

```json
[
  {
    "id": "patient001",
    "name": "赵明",
    "birthday": "1985-03-15",
    "phone": "138****1234",
    "gender": "男"
  }
]
```

### 4.4 question-list.json

```json
[
  {
    "id": "q001",
    "patientId": "patient001",
    "patientName": "赵明",
    "doctorId": "doc001",
    "doctorName": "张伟医生",
    "question": "最近总是感觉胸闷气短...",
    "submitTime": "2025-11-02T09:30:00",
    "status": "answered",
    "answer": "根据您的描述...",
    "answerTime": "2025-11-02T09:45:00"
  }
]
```

---

## 五、数据流向

### 5.1 初始化流程

```
应用启动
    │
    ▼
加载 JSON 数据文件
    │
    ├── doctor-user-list.json ──► state.doctors
    ├── patient-user.json ──────► state.patients
    └── question-list.json ─────► state.questions
    │
    ▼
Vue Reactive 包装
    │
    ▼
组件通过 Store 访问数据
```

### 5.2 数据操作流向

```
用户操作（点击、提交）
    │
    ▼
调用 Store 方法
    │
    ├── loginDoctor() ────► 更新 state.currentDoctor
    ├── verifyPatient() ──► 更新 state.currentPatient
    ├── addQuestion() ────► state.questions.push()
    ├── answerQuestion() ─► 修改 question 属性
    └── ...
    │
    ▼
Vue 响应式更新
    │
    ▼
UI 自动刷新
```

---

## 六、使用示例

### 6.1 在组件中使用数据模型

```vue
<script setup lang="ts">
import { computed } from 'vue';
import { store, Doctor, Question } from '@/store';

// 获取在线医生列表
const activeDoctors = computed<Doctor[]>(() => {
  return store.getActiveDoctors();
});

// 获取当前医生的待处理问题
const pendingQuestions = computed<Question[]>(() => {
  if (!store.state.currentDoctor) return [];
  return store.getQuestionsByDoctor(store.state.currentDoctor.id)
    .filter(q => q.status === 'pending');
});

// 获取统计数据
const stats = computed(() => store.getStatistics());
</script>
```

### 6.2 修改数据示例

```typescript
import { store } from '@/store';

// 医生登录
const doctor = store.loginDoctor('dr-zhang-wei', '123456');
if (doctor) {
  console.log('登录成功:', doctor.name);
}

// 患者验证
const patient = store.verifyPatient('赵明', '1985-03-15');

// 提交问题
const newQuestion = store.addQuestion({
  patientId: patient.id,
  patientName: patient.name,
  doctorId: 'doc001',
  doctorName: '张伟医生',
  question: '最近总是感觉胸闷...'
});

// 回答问题
store.answerQuestion('q001', '根据您的描述，可能是...');
```

---

## 七、类型导出

### 7.1 导出类型

```typescript
// store/index.ts 中导出
export interface Doctor { /* ... */ }
export interface Patient { /* ... */ }
export interface Question { /* ... */ }
```

### 7.2 在其他文件中使用

```typescript
// 在组件中导入类型
import { store, Doctor, Patient, Question } from '@/store';

// 使用类型定义变量
const currentDoctor: Doctor | null = store.state.currentDoctor;
const myQuestions: Question[] = store.getQuestionsByPatient('patient001');
```

---

## 八、注意事项

### 8.1 数据持久化说明

⚠️ **当前项目使用内存存储，数据不会持久化：**

- 所有数据存储在 Vue Reactive State 中
- 页面刷新后数据会重置为初始 JSON 文件内容
- 后续可接入后端 API 和数据库实现持久化

### 8.2 ID 生成规则

| 类型 | 前缀 | 示例 |
|------|------|------|
| 医生 | `doc` | `doc001`, `doc002` |
| 患者 | `patient` | `patient001`, `patient002` |
| 问题 | `q` | `q001`, `q002` |
| 动态生成 | `前缀 + 时间戳` | `patient1699000000000` |

### 8.3 数据验证

当前版本的数据验证：
- 登录时验证用户名和密码匹配
- 提交问题时验证必填字段
- 患者验证时根据姓名+生日匹配

建议后续添加：
- 邮箱格式验证
- 手机号格式验证
- 日期格式验证

---

## 九、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2025-11-03 | 初始版本，定义 Doctor、Patient、Question 三个核心模型 |

---

## 附录

### A. 模型对照表

| 前端模型 | 对应后端实体 | 说明 |
|----------|--------------|------|
| `Doctor` | `User` + `UserProfile` | 医生用户数据 |
| `Patient` | `User` | 患者用户数据 |
| `Question` | `Question` + `Answer` | 问诊问题数据 |

### B. 扩展建议

未来可考虑扩展的数据模型：

```typescript
// 科室模型
interface Department {
  id: string;
  name: string;
  description: string;
  icon: string;
}

// 消息模型
interface Message {
  id: string;
  senderId: string;
  receiverId: string;
  content: string;
  type: 'text' | 'image' | 'voice';
  createTime: string;
}

// 统计数据模型
interface Statistics {
  date: string;
  newUsers: number;
  newQuestions: number;
  activeDoctors: number;
}
```
