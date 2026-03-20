# 企业级 Spring Boot 项目文件结构规范

## 重要说明

**本文档是标准企业级 Spring Boot 项目的官方文件结构规范，所有开发者必须严格遵守以下规则：**

1. **强制性规范**: 所有新增文件必须按照本规范放置在对应目录中
2. **命名约定**: 遵循 Java 和 Spring Boot 的标准命名规范
3. **分层架构**: 严格按照 Controller → Service → Repository → Entity 的分层结构开发
4. **包结构**: 按功能模块和技术层次组织包结构
5. **文档更新**: 当添加新的目录结构时，必须同步更新本文档

**违反本规范的代码将不被接受合并到主分支！**

### 状态标记说明
- `[必需]` - 项目核心必须存在的文件/目录
- `[可选]` - 根据需要创建的扩展结构
- `[示例]` - 展示命名规范的示例文件

---

## 标准项目文件结构

```
project-name/                                       # 项目根目录
├── .gitattributes                                  # [必需] Git 属性配置文件
├── .gitignore                                      # [必需] Git 忽略文件配置
├── README.md                                       # [必需] 项目说明文档
├── LICENSE                                         # [可选] 开源协议文件
├── CHANGELOG.md                                    # [可选] 版本变更日志
├── mvnw                                            # [必需] Maven Wrapper 脚本(Unix/Linux)
├── mvnw.cmd                                        # [必需] Maven Wrapper 脚本(Windows)
├── pom.xml                                         # [必需] Maven 项目配置文件
├── .mvn/                                           # [必需] Maven Wrapper 配置目录
│   └── wrapper/                                    # [必需] Maven Wrapper 配置
│       └── maven-wrapper.properties                # [必需] Maven Wrapper 属性配置
├── docs/                                           # [必需] 项目文档目录
│   ├── project-structure.md                        # [必需] 项目结构规范文档
│   ├── api.md                                      # [可选] API 接口文档
│   ├── architecture.md                             # [可选] 架构设计文档
│   ├── design/                                     # [可选] 设计文档目录
│   │   ├── database-design.md                      # [示例] 数据库设计文档
│   │   ├── sequence-diagrams/                      # [示例] 时序图目录
│   │   └── class-diagrams/                         # [示例] 类图目录
│   └── deployment/                                 # [可选] 部署文档目录
│       ├── docker-deployment.md                    # [示例] Docker 部署文档
│       └── k8s-deployment.md                       # [示例] Kubernetes 部署文档
├── docker/                                         # [可选] Docker 相关文件目录
│   ├── Dockerfile                                  # [示例] Docker 构建文件
│   ├── docker-compose.yml                          # [示例] Docker Compose 配置
│   └── .dockerignore                               # [示例] Docker 忽略文件
├── scripts/                                        # [可选] 脚本文件目录
│   ├── build.sh                                    # [示例] 构建脚本
│   ├── start.sh                                    # [示例] 启动脚本
│   ├── stop.sh                                     # [示例] 停止脚本
│   └── db-migrate.sh                               # [示例] 数据库迁移脚本
├── config/                                         # [可选] 外部配置目录
│   ├── application-prod.yml                        # [示例] 生产环境配置
│   └── logback-prod.xml                            # [示例] 生产日志配置
└── src/                                            # [必需] 源代码目录
    ├── main/                                       # [必需] 主要源代码
    │   ├── java/                                   # [必需] Java 源代码
    │   │   └── com/company/projectname/            # [必需] 根包目录
    │   │       ├── Application.java                # [必需] Spring Boot 启动类
    │   │       │
    │   │       ├── config/                         # [必需] 配置类目录
    │   │       │   ├── WebConfig.java              # [示例] Web 配置类
    │   │       │   ├── CorsConfig.java             # [示例] CORS 跨域配置
    │   │       │   ├── SecurityConfig.java         # [示例] 安全配置类
    │   │       │   ├── SwaggerConfig.java          # [示例] API 文档配置
    │   │       │   ├── JacksonConfig.java          # [示例] JSON 序列化配置
    │   │       │   ├── RedisConfig.java            # [示例] Redis 配置类
    │   │       │   ├── RabbitConfig.java           # [示例] 消息队列配置
    │   │       │   ├── ThreadPoolConfig.java       # [示例] 线程池配置
    │   │       │   └── properties/                 # [可选] 配置属性类目录
    │   │       │       └── AppProperties.java      # [示例] 应用配置属性类
    │   │       │
    │   │       ├── controller/                     # [必需] 控制器层目录
    │   │       │   ├── UserController.java         # [示例] 用户控制器
    │   │       │   ├── OrderController.java        # [示例] 订单控制器
    │   │       │   └── advice/                     # [必需] 全局异常处理目录
    │   │       │       ├── GlobalExceptionHandler.java    # [示例] 全局异常处理器
    │   │       │       ├── BusinessException.java         # [示例] 业务异常类
    │   │       │       └── ErrorResponse.java             # [示例] 错误响应类
    │   │       │
    │   │       ├── service/                        # [必需] 服务层目录
    │   │       │   ├── UserService.java            # [示例] 用户服务接口
    │   │       │   ├── OrderService.java           # [示例] 订单服务接口
    │   │       │   └── impl/                       # [必需] 服务实现类目录
    │   │       │       ├── UserServiceImpl.java    # [示例] 用户服务实现
    │   │       │       └── OrderServiceImpl.java   # [示例] 订单服务实现
    │   │       │
    │   │       ├── repository/                     # [必需] 数据访问层目录
    │   │       │   ├── UserRepository.java         # [示例] 用户数据仓库
    │   │       │   ├── OrderRepository.java        # [示例] 订单数据仓库
    │   │       │   └── custom/                     # [可选] 自定义查询目录
    │   │       │       ├── UserRepositoryCustom.java      # [示例] 自定义查询接口
    │   │       │       └── UserRepositoryImpl.java        # [示例] 自定义查询实现
    │   │       │
    │   │       ├── entity/                         # [必需] 实体类目录
    │   │       │   ├── User.java                   # [示例] 用户实体
    │   │       │   ├── Order.java                  # [示例] 订单实体
    │   │       │   ├── BaseEntity.java             # [示例] 基础实体类
    │   │       │   └── audit/                      # [可选] 审计相关目录
    │   │       │       └── AuditableEntity.java    # [示例] 审计实体基类
    │   │       │
    │   │       ├── dto/                            # [必需] 数据传输对象目录
    │   │       │   ├── request/                    # [必需] 请求 DTO 目录
    │   │       │   │   ├── UserCreateRequest.java  # [示例] 用户创建请求
    │   │       │   │   ├── UserUpdateRequest.java  # [示例] 用户更新请求
    │   │       │   │   └── UserQueryRequest.java   # [示例] 用户查询请求
    │   │       │   ├── response/                   # [必需] 响应 DTO 目录
    │   │       │   │   ├── UserResponse.java       # [示例] 用户响应
    │   │       │   │   ├── UserListResponse.java   # [示例] 用户列表响应
    │   │       │   │   └── PageResponse.java       # [示例] 分页响应
    │   │       │   └── converter/                  # [可选] DTO 转换器目录
    │   │       │       ├── UserConverter.java      # [示例] 用户转换器
    │   │       │       └── OrderConverter.java     # [示例] 订单转换器
    │   │       │
    │   │       ├── vo/                             # [可选] 值对象目录
    │   │       │   ├── UserVO.java                 # [示例] 用户值对象
    │   │       │   └── OrderVO.java                # [示例] 订单值对象
    │   │       │
    │   │       ├── mapper/                         # [可选] 数据映射层目录
    │   │       │   ├── UserMapper.java             # [示例] 用户 Mapper 接口
    │   │       │   └── xml/                        # [可选] Mapper XML 目录
    │   │       │       └── UserMapper.xml          # [示例] 用户 Mapper XML
    │   │       │
    │   │       ├── exception/                      # [必需] 自定义异常目录
    │   │       │   ├── BusinessException.java      # [示例] 业务异常
    │   │       │   ├── NotFoundException.java      # [示例] 资源不存在异常
    │   │       │   └── UnauthorizedException.java  # [示例] 未授权异常
    │   │       │
    │   │       ├── enums/                          # [必需] 枚举类目录
    │   │       │   ├── UserStatus.java             # [示例] 用户状态枚举
    │   │       │   ├── OrderStatus.java            # [示例] 订单状态枚举
    │   │       │   └── ErrorCode.java              # [示例] 错误码枚举
    │   │       │
    │   │       ├── util/                           # [必需] 工具类目录
    │   │       │   ├── DateUtil.java               # [示例] 日期工具类
    │   │       │   ├── JsonUtil.java               # [示例] JSON 工具类
    │   │       │   ├── JwtUtil.java                # [示例] JWT 工具类
    │   │       │   ├── EncryptUtil.java            # [示例] 加密工具类
    │   │       │   └── HttpUtil.java               # [示例] HTTP 工具类
    │   │       │
    │   │       ├── constant/                       # [必需] 常量类目录
    │   │       │   ├── ApiConstants.java           # [示例] API 常量
    │   │       │   ├── CacheConstants.java         # [示例] 缓存常量
    │   │       │   └── MessageConstants.java       # [示例] 消息常量
    │   │       │
    │   │       ├── security/                       # [可选] 安全相关目录
    │   │       │   ├── JwtTokenProvider.java       # [示例] JWT Token 提供者
    │   │       │   ├── JwtAuthenticationFilter.java # [示例] JWT 认证过滤器
    │   │       │   ├── UserDetailsServiceImpl.java # [示例] 用户详情服务
    │   │       │   └── PermissionEvaluator.java    # [示例] 权限评估器
    │   │       │
    │   │       ├── aspect/                         # [可选] 切面编程目录
    │   │       │   ├── LoggingAspect.java          # [示例] 日志切面
    │   │       │   ├── PerformanceAspect.java      # [示例] 性能监控切面
    │   │       │   └── TransactionAspect.java      # [示例] 事务切面
    │   │       │
    │   │       ├── validation/                     # [可选] 自定义验证目录
    │   │       │   ├── annotation/                 # [必需] 验证注解目录
    │   │       │   │   ├── ValidEmail.java         # [示例] 邮箱验证注解
    │   │       │   │   └── ValidPhone.java         # [示例] 手机号验证注解
    │   │       │   └── validator/                  # [必需] 验证器目录
    │   │       │       ├── EmailValidator.java     # [示例] 邮箱验证器
    │   │       │       └── PhoneValidator.java     # [示例] 手机号验证器
    │   │       │
    │   │       ├── event/                          # [可选] 事件处理目录
    │   │       │   ├── UserCreatedEvent.java       # [示例] 用户创建事件
    │   │       │   ├── UserEventListener.java      # [示例] 用户事件监听器
    │   │       │   └── UserEventPublisher.java     # [示例] 用户事件发布器
    │   │       │
    │   │       ├── job/                            # [可选] 定时任务目录
    │   │       │   ├── UserSyncJob.java            # [示例] 用户同步任务
    │   │       │   └── OrderCleanupJob.java        # [示例] 订单清理任务
    │   │       │
    │   │       ├── listener/                       # [可选] 消息监听器目录
    │   │       │   ├── OrderMessageListener.java   # [示例] 订单消息监听器
    │   │       │   └── NotificationListener.java   # [示例] 通知监听器
    │   │       │
    │   │       └── component/                      # [可选] Spring 组件目录
    │   │           ├── RedisCache.java             # [示例] Redis 缓存组件
    │   │           ├── EmailSender.java            # [示例] 邮件发送组件
    │   │           └── SmsSender.java              # [示例] 短信发送组件
    │   │
    │   └── resources/                              # [必需] 资源文件目录
    │       ├── application.yml                     # [必需] Spring Boot 主配置文件
    │       ├── application-dev.yml                 # [必需] 开发环境配置
    │       ├── application-test.yml                # [必需] 测试环境配置
    │       ├── application-prod.yml                # [必需] 生产环境配置
    │       │
    │       ├── db/                                 # [可选] 数据库相关目录
    │       │   ├── migration/                      # [可选] 数据库迁移脚本目录
    │       │   │   ├── V1__Create_user_table.sql   # [示例] 数据库迁移脚本
    │       │   │   └── V2__Create_order_table.sql  # [示例] 数据库迁移脚本
    │       │   └── data/                           # [可选] 初始化数据目录
    │       │       └── init-data.sql               # [示例] 初始化数据脚本
    │       │
    │       ├── mybatis/                            # [可选] MyBatis 配置目录
    │       │   └── mybatis-config.xml              # [示例] MyBatis 配置文件
    │       │
    │       ├── static/                             # [可选] 静态资源目录
    │       │   ├── css/                            # [可选] CSS 文件目录
    │       │   ├── js/                             # [可选] JS 文件目录
    │       │   └── images/                         # [可选] 图片目录
    │       │
    │       ├── templates/                          # [可选] 模板文件目录
    │       │   ├── email/                          # [可选] 邮件模板目录
    │       │   │   ├── welcome.html                # [示例] 欢迎邮件模板
    │       │   │   └── notification.html           # [示例] 通知邮件模板
    │       │   └── pdf/                            # [可选] PDF 模板目录
    │       │       └── report-template.html        # [示例] 报表模板
    │       │
    │       ├── i18n/                               # [可选] 国际化资源目录
    │       │   ├── messages.properties             # [示例] 默认语言资源
    │       │   ├── messages_zh_CN.properties       # [示例] 中文资源
    │       │   └── messages_en_US.properties       # [示例] 英文资源
    │       │
    │       ├── logback-spring.xml                  # [可选] 日志配置文件
    │       ├── banner.txt                          # [可选] 自定义启动 Banner
    │       └── META-INF/                           # [可选] 元信息目录
    │           └── spring.factories                # [示例] Spring 自动配置
    │
    └── test/                                       # [必需] 测试代码目录
        ├── java/                                   # [必需] Java 测试代码
        │   └── com/company/projectname/            # [必需] 测试根包目录
        │       ├── ApplicationTests.java           # [必需] 应用启动测试
        │       ├── TestcontainersConfiguration.java # [可选] Testcontainers 配置
        │       │
        │       ├── controller/                     # [必需] 控制器测试目录
        │       │   ├── UserControllerTest.java     # [示例] 用户控制器测试
        │       │   └── integration/                # [可选] 集成测试目录
        │       │       └── UserControllerIntegrationTest.java  # [示例] 集成测试
        │       │
        │       ├── service/                        # [必需] 服务层测试目录
        │       │   ├── UserServiceTest.java        # [示例] 用户服务测试
        │       │   └── OrderServiceTest.java       # [示例] 订单服务测试
        │       │
        │       ├── repository/                     # [必需] 数据访问层测试目录
        │       │   ├── UserRepositoryTest.java     # [示例] 用户仓库测试
        │       │   └── OrderRepositoryTest.java    # [示例] 订单仓库测试
        │       │
        │       ├── util/                           # [可选] 工具类测试目录
        │       │   ├── DateUtilTest.java           # [示例] 日期工具测试
        │       │   └── JsonUtilTest.java           # [示例] JSON 工具测试
        │       │
        │       ├── config/                         # [可选] 测试配置目录
        │       │   └── TestConfig.java             # [示例] 测试配置类
        │       │
        │       └── fixture/                        # [可选] 测试夹具目录
        │           ├── UserFixture.java            # [示例] 用户测试数据
        │           └── OrderFixture.java           # [示例] 订单测试数据
        │
        └── resources/                              # [可选] 测试资源目录
            ├── application-test.yml                # [必需] 测试环境配置
            ├── test-data/                          # [可选] 测试数据目录
            │   ├── users.json                      # [示例] 用户测试数据
            │   └── orders.json                     # [示例] 订单测试数据
            └── db/                                 # [可选] 测试数据库目录
                └── test-data.sql                   # [示例] 测试 SQL 脚本
```

---

## 目录说明

### 状态标记详解
- **[必需]**: 项目核心必须存在的文件/目录，是 Spring Boot 项目的基础结构
- **[可选]**: 根据业务需要可以创建的扩展结构，按项目发展逐步添加
- **[示例]**: 展示命名规范的示例文件，实际开发时按需创建

### 核心目录说明

| 目录 | 说明 | 用途 |
|------|------|------|
| `config/` | 配置类目录 | 存放 Spring 配置、跨域、安全等配置类 |
| `controller/` | 控制器层 | 处理 HTTP 请求，参数校验，响应封装 |
| `service/` | 服务层 | 业务逻辑处理，事务管理 |
| `repository/` | 数据访问层 | 数据库访问，CRUD 操作 |
| `entity/` | 实体层 | JPA 实体类，与数据库表对应 |
| `dto/` | 数据传输对象 | 请求/响应数据的封装 |
| `vo/` | 值对象 | 视图层数据展示对象 |
| `mapper/` | 数据映射层 | MyBatis Mapper 接口和 XML |
| `exception/` | 异常处理 | 自定义业务异常 |
| `enums/` | 枚举类 | 状态、类型等枚举定义 |
| `util/` | 工具类 | 通用工具方法 |
| `constant/` | 常量类 | 系统常量定义 |
| `aspect/` | 切面编程 | 日志、性能监控等切面 |
| `validation/` | 自定义验证 | 参数校验注解和验证器 |
| `security/` | 安全相关 | JWT、认证、授权等 |
| `event/` | 事件处理 | Spring 事件发布和监听 |
| `job/` | 定时任务 | 定时任务定义 |
| `listener/` | 消息监听 | 消息队列监听器 |

---

## 开发规范

### 1. 包命名规范

- **根包**: `com.company.projectname`（公司域名倒序 + 项目名称）
- **子包**: 按技术层次划分（`config`, `controller`, `service`, `repository`, `entity` 等）
- **业务包**: 在技术层次下按业务模块划分（如 `user`, `order`, `product` 等）

```
com.company.projectname
├── config          # 配置类
├── controller      # 控制器
│   └── user        # 用户模块控制器
├── service         # 服务层
│   └── user        # 用户模块服务
├── repository      # 数据访问层
│   └── user        # 用户模块仓库
├── entity          # 实体类
│   └── user        # 用户模块实体
└── dto             # 数据传输对象
    └── user        # 用户模块 DTO
```

### 2. 类命名规范

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| **启动类** | `Application` | `Application.java` |
| **配置类** | 以 `Config` 结尾 | `WebConfig`, `SecurityConfig` |
| **控制器** | 以 `Controller` 结尾 | `UserController`, `OrderController` |
| **服务接口** | 业务名词 + `Service` | `UserService`, `OrderService` |
| **服务实现** | 接口名 + `Impl` | `UserServiceImpl`, `OrderServiceImpl` |
| **数据仓库** | 以 `Repository` 结尾 | `UserRepository`, `OrderRepository` |
| **Mapper** | 以 `Mapper` 结尾 | `UserMapper`, `OrderMapper` |
| **实体类** | 业务名词 | `User`, `Order`, `Product` |
| **DTO 请求** | 以 `Request` 结尾 | `UserCreateRequest`, `UserUpdateRequest` |
| **DTO 响应** | 以 `Response` 结尾 | `UserResponse`, `UserListResponse` |
| **VO** | 以 `VO` 结尾 | `UserVO`, `OrderVO` |
| **异常类** | 以 `Exception` 结尾 | `BusinessException`, `NotFoundException` |
| **枚举类** | 业务名词 + 状态/类型 | `UserStatus`, `OrderStatus` |
| **工具类** | 以 `Util` 结尾 | `DateUtil`, `JsonUtil`, `JwtUtil` |
| **常量类** | 以 `Constants` 结尾 | `ApiConstants`, `CacheConstants` |
| **切面类** | 以 `Aspect` 结尾 | `LoggingAspect`, `PerformanceAspect` |
| **验证注解** | 以 `Valid` 开头 | `ValidEmail`, `ValidPhone` |
| **验证器** | 以 `Validator` 结尾 | `EmailValidator`, `PhoneValidator` |
| **任务类** | 以 `Job` 结尾 | `UserSyncJob`, `OrderCleanupJob` |
| **监听器** | 以 `Listener` 结尾 | `UserEventListener`, `OrderMessageListener` |
| **转换器** | 以 `Converter` 结尾 | `UserConverter`, `OrderConverter` |
| **事件类** | 以 `Event` 结尾 | `UserCreatedEvent`, `OrderPaidEvent` |

### 3. 方法命名规范

| 操作类型 | 命名前缀 | 示例 |
|----------|----------|------|
| **查询单个** | `get`, `find`, `query` | `getById`, `findByUsername` |
| **查询列表** | `list`, `findAll` | `listByStatus`, `findAllActive` |
| **分页查询** | `page`, `listPage` | `pageByCondition`, `listPage` |
| **创建** | `create`, `save`, `add` | `createUser`, `saveOrder` |
| **更新** | `update`, `modify` | `updateStatus`, `modifyInfo` |
| **删除** | `delete`, `remove` | `deleteById`, `removeUser` |
| **校验** | `validate`, `check` | `validateEmail`, `checkPermission` |
| **转换** | `convert`, `to`, `from` | `convertToDTO`, `toResponse` |
| **发送** | `send`, `notify` | `sendEmail`, `notifyUser` |

### 4. 配置文件规范

```yaml
# application.yml 主配置文件
spring:
  application:
    name: project-name
  profiles:
    active: dev  # 默认激活 dev 环境

# 公共配置
server:
  port: 8080
  servlet:
    context-path: /api

# 日志配置
logging:
  level:
    root: INFO
    com.company.projectname: DEBUG
```

#### 环境配置分离

- `application-dev.yml` - 开发环境
- `application-test.yml` - 测试环境
- `application-prod.yml` - 生产环境

#### 配置优先级

1. 命令行参数
2. `java:comp/env` 的 JNDI 属性
3. Java 系统属性（System.getProperties()）
4. 操作系统环境变量
5. `application-{profile}.yml`
6. `application.yml`

### 5. 文件组织原则

- **单一职责**: 每个类只负责一个功能
- **分层清晰**: 严格按照 MVC 分层架构组织
- **模块化**: 相关功能放在同一包下
- **可测试**: 每个类都应该有对应的测试类
- **开闭原则**: 对扩展开放，对修改关闭

---

## 分层架构说明

### 标准分层架构

```
┌─────────────────────────────────────────────────────────────┐
│                        Controller 层                        │
│              处理 HTTP 请求，参数校验，响应封装               │
│                    [UserController]                         │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                         Service 层                          │
│              业务逻辑处理，事务管理，数据组装                 │
│              [UserService] → [UserServiceImpl]              │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                        Repository 层                        │
│              数据访问，CRUD 操作，数据查询                    │
│                  [UserRepository]                           │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                          Entity 层                          │
│              数据库实体映射，领域模型                         │
│                     [User]                                  │
└─────────────────────────────────────────────────────────────┘
```

### 分层职责

| 层级 | 职责 | 禁止操作 |
|------|------|----------|
| **Controller** | 接收请求、参数校验、调用 Service、返回响应 | 直接操作数据库、处理业务逻辑 |
| **Service** | 业务逻辑处理、事务控制、调用 Repository | 直接处理 HTTP 请求、响应 |
| **Repository** | 数据持久化、数据库操作 | 包含业务逻辑 |
| **Entity** | 定义数据模型、映射数据库表 | 包含业务方法 |

### 数据流转

```
Request → Controller → DTO → Service → Entity → Repository → Database
                                         ↓
Response ← Controller ← DTO ← Service ← Entity ← Repository ← Database
```

---

## 扩展指导

### 新增功能模块步骤

1. **确定功能模块**: 明确新功能属于哪个业务模块
2. **选择技术层次**: 确定需要在哪些层次添加代码
3. **创建对应目录**: 如果目录不存在，按照本规范创建
4. **编写代码**: 遵循命名规范和编码标准
5. **添加测试**: 为新功能编写对应的测试用例
6. **更新文档**: 如有必要，更新相关文档

### 新增业务模块示例

以添加 `product` 模块为例：

```
1. 创建 Controller
   └── controller/ProductController.java

2. 创建 Service
   ├── service/ProductService.java
   └── service/impl/ProductServiceImpl.java

3. 创建 Repository
   └── repository/ProductRepository.java

4. 创建 Entity
   └── entity/Product.java

5. 创建 DTO
   ├── dto/request/ProductCreateRequest.java
   ├── dto/request/ProductUpdateRequest.java
   └── dto/response/ProductResponse.java

6. 创建测试
   └── test/.../controller/ProductControllerTest.java
   └── test/.../service/ProductServiceTest.java
```

---

## 常用依赖推荐

```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- 数据访问 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- MyBatis -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
    </dependency>
    
    <!-- 安全 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- 验证 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- 缓存 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- 消息队列 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
    
    <!-- 监控 -->
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

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0.0 | 2026-03-20 | 初始版本，标准企业级 Spring Boot 项目结构规范 |

---

**注意**: 本文档会随着项目发展持续更新，请定期查看最新版本。如有疑问或建议，请联系项目负责人。
