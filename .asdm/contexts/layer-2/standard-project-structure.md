# 标准项目结构（L2 层）

## 概述

本文档定义本工作区的标准项目结构。QA Healthcare 采用**两级多服务 Monorepo** 后端 + 独立前端（Vue 3）的结构，基于微服务架构、按业务领域拆分。

## 整体目录结构

```
qa-live-healthcare/
├── server/                         # 后端服务根目录（多服务 Monorepo）
│   ├── qa-service-user/            # 用户/医生服务 (端口 8080) ✅ 已实现
│   ├── qa-service-question/        # 问题/问答服务 (端口 8081) 🔲 基础框架
│   ├── qa-service-statistic/       # 统计服务 (端口 8082) 🔲 占位/待开发
│   ├── docs/                       # 后端总体文档（arch/coding-style/data-models/api/deployment）
│   └── README.md
├── web/
│   └── qa-web/                     # 前端应用 Vue3 + TS + Vite (端口 5173)
│       └── src/                    # components/ views/ router/ store/ data/
├── test/                           # 测试脚本与用例 (Playwright)
├── _TRAINING_ASSETS/               # 训练资源 (zip)
├── docker-compose.yml              # 一体化编排 (MySQL + 后端 + 前端)
├── package.json                    # 根目录并发脚本 (concurrently)
└── README.md
```

## 后端服务标准结构（server/）

`server/` 为两级多服务 Monorepo：第 1 级是服务容器目录，第 2 级是各独立 Spring Boot 项目。

### 已注册服务

| 服务目录 | 业务领域 | 包路径 | 状态 |
|----------|---------|--------|------|
| `qa-service-user/` | 用户/医生管理、认证 | `com.leansofx.qaserviceuser` | ✅ 已实现（TestController、DoctorUserController，内存数据） |
| `qa-service-question/` | 问题/问答流程 | `com.leansofx.qaservicequestion` | 🔲 基础框架（业务 API 待开发） |
| `qa-service-statistic/` | 数据统计/报表 | `com.leansofx.qaservicestatistic` | 🔲 占位目录（待开发） |

> 新建服务：在 `server/` 下创建 `qa-service-{domain}/`，遵循以下内部结构。

### 单服务内部结构

```
qa-service-{domain}/
├── pom.xml                         # Maven 构建配置
├── mvnw / mvnw.cmd                 # Maven Wrapper
├── Dockerfile                      # 多阶段构建镜像
├── start.sh / stop.sh / status.sh  # 运维脚本
├── src/main/java/com/leansofx/qaservice{Domain}/
│   ├── QaService{Domain}Application.java  # 启动类
│   ├── config/                     # 配置类 (CorsConfig 等)
│   ├── controller/                 # REST 控制器
│   ├── service/ + impl/            # 业务逻辑层
│   ├── repository/                 # 数据访问层 (Spring Data JPA)
│   ├── entity/ 或 model/           # JPA 实体
│   ├── dto/                        # 请求/响应 DTO
│   ├── exception/                  # 异常处理
│   ├── utils/                      # 工具类
│   └── constants/                  # 常量/错误码
└── src/main/resources/
    ├── application.yml             # 默认配置
    ├── application-dev/test/prod.yml # 环境配置
    └── data/                       # 内存数据 JSON (Phase 1 临时方案)
```

## 前端代码目录（web/qa-web）

```
web/qa-web/src/
├── main.ts                         # 应用入口
├── App.vue                         # 根组件
├── api/                            # 后端 API 调用层 (前后端联调阶段)
│   ├── request.ts                  # axios 实例 (baseURL=/api, 响应拦截统一拆解 data)
│   ├── types.ts                    # 共享类型 Doctor / ApiResponse<T>
│   └── modules/
│       └── doctor.ts               # doctorApi: 封装 /api/doctors 的 CRUD 调用
├── locales/                        # 国际化资源 (vue-i18n)
│   ├── index.ts                    # i18n 实例装配
│   ├── zh-cn.json                  # 中文文案
│   └── en-us.json                  # 英文文案
├── components/                     # 可复用组件 (AppHeader/AppFooter)
├── views/                         # 页面组件 (Home/Doctors/Consultation/DoctorLogin/DoctorRoom/About)
├── router/index.ts                 # 路由配置 (createWebHistory)
├── store/index.ts                  # 全局状态 (Pinia 风格: doctors/patients/questions + 认证方法)
├── data/                          # 本地静态 JSON (doctor-user-list/patient-user/question-list) — 当前 store 初始化数据源
└── assets/                        # 资源
```

### 前端 API 调用层（web/qa-web/src/api）

在"前后端联调"阶段新增，用于替代/补充本地 JSON 直读：

- `request.ts`：基于 `axios` 创建实例，`baseURL: '/api'`，响应拦截器直接返回 `response.data`；开发态 `vite.config.ts` 已配置 `/api/doctors` 代理到 `http://localhost:8080`（CORS 友好）。
- `types.ts`：导出 `Doctor`（从 `store` 复用）与通用 `ApiResponse<T>`；统一响应包装 `{ data, message?, status? }`。
- `modules/doctor.ts`：按业务模块拆分的 `doctorApi`，封装 `/api/doctors` 的 `getAll / getById / getByUsername / getActive / create / update / delete`，与后端 `DoctorUserController` 端点一一对应。

> 当前状态：`store/index.ts` 仍以 `data/*.json` 初始化内存数据；`api/` 层已就绪但尚未接入 store（处于联调过渡期）。

## 命名约定

| 层级 | 格式 | 示例 |
|------|------|------|
| 服务目录 | `qa-service-{domain}` | `qa-service-user` |
| Java 包路径 | `com.leansofx.qaservice{Domain}` | `com.leansofx.qaserviceuser` |
| 启动类 | `{Domain}Application.java` | `QaServiceUserApplication.java` |
| 目录 | kebab-case | `user-management` |
| Java 文件 | PascalCase | `UserController.java` |
| TS/Vue 文件 | PascalCase | `UserService.ts`、`UserForm.vue` |
| 工具函数 | kebab-case | `date-utils.ts` |

## 最佳实践

1. **服务边界清晰**：每服务对应一个业务领域，遵循 DDD；服务间通过 REST API 通信，避免跨包直接引用。
2. **模块化分层**：Controller → Service → Repository → Entity，依赖单向、禁止循环。
3. **可扩展性**：两级结构支持按需新增 `qa-service-{domain}/`。
4. **配置外置**：敏感配置（密码/密钥）通过环境变量注入，不提交版本控制。

---

*由 QA Healthcare 上下文构建工具集维护。*
