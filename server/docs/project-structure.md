# 企业级 Spring Boot 项目目录结构规范

## 1. 概述

本文档定义了标准的企业级 Spring Boot 项目目录结构规范，适用于单体应用和微服务架构。良好的目录结构有助于代码维护、团队协作和项目扩展。

### 1.1 设计原则

| 原则 | 说明 |
|------|------|
| **清晰分层** | 按职责分层，代码边界清晰 |
| **模块化** | 高内聚低耦合，便于维护和测试 |
| **可扩展** | 预留扩展点，支持业务增长 |
| **规范统一** | 团队遵循统一标准，降低沟通成本 |

---

## 2. 标准目录结构

### 2.1 完整目录树

```
project-root/                                    # 项目根目录
│
├── pom.xml                                      # Maven 构建配置
├── build.gradle / settings.gradle              # Gradle 构建配置 (可选)
├── mvnw / mvnw.cmd                             # Maven Wrapper 脚本
├── gradlew / gradlew.bat                       # Gradle Wrapper 脚本 (可选)
│
├── README.md                                    # 项目说明文档
├── LICENSE                                      # 许可证文件
├── .gitignore                                   # Git 忽略配置
├── .gitattributes                               # Git 属性配置
├── .editorconfig                               # 编辑器配置
│
├── docs/                                        # 项目文档目录
│   ├── api.md                                   # API 接口文档
│   ├── arch.md                                  # 架构设计文档
│   ├── coding-style.md                          # 编码规范
│   ├── data-models.md                           # 数据模型文档
│   ├── project-structure.md                     # 项目结构说明
│   └── deployment.md                            # 部署文档
│
├── scripts/                                     # 脚本目录
│   ├── start.sh / start.bat                    # 启动脚本
│   ├── stop.sh / stop.bat                      # 停止脚本
│   ├── restart.sh                              # 重启脚本
│   ├── status.sh                               # 状态检查脚本
│   └── init-db.sh                              # 数据库初始化脚本
│
├── config/                                      # 配置文件目录 (可选)
│   ├── dev/                                     # 开发环境配置
│   ├── test/                                    # 测试环境配置
│   └── prod/                                    # 生产环境配置
│
├── docker/                                      # Docker 配置目录
│   ├── Dockerfile                               # Docker 构建文件
│   ├── docker-compose.yml                       # Docker Compose 配置
│   ├── .dockerignore                            # Docker 忽略配置
│   └── entrypoint.sh                            # 容器启动脚本
│
├── k8s/                                         # Kubernetes 配置目录
│   ├── deployment.yaml                          # 部署配置
│   ├── service.yaml                             # 服务配置
│   ├── configmap.yaml                           # 配置映射
│   ├── secret.yaml                              # 密钥配置
│   └── hpa.yaml                                 # 水平自动伸缩配置
│
├── src/                                         # 源代码目录
│   │
│   ├── main/                                    # 主代码目录
│   │   │
│   │   ├── java/                                # Java 源代码
│   │   │   └── com/company/project/            # 基础包目录
│   │   │       │
│   │   │       ├── ProjectApplication.java     # 【启动类】Spring Boot 入口
│   │   │       │
│   │   │       ├── config/                     # 【配置层】
│   │   │       │   ├── WebConfig.java          # Web 配置 (CORS、拦截器等)
│   │   │       │   ├── SecurityConfig.java     # 安全配置
│   │   │       │   ├── SwaggerConfig.java      # API 文档配置
│   │   │       │   ├── JacksonConfig.java      # JSON 序列化配置
│   │   │       │   ├── RedisConfig.java        # Redis 配置
│   │   │       │   ├── AsyncConfig.java        # 异步配置
│   │   │       │   ├── ScheduleConfig.java     # 定时任务配置
│   │   │       │   ├── MybatisConfig.java      # MyBatis 配置
│   │   │       │   └── properties/             # 配置属性类
│   │   │       │       ├── AppProperties.java
│   │   │       │       └── JwtProperties.java
│   │   │       │
│   │   │       ├── controller/                 # 【控制层】REST API
│   │   │       │   ├── UserController.java     # 用户接口
│   │   │       │   ├── OrderController.java    # 订单接口
│   │   │       │   └── request/                # 请求 DTO
│   │   │       │       ├── CreateUserRequest.java
│   │   │       │       └── UpdateUserRequest.java
│   │   │       │
│   │   │       ├── service/                    # 【服务层】业务逻辑
│   │   │       │   ├── UserService.java        # 服务接口
│   │   │       │   ├── OrderService.java
│   │   │       │   └── impl/                   # 服务实现
│   │   │       │       ├── UserServiceImpl.java
│   │   │       │       └── OrderServiceImpl.java
│   │   │       │
│   │   │       ├── repository/                 # 【数据层】数据访问
│   │   │       │   ├── UserRepository.java     # JPA Repository
│   │   │       │   ├── OrderRepository.java
│   │   │       │   ├── mapper/                 # MyBatis Mapper (可选)
│   │   │       │   │   ├── UserMapper.java
│   │   │       │   │   └── xml/                # Mapper XML
│   │   │       │   │       ├── UserMapper.xml
│   │   │       │   │       └── OrderMapper.xml
│   │   │       │   └── specification/          # JPA 查询规格
│   │   │       │       └── UserSpecification.java
│   │   │       │
│   │   │       ├── entity/                     # 【实体层】数据模型
│   │   │       │   ├── User.java               # JPA 实体
│   │   │       │   ├── Order.java
│   │   │       │   └── base/                   # 基础实体
│   │   │       │       ├── BaseEntity.java     # 通用字段 (id, createTime等)
│   │   │       │       └── AuditableEntity.java # 审计字段
│   │   │       │
│   │   │       ├── dto/                        # 【DTO 层】数据传输对象
│   │   │       │   ├── UserDTO.java            # 用户 DTO
│   │   │       │   ├── OrderDTO.java
│   │   │       │   ├── response/               # 响应 DTO
│   │   │       │   │   ├── UserResponse.java
│   │   │       │   │   └── PageResponse.java
│   │   │       │   └── converter/              # DTO 转换器
│   │   │       │       ├── UserConverter.java
│   │   │       │       └── MapStructConfig.java
│   │   │       │
│   │   │       ├── vo/                         # 【VO 层】视图对象 (可选)
│   │   │       │   ├── UserVO.java
│   │   │       │   └── OrderVO.java
│   │   │       │
│   │   │       ├── exception/                  # 【异常层】
│   │   │       │   ├── BusinessException.java  # 业务异常
│   │   │       │   ├── NotFoundException.java  # 资源不存在异常
│   │   │       │   ├── UnauthorizedException.java
│   │   │       │   ├── ErrorCode.java          # 错误码枚举
│   │   │       │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │   │       │
│   │   │       ├── security/                   # 【安全层】
│   │   │       │   ├── JwtTokenProvider.java   # JWT 工具
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   ├── UserDetailsServiceImpl.java
│   │   │       │   └── annotation/             # 安全注解
│   │   │       │       └── RequireRole.java
│   │   │       │
│   │   │       ├── aspect/                     # 【切面层】
│   │   │       │   ├── LoggingAspect.java      # 日志切面
│   │   │       │   ├── PerformanceAspect.java  # 性能监控切面
│   │   │       │   └── RateLimitAspect.java    # 限流切面
│   │   │       │
│   │   │       ├── component/                  # 【组件层】
│   │   │       │   ├── JwtTokenFilter.java     # 过滤器
│   │   │       │   ├── RequestIdInterceptor.java # 拦截器
│   │   │       │   └── StartupRunner.java      # 启动任务
│   │   │       │
│   │   │       ├── enums/                      # 【枚举层】
│   │   │       │   ├── UserStatus.java
│   │   │       │   ├── OrderStatus.java
│   │   │       │   └── Gender.java
│   │   │       │
│   │   │       ├── constants/                  # 【常量层】
│   │   │       │   ├── Constants.java          # 通用常量
│   │   │       │   ├── CacheConstants.java     # 缓存常量
│   │   │       │   └── SecurityConstants.java  # 安全常量
│   │   │       │
│   │   │       ├── utils/                      # 【工具层】
│   │   │       │   ├── JsonUtils.java          # JSON 工具
│   │   │       │   ├── DateUtils.java          # 日期工具
│   │   │       │   ├── StringUtils.java        # 字符串工具
│   │   │       │   ├── JwtUtils.java           # JWT 工具
│   │   │       │   └── ValidationUtils.java    # 校验工具
│   │   │       │
│   │   │       ├── listener/                   # 【监听层】
│   │   │       │   ├── UserEventListener.java  # 用户事件监听
│   │   │       │   └── ApplicationListener.java
│   │   │       │
│   │   │       ├── scheduler/                  # 【定时任务层】
│   │   │       │   ├── OrderCleanupJob.java    # 订单清理任务
│   │   │       │   └── DataSyncJob.java        # 数据同步任务
│   │   │       │
│   │   │       └── mq/                         # 【消息队列层】
│   │   │           ├── producer/               # 生产者
│   │   │           │   └── OrderMessageProducer.java
│   │   │           ├── consumer/               # 消费者
│   │   │           │   └── OrderMessageConsumer.java
│   │   │           └── config/                 # MQ 配置
│   │   │               └── RabbitMQConfig.java
│   │   │
│   │   ├── resources/                          # 资源文件目录
│   │   │   │
│   │   │   ├── application.yml                 # 【主配置文件】
│   │   │   ├── application-dev.yml             # 开发环境配置
│   │   │   ├── application-test.yml            # 测试环境配置
│   │   │   ├── application-prod.yml            # 生产环境配置
│   │   │   ├── application-local.yml           # 本地环境配置
│   │   │   │
│   │   │   ├── banner.txt                      # 启动横幅
│   │   │   │
│   │   │   ├── static/                         # 静态资源
│   │   │   │   └── (图片、CSS、JS 等)
│   │   │   │
│   │   │   ├── templates/                      # 模板文件
│   │   │   │   └── (Thymeleaf、Freemarker 等)
│   │   │   │
│   │   │   ├── mybatis/                        # MyBatis 配置
│   │   │   │   └── mapper/                     # Mapper XML 文件
│   │   │   │       ├── UserMapper.xml
│   │   │   │       └── OrderMapper.xml
│   │   │   │
│   │   │   ├── db/                             # 数据库脚本
│   │   │   │   ├── migration/                  # Flyway/Liquibase 脚本
│   │   │   │   │   ├── V1__init.sql
│   │   │   │   │   └── V2__add_index.sql
│   │   │   │   └── seed/                       # 种子数据
│   │   │   │       └── initial-data.sql
│   │   │   │
│   │   │   └── i18n/                           # 国际化资源
│   │   │       ├── messages.properties         # 默认语言
│   │   │       ├── messages_zh_CN.properties   # 中文
│   │   │       └── messages_en_US.properties   # 英文
│   │   │
│   │   └── webapp/                             # Web 应用目录 (可选)
│   │       └── WEB-INF/
│   │
│   └── test/                                    # 测试代码目录
│       │
│       ├── java/                                # 测试 Java 代码
│       │   └── com/company/project/
│       │       ├── ProjectApplicationTests.java # 应用上下文测试
│       │       ├── TestcontainersConfig.java    # 测试容器配置
│       │       │
│       │       ├── unit/                        # 单元测试
│       │       │   ├── service/
│       │       │   │   └── UserServiceTest.java
│       │       │   └── utils/
│       │       │       └── JsonUtilsTest.java
│       │       │
│       │       ├── integration/                 # 集成测试
│       │       │   ├── controller/
│       │       │   │   └── UserControllerIT.java
│       │       │   └── repository/
│       │       │       └── UserRepositoryIT.java
│       │       │
│       │       └── e2e/                         # 端到端测试
│       │           └── UserFlowE2E.java
│       │
│       └── resources/                           # 测试资源
│           ├── application-test.yml             # 测试环境配置
│           ├── data/                            # 测试数据
│           │   ├── user-data.json
│           │   └── order-data.sql
│           └── mock/                            # Mock 数据
│               └── mock-server.json
│
├── logs/                                        # 日志目录 (gitignore)
│   ├── application.log                          # 应用日志
│   ├── error.log                                # 错误日志
│   └── audit.log                                # 审计日志
│
├── target/ / build/                            # 构建输出 (gitignore)
│   └── *.jar / *.war
│
└── .mvn/ / .gradle/                            # 构建工具配置
    └── wrapper/
```

---

## 3. 分层详解

### 3.1 控制层 (Controller)

```
controller/
├── UserController.java          # 用户相关接口
├── OrderController.java         # 订单相关接口
└── request/                     # 请求 DTO 目录
    ├── CreateUserRequest.java   # 创建用户请求
    ├── UpdateUserRequest.java   # 更新用户请求
    └── QueryUserRequest.java    # 查询用户请求
```

**职责：**
- 接收 HTTP 请求，参数校验
- 调用 Service 层处理业务
- 封装响应结果，统一返回格式
- 处理异常，返回友好错误信息

**规范：**
```java
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    public Result<UserResponse> getUser(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        return Result.success(UserConverter.toResponse(user));
    }
    
    @PostMapping
    @Operation(summary = "创建用户")
    public Result<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserDTO user = userService.create(request);
        return Result.success(UserConverter.toResponse(user));
    }
}
```

### 3.2 服务层 (Service)

```
service/
├── UserService.java             # 服务接口
├── OrderService.java
└── impl/                        # 实现类目录
    ├── UserServiceImpl.java
    └── OrderServiceImpl.java
```

**职责：**
- 业务逻辑处理
- 事务管理 (`@Transactional`)
- 协调多个 Repository 操作
- 数据转换 (Entity ↔ DTO)

**规范：**
```java
public interface UserService {
    UserDTO findById(Long id);
    UserDTO create(CreateUserRequest request);
    void update(Long id, UpdateUserRequest request);
    void delete(Long id);
    Page<UserDTO> query(QueryUserRequest request);
}

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserConverter userConverter;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO create(CreateUserRequest request) {
        // 业务逻辑...
    }
}
```

### 3.3 数据层 (Repository)

```
repository/
├── UserRepository.java          # JPA Repository
├── OrderRepository.java
├── mapper/                      # MyBatis Mapper (可选)
│   ├── UserMapper.java
│   └── xml/
│       ├── UserMapper.xml
│       └── OrderMapper.xml
└── specification/               # JPA 查询规格
    └── UserSpecification.java
```

**职责：**
- 数据库访问，CRUD 操作
- 自定义查询方法
- 分页、排序支持

**规范：**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long>, 
                                        JpaSpecificationExecutor<User> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.status = :status")
    List<User> findByStatus(@Param("status") UserStatus status);
}
```

### 3.4 实体层 (Entity)

```
entity/
├── User.java                    # 用户实体
├── Order.java                   # 订单实体
└── base/                        # 基础实体
    ├── BaseEntity.java          # 通用字段
    └── AuditableEntity.java     # 审计字段
```

**规范：**
```java
@Entity
@Table(name = "sys_user")
public class User extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;
    
    // getters, setters, equals, hashCode...
}
```

### 3.5 DTO 层 (Data Transfer Object)

```
dto/
├── UserDTO.java                 # 用户 DTO
├── OrderDTO.java
├── response/                    # 响应 DTO
│   ├── UserResponse.java
│   └── PageResponse.java
└── converter/                   # 转换器
    ├── UserConverter.java
    └── MapStructConfig.java
```

**规范：**
```java
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private UserStatus status;
    private LocalDateTime createTime;
}

@Mapper(componentModel = "spring")
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);
    
    UserDTO toDTO(User entity);
    UserResponse toResponse(UserDTO dto);
    List<UserResponse> toResponseList(List<UserDTO> dtoList);
}
```

### 3.6 异常层 (Exception)

```
exception/
├── BusinessException.java       # 业务异常
├── NotFoundException.java       # 资源不存在
├── UnauthorizedException.java   # 未授权
├── ErrorCode.java               # 错误码枚举
└── GlobalExceptionHandler.java  # 全局处理器
```

**规范：**
```java
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(1001, "用户不存在"),
    DUPLICATE_USERNAME(1002, "用户名已存在"),
    INVALID_PASSWORD(1003, "密码错误"),
    INTERNAL_ERROR(5000, "系统内部错误");
    
    private final int code;
    private final String message;
}

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getErrorCode());
    }
    
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(ErrorCode.INTERNAL_ERROR);
    }
}
```

---

## 4. 多模块项目结构

### 4.1 多模块目录结构

```
project-root/                                    # 父项目
│
├── pom.xml                                      # 父 POM (依赖管理)
├── README.md
│
├── project-common/                              # 公共模块
│   ├── pom.xml
│   └── src/
│       └── java/
│           └── com/company/common/
│               ├── constants/                   # 常量
│               ├── enums/                       # 枚举
│               ├── exception/                   # 异常
│               ├── result/                      # 统一返回
│               ├── utils/                       # 工具类
│               └── dto/                         # 公共 DTO
│
├── project-domain/                              # 领域模型模块
│   ├── pom.xml
│   └── src/
│       └── java/
│           └── com/company/domain/
│               ├── entity/                      # 实体
│               ├── repository/                  # 仓库接口
│               └── service/                     # 领域服务
│
├── project-infrastructure/                      # 基础设施模块
│   ├── pom.xml
│   └── src/
│       └── java/
│           └── com/company/infrastructure/
│               ├── config/                      # 配置
│               ├── persistence/                 # 持久化实现
│               ├── mq/                          # 消息队列
│               ├── cache/                       # 缓存
│               └── external/                    # 外部服务
│
├── project-application/                         # 应用服务模块
│   ├── pom.xml
│   └── src/
│       └── java/
│           └── com/company/application/
│               ├── service/                     # 应用服务
│               ├── dto/                         # 应用 DTO
│               ├── converter/                   # 转换器
│               └── event/                       # 事件
│
├── project-interfaces/                          # 接口层模块
│   ├── pom.xml
│   └── src/
│       └── java/
│           └── com/company/interfaces/
│               ├── controller/                  # REST API
│               ├── schedule/                    # 定时任务
│               ├── listener/                    # 消息监听
│               └── facade/                      # 门面接口
│
└── project-start/                               # 启动模块
    ├── pom.xml
    └── src/
        └── java/
            └── com/company/start/
                └── Application.java             # 启动类
```

### 4.2 模块依赖关系

```
                    ┌─────────────────┐
                    │  project-start  │
                    │    (启动模块)    │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
    ┌─────────────────┐ ┌─────────┐ ┌─────────────────┐
    │ project-interfaces│ │project-app│ project-infrastructure│
    │   (接口层)       │ │(应用层)  │    (基础设施)    │
    └────────┬────────┘ └────┬────┘ └────────┬────────┘
             │               │               │
             └───────────────┼───────────────┘
                             │
                    ┌────────┴────────┐
                    │  project-domain │
                    │   (领域模型)     │
                    └────────┬────────┘
                             │
                    ┌────────┴────────┐
                    │  project-common │
                    │   (公共模块)     │
                    └─────────────────┘

依赖规则:
• 上层依赖下层，下层不依赖上层
• common 不依赖任何模块
• domain 只依赖 common
• infrastructure 依赖 domain 和 common
• application 依赖 domain 和 common
• interfaces 依赖 application、domain、common
• start 依赖所有模块
```

---

## 5. 配置管理

### 5.1 配置文件结构

```
resources/
├── application.yml              # 主配置文件
├── application-dev.yml          # 开发环境
├── application-test.yml         # 测试环境
├── application-prod.yml         # 生产环境
└── application-local.yml        # 本地环境
```

### 5.2 配置分离原则

| 配置类型 | 位置 | 示例 |
|----------|------|------|
| **应用配置** | `application.yml` | 应用名称、端口 |
| **环境配置** | `application-{env}.yml` | 数据库连接、日志级别 |
| **敏感配置** | 环境变量 / 配置中心 | 密码、密钥 |
| **业务配置** | 配置中心 / 数据库 | 开关、阈值 |

---

## 6. 测试目录结构

### 6.1 测试分层

```
test/
├── java/
│   └── com/company/project/
│       ├── unit/                    # 单元测试
│       │   ├── service/
│       │   └── utils/
│       ├── integration/             # 集成测试
│       │   ├── controller/
│       │   └── repository/
│       └── e2e/                     # 端到端测试
│           └── UserFlowE2E.java
└── resources/
    └── application-test.yml
```

### 6.2 测试命名规范

| 测试类型 | 类名后缀 | 方法前缀 |
|----------|----------|----------|
| 单元测试 | `Test` | `should`, `when` |
| 集成测试 | `IT` | `should` |
| 端到端测试 | `E2E` | `test` |

---

## 7. 最佳实践

### 7.1 目录设计原则

```
1. 单一职责原则
   - 每个类只负责一种职责
   - 每个包只包含相关类

2. 依赖倒置原则
   - 上层依赖下层接口
   - 通过依赖注入实现解耦

3. 接口隔离原则
   - 服务层定义接口
   - 实现类放在 impl 包

4. 不要重复自己 (DRY)
   - 公共代码提取到 common
   - 工具类统一放在 utils
```

### 7.2 包命名规范

```
com.company.project.
├── config          # 配置类
├── controller      # 控制器
├── service         # 服务
│   └── impl        # 服务实现
├── repository      # 数据访问
├── entity          # 实体
├── dto             # 数据传输对象
├── vo              # 视图对象
├── exception       # 异常
├── enums           # 枚举
├── constants       # 常量
├── utils           # 工具类
├── aspect          # 切面
├── component       # 组件
├── listener        # 监听器
└── scheduler       # 定时任务
```

### 7.3 文件组织建议

```
✅ 推荐做法:
• 相关文件放在同一包下
• 使用子包进一步细分
• 测试代码与被测试代码包结构一致

❌ 避免做法:
• 所有类放在根包下
• 包深度超过 5 层
• 循环依赖
```

---

## 8. 参考项目结构

### 8.1 小型项目结构

```
small-project/
├── src/
│   └── main/
│       └── java/
│           └── com/example/demo/
│               ├── DemoApplication.java
│               ├── config/
│               ├── controller/
│               ├── service/
│               ├── repository/
│               ├── entity/
│               ├── dto/
│               ├── exception/
│               └── utils/
└── pom.xml
```

### 8.2 中型项目结构

```
medium-project/
├── src/
│   └── main/
│       └── java/
│           └── com/example/project/
│               ├── ProjectApplication.java
│               ├── config/
│               ├── controller/
│               ├── service/
│               │   └── impl/
│               ├── repository/
│               │   ├── mapper/
│               │   └── xml/
│               ├── entity/
│               ├── dto/
│               │   ├── request/
│               │   └── response/
│               ├── converter/
│               ├── exception/
│               ├── enums/
│               ├── constants/
│               ├── utils/
│               └── aspect/
└── pom.xml
```

### 8.3 大型/微服务项目结构

```
large-project/
├── project-common/              # 公共模块
├── project-gateway/             # 网关服务
├── project-auth/                # 认证服务
├── project-user/                # 用户服务
├── project-order/               # 订单服务
├── project-message/             # 消息服务
└── pom.xml                      # 父 POM
```

---

## 9. 总结

### 9.1 核心要点

| 要点 | 说明 |
|------|------|
| **分层清晰** | Controller → Service → Repository → Entity |
| **职责单一** | 每层只负责特定职责 |
| **依赖规范** | 上层依赖下层，禁止循环依赖 |
| **配置分离** | 按环境分离配置，敏感信息外部化 |
| **测试完备** | 单元测试 + 集成测试 + E2E 测试 |

### 9.2 选择合适结构

| 项目规模 | 推荐结构 | 模块数 |
|----------|----------|--------|
| 小型 (< 10 表) | 单体结构 | 1 |
| 中型 (10-30 表) | 单体分层结构 | 1 |
| 大型 (> 30 表) | 多模块单体 | 3-5 |
| 微服务 | 多项目结构 | 多个 |

---

## 10. 附录

### 10.1 常用 .gitignore

```gitignore
# Compiled class files
*.class

# Log files
*.log
logs/

# Package Files
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

# Maven
target/
.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

# Gradle
build/
.gradle
!gradle/wrapper/gradle-wrapper.jar

# IDE
.idea/
*.iws
*.iml
*.ipr
.vscode/
*.swp
*.swo

# OS
.DS_Store
Thumbs.db

# Application
application-local.yml
*.pid
```

### 10.2 推荐插件

| 插件 | 用途 |
|------|------|
| Lombok | 简化代码 |
| MapStruct | 对象转换 |
| Spring Boot DevTools | 热部署 |
| Maven Helper | 依赖分析 |
| SonarLint | 代码质量 |
