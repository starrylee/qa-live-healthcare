# QA Healthcare 项目知识库文档

> 医疗问答系统 - 基于微服务架构的前后端分离项目

---

## 目录

- [一、启动点与启动方式](#一启动点与启动方式)
- [二、代码层级与模块结构](#二代码层级与模块结构)
- [三、接口分析](#三接口分析)
- [四、功能与业务流程](#四功能与业务流程)
- [五、关联代码与调用链](#五关联代码与调用链)
- [六、技术栈与依赖](#六技术栈与依赖)

---

## 一、启动点与启动方式

### 1.1 项目启动入口总览

本项目采用**前后端分离**架构，包含多个独立启动的服务组件：

| 组件类型 | 组件名称 | 端口 | 启动入口 | 启动命令 |
|---------|---------|------|----------|----------|
| 前端应用 | qa-web | 5173 | `web/qa-web/src/main.ts` | `npm run dev:web` |
| 后端微服务 | qa-service-user | 8080 | `QaServiceUserApplication.java` | `npm run dev:user` |
| 后端微服务 | qa-service-question | 8081 | `QaServiceQuestionApplication.java` | `npm run dev:question` |

### 1.2 启动方式详解

#### 1.2.1 统一启动（推荐）

```bash
# 同时启动前端和所有后端服务
npm run dev
```

该命令使用 `concurrently` 并发执行，会同时启动：
- 前端开发服务器 (Vite)
- 用户管理服务 (Spring Boot)
- 问题管理服务 (Spring Boot)

#### 1.2.2 前端启动点

**入口文件**: `web/qa-web/src/main.ts`

```typescript
// 启动流程
import { createApp } from 'vue'
import Antd from 'ant-design-vue'
import App from './App.vue'
import router from './router'
import 'ant-design-vue/dist/reset.css'
import './style.css'

const app = createApp(App)
app.use(Antd)        // 注册 Ant Design Vue
app.use(router)      // 注册 Vue Router
app.mount('#app')    // 挂载到 DOM
```

**启动命令**:
```bash
npm run dev:web
# 或
cd web/qa-web && npm run dev
```

**启动流程**:
1. Vite 开发服务器初始化
2. 加载 `index.html` 入口页面
3. 执行 `main.ts` 创建 Vue 应用实例
4. 注册全局组件和插件 (Ant Design Vue, Vue Router)
5. 挂载根组件 `App.vue` 到 `#app` 节点
6. 路由解析并渲染对应页面组件

#### 1.2.3 后端启动点

**用户管理服务** - `qa-service-user`

| 项目 | 详情 |
|-----|------|
| 启动类 | `server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/QaServiceUserApplication.java` |
| 包名 | `com.leansofx.qaserviceuser` |
| 端口 | 8080 |
| 启动命令 | `npm run dev:user` 或 `cd server/qa-service-user && ./mvnw spring-boot:run` |

**问题管理服务** - `qa-service-question`

| 项目 | 详情 |
|-----|------|
| 启动类 | `server/qa-service-question/src/main/java/com/leansofx/qaservicequestion/QaServiceQuestionApplication.java` |
| 包名 | `com.leansofx.qaservicequestion` |
| 端口 | 8081 |
| 启动命令 | `npm run dev:question` 或 `cd server/qa-service-question && ./mvnw spring-boot:run` |

**启动流程**:
1. Maven Wrapper (`mvnw`) 下载并配置 Maven 环境
2. 解析 `pom.xml` 下载依赖
3. 编译 Java 源代码
4. Spring Boot 自动配置（扫描 `@Configuration`, `@Component` 等）
5. 内嵌 Tomcat 服务器启动
6. 应用就绪，监听指定端口

### 1.3 环境要求

| 工具 | 版本要求 | 当前版本 |
|-----|---------|---------|
| Node.js | >= 16.0.0 | v22.18.0 |
| npm | >= 8.0.0 | 10.9.3 |
| Java | 17 | OpenJDK 17.0.14+7 |
| Maven | >= 3.6.0 | 内置 (mvnw) |

---

## 二、代码层级与模块结构

### 2.1 项目完整目录树

```
📦 qa-live-healthcare/
│
├── 📁 _TRAINING_ASSETS/                      # 训练资源文件
│   └── 📄 *.zip                              # 压缩资源包
│
├── 📁 server/                                # 🔧 后端服务目录
│   │
│   ├── 📁 qa-service-user/                   # 👤 用户管理微服务 (端口:8080)
│   │   │
│   │   ├── 📄 pom.xml                        # Maven 依赖配置
│   │   ├── 📄 mvnw                          # Maven Wrapper (Unix/Linux)
│   │   ├── 📄 mvnw.cmd                      # Maven Wrapper (Windows)
│   │   ├── 📄 README.md                     # 服务说明文档
│   │   ├── 📄 start.sh                      # 启动脚本
│   │   ├── 📄 stop.sh                       # 停止脚本
│   │   ├── 📄 restart.sh                    # 重启脚本
│   │   ├── 📄 status.sh                     # 状态查看脚本
│   │   │
│   │   ├── 📁 docs/                         # 📚 项目文档
│   │   │   ├── 📄 project-structure.md      # 项目结构规范
│   │   │   └── 📄 api.md                    # API 接口文档
│   │   │
│   │   └── 📁 src/                          # 源代码目录
│   │       │
│   │       ├── 📁 main/                     # 主要源代码
│   │       │   │
│   │       │   ├── 📁 java/com/leansofx/qaserviceuser/
│   │       │   │   │
│   │       │   │   ├── 📄 QaServiceUserApplication.java  # ⭐ Spring Boot 启动类
│   │       │   │   │
│   │       │   │   ├── 📁 config/           # ⚙️ 配置层
│   │       │   │   │   └── 📄 CorsConfig.java           # CORS 跨域配置
│   │       │   │   │
│   │       │   │   ├── 📁 controller/       # 🎮 控制器层
│   │       │   │   │   └── 📄 TestController.java       # 测试接口控制器
│   │       │   │   │
│   │       │   │   ├── 📁 service/          # 💼 服务层 (待开发)
│   │       │   │   │   └── impl/            # 服务实现
│   │       │   │   │
│   │       │   │   ├── 📁 repository/       # 💾 数据访问层 (待开发)
│   │       │   │   │
│   │       │   │   ├── 📁 entity/           # 📊 实体类 (待开发)
│   │       │   │   │
│   │       │   │   ├── 📁 dto/              # 📦 数据传输对象 (待开发)
│   │       │   │   │   ├── request/         # 请求 DTO
│   │       │   │   │   └── response/        # 响应 DTO
│   │       │   │   │
│   │       │   │   ├── 📁 exception/        # ❌ 自定义异常 (待开发)
│   │       │   │   ├── 📁 enums/            # 📋 枚举类 (待开发)
│   │       │   │   ├── 📁 util/             # 🔧 工具类 (待开发)
│   │       │   │   ├── 📁 constant/         # 📌 常量类 (待开发)
│   │       │   │   ├── 📁 security/         # 🔐 安全相关 (待开发)
│   │       │   │   ├── 📁 aspect/           # 🎯 切面编程 (待开发)
│   │       │   │   └── 📁 validation/       # ✅ 自定义验证 (待开发)
│   │       │   │
│   │       │   └── 📁 resources/            # 资源文件
│   │       │       └── 📄 application.properties  # 应用配置文件
│   │       │
│   │       └── 📁 test/                     # 🧪 测试代码
│   │           └── 📁 java/com/leansofx/qaserviceuser/
│   │               └── 📄 QaServiceUserApplicationTests.java
│   │
│   ├── 📁 qa-service-question/               # ❓ 问题管理微服务 (端口:8081)
│   │   │
│   │   ├── 📄 pom.xml                        # Maven 依赖配置
│   │   ├── 📄 mvnw                          # Maven Wrapper (Unix/Linux)
│   │   ├── 📄 mvnw.cmd                      # Maven Wrapper (Windows)
│   │   │
│   │   └── 📁 src/                          # 源代码目录
│   │       │
│   │       ├── 📁 main/                     # 主要源代码
│   │       │   │
│   │       │   ├── 📁 java/com/leansofx/qaservicequestion/
│   │       │   │   └── 📄 QaServiceQuestionApplication.java  # ⭐ Spring Boot 启动类
│   │       │   │
│   │       │   └── 📁 resources/
│   │       │       └── 📄 application.properties  # 应用配置文件
│   │       │
│   │       └── 📁 test/                     # 🧪 测试代码
│   │           └── 📁 java/com/leansofx/qaservicequestion/
│   │               ├── 📄 QaServiceQuestionApplicationTests.java
│   │               ├── 📄 TestQaServiceQuestionApplication.java
│   │               └── 📄 TestcontainersConfiguration.java  # Testcontainers 配置
│   │
│   └── 📁 qa-service-statistic/              # 📈 统计分析服务 (规划中)
│       └── 📄 .gitkeep                       # 目录占位文件
│
├── 📁 web/                                   # 🌐 前端应用目录
│   │
│   └── 📁 qa-web/                            # 💻 Vue.js 前端应用 (端口:5173)
│       │
│       ├── 📄 index.html                     # 🚪 HTML 入口文件
│       ├── 📄 package.json                   # 📦 项目依赖配置
│       ├── 📄 vite.config.ts                 # ⚡ Vite 构建配置
│       ├── 📄 tsconfig.json                  # 📘 TypeScript 配置
│       ├── 📄 tsconfig.app.json              # TypeScript 应用配置
│       ├── 📄 tsconfig.node.json             # TypeScript Node 配置
│       ├── 📄 README.md                      # 前端项目说明
│       ├── 📄 app-management.sh              # 应用管理脚本
│       │
│       ├── 📁 docs/                          # 📚 前端文档
│       ├── 📁 public/                        # 📁 静态资源目录
│       │
│       └── 📁 src/                           # 🎨 源代码目录
│           │
│           ├── 📄 main.ts                    # ⭐ 应用入口文件
│           ├── 📄 App.vue                    # 🏠 根组件
│           ├── 📄 style.css                  # 🎨 全局样式
│           ├── 📄 vite-env.d.ts              # Vite 类型声明
│           │
│           ├── 📁 assets/                    # 🖼️ 静态资源
│           │   └── 📄 vue.svg                # Vue Logo
│           │
│           ├── 📁 components/                # 🧩 公共组件
│           │   ├── 📄 AppHeader.vue          # 📌 顶部导航栏组件
│           │   ├── 📄 AppFooter.vue          # 📌 底部页脚组件
│           │   └── 📄 HelloWorld.vue         # 示例组件
│           │
│           ├── 📁 views/                     # 📄 页面组件
│           │   ├── 📄 Home.vue               # 🏠 首页
│           │   ├── 📄 Consultation.vue       # 💬 问诊页面
│           │   ├── 📄 Doctors.vue            # 👨‍⚕️ 医生列表页
│           │   ├── 📄 DoctorLogin.vue        # 🔐 医生登录页
│           │   ├── 📄 DoctorRoom.vue         # 🏥 医生诊室页
│           │   └── 📄 About.vue              # ℹ️ 关于我们页
│           │
│           ├── 📁 router/                    # 🔀 路由配置
│           │   └── 📄 index.ts               # 路由定义文件
│           │
│           ├── 📁 store/                     # 🗄️ 状态管理
│           │   └── 📄 index.ts               # 全局状态 (Vue 3 Reactive)
│           │
│           └── 📁 data/                      # 📊 静态数据 (模拟后端)
│               ├── 📄 doctor-user-list.json  # 医生用户数据
│               ├── 📄 patient-user.json      # 患者用户数据
│               └── 📄 question-list.json     # 问题列表数据
│
├── 📄 package.json                           # 📦 根项目配置
└── 📄 README.md                              # 📖 项目文档
```

### 2.2 模块职责详解

#### 2.2.1 后端模块职责

| 模块 | 路径 | 职责 | 端口 | 状态 |
|-----|------|------|------|------|
| **用户管理服务** | `server/qa-service-user/` | 用户注册、登录、权限管理 | 8080 | 🟢 基础框架完成 |
| **问题管理服务** | `server/qa-service-question/` | 医疗问题发布、回答、管理 | 8081 | 🟡 框架搭建中 |
| **统计分析服务** | `server/qa-service-statistic/` | 数据统计、分析、报表 | 待定 | 🔴 规划中 |

#### 2.2.2 后端分层架构职责

```
┌─────────────────────────────────────────────────────────────┐
│                      Controller 层                          │
│         处理 HTTP 请求，参数验证，响应封装                     │
│                    [TestController.java]                    │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                       Service 层                            │
│         业务逻辑处理，事务管理，数据组装                       │
│                     [待开发]                                 │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                     Repository 层                           │
│         数据访问，CRUD 操作，数据查询                         │
│                     [待开发]                                 │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                       Entity 层                             │
│         数据库实体映射，领域模型                              │
│                     [待开发]                                 │
└─────────────────────────────────────────────────────────────┘
```

#### 2.2.3 前端模块职责

| 目录 | 路径 | 职责 | 关键文件 |
|-----|------|------|---------|
| **views/** | `src/views/` | 页面级组件，对应路由 | `Home.vue`, `Consultation.vue` 等 6 个 |
| **components/** | `src/components/` | 可复用公共组件 | `AppHeader.vue`, `AppFooter.vue` |
| **router/** | `src/router/` | 路由配置，URL 映射 | `index.ts` |
| **store/** | `src/store/` | 全局状态管理 | `index.ts` |
| **data/** | `src/data/` | 静态 JSON 数据模拟 | `doctor-user-list.json` 等 3 个 |

### 2.3 前端页面模块详解

| 页面文件 | 路由路径 | 功能描述 | 核心功能 |
|---------|---------|---------|---------|
| `Home.vue` | `/` | 首页 | 平台介绍、统计数据、在线医生展示 |
| `Consultation.vue` | `/consultation` | 问诊页面 | 患者验证、提交问题、查看回复 |
| `Doctors.vue` | `/doctors` | 医生列表 | 展示所有医生信息 |
| `DoctorLogin.vue` | `/doctor/login` | 医生登录 | 医生身份验证 |
| `DoctorRoom.vue` | `/doctor/room/:username` | 医生诊室 | 查看问题、回复患者 |
| `About.vue` | `/about` | 关于我们 | 平台介绍和联系方式 |

### 2.4 模块依赖关系

```
┌─────────────────────────────────────────────────────────────────┐
│                         用户界面层                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │ Home.vue │  │Consult-  │  │ Doctor-  │  │ Doctor-  │        │
│  │          │  │ ation.vue│  │ Login.vue│  │ Room.vue │        │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘        │
└───────┼─────────────┼─────────────┼─────────────┼──────────────┘
        │             │             │             │
        ▼             ▼             ▼             ▼
┌─────────────────────────────────────────────────────────────────┐
│                       公共组件层                                 │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐      │
│  │ AppHeader.vue│    │ AppFooter.vue│    │  Ant Design  │      │
│  └──────────────┘    └──────────────┘    └──────────────┘      │
└─────────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────────┐
│                       状态管理层                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    store/index.ts                        │   │
│  │  • doctors: Doctor[]     • currentDoctor: Doctor         │   │
│  │  • patients: Patient[]   • currentPatient: Patient       │   │
│  │  • questions: Question[]                                  │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────────┐
│                       数据层 (模拟)                              │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │doctor-user-list │  │ patient-user    │  │ question-list   │ │
│  │     .json       │  │    .json        │  │    .json        │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 三、接口分析

### 3.1 前端路由接口

| 路由路径 | 页面组件 | 功能描述 | 权限 |
|---------|---------|---------|------|
| `/` | `Home.vue` | 首页，展示平台介绍、统计数据、在线医生 | 公开 |
| `/consultation` | `Consultation.vue` | 问诊页面，患者身份验证、提交问题 | 患者 |
| `/consultation/:doctorUsername` | `Consultation.vue` | 指定医生的问诊室入口 | 患者 |
| `/doctors` | `Doctors.vue` | 医生列表，展示所有医生信息 | 公开 |
| `/doctor/login` | `DoctorLogin.vue` | 医生登录页面 | 公开 |
| `/doctor/room/:username` | `DoctorRoom.vue` | 医生诊室，查看待回复问题、回复患者 | 医生 |
| `/about` | `About.vue` | 关于我们页面 | 公开 |

### 3.2 后端 REST API 接口

#### 3.2.1 用户管理服务 (qa-service-user:8080)

**测试控制器 - TestController**

| HTTP 方法 | 端点 | 功能 | 请求体 | 响应 |
|----------|------|------|--------|------|
| GET | `/api/test/cors` | 测试 CORS 配置 | - | TestResponse |
| POST | `/api/test/cors` | 测试 CORS 配置 | Map (可选) | TestResponse |
| OPTIONS | `/api/test/cors` | CORS 预检请求 | - | - |

**响应数据结构**:

```json
{
  "message": "CORS configuration is working!",
  "timestamp": 1699000000000,
  "service": "qa-service-user",
  "receivedData": {}
}
```

#### 3.2.2 Actuator 监控端点

| 端点 | 功能 | 访问示例 |
|-----|------|---------|
| `/actuator/health` | 应用健康状态 | `curl http://localhost:8080/actuator/health` |
| `/actuator/info` | 应用信息 | `curl http://localhost:8080/actuator/info` |
| `/actuator/metrics` | 性能指标 | `curl http://localhost:8080/actuator/metrics` |
| `/actuator/env` | 环境变量 | `curl http://localhost:8080/actuator/env` |
| `/actuator/beans` | Spring Beans | `curl http://localhost:8080/actuator/beans` |
| `/actuator/loggers` | 日志配置 | `curl http://localhost:8080/actuator/loggers` |

### 3.3 Store 数据接口

前端状态管理 (`store/index.ts`) 提供以下数据操作接口：

#### 3.3.1 医生相关

| 方法 | 参数 | 返回值 | 功能 |
|-----|------|--------|------|
| `loginDoctor(username, password)` | string, string | Doctor \| null | 医生登录验证 |
| `logoutDoctor()` | - | void | 医生登出 |
| `getDoctorByUsername(username)` | string | Doctor \| undefined | 根据用户名获取医生 |
| `getActiveDoctors()` | - | Doctor[] | 获取所有在线医生 |

#### 3.3.2 患者相关

| 方法 | 参数 | 返回值 | 功能 |
|-----|------|--------|------|
| `verifyPatient(name, birthday)` | string, string | Patient | 患者身份验证/注册 |
| `logoutPatient()` | - | void | 患者登出 |

#### 3.3.3 问题相关

| 方法 | 参数 | 返回值 | 功能 |
|-----|------|--------|------|
| `addQuestion(question)` | Omit<Question, 'id'...> | Question | 添加新问题 |
| `answerQuestion(questionId, answer)` | string, string | void | 回复问题 |
| `getQuestionsByDoctor(doctorId)` | string | Question[] | 获取医生的问题列表 |
| `getQuestionsByPatient(patientId)` | string | Question[] | 获取患者的问题列表 |
| `markQuestionAsAnswered(questionId)` | string | void | 标记问题已口述解答 |

#### 3.3.4 统计相关

| 方法 | 参数 | 返回值 | 功能 |
|-----|------|--------|------|
| `getStatistics()` | - | Statistics | 获取平台统计数据 |

### 3.4 数据模型定义

```typescript
// 医生数据模型
interface Doctor {
  id: string;
  username: string;
  password: string;
  name: string;
  title: string;           // 职称
  department: string;      // 科室
  avatar: string;
  experience: string;      // 临床经验
  specialties: string[];   // 专长
  isActive: boolean;       // 是否在线
}

// 患者数据模型
interface Patient {
  id: string;
  name: string;
  birthday: string;
  phone: string;
  gender: string;
}

// 问题数据模型
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

### 4.1 功能模块概览

```
┌─────────────────────────────────────────────────────────────┐
│                    QA Healthcare 功能架构                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │   首页模块   │    │  问诊模块   │    │  医生模块   │     │
│  │             │    │             │    │             │     │
│  │ • 平台介绍  │    │ • 身份验证  │    │ • 医生列表  │     │
│  │ • 统计展示  │    │ • 提交问题  │    │ • 医生登录  │     │
│  │ • 在线医生  │    │ • 查看回复  │    │ • 诊室管理  │     │
│  └─────────────┘    └─────────────┘    └─────────────┘     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 核心业务流程

#### 4.2.1 患者问诊流程

```
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ 访问首页 │───▶│ 选择医生    │───▶│ 身份验证    │───▶│ 提交问题    │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘
                                                              │
                                                              ▼
┌─────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ 问诊结束 │◀───│ 查看回复    │◀───│ 等待回复    │◀───│ 问题已提交  │
└─────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

**详细步骤**:

1. **访问首页** (`Home.vue`)
   - 查看平台介绍和统计数据
   - 浏览在线医生列表
   - 点击"在线问诊"进入问诊页面

2. **身份验证** (`Consultation.vue`)
   - 输入姓名和生日
   - 系统验证是否为已注册患者
   - 未注册则自动创建新患者记录

3. **选择医生**
   - 从医生列表选择目标医生
   - 或通过 `/consultation/:doctorUsername` 直接进入

4. **提交问题**
   - 填写问诊内容
   - 选择问题类型
   - 提交到指定医生

5. **查看回复**
   - 查看问题的回复状态
   - 查看医生的答复内容

**关键代码路径**:
```
Home.vue → router.push('/consultation')
         ↓
Consultation.vue → store.verifyPatient()
         ↓
Consultation.vue → store.addQuestion()
         ↓
store.questions[] 状态更新
```

#### 4.2.2 医生诊室流程

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  医生登录   │───▶│  进入诊室   │───▶│ 查看问题    │
└─────────────┘    └─────────────┘    └─────────────┘
                                            │
                                            ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  回复完成   │◀───│  提交回复   │◀───│  编写回复   │
└─────────────┘    └─────────────┘    └─────────────┘
```

**详细步骤**:

1. **医生登录** (`DoctorLogin.vue`)
   - 输入用户名和密码
   - 调用 `store.loginDoctor()` 验证
   - 验证成功后跳转到诊室

2. **进入诊室** (`DoctorRoom.vue`)
   - 根据 `:username` 路由参数加载医生数据
   - 获取该医生的所有待回复问题

3. **处理问题**
   - 查看待回复问题列表
   - 选择问题进行回复
   - 可选择"文字回复"或"标记口述解答"

4. **提交回复**
   - 调用 `store.answerQuestion()` 保存回复
   - 问题状态更新为 `answered`

**关键代码路径**:
```
DoctorLogin.vue → store.loginDoctor()
         ↓
router.push(`/doctor/room/${username}`)
         ↓
DoctorRoom.vue → store.getQuestionsByDoctor()
         ↓
DoctorRoom.vue → store.answerQuestion()
```

### 4.3 功能模块与代码映射

| 功能模块 | 前端页面 | Store 方法 | 数据来源 |
|---------|---------|-----------|---------|
| 平台首页 | `Home.vue` | `getStatistics()`, `getActiveDoctors()` | `doctor-user-list.json` |
| 患者验证 | `Consultation.vue` | `verifyPatient()` | `patient-user.json` |
| 问题提交 | `Consultation.vue` | `addQuestion()` | 内存状态 |
| 问题列表 | `DoctorRoom.vue` | `getQuestionsByDoctor()` | 内存状态 |
| 问题回复 | `DoctorRoom.vue` | `answerQuestion()` | 内存状态 |
| 医生登录 | `DoctorLogin.vue` | `loginDoctor()` | `doctor-user-list.json` |
| 医生列表 | `Doctors.vue` | `state.doctors` | `doctor-user-list.json` |

---

## 五、关联代码与调用链

### 5.1 前端启动调用链

```
index.html
    │
    ▼
main.ts
    ├── createApp(App)
    ├── app.use(Antd)          ← Ant Design Vue
    ├── app.use(router)        ← Vue Router
    └── app.mount('#app')
         │
         ▼
    App.vue
    ├── <AppHeader />          ← 顶部导航
    ├── <RouterView />         ← 页面内容
    │       │
    │       ├── '/' → Home.vue
    │       ├── '/consultation' → Consultation.vue
    │       ├── '/doctors' → Doctors.vue
    │       ├── '/doctor/login' → DoctorLogin.vue
    │       ├── '/doctor/room/:username' → DoctorRoom.vue
    │       └── '/about' → About.vue
    │
    └── <AppFooter />          ← 底部页脚
```

### 5.2 患者问诊调用链

```
用户点击"在线问诊"
    │
    ▼
Home.vue: handleStartConsultation()
    │
    ▼
router.push('/consultation')
    │
    ▼
Consultation.vue 加载
    │
    ├── onMounted()
    │       └── 解析路由参数，获取医生信息
    │
    ├── 患者验证
    │       │
    │       ▼
    │   handlePatientVerify()
    │       │
    │       ▼
    │   store.verifyPatient(name, birthday)
    │       │
    │       ├── 查找: patients.find(p => p.name === name && p.birthday === birthday)
    │       │
    │       ├── 找到 → 返回患者
    │       │
    │       └── 未找到 → 创建新患者并添加到 patients[]
    │
    └── 提交问题
            │
            ▼
        handleSubmitQuestion()
            │
            ▼
        store.addQuestion({
            patientId, patientName, doctorId, doctorName, question
        })
            │
            ▼
        创建 Question 对象
            {
                id: `q${Date.now()}`,
                submitTime: new Date().toISOString(),
                status: 'pending',
                answer: null,
                answerTime: null
            }
            │
            ▼
        state.questions.push(newQuestion)
```

### 5.3 医生回复调用链

```
医生登录
    │
    ▼
DoctorLogin.vue: handleLogin()
    │
    ▼
store.loginDoctor(username, password)
    │
    ├── 查找: doctors.find(d => d.username === username && d.password === password)
    │
    ├── 找到 → state.currentDoctor = doctor, 返回 doctor
    │
    └── 未找到 → 返回 null
    │
    ▼
登录成功 → router.push(`/doctor/room/${doctor.username}`)
    │
    ▼
DoctorRoom.vue 加载
    │
    ├── onMounted()
    │       ├── 获取路由参数: route.params.username
    │       └── store.getDoctorByUsername(username)
    │
    └── 加载问题列表
            │
            ▼
        store.getQuestionsByDoctor(doctorId)
            │
            ▼
        return state.questions.filter(q => q.doctorId === doctorId)
    │
    ▼
医生选择问题回复
    │
    ▼
handleAnswer(questionId, answer)
    │
    ▼
store.answerQuestion(questionId, answer)
    │
    ├── 查找问题: questions.find(q => q.id === questionId)
    │
    └── 更新问题状态:
            question.status = 'answered'
            question.answer = answer
            question.answerTime = new Date().toISOString()
```

### 5.4 后端服务调用链

```
Spring Boot 启动
    │
    ▼
QaServiceUserApplication.main()
    │
    ▼
SpringApplication.run()
    │
    ├── @SpringBootApplication
    │       ├── @Configuration
    │       ├── @EnableAutoConfiguration
    │       └── @ComponentScan
    │
    ├── 扫描并加载配置类
    │       └── CorsConfig.java
    │               ├── addCorsMappings() → 配置全局 CORS
    │               └── corsConfigurationSource() → 提供 CORS 配置 Bean
    │
    ├── 扫描并加载控制器
    │       └── TestController.java
    │               ├── @GetMapping("/api/test/cors")
    │               ├── @PostMapping("/api/test/cors")
    │               └── @RequestMapping("/api/test")
    │
    └── 启动内嵌 Tomcat
            │
            ▼
        监听端口 8080
            │
            ▼
        等待 HTTP 请求
            │
            ▼
        请求到达 → DispatcherServlet
            │
            ├── 路由匹配 → Controller
            │
            ├── 执行方法
            │
            └── 返回响应
```

### 5.5 数据流向图

```
┌─────────────────────────────────────────────────────────────────────┐
│                           数据流向                                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  前端数据层                                                          │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  doctor-user-list.json ────┐                                │   │
│  │  patient-user.json ────────┼──▶ store.state (reactive)      │   │
│  │  question-list.json ───────┘                                │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│  状态管理层                                                          │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  store.state                                                │   │
│  │  ├── doctors: Doctor[]                                      │   │
│  │  ├── patients: Patient[]                                    │   │
│  │  ├── questions: Question[]                                  │   │
│  │  ├── currentDoctor: Doctor | null                           │   │
│  │  └── currentPatient: Patient | null                         │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│  视图层 (双向绑定)                                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  Home.vue ◀───────────────────▶ statistics, activeDoctors   │   │
│  │  Consultation.vue ◀───────────▶ currentPatient, questions   │   │
│  │  DoctorLogin.vue ◀────────────▶ currentDoctor               │   │
│  │  DoctorRoom.vue ◀─────────────▶ questions, currentDoctor    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 六、技术栈与依赖

### 6.1 前端技术栈

| 技术 | 版本 | 用途 |
|-----|------|------|
| Vue.js | 3.5.10 | 前端框架 |
| TypeScript | 5.5.3 | 类型安全 |
| Vite | 5.4.8 | 构建工具 |
| Vue Router | 4.6.3 | 路由管理 |
| Ant Design Vue | 4.2.6 | UI 组件库 |
| dayjs | 1.11.19 | 日期处理 |

**关键依赖说明**:

```json
{
  "dependencies": {
    "vue": "^3.5.10",
    "vue-router": "^4.6.3",
    "ant-design-vue": "^4.2.6",
    "dayjs": "^1.11.19"
  },
  "devDependencies": {
    "typescript": "~5.5.3",
    "vite": "^5.4.8",
    "@vitejs/plugin-vue": "^5.1.4"
  }
}
```

### 6.2 后端技术栈

| 技术 | 版本 | 用途 |
|-----|------|------|
| Spring Boot | 3.5.7 | 后端框架 |
| Java | 17 | 编程语言 |
| Maven | 3.x | 构建工具 |
| Spring Boot Actuator | - | 应用监控 |
| Apache Tomcat | 10.1.48 | Web 服务器 |

**核心依赖 (pom.xml)**:

```xml
<!-- qa-service-user -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 6.3 开发工具依赖

| 工具 | 版本 | 用途 |
|-----|------|------|
| concurrently | 8.2.2 | 并发执行多个命令 |
| Node.js | >= 16.0.0 | JavaScript 运行时 |
| npm | >= 8.0.0 | 包管理器 |
| Git | >= 2.0 | 版本控制 |

### 6.4 项目配置文件清单

| 文件 | 路径 | 用途 |
|-----|------|------|
| `package.json` | `/` | 根项目配置和脚本 |
| `package.json` | `/web/qa-web/` | 前端项目配置 |
| `vite.config.ts` | `/web/qa-web/` | Vite 构建配置 |
| `tsconfig.json` | `/web/qa-web/` | TypeScript 配置 |
| `pom.xml` | `/server/qa-service-user/` | 用户服务 Maven 配置 |
| `pom.xml` | `/server/qa-service-question/` | 问题服务 Maven 配置 |
| `application.properties` | `/server/qa-service-user/src/main/resources/` | 用户服务应用配置 |
| `application.properties` | `/server/qa-service-question/src/main/resources/` | 问题服务应用配置 |

---

## 附录

### A. 快速命令参考

```bash
# 安装依赖
npm install
npm run install:all

# 启动开发环境
npm run dev              # 同时启动前后端
npm run dev:web          # 仅启动前端
npm run dev:server       # 启动所有后端服务
npm run dev:user         # 仅启动用户服务
npm run dev:question     # 仅启动问题服务

# 构建
npm run build            # 构建前端

# 清理
npm run clean            # 清理所有构建文件
npm run clean:web        # 清理前端
npm run clean:server     # 清理所有后端
```

### B. 服务访问地址

| 服务 | 地址 | 说明 |
|-----|------|------|
| 前端应用 | http://localhost:5173 | Vite 开发服务器 |
| 用户服务 | http://localhost:8080 | Spring Boot 服务 |
| 问题服务 | http://localhost:8081 | Spring Boot 服务 |
| 健康检查 | http://localhost:8080/actuator/health | Actuator 端点 |

### C. 测试账号

**医生账号** (密码均为 `123456`):

| 用户名 | 姓名 | 科室 |
|-------|------|------|
| dr-zhang-wei | 张伟医生 | 心内科 |
| dr-li-na | 李娜医生 | 儿科 |
| dr-wang-qiang | 王强医生 | 骨科 |
| dr-liu-min | 刘敏医生 | 妇产科 |
| dr-chen-jie | 陈杰医生 | 消化内科 |

### D. 项目开发状态

| 模块 | 状态 | 说明 |
|-----|------|------|
| 前端基础框架 | ✅ 已完成 | Vue 3 + TypeScript + Ant Design Vue |
| 前端页面开发 | ✅ 已完成 | 首页、问诊、医生管理等功能页面 |
| 前端状态管理 | ✅ 已完成 | 自定义 reactive store |
| 后端用户服务框架 | ✅ 已完成 | Spring Boot 基础架构 |
| 后端问题服务框架 | ✅ 已完成 | Spring Boot 基础架构 |
| 后端业务实现 | ⏳ 待开发 | Controller/Service/Repository 层 |
| 数据库集成 | ⏳ 待开发 | MySQL/PostgreSQL 等 |
| 前后端联调 | ⏳ 待开发 | API 接口对接 |
| 统计分析服务 | 📋 规划中 | qa-service-statistic |

---

*文档生成日期: 2026-03-20*
