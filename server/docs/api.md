# QA Healthcare 后端 API 文档

## 概述

QA Healthcare 后端采用微服务架构，基于 Spring Boot 3.5.7 构建，提供医疗问答系统的核心 API 服务。

**服务架构：**
- **用户服务 (qa-service-user)**：端口 8080 - 用户管理、认证授权
- **问题服务 (qa-service-question)**：端口 8081 - 问题管理、问答流程
- **统计服务 (qa-service-statistic)**：端口待定 - 数据统计分析（待开发）

---

## API 端点列表

### TestController

**文件位置：** `qa-service-user/src/main/java/com/leansofx/qaserviceuser/controller/TestController.java`

测试控制器，主要用于 CORS 配置测试、服务健康检查和基础连通性验证。

| 方法 | 端点 | 描述 | 参数 | 请求体 | 响应 |
|------|------|------|------|--------|------|
| GET | `/api/test/cors` | CORS 配置测试（GET 请求） | 无 | 无 | `Map<String, Object>` |
| POST | `/api/test/cors` | CORS 配置测试（POST 请求） | 无 | `Map<String, Object>`（可选） | `Map<String, Object>` |
| OPTIONS | `/api/test/cors` | 处理 CORS 预检请求 | 无 | 无 | 无内容（204） |

#### 接口详情

**1. GET /api/test/cors**

测试 GET 请求的 CORS 配置是否正常。

- **基础路径：** `http://localhost:8080`
- **Content-Type：** `application/json`

**响应示例：**
```json
{
  "message": "CORS configuration is working!",
  "timestamp": 1699000000000,
  "service": "qa-service-user"
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| `message` | String | 响应消息，固定为 "CORS configuration is working!" |
| `timestamp` | Long | 服务器当前时间戳（毫秒） |
| `service` | String | 服务标识，固定为 "qa-service-user" |

---

**2. POST /api/test/cors**

测试 POST 请求的 CORS 配置是否正常，可携带请求体数据。

- **基础路径：** `http://localhost:8080`
- **Content-Type：** `application/json`

**请求体示例：**
```json
{
  "testData": "example",
  "userId": 123,
  "action": "test"
}
```

**响应示例：**
```json
{
  "message": "POST request with CORS is working!",
  "receivedData": {
    "testData": "example",
    "userId": 123,
    "action": "test"
  },
  "timestamp": 1699000000000,
  "service": "qa-service-user"
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| `message` | String | 响应消息，固定为 "POST request with CORS is working!" |
| `receivedData` | Object | 回显客户端发送的请求体数据（可为 null） |
| `timestamp` | Long | 服务器当前时间戳（毫秒） |
| `service` | String | 服务标识，固定为 "qa-service-user" |

---

**3. OPTIONS /api/test/cors**

处理 CORS 预检请求（Preflight Request）。

- **基础路径：** `http://localhost:8080`
- **说明：** Spring Boot 自动处理 OPTIONS 请求，返回 CORS 响应头

**响应：** HTTP 204 No Content

---

## Spring Boot Actuator 端点

### 用户服务 Actuator (端口 8080)

项目集成了 Spring Boot Actuator，提供以下监控和管理端点：

| 方法 | 端点 | 描述 | 参数 | 请求体 | 响应 |
|------|------|------|------|--------|------|
| GET | `/actuator/health` | 应用健康检查 | 无 | 无 | HealthResponse |
| GET | `/actuator/info` | 应用信息 | 无 | 无 | InfoResponse |
| GET | `/actuator/metrics` | 应用指标 | 无 | 无 | MetricsResponse |
| GET | `/actuator/env` | 环境信息 | 无 | 无 | EnvironmentResponse |
| GET | `/actuator/beans` | Spring Bean 信息 | 无 | 无 | BeansResponse |
| GET | `/actuator/loggers` | 日志配置信息 | 无 | 无 | LoggersResponse |

#### Actuator 数据结构示例

**HealthResponse（健康检查响应）**
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 123456789012,
        "threshold": 10485760,
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

**InfoResponse（应用信息响应）**
```json
{
  "app": {
    "name": "qa-service-user",
    "description": "QA Service User - Healthcare QA System User Management Service",
    "version": "0.0.1-SNAPSHOT",
    "encoding": "UTF-8",
    "java": {
      "version": "17"
    }
  },
  "team": "QA Healthcare Team",
  "environment": "development",
  "build": {
    "timestamp": "2025-11-03"
  },
  "features": "CORS,Actuator,Health Checks,User Management",
  "java": {
    "version": "17.0.x",
    "vendor": "Eclipse Adoptium"
  },
  "os": {
    "name": "Linux",
    "version": "x.x.x",
    "arch": "amd64"
  }
}
```

### 问题服务 Actuator (端口 8081)

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `http://localhost:8081/actuator/health` | 健康检查 |
| GET | `http://localhost:8081/actuator/info` | 应用信息 |
| GET | `http://localhost:8081/actuator/metrics` | 应用指标 |
| GET | `http://localhost:8081/actuator/env` | 环境信息 |

---

## CORS 配置

服务已配置跨域资源共享（CORS），支持以下配置：

| 配置项 | 值 | 说明 |
|--------|-----|------|
| **允许的源** | `*` | 允许所有来源访问 |
| **允许的方法** | `GET, POST, PUT, DELETE, OPTIONS` | 支持的 HTTP 方法 |
| **允许的头部** | `*` | 允许所有请求头 |
| **允许凭证** | `true` | 允许携带 Cookie/认证信息 |
| **最大缓存时间** | `3600` 秒 | 预检请求缓存时间 |

**CORS 配置类位置：**
`qa-service-user/src/main/java/com/leansofx/qaserviceuser/config/CorsConfig.java`

---

## 错误响应格式

当 API 调用出现错误时，服务会返回标准的 Spring Boot 错误响应格式：

```json
{
  "timestamp": "2025-11-03T10:15:30.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "详细错误信息",
  "path": "/api/test/cors"
}
```

**错误响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| `timestamp` | String | 错误发生时间（ISO 8601 格式） |
| `status` | Integer | HTTP 状态码 |
| `error` | String | 错误类型描述 |
| `message` | String | 详细错误信息 |
| `path` | String | 请求路径 |

**常见 HTTP 状态码：**

| 状态码 | 含义 | 场景 |
|--------|------|------|
| 200 | OK | 请求成功 |
| 204 | No Content | OPTIONS 预检请求成功 |
| 400 | Bad Request | 请求参数错误 |
| 404 | Not Found | 端点不存在 |
| 500 | Internal Server Error | 服务器内部错误 |

---

## 使用示例

### 测试 CORS 配置

**GET 请求示例：**
```bash
curl -X GET http://localhost:8080/api/test/cors
```

**POST 请求示例：**
```bash
curl -X POST http://localhost:8080/api/test/cors \
  -H "Content-Type: application/json" \
  -d '{"testData": "example", "userId": 123}'
```

### 健康检查

**用户服务健康检查：**
```bash
curl -X GET http://localhost:8080/actuator/health
```

**问题服务健康检查：**
```bash
curl -X GET http://localhost:8081/actuator/health
```

### 获取应用信息

```bash
curl -X GET http://localhost:8080/actuator/info
```

---

## 服务端口配置

| 服务 | 端口 | 基础路径 | 说明 |
|------|------|----------|------|
| 用户服务 | 8080 | `http://localhost:8080` | 用户管理、认证 |
| 问题服务 | 8081 | `http://localhost:8081` | 问题管理（基础框架） |
| 统计服务 | 待定 | 待定 | 待开发 |

---

## 注意事项

1. **当前项目处于开发阶段**，仅 qa-service-user 服务包含 TestController 测试端点
2. **所有 API 端点都支持 CORS**，便于前端开发调试
3. **Actuator 端点** 提供了丰富的监控和管理功能，生产环境建议限制访问
4. **问题服务 (qa-service-question)** 目前仅包含基础框架，业务 API 待开发
5. **统计服务 (qa-service-statistic)** 尚未创建，规划中

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v0.0.1-SNAPSHOT | 2025-11-03 | 初始版本，包含基础的 CORS 测试功能和 Actuator 监控 |

---

## 相关文档

- [用户服务详细文档](../qa-service-user/README.md)
- [后端整体架构文档](../README.md)
- [前端项目文档](../../web/qa-web/README.md)
