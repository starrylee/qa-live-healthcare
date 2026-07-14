# 编码规范与风格指南（L2 层）

## 概述

本文档汇总 QA Healthcare 前后端实际使用的编码规范。后端规范以 `server/docs/coding-style.md` 为准（基于 Google Java Style、Spring Boot 最佳实践、阿里巴巴 Java 开发手册）；前端遵循 Vue 3 + TypeScript 社区约定。

## 通用原则

- **可读性优先**：命名表达意图，避免模糊缩写与拼音。
- **一致性**：全代码库遵循同一模式与语言约定。
- **可维护性**：单一职责、清晰边界、易扩展。

## Java 编码规范（后端）

### 命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 包 | 全小写、无下划线 | `com.leansofx.qaserviceuser.controller` |
| 类/接口 | 大驼峰；接口/实现 `Xxx` / `XxxImpl` | `UserService`、`UserServiceImpl` |
| 方法 | 小驼峰，动作前缀 | `getUserById`、`listUsers`、`saveUser`、`removeUser` |
| 变量 | 小驼峰；布尔 `is/has/can` 前缀 | `userName`、`isActive` |
| 常量 | 全大写+下划线 | `MAX_RETRY_COUNT` |

### 格式

- 缩进 **4 个空格**（禁 Tab）；左大括号不换行（K&R）。
- 单行 ≤ 120 字符；包声明/import/类注释后空一行；方法间空一行。
- 操作符两侧、逗号后、冒号后加空格；小括号内侧不加空格。

### REST API

- URL：`/api/v1/{resources}`（小写复数、kebab-case、版本前缀）。
- 方法语义：GET 查询、POST 创建、PUT 全量更新、DELETE 删除（幂等）。
- 统一响应包装：`{ code, message, data, timestamp }`。
- 状态码：200/201/204/400/401/403/404/500。

### 注解/异常/日志

- 标准修饰符顺序；重写方法加 `@Override`。
- 自定义 `BusinessException` + `@RestControllerAdvice` 全局异常处理；参数校验用 `MethodArgumentNotValidException`。
- SLF4J 占位符日志，按 ERROR/WARN/INFO/DEBUG/TRACE 分级，禁止字符串拼接、禁止吞异常。

### 数据库/测试

- 实体表名 `t_{name}`；字段 `created_at`/`updated_at`/`deleted`（逻辑删除）。
- Repository 继承 `JpaRepository<X, Long>` + `JpaSpecificationExecutor`，方法名查询或 `@Query`。
- 测试：`{Class}Test`，src/test 同包路径；遵循 AAA（Arrange/Act/Assert），返回空集合而非 null。

## TypeScript / Vue 编码规范（前端）

### 命名

- 变量/函数 camelCase；类/接口/组件 PascalCase；常量 UPPER_SNAKE_CASE。
- 单文件组件结构：`<template>` → `<script setup lang="ts">` → `<style scoped>`。

### 格式

- 缩进 **2 空格**；字符串单引号（插值用模板串）；显式类型，避免 `any`。
- 组合式 API 顺序：imports → props/emits → 响应式状态 → 计算属性 → 方法 → 生命周期。

### 状态与请求

- 全局状态集中在 `store/index.ts`（doctors/patients/questions + 认证方法）。
- 当前 `store` 以本地 `data/*.json` 初始化内存数据（联调过渡期）；后端 API 调用统一收敛到 `src/api/` 层，不散落在组件内。
- `src/api/request.ts` 用 `axios` 创建单例（`baseURL: '/api'`，响应拦截器拆解 `data`）；按业务模块拆分到 `src/api/modules/*.ts`（如 `doctor.ts`），组件/store 只调用 `xxxApi` 方法。
- 统一响应包装 `{ data, message?, status? }`（`ApiResponse<T>` 类型见 `src/api/types.ts`）。

### 国际化（vue-i18n）

- 文案统一抽取到 `src/locales/{zh-cn,en-us}.json`，由 `src/locales/index.ts` 装配 `createI18n` 实例；组件通过 `t()` 取词，禁止硬编码界面文本。
- 新增界面文案须同步更新中/英两份 JSON。

## 提交与协作

- 使用**中文 commit message**（见仓库 README 贡献指南）。
- API 变更需同步更新文档；提交前运行测试。

---

*由 QA Healthcare 上下文构建工具集维护。*
