# API 接口文档（L2 层）

## 概述

QA Healthcare 后端为微服务，基于 Spring Boot 3.5.7。当前**仅 `qa-service-user` 含业务接口**（`DoctorUserController`），`qa-service-question` 仍为基础框架（业务 API 待开发），`qa-service-statistic` 尚未创建。所有接口支持 CORS，便于前端联调。

## 服务端口

| 服务 | 端口 | 基础路径 | 状态 |
|------|------|----------|------|
| 用户/医生服务 | 8080 | http://localhost:8080 | ✅ 已实现 |
| 问题/问答服务 | 8081 | http://localhost:8081 | 🔲 基础框架 |
| 统计服务 | 待定 | 待定 | 🔲 待开发 |

## 一、qa-service-user · TestController

**文件：** `server/qa-service-user/.../controller/TestController.java`（CORS / 连通性测试）

| 方法 | 端点 | 描述 | 请求体 | 响应 |
|------|------|------|--------|------|
| GET | `/api/test/cors` | CORS GET 测试 | 无 | `Map<String,Object>` |
| POST | `/api/test/cors` | CORS POST 测试 | `Map<String,Object>`(可选) | `Map<String,Object>` |
| OPTIONS | `/api/test/cors` | 预检请求 | 无 | 204 |

**GET 响应示例**
```json
{ "message": "CORS configuration is working!", "timestamp": 1699000000000, "service": "qa-service-user" }
```

## 二、qa-service-user · DoctorUserController

**文件：** `server/qa-service-user/.../controller/DoctorUserController.java`（医生 CRUD）

| 方法 | 端点 | 描述 | 参数 | 请求体 | 响应 |
|------|------|------|------|--------|------|
| GET | `/api/doctors` | 全部医生列表 | 无 | 无 | `List<DoctorUserDTO>` |
| GET | `/api/doctors/{id}` | 按 ID 获取 | id(路径) | 无 | `DoctorUserDTO` |
| GET | `/api/doctors/username/{username}` | 按用户名获取 | username(路径) | 无 | `DoctorUserDTO` |
| GET | `/api/doctors/active` | 活跃医生列表 | 无 | 无 | `List<DoctorUserDTO>` |
| POST | `/api/doctors` | 创建医生 | 无 | `DoctorUserDTO` | `DoctorUserDTO` |
| PUT | `/api/doctors/{id}` | 更新医生 | id(路径) | `DoctorUserDTO` | `DoctorUserDTO` |
| DELETE | `/api/doctors/{id}` | 删除医生 | id(路径) | 无 | 204 |

**DoctorUserDTO**
```json
{
  "id": "doctor-001",
  "username": "dr_zhang",
  "password": "securepassword123",
  "name": "张医生",
  "title": "主任医师",
  "department": "心内科",
  "avatar": "https://example.com/avatars/dr_zhang.jpg",
  "experience": "20年临床经验",
  "specialties": ["冠心病", "高血压", "心律失常"],
  "isActive": true
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | String | 否 | 创建时由系统生成（如 `doctor-001`） |
| username | String | 是 | 登录用户名 |
| password | String | 是 | 登录密码 |
| name | String | 是 | 医生姓名 |
| title | String | 否 | 职称 |
| department | String | 否 | 科室 |
| avatar | String | 否 | 头像 URL |
| experience | String | 否 | 经验描述 |
| specialties | List<String> | 否 | 专长 |
| isActive | Boolean | 否 | 默认 true |

## 三、qa-service-question（基础框架）

当前仅含 Spring Boot 骨架，业务接口（问题提交/回答/状态流转）**待开发**。规划端点：`/api/questions`、`/api/questions/{id}/answer` 等。

## 四、Spring Boot Actuator

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `/actuator/health` | 健康检查 |
| GET | `/actuator/info` | 应用信息 |
| GET | `/actuator/metrics` | 指标 |
| GET | `/actuator/env` | 环境信息 |
| GET | `/actuator/beans` | Bean 信息 |
| GET | `/actuator/loggers` | 日志配置 |

**HealthResponse**
```json
{ "status": "UP", "components": { "diskSpace": { "status": "UP" }, "ping": { "status": "UP" } } }
```

## 五、CORS 配置

| 项 | 值 |
|----|----|
| 允许源 | `*` |
| 允许方法 | GET, POST, PUT, DELETE, OPTIONS |
| 允许头部 | `*` |
| 允许凭证 | false（user）/ 配置不一 |
| 最大缓存 | 3600 秒 |

配置类：`qa-service-user/.../config/CorsConfig.java`（`/api/**` 映射）。

## 六、错误响应格式

```json
{ "timestamp": "2025-11-03T10:15:30.000+00:00", "status": 400, "error": "Bad Request", "message": "详细错误信息", "path": "/api/..." }
```

| 状态码 | 场景 |
|--------|------|
| 200 | 成功 |
| 204 | OPTIONS / DELETE 成功 |
| 400 | 参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 七、调用示例

```bash
curl -X GET http://localhost:8080/api/doctors/active
curl -X POST http://localhost:8080/api/doctors -H "Content-Type: application/json" \
  -d '{"username":"dr_wang","password":"pw","name":"王医生","department":"呼吸内科","isActive":true}'
curl -X GET http://localhost:8080/actuator/health
```

---

*由 QA Healthcare 上下文构建工具集维护。*
