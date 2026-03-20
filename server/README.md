# QA Live Healthcare 后端项目知识库

## 项目概述

QA Live Healthcare 后端采用微服务架构，基于 Spring Boot 3.5.7 构建，Java 17 运行时环境。系统包含三个微服务模块：用户服务、问题服务和统计服务。

- **项目架构**: 微服务架构
- **技术栈**: Spring Boot 3.5.7 + Java 17 + Maven
- **服务数量**: 3 个微服务
- **构建工具**: Maven (使用 Maven Wrapper)

---

## 一、启动点与启动方式

### 1.1 启动点识别

| 服务名称 | 启动类 | 文件路径 | 服务端口 |
|----------|--------|----------|----------|
| qa-service-user | QaServiceUserApplication | `qa-service-user/src/main/java/com/leansofx/qaserviceuser/QaServiceUserApplication.java` | 8080 |
| qa-service-question | QaServiceQuestionApplication | `qa-service-question/src/main/java/com/leansofx/qaservicequestion/QaServiceQuestionApplication.java` | 8081 |
| qa-service-statistic | (待开发) | - | - |

### 1.2 启动方式

#### 开发环境启动

```bash
# 进入对应服务目录
cd qa-service-user

# 方式1: 使用 Maven Wrapper 启动
./mvnw spring-boot:run

# 方式2: 使用 Maven 启动
mvn spring-boot:run
```

#### 生产环境启动

```bash
# 1. 构建项目
./mvnw clean package

# 2. 使用启动脚本
./start.sh

# 或者使用 Java 命令
java -jar target/qa-service-user-0.0.1-SNAPSHOT.jar
```

#### 应用管理脚本（qa-service-user）

| 脚本 | 功能 | 说明 |
|------|------|------|
| `start.sh` | 启动服务 | 后台启动，保存 PID |
| `stop.sh` | 停止服务 | 读取 PID 终止进程 |
| `restart.sh` | 重启服务 | 先停止后启动 |
| `status.sh` | 查看状态 | 显示运行状态和进程信息 |

### 1.3 启动流程说明

**Spring Boot 应用启动流程**:

```
1. 执行 main() 方法
   └── SpringApplication.run()
       ├── 创建 Spring 应用上下文
       ├── 加载 application.properties 配置
       ├── 自动配置扫描 (@SpringBootApplication)
       ├── 启动内嵌 Tomcat 服务器
       └── 暴露 REST API 端点
```

---

## 二、代码层级与模块结构

### 2.1 整体项目结构

```
server/
├── qa-service-user/              # 用户服务
│   ├── pom.xml                   # Maven 配置
│   ├── mvnw / mvnw.cmd           # Maven Wrapper
│   ├── start.sh / stop.sh        # 应用管理脚本
│   ├── restart.sh / status.sh    # 应用管理脚本
│   ├── README.md                 # 服务文档
│   └── src/
│       ├── main/
│       │   ├── java/com/leansofx/qaserviceuser/
│       │   │   ├── QaServiceUserApplication.java    # 启动类
│       │   │   ├── config/
│       │   │   │   └── CorsConfig.java              # 跨域配置
│       │   │   └── controller/
│       │   │       └── TestController.java          # 测试接口
│       │   └── resources/
│       │       └── application.properties           # 应用配置
│       └── test/
│           └── java/com/leansofx/qaserviceuser/
│               └── QaServiceUserApplicationTests.java
│
├── qa-service-question/          # 问题服务
│   ├── pom.xml                   # Maven 配置
│   ├── mvnw / mvnw.cmd           # Maven Wrapper
│   └── src/
│       ├── main/
│       │   ├── java/com/leansofx/qaservicequestion/
│       │   │   ├── QaServiceQuestionApplication.java  # 启动类
│       │   │   └── (其他代码待开发)
│       │   └── resources/
│       │       └── application.properties
│       └── test/
│           └── java/com/leansofx/qaservicequestion/
│               └── QaServiceQuestionApplicationTests.java
│
└── qa-service-statistic/         # 统计服务 (待开发)
    └── (空目录)
```

### 2.2 模块功能说明

#### qa-service-user（用户服务）

| 模块 | 功能 | 关键文件 |
|------|------|----------|
| **启动模块** | Spring Boot 应用启动 | `QaServiceUserApplication.java` |
| **配置模块** | CORS 跨域配置 | `config/CorsConfig.java` |
| **控制层** | REST API 接口 | `controller/TestController.java` |
| **监控模块** | Actuator 健康检查 | `application.properties` |

#### qa-service-question（问题服务）

| 模块 | 功能 | 关键文件 |
|------|------|----------|
| **启动模块** | Spring Boot 应用启动 | `QaServiceQuestionApplication.java` |
| **测试模块** | Testcontainers 集成测试 | `TestcontainersConfiguration.java` |

#### qa-service-statistic（统计服务）

当前为空目录，待开发。

### 2.3 模块依赖关系

```
┌─────────────────────────────────────────────────────────────┐
│                    前端应用 (qa-web)                         │
│                   http://localhost:5173                     │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼ (CORS 跨域请求)
        ┌────────────────┼────────────────┐
        │                │                │
        ▼                ▼                ▼
┌───────────────┐ ┌───────────────┐ ┌───────────────┐
│ qa-service- │ │ qa-service-   │ │ qa-service-   │
│ user        │ │ question      │ │ statistic   │
│ :8080       │ │ :8081         │ │ (待开发)      │
└───────────────┘ └───────────────┘ └───────────────┘
        │                │                │
        │                │                │
        ▼                ▼                ▼
   ┌─────────┐     ┌─────────┐     ┌─────────┐
   │ User    │     │Question │     │Statistic│
   │ Mgmt    │     │ Service │     │ Service │
   └─────────┘     └─────────┘     └─────────┘
```

---

## 三、接口分析

### 3.1 qa-service-user 接口

#### REST API 接口

| 接口路径 | 方法 | 功能 | 请求参数 | 响应 |
|----------|------|------|----------|------|
| `/api/test/cors` | GET | CORS 配置测试 | 无 | `{message, timestamp, service}` |
| `/api/test/cors` | POST | POST 请求测试 | `Map<String, Object>` | `{message, receivedData, timestamp, service}` |
| `/api/test/cors` | OPTIONS | CORS 预检请求 | - | - |

#### Actuator 监控端点

| 端点路径 | 方法 | 功能 |
|----------|------|------|
| `/actuator/health` | GET | 应用健康状态 |
| `/actuator/info` | GET | 应用信息 |
| `/actuator/metrics` | GET | 应用指标 |
| `/actuator/env` | GET | 环境变量 |
| `/actuator/beans` | GET | Spring Bean 列表 |
| `/actuator/loggers` | GET | 日志配置 |

### 3.2 接口代码示例

**TestController.java**:
```java
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/cors")
    public Map<String, Object> testCors() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CORS configuration is working!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", "qa-service-user");
        return response;
    }

    @PostMapping("/cors")
    public Map<String, Object> testCorsPost(@RequestBody(required = false) Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "POST request with CORS is working!");
        response.put("receivedData", data);
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", "qa-service-user");
        return response;
    }
}
```

### 3.3 CORS 跨域配置

**CorsConfig.java**:
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")           // 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")                  // 允许所有头部
                .allowCredentials(true)               // 允许携带凭证
                .maxAge(3600);                        // 预检缓存时间
    }
}
```

---

## 四、功能与业务流程

### 4.1 当前功能状态

| 服务 | 功能 | 状态 | 说明 |
|------|------|------|------|
| qa-service-user | CORS 跨域支持 | ✅ 已开发 | 配置类已实现 |
| qa-service-user | 测试接口 | ✅ 已开发 | TestController |
| qa-service-user | 健康监控 | ✅ 已开发 | Actuator |
| qa-service-user | 用户管理 | ⬜ 待开发 | 需添加实体和 Service |
| qa-service-question | 基础框架 | ✅ 已开发 | 启动类可用 |
| qa-service-question | 问题管理 | ⬜ 待开发 | 需添加业务代码 |
| qa-service-statistic | 统计服务 | ⬜ 待开发 | 空目录 |

### 4.2 与前端的数据交互流程

```
┌──────────────────────────────────────────────────────────────┐
│                      前端应用                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ 患者问诊    │  │ 医生登录    │  │ 医生诊室管理        │  │
│  └──────┬──────┘  └──────┬──────┘  └──────────┬──────────┘  │
└─────────┼────────────────┼────────────────────┼─────────────┘
          │                │                    │
          ▼                ▼                    ▼
    ┌─────────────────────────────────────────────────────────┐
    │                    REST API 请求                        │
    │              (CORS 跨域支持已配置)                       │
    └─────────────────────────────────────────────────────────┘
          │                │                    │
          ▼                ▼                    ▼
   ┌─────────────┐  ┌─────────────┐    ┌─────────────┐
   │ qa-service- │  │ qa-service- │    │ qa-service- │
   │ user        │  │ question    │    │ statistic   │
   │ :8080       │  │ :8081       │    │ (待开发)    │
   └─────────────┘  └─────────────┘    └─────────────┘
```

### 4.3 当前数据流转说明

**注意**: 当前后端服务仅提供基础框架和测试接口，实际业务数据存储在前端本地（内存存储）。

```
当前状态:
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   前端      │────▶│ 后端 API    │────▶│   响应      │
│ (内存数据)  │◀────│ (测试接口)  │◀────│ (测试数据)  │
└─────────────┘     └─────────────┘     └─────────────┘

未来规划:
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   前端      │────▶│ 后端 API    │────▶│  数据库     │
│             │◀────│             │◀────│ (MySQL等)   │
└─────────────┘     └─────────────┘     └─────────────┘
```

---

## 五、关联代码与调用链

### 5.1 启动调用链

```
用户执行 ./start.sh
  │
  ▼
start.sh
  ├── 检查 PID 文件
  ├── 检查 JAR 文件存在性
  ├── nohup java -jar xxx.jar &
  │     │
  │     ▼
  │   QaServiceUserApplication.main()
  │     │
  │     ▼
  │   SpringApplication.run()
  │     ├── 创建 ApplicationContext
  │     ├── 加载自动配置
  │     ├── 扫描 @Component
  │     │     ├── CorsConfig (跨域配置)
  │     │     └── TestController (控制器)
  │     └── 启动 Tomcat 内嵌服务器
  │           └── 监听端口 8080
  │
  └── 保存 PID 到文件
```

### 5.2 HTTP 请求处理调用链

```
HTTP Request: GET /api/test/cors
  │
  ▼
Tomcat (内嵌服务器)
  │
  ▼
DispatcherServlet
  │
  ▼
HandlerMapping (路由映射)
  │
  ▼
TestController.testCors()
  ├── 创建响应 Map
  ├── 设置 message/timestamp/service
  │
  ▼
Response Body (JSON)
```

### 5.3 CORS 请求处理流程

```
浏览器发送 CORS 请求
  │
  ├─ 简单请求 ───────────────────────────────┐
  │                                          │
  ▼                                          │
CorsConfig.addCorsMappings()                │
  ├── 添加响应头 Access-Control-Allow-Origin │
  ├── Access-Control-Allow-Methods           │
  └── Access-Control-Allow-Headers           │
                                             │
  ├─ 预检请求 (OPTIONS) ─────────────────────┤
  │                                          │
  ▼                                          │
CorsConfig.corsConfigurationSource()        │
  └── 返回 CORS 配置                         │
                                             ▼
                                   请求到达 Controller
```

---

## 六、关键配置文件说明

### 6.1 pom.xml (qa-service-user)

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.7</version>
</parent>

<dependencies>
    <!-- Web 开发 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- 监控管理 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <!-- 测试 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 6.2 pom.xml (qa-service-question)

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- Testcontainers 测试 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-testcontainers</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 6.3 application.properties (qa-service-user)

```properties
# 应用名称
spring.application.name=qa-service-user

# 服务器端口
server.port=8080

# Actuator 配置
management.endpoints.web.exposure.include=health,info,metrics,env,beans,loggers
management.endpoint.health.show-details=always

# 应用信息
info.app.name=qa-service-user
info.app.description=QA Service User - Healthcare QA System User Management Service
info.app.version=0.0.1-SNAPSHOT
```

### 6.4 application.properties (qa-service-question)

```properties
spring.application.name=qa-service-question
server.port=8081
```

---

## 七、服务间关系与通信

### 7.1 当前服务状态

```
┌─────────────────────────────────────────────────────────────┐
│                      微服务架构                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────┐                                        │
│  │ qa-service-user │  ✅ 基础框架完成                       │
│  │    用户服务      │     - 启动类                          │
│  │    Port: 8080   │     - CORS 配置                       │
│  │                 │     - 测试接口                        │
│  │                 │     - Actuator 监控                   │
│  └─────────────────┘                                        │
│                                                             │
│  ┌─────────────────┐                                        │
│  │qa-service-question│ ✅ 基础框架完成                      │
│  │    问题服务      │     - 启动类                          │
│  │    Port: 8081   │     - Testcontainers 测试配置         │
│  │                 │     ⬜ 业务代码待开发                  │
│  └─────────────────┘                                        │
│                                                             │
│  ┌─────────────────┐                                        │
│  │qa-service-statistic│ ⬜ 待开发                           │
│  │    统计服务      │                                        │
│  │    Port: -      │                                        │
│  └─────────────────┘                                        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 7.2 未来服务通信规划

```
前端应用 (:5173)
    │
    ├──▶ API Gateway (可选)
    │
    ├──▶ qa-service-user (:8080) ────┐
    │                                 │
    ├──▶ qa-service-question (:8081)─┼──▶ 服务间通信 (Feign/HTTP)
    │                                 │
    └──▶ qa-service-statistic (:8082)┘
```

---

## 八、测试相关

### 8.1 测试类结构

| 服务 | 测试类 | 测试内容 |
|------|--------|----------|
| qa-service-user | QaServiceUserApplicationTests.java | Spring 上下文加载测试 |
| qa-service-question | QaServiceQuestionApplicationTests.java | Spring 上下文加载测试 |
| qa-service-question | TestcontainersConfiguration.java | 测试容器配置 |

### 8.2 运行测试

```bash
# 运行所有测试
./mvnw test

# 运行单个测试类
./mvnw test -Dtest=QaServiceUserApplicationTests
```

### 8.3 测试容器配置

```java
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    // Testcontainers 配置
}
```

---

## 九、开发注意事项

### 9.1 端口占用

| 服务 | 端口 | 冲突处理 |
|------|------|----------|
| qa-service-user | 8080 | 修改 application.properties |
| qa-service-question | 8081 | 修改 application.properties |
| qa-service-statistic | 待定 | 需规划 |

### 9.2 开发顺序建议

1. **第一阶段**: 完成 qa-service-user 用户管理功能
   - User 实体类
   - UserRepository 数据访问
   - UserService 业务逻辑
   - UserController REST 接口

2. **第二阶段**: 完成 qa-service-question 问题管理功能
   - Question 实体类
   - QuestionService 业务逻辑
   - QuestionController REST 接口

3. **第三阶段**: 完成 qa-service-statistic 统计服务
   - 数据统计接口
   - 报表功能

### 9.3 与前端联调

前端当前使用内存存储，后端提供测试接口：

```javascript
// 前端可调用后端测试接口验证 CORS
fetch('http://localhost:8080/api/test/cors')
  .then(res => res.json())
  .then(data => console.log(data));
```

---

## 十、技术栈详细说明

### 10.1 核心依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| spring-boot-starter-parent | 3.5.7 | 父 POM，管理依赖版本 |
| spring-boot-starter-web | 3.5.7 | Web 开发，REST API |
| spring-boot-starter-actuator | 3.5.7 | 监控和管理端点 |
| spring-boot-starter-test | 3.5.7 | 测试框架 (JUnit 5) |
| spring-boot-testcontainers | 3.5.7 | Testcontainers 支持 |

### 10.2 构建工具

| 工具 | 版本 | 说明 |
|------|------|------|
| Maven | 3.9+ | 项目构建和依赖管理 |
| Java | 17 | 运行时环境 |
| Maven Wrapper | - | 无需本地安装 Maven |

### 10.3 依赖关系图

```
spring-boot-starter-parent (3.5.7)
    │
    ├── spring-boot-starter-web
    │       ├── spring-boot-starter
    │       ├── spring-boot-starter-json
    │       ├── spring-boot-starter-tomcat
    │       └── spring-webmvc
    │
    ├── spring-boot-starter-actuator
    │       └── spring-boot-actuator-autoconfigure
    │
    └── spring-boot-starter-test
            ├── junit-jupiter
            ├── mockito
            └── assertj
```

---

## 十一、附录：快速参考

### 11.1 常用命令

```bash
# 构建
./mvnw clean package

# 运行
./mvnw spring-boot:run

# 测试
./mvnw test

# 启动服务 (qa-service-user)
cd qa-service-user && ./start.sh

# 停止服务
cd qa-service-user && ./stop.sh
```

### 11.2 关键文件索引

| 用途 | 文件路径 |
|------|----------|
| 用户服务启动类 | `qa-service-user/src/main/java/.../QaServiceUserApplication.java` |
| 问题服务启动类 | `qa-service-question/src/main/java/.../QaServiceQuestionApplication.java` |
| 跨域配置 | `qa-service-user/src/main/java/.../config/CorsConfig.java` |
| 测试控制器 | `qa-service-user/src/main/java/.../controller/TestController.java` |
| 用户服务配置 | `qa-service-user/src/main/resources/application.properties` |
| 问题服务配置 | `qa-service-question/src/main/resources/application.properties` |
