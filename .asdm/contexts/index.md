# QA Healthcare — 工作区上下文索引

## 基本信息

- **名称**：QA Healthcare（仓库：`qa-live-healthcare`）
- **描述**：基于微服务架构的医疗问答系统，提供用户管理、医疗问题咨询解答与统计分析能力
- **技术栈**：Java 17 + Spring Boot 3.5.7 + Maven（后端）/ Vue.js 3.5 + TypeScript 5.5 + Vite 5.4 + Ant Design Vue 4 + axios（HTTP 客户端）+ vue-i18n（国际化）（前端）/ MySQL 8 + Spring Data JPA（持久化）

## 目录结构

```
qa-live-healthcare/
├── server/
│   ├── qa-service-user/        # 用户/医生管理服务 (端口 8080)
│   ├── qa-service-question/    # 问题/问答管理服务 (端口 8081)
│   ├── qa-service-statistic/   # 统计分析服务 (占位目录，待开发)
│   ├── docs/                   # 后端总体文档 (arch/coding-style/data-models/api/deployment/project-structure)
│   └── README.md
├── web/
│   └── qa-web/                 # 前端应用 Vue3 + TS + Vite (端口 5173)
│       └── src/
│           ├── api/            # 后端 API 调用层 (axios 封装 + 按模块拆分)
│           ├── locales/        # 国际化资源 (vue-i18n: zh-cn / en-us)
│           ├── components/     # 可复用组件
│           ├── views/          # 页面组件
│           ├── router/         # 路由配置
│           ├── store/          # 全局状态 (Pinia 风格)
│           ├── data/           # 本地静态 JSON (模拟数据)
│           └── assets/         # 静态资源
├── test/                       # 测试脚本与用例 (Playwright)
├── _TRAINING_ASSETS/           # 训练资源 (zip)
├── docker-compose.yml          # 本地一体化编排 (MySQL 等)
├── package.json                # 根目录并发脚本 (concurrently)
└── README.md
```

## 服务与端口

| 服务 | 端口 | 说明 |
|------|------|------|
| 前端应用 (qa-web) | 5173 | Vite 开发服务器 |
| 用户管理服务 | 8080 | Spring Boot，健康检查 `/actuator/health` |
| 问题管理服务 | 8081 | Spring Boot，健康检查 `/actuator/health` |
| 统计分析服务 | 待配置 | 规划中 |

## 开发命令

| 命令 | 说明 |
|------|------|
| `npm run dev` | 并发启动前端 + 用户/问题服务 |
| `npm run dev:web` | 仅启动前端 |
| `npm run dev:user` / `npm run dev:question` | 单独启动后端服务 |
| `npm run build` | 构建前端生产版本 |

## L2 上下文导航

| 文件 | 说明 | 状态 |
|------|------|------|
| [standard-project-structure.md](./layer-2/standard-project-structure.md) | 项目结构详情 | 已生成 |
| [standard-coding-style.md](./layer-2/standard-coding-style.md) | 编码规范 | 已生成 |
| [data-models.md](./layer-2/data-models.md) | 数据模型 | 已生成 |
| [deployment.md](./layer-2/deployment.md) | 部署配置 | 已生成 |
| [api.md](./layer-2/api.md) | API 文档 | 已生成 |
| [architecture.md](./layer-2/architecture.md) | 架构设计 | 已生成 |

---

*由 QA Healthcare 上下文构建工具集维护。*
