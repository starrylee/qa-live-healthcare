# QA Healthcare API 文档

## 概述

QA Healthcare 是医疗问答系统的后端服务，采用微服务架构，基于 Spring Boot 构建。

**服务信息：**

| 服务名称 | 服务标识 | 版本 | 端口 | 基础路径 |
|---------|---------|------|------|----------|
| 用户管理服务 | qa-service-user | 0.0.1-SNAPSHOT | 8080 | http://localhost:8080 |
| 问题管理服务 | qa-service-question | 0.0.1-SNAPSHOT | 8081 | http://localhost:8081 |

---

## API 端点列表

### TestController

**文件位置：** [server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/controller/TestController.java](../server/qa-service-user/src/main/java/com/leansofx/qaserviceuser/controller/TestController.java)

**所属服务：** qa-service-user (端口: 8080)

测试控制器，主要用于 CORS 配置测试和服务健康检查。

| 方法 | 端点 | 描述 | 参数 | 请求体 | 响应 |
|------|------|------|------|--------|------|
| GET | `/api/test/cors` | 测试 CORS 配置（GET 请求） | 无 | 无 | TestResponse |
| POST | `/api/test/cors` | 测试 CORS 配置（POST 请求） | 无 | Map<String, Object>（可选） | TestResponse |
| OPTIONS | `/api/test/cors` | 处理 CORS 预检请求 | 无 | 无 | 无内容 |

#### 数据结构示例

**TestResponse（测试响应）**
```json
{
  "message": "CORS configuration is working!",
  "timestamp": 1699000000000,
  "service": "qa-service-user",
  "receivedData": {
    "key": "value"
  }
}
```

**请求体示例（POST /api/test/cors）**
```json
{
  "testData": "example",
  "userId": 123,
  "action": "test"
}
```

---

## Spring Boot Actuator 端点

### qa-service-user 服务 (端口: 8080)

| 方法 | 端点 | 描述 | 参数 | 请求体 | 响应 |
|------|------|------|------|--------|------|
| GET | `/actuator/health` | 应用健康检查 | 无 | 无 | HealthResponse |
| GET | `/actuator/info` | 应用信息 | 无 | 无 | InfoResponse |
| GET | `/actuator/metrics` | 应用指标 | 无 | 无 | MetricsResponse |
| GET | `/actuator/env` | 环境信息 | 无 | 无 | EnvironmentResponse |
| GET | `/actuator/beans` | Spring Bean 信息 | 无 | 无 | BeansResponse |
| GET | `/actuator/loggers` | 日志配置信息 | 无 | 无 | LoggersResponse |

### qa-service-question 服务 (端口: 8081)

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

---

## CORS 配置

服务已配置跨域资源共享（CORS），支持以下配置：

- **允许的源**：`*`（所有源）
- **允许的方法**：`GET, POST, PUT, DELETE, OPTIONS`
- **允许的头部**：`*`（所有头部）
- **允许凭证**：`false`
- **最大缓存时间**：`3600` 秒

---

## 错误响应格式

当 API 调用出现错误时，服务会返回标准的错误响应格式：

```json
{
  "timestamp": "2025-11-03T10:15:30.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "详细错误信息",
  "path": "/api/test/cors"
}
```

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

**健康检查示例：**
```bash
# 用户服务健康检查
curl -X GET http://localhost:8080/actuator/health

# 问题服务健康检查
curl -X GET http://localhost:8081/actuator/health
```

---

## 注意事项

1. 当前项目处于开发阶段，仅包含测试端点
2. 所有 API 端点都支持 CORS
3. Actuator 端点提供了丰富的监控和管理功能
4. 建议在生产环境中限制 CORS 配置和 Actuator 端点的访问权限

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v0.0.1-SNAPSHOT | 2026-03-20 | 初始版本，包含基础的 CORS 测试功能和 Actuator 监控 |

---

*文档生成日期: 2026-03-20*
