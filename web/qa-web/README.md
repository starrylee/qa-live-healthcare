# QA Live Healthcare 前端项目知识库

## 项目概述

QA Live Healthcare 是一个在线医疗问诊平台的前端应用，基于 Vue 3 + TypeScript + Vite 构建，使用 Ant Design Vue 作为 UI 组件库。

- **项目名称**: qa-web
- **技术栈**: Vue 3 + TypeScript + Vite + Ant Design Vue
- **构建工具**: Vite 5.4.8
- **包管理器**: npm

---

## 一、启动点与启动方式

### 1.1 启动点识别

| 启动点 | 文件路径 | 说明 |
|--------|----------|------|
| 应用入口 | `src/main.ts` | Vue 应用初始化入口 |
| 根组件 | `src/App.vue` | 应用根组件，包含布局框架 |
| 开发服务器 | `vite.config.ts` | Vite 开发服务器配置 |
| 应用管理脚本 | `app-management.sh` | 生产环境应用生命周期管理脚本 |

### 1.2 启动方式

#### 开发环境启动

```bash
# 方式1: 使用 npm 脚本
npm run dev

# 方式2: 使用应用管理脚本
./app-management.sh start
```

**启动流程**:
1. Vite 读取 `vite.config.ts` 配置
2. 启动开发服务器，默认端口 5173
3. 加载 `index.html` → 执行 `src/main.ts`
4. 创建 Vue 应用实例，挂载到 `#app` 元素

#### 生产环境构建

```bash
# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

#### 应用管理脚本命令

| 命令 | 功能 | 说明 |
|------|------|------|
| `./app-management.sh start` | 后台启动应用 | 使用 nohup 在后台运行 |
| `./app-management.sh stop` | 停止应用 | 读取 PID 文件终止进程 |
| `./app-management.sh restart` | 重启应用 | 先停止后启动 |
| `./app-management.sh status` | 查看状态 | 显示运行状态和 PID |
| `./app-management.sh logs` | 查看日志 | 实时跟踪日志输出 |

---

## 二、代码层级与模块结构

### 2.1 项目目录结构

```
qa-web/
├── index.html              # HTML 入口文件
├── package.json            # 项目依赖和脚本配置
├── vite.config.ts          # Vite 构建配置
├── tsconfig.json           # TypeScript 配置
├── tsconfig.app.json       # 应用 TypeScript 配置
├── tsconfig.node.json      # Node 环境 TypeScript 配置
├── app-management.sh       # 应用管理脚本
├── public/                 # 静态资源目录
├── docs/                   # 项目文档
└── src/
    ├── main.ts             # 应用入口
    ├── App.vue             # 根组件
    ├── style.css           # 全局样式
    ├── vite-env.d.ts       # Vite 环境类型声明
    ├── assets/             # 资源文件
    ├── components/         # 公共组件
    │   ├── AppHeader.vue   # 页面头部导航
    │   ├── AppFooter.vue   # 页面底部
    │   └── HelloWorld.vue  # 示例组件
    ├── views/              # 页面视图组件
    │   ├── Home.vue        # 首页
    │   ├── Doctors.vue     # 医生列表页
    │   ├── Consultation.vue # 问诊页面
    │   ├── DoctorLogin.vue # 医生登录页
    │   ├── DoctorRoom.vue  # 医生诊室页
    │   └── About.vue       # 关于我们页
    ├── router/             # 路由配置
    │   └── index.ts        # 路由定义
    ├── store/              # 状态管理
    │   └── index.ts        # 全局状态存储
    └── data/               # 本地数据
        ├── doctor-user-list.json   # 医生数据
        ├── patient-user.json       # 患者数据
        └── question-list.json      # 问题数据
```

### 2.2 模块功能说明

#### 核心模块

| 模块 | 功能 | 关键文件 |
|------|------|----------|
| **入口模块** | Vue 应用初始化 | `main.ts`, `App.vue` |
| **路由模块** | 页面导航和路由管理 | `router/index.ts` |
| **状态模块** | 全局状态管理和数据操作 | `store/index.ts` |
| **视图模块** | 页面级组件 | `views/*.vue` |
| **组件模块** | 可复用 UI 组件 | `components/*.vue` |
| **数据模块** | 本地静态数据 | `data/*.json` |

#### 模块依赖关系

```
main.ts
  ├── App.vue
  │     ├── AppHeader.vue (导航组件)
  │     ├── RouterView (动态路由视图)
  │     │     ├── Home.vue
  │     │     ├── Doctors.vue
  │     │     ├── Consultation.vue
  │     │     ├── DoctorLogin.vue
  │     │     ├── DoctorRoom.vue
  │     │     └── About.vue
  │     └── AppFooter.vue (底部组件)
  ├── router/index.ts (路由配置)
  └── store/index.ts (状态管理)
```

---

## 三、接口分析

### 3.1 路由接口（页面级）

| 路由路径 | 组件 | 功能说明 | 访问权限 |
|----------|------|----------|----------|
| `/` | `Home.vue` | 首页，展示平台信息和在线医生 | 公开 |
| `/doctors` | `Doctors.vue` | 医生列表页，展示所有医生 | 公开 |
| `/consultation` | `Consultation.vue` | 问诊页面，患者提问 | 需患者登录 |
| `/consultation/:doctorUsername` | `Consultation.vue` | 指定医生的问诊页面 | 需患者登录 |
| `/doctor/login` | `DoctorLogin.vue` | 医生登录页面 | 公开 |
| `/doctor/room/:username` | `DoctorRoom.vue` | 医生诊室管理页面 | 需医生登录 |
| `/about` | `About.vue` | 关于我们页面 | 公开 |

### 3.2 状态管理接口

**Store 提供的核心方法**:

```typescript
// 医生认证
store.loginDoctor(username: string, password: string): Doctor | null
store.logoutDoctor(): void

// 患者认证
store.verifyPatient(name: string, birthday: string): Patient
store.logoutPatient(): void

// 问题管理
store.addQuestion(question: Partial<Question>): Question
store.answerQuestion(questionId: string, answer: string): void
store.markQuestionAsAnswered(questionId: string): void

// 数据查询
store.getQuestionsByDoctor(doctorId: string): Question[]
store.getQuestionsByPatient(patientId: string): Question[]
store.getDoctorByUsername(username: string): Doctor | undefined
store.getActiveDoctors(): Doctor[]
store.getStatistics(): Statistics
```

### 3.3 数据结构定义

```typescript
// 医生
interface Doctor {
  id: string;
  username: string;
  password: string;
  name: string;
  title: string;
  department: string;
  avatar: string;
  experience: string;
  specialties: string[];
  isActive: boolean;
}

// 患者
interface Patient {
  id: string;
  name: string;
  birthday: string;
  phone: string;
  gender: string;
}

// 问题
interface Question {
  id: string;
  patientId: string;
  patientName: string;
  doctorId: string;
  doctorName: string;
  question: string;
  submitTime: string;
  status: 'pending' | 'answered';
  answer: string | null;
  answerTime: string | null;
}
```

---

## 四、功能与业务流程

### 4.1 核心功能模块

#### 4.1.1 首页功能 (Home.vue)

**功能描述**:
- 展示平台介绍和特色
- 显示统计数据（医生数、问题数、待响应数、在线诊室数）
- 展示在线医生诊室卡片
- 提供快速入口按钮

**调用链**:
```
Home.vue
  ├── store.getStatistics()      # 获取统计数据
  └── store.getActiveDoctors()   # 获取在线医生列表
```

#### 4.1.2 医生列表功能 (Doctors.vue)

**功能描述**:
- 展示所有医生信息
- 显示医生在线状态
- 支持点击进入医生诊室

**调用链**:
```
Doctors.vue
  └── store.state.doctors        # 获取所有医生数据
```

#### 4.1.3 问诊功能 (Consultation.vue)

**功能描述**:
- 患者身份验证（姓名+生日）
- 展示患者历史问题列表
- 提交新问题
- 查看医生回复

**业务流程**:
1. 未登录患者 → 显示身份验证表单
2. 验证成功后 → 显示患者问诊界面
3. 可查看历史问题和提交新问题
4. 新问题提交到 store，关联当前患者和选定医生

**调用链**:
```
Consultation.vue
  ├── store.state.currentPatient        # 获取当前患者
  ├── store.verifyPatient()             # 验证/创建患者
  ├── store.getQuestionsByPatient()     # 获取患者问题
  └── store.addQuestion()               # 提交新问题
```

#### 4.1.4 医生登录功能 (DoctorLogin.vue)

**功能描述**:
- 医生账号密码登录
- 登录成功后跳转到医生诊室

**业务流程**:
1. 输入用户名和密码
2. 调用 store.loginDoctor() 验证
3. 验证成功 → 跳转 /doctor/room/:username
4. 验证失败 → 显示错误提示

**调用链**:
```
DoctorLogin.vue
  └── store.loginDoctor(username, password)  # 医生登录验证
```

#### 4.1.5 医生诊室功能 (DoctorRoom.vue)

**功能描述**:
- 医生个人信息展示
- 诊室链接复制
- 待响应问题列表管理
- 文字回复问题
- 标记问题为已解答
- 已解答问题历史查看

**业务流程**:
1. 检查登录状态（onMounted）
2. 展示待响应问题列表
3. 医生可文字回复或标记已解答
4. 展示已解答问题历史

**调用链**:
```
DoctorRoom.vue
  ├── store.state.currentDoctor         # 获取当前医生
  ├── store.getQuestionsByDoctor()      # 获取医生问题列表
  ├── store.answerQuestion()            # 回复问题
  └── store.markQuestionAsAnswered()    # 标记已解答
```

### 4.2 功能依赖关系图

```
┌─────────────────────────────────────────────────────────────┐
│                        用户访问                              │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┼──────────────┐
        ▼              ▼              ▼
   ┌─────────┐   ┌─────────┐   ┌──────────┐
   │  患者   │   │  医生   │   │  访客    │
   └────┬────┘   └────┬────┘   └────┬─────┘
        │              │              │
        ▼              ▼              ▼
  ┌──────────┐   ┌──────────┐   ┌──────────┐
  │Consultation│  │DoctorLogin│   │  Home    │
  │  问诊页   │   │  登录页   │   │  首页    │
  └────┬─────┘   └────┬─────┘   └────┬─────┘
       │              │              │
       ▼              ▼              ▼
  ┌──────────┐   ┌──────────┐   ┌──────────┐
  │ 患者验证  │   │ 登录验证  │   │ 展示数据  │
  │(store)   │   │(store)   │   │(store)   │
  └────┬─────┘   └────┬─────┘   └────┬─────┘
       │              │              │
       ▼              ▼              ▼
  ┌──────────┐   ┌──────────┐   ┌──────────┐
  │提交/查看 │   │DoctorRoom│   │Doctors/  │
  │  问题    │   │  诊室页   │   │  About   │
  └──────────┘   └──────────┘   └──────────┘
```

---

## 五、关联代码与调用链

### 5.1 患者问诊流程调用链

```
用户访问 /consultation/:doctorUsername
  │
  ▼
Consultation.vue (onMounted)
  ├── 从 route.params 获取 doctorUsername
  ├── store.getDoctorByUsername(username) 查找医生
  └── 设置 selectedDoctor
  │
  ▼ (患者未登录)
显示身份验证表单
  │
  ▼ (用户提交表单)
verifyPatient(name, birthday)
  ├── store.verifyPatient() 
  │     ├── 查找已存在患者
  │     ├── 不存在则创建新患者
  │     └── 设置 store.state.currentPatient
  │
  ▼ (患者已登录)
显示患者问诊界面
  ├── store.getQuestionsByPatient(patientId) 获取历史问题
  └── 展示问题列表
  │
  ▼ (用户点击提交问题)
显示问题提交弹窗
  │
  ▼ (用户确认提交)
submitQuestion()
  ├── 验证表单
  └── store.addQuestion({...})
        └── 创建新问题对象，添加到 store.state.questions
```

### 5.2 医生登录流程调用链

```
用户访问 /doctor/login
  │
  ▼
DoctorLogin.vue
  ├── 显示登录表单
  │
  ▼ (用户提交表单)
onFinish()
  ├── store.loginDoctor(username, password)
  │     ├── 在 store.state.doctors 中查找匹配
  │     ├── 匹配成功: 设置 store.state.currentDoctor
  │     └── 返回医生对象
  ├── 登录成功: router.push(`/doctor/room/${doctor.username}`)
  └── 登录失败: 显示错误提示
```

### 5.3 医生回复问题调用链

```
用户访问 /doctor/room/:username (需已登录)
  │
  ▼
DoctorRoom.vue (onMounted)
  ├── 检查 store.state.currentDoctor
  ├── 未登录则跳转 /doctor/login
  └── 已登录则继续
  │
  ▼
计算属性 pendingQuestions
  └── store.getQuestionsByDoctor(doctorId)
        └── 过滤 store.state.questions 获取待响应问题
  │
  ▼ (医生点击"文字回复")
showAnswerModal(question)
  └── 显示回复弹窗
  │
  ▼ (医生输入回复并提交)
submitAnswer()
  ├── 验证回复内容
  └── store.answerQuestion(questionId, answerText)
        ├── 查找问题
        ├── 设置 question.status = 'answered'
        ├── 设置 question.answer
        └── 设置 question.answerTime
```

### 5.4 数据流图

```
┌────────────────────────────────────────────────────────────┐
│                        Data Layer                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │ doctor-user-list│  │ patient-user    │  │question-list│ │
│  │    .json        │  │    .json        │  │   .json     │ │
│  └────────┬────────┘  └────────┬────────┘  └──────┬──────┘ │
└───────────┼────────────────────┼──────────────────┼────────┘
            │                    │                  │
            ▼                    ▼                  ▼
┌────────────────────────────────────────────────────────────┐
│                        Store Layer                         │
│                    (store/index.ts)                        │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  state: { doctors[], patients[], questions[],       │   │
│  │          currentDoctor, currentPatient }            │   │
│  └─────────────────────────────────────────────────────┘   │
│                           │                                │
│  ┌────────────────────────┼────────────────────────┐       │
│  │                        ▼                        │       │
│  │  loginDoctor()  verifyPatient()  addQuestion()  │       │
│  │  logoutDoctor() logoutPatient() answerQuestion()│       │
│  └────────────────────────┬────────────────────────┘       │
└───────────────────────────┼────────────────────────────────┘
                            │
            ┌───────────────┼───────────────┐
            ▼               ▼               ▼
     ┌──────────┐    ┌──────────┐    ┌──────────┐
     │Home.vue  │    │Consultation│   │DoctorRoom│
     │Doctors   │    │   .vue    │    │  .vue   │
     │ .vue     │    │DoctorLogin│    │         │
     │About.vue │    │   .vue    │    │         │
     └──────────┘    └──────────┘    └──────────┘
```

---

## 六、关键配置文件说明

### 6.1 package.json

```json
{
  "scripts": {
    "dev": "vite",              // 开发服务器
    "build": "vue-tsc -b && vite build",  // 生产构建
    "preview": "vite preview",  // 预览生产构建
    "app:start": "./app-management.sh start",
    "app:stop": "./app-management.sh stop",
    "app:restart": "./app-management.sh restart",
    "app:status": "./app-management.sh status",
    "app:logs": "./app-management.sh logs"
  },
  "dependencies": {
    "ant-design-vue": "^4.2.6",  // UI 组件库
    "dayjs": "^1.11.19",         // 日期处理
    "vue": "^3.5.10",            // Vue 框架
    "vue-router": "^4.6.3"       // 路由管理
  }
}
```

### 6.2 vite.config.ts

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],  // 使用 Vue 插件
})
```

### 6.3 tsconfig.json

TypeScript 配置文件，定义编译选项和包含文件。

---

## 七、开发注意事项

### 7.1 数据存储说明

本项目使用内存存储（基于 JSON 文件加载），数据在页面刷新后会重置：
- `doctor-user-list.json` - 预置医生数据
- `patient-user.json` - 预置患者数据
- `question-list.json` - 预置问题数据

**注意**: 所有数据变更仅保存在内存中，刷新页面后会恢复初始状态。

### 7.2 测试账号

- **医生测试账号**: 
  - 用户名: `dr-zhang-wei`
  - 密码: `123456`

### 7.3 关键路径说明

| 路径 | 说明 |
|------|------|
| `src/main.ts` | 应用入口，初始化 Vue 和插件 |
| `src/store/index.ts` | 全局状态管理，核心业务逻辑 |
| `src/router/index.ts` | 路由配置，页面导航定义 |
| `src/App.vue` | 根组件，整体布局框架 |

---

## 八、技术依赖说明

### 8.1 主要依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| vue | 3.5.10 | 前端框架 |
| vue-router | 4.6.3 | 路由管理 |
| ant-design-vue | 4.2.6 | UI 组件库 |
| dayjs | 1.11.19 | 日期处理 |
| vite | 5.4.8 | 构建工具 |
| typescript | 5.5.3 | 类型系统 |

### 8.2 依赖关系图

```
qa-web
  ├── Vue 3 (核心框架)
  │     ├── Reactivity System (响应式系统)
  │     ├── Compiler (模板编译)
  │     └── Runtime (运行时)
  ├── Vue Router (路由)
  │     └── 页面导航管理
  ├── Ant Design Vue (UI)
  │     └── 组件库
  ├── Dayjs (工具)
  │     └── 日期格式化
  └── Vite (构建)
        ├── Dev Server
        └── Build Tool
```
