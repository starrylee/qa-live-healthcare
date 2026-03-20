# QA Healthcare 后端编码规范

## 概述

本文档定义了 QA Healthcare 后端项目的 Java 编码规范，基于业界标准（Google Java Style、Spring Boot 最佳实践、阿里巴巴 Java 开发手册）制定。

**适用范围：**
- 所有 Java 源代码文件（*.java）
- Spring Boot 微服务项目
- 测试代码（src/test）

**目标：**
- 提高代码可读性和可维护性
- 保证代码质量一致性
- 降低团队协作成本
- 便于 Code Review

---

## 一、命名规范

### 1.1 通用原则

| 类型 | 规范 | 示例 |
|------|------|------|
| **清晰明确** | 命名应表达意图，避免模糊缩写 | `userRepository` ✓ `ur` ✗ |
| **避免歧义** | 不使用单字母（循环变量除外） | `index` ✓ `i` ✗（for循环内可用） |
| **英文命名** | 使用英文单词，禁止拼音 | `userName` ✓ `yongHuMing` ✗ |
| **驼峰命名** | 类名大驼峰，变量/方法小驼峰 | `UserService`, `getUserName()` |

### 1.2 包名（Package）

```java
// 格式：com.leansofx.{服务名}.{模块名}
// 全部小写，无下划线

// ✅ 正确示例
package com.leansofx.qaserviceuser.controller;
package com.leansofx.qaserviceuser.service;
package com.leansofx.qaserviceuser.repository;

// ❌ 错误示例
package com.leansofx.qaServiceUser.controller;  // 包含大写
package com.leansofx.qa_service_user.controller; // 包含下划线
```

**包结构规范：**
```
com.leansofx.qaserviceuser
├── config          # 配置类
├── controller      # 控制器层（REST API）
├── service         # 服务层（业务逻辑）
│   └── impl        # 服务实现
├── repository      # 数据访问层（DAO）
├── entity          # 实体类（数据库映射）
├── dto             # 数据传输对象
├── vo              # 视图对象（返回给前端）
├── mapper          # 对象映射器
├── exception       # 自定义异常
├── utils           # 工具类
├── constants       # 常量定义
├── enums           # 枚举类
└── aspect          # AOP 切面
```

### 1.3 类名（Class）

| 类型 | 规范 | 示例 |
|------|------|------|
| **普通类** | 名词，大驼峰 | `UserService`, `QuestionController` |
| **接口** | 名词/形容词，大驼峰 | `UserRepository`, `Serializable` |
| **实现类** | 接口名 + Impl | `UserServiceImpl` |
| **异常类** | 异常类型 + Exception | `BusinessException` |
| **工具类** | 功能 + Utils | `DateUtils`, `JsonUtils` |
| **常量类** | 名词 + Constants | `SystemConstants` |
| **配置类** | 功能 + Config | `CorsConfig`, `WebMvcConfig` |
| **测试类** | 被测类名 + Test | `UserServiceTest` |

```java
// ✅ 正确示例
public class UserService { }
public interface UserRepository { }
public class UserServiceImpl implements UserService { }
public class BusinessException extends RuntimeException { }
public final class DateUtils { }

// ❌ 错误示例
public class userService { }           // 小写开头
public class User { }                  // 过于简单
public class T { }                     // 无意义
public class HandleData { }            // 动词开头
```

### 1.4 方法名（Method）

| 操作类型 | 前缀 | 示例 |
|----------|------|------|
| **查询单个** | `get` / `find` / `query` | `getUserById()`, `findByUsername()` |
| **查询列表** | `list` / `find` / `query` | `listUsers()`, `findAll()` |
| **分页查询** | `page` | `pageUsers()` |
| **新增** | `save` / `insert` / `add` | `saveUser()`, `addUser()` |
| **修改** | `update` / `modify` | `updateUser()`, `modifyStatus()` |
| **删除** | `remove` / `delete` | `removeUser()`, `deleteById()` |
| **检查/判断** | `is` / `has` / `can` / `check` | `isValid()`, `hasPermission()` |
| **计算/转换** | `calculate` / `convert` / `to` | `calculateAge()`, `toDTO()` |
| **处理** | `handle` / `process` | `handleException()` |

```java
// ✅ 正确示例
public User getUserById(Long id) { }
public List<User> listUsers() { }
public Page<User> pageUsers(Pageable pageable) { }
public User saveUser(UserDTO dto) { }
public void updateUser(UserDTO dto) { }
public void removeUser(Long id) { }
public boolean isValid(User user) { }

// ❌ 错误示例
public User getuserbyid(Long id) { }   // 不符合驼峰
public List<User> getAll() { }         // 语义不清
public User insert(User user) { }      // 缺少业务含义
public void dealWith() { }             // 过于笼统
```

### 1.5 变量名（Variable）

| 类型 | 规范 | 示例 |
|------|------|------|
| **普通变量** | 小驼峰，名词 | `userName`, `createTime` |
| **布尔变量** | is/has/can + 形容词 | `isActive`, `hasPermission` |
| **集合变量** | 名词复数或 + List/Map | `users`, `userList`, `userMap` |
| **临时变量** | 有意义的名字 | `tempUser` > `tmp` |
| **常量** | 全大写，下划线分隔 | `MAX_RETRY_COUNT` |
| **静态变量** | 大写（如果是常量）或驼峰 | `CACHE_MANAGER`, `instance` |

```java
// ✅ 正确示例
private String userName;
private Long createTime;
private boolean isActive;
private List<User> users;
private Map<Long, User> userMap;

public static final int MAX_RETRY_COUNT = 3;
public static final String DEFAULT_ENCODING = "UTF-8";

// ❌ 错误示例
private String UserName;        // 大写开头
private Long createtime;        // 未驼峰
private boolean active;         // 不明确是布尔
private List<User> user;        // 单复数不一致
private Map<Long, User> map;    // 无意义
```

### 1.6 常量命名

```java
public class SystemConstants {
    
    // 全大写，下划线分隔
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final long CACHE_EXPIRE_TIME = 3600L;
    
    // 枚举值也是常量
    public enum Status {
        ACTIVE,
        INACTIVE,
        DELETED
    }
}
```

---

## 二、代码格式

### 2.1 缩进与换行

```java
// 使用 4 个空格缩进（禁止使用 Tab）
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User getUserById(Long id) {
        if (id == null) {
            return null;
        }
        return userRepository.findById(id).orElse(null);
    }
}
```

### 2.2 大括号规范

```java
// 左大括号不换行（K&R 风格）
public class UserController {
    
    // if-else 必须有大括号，即使只有一行
    public void checkUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (user.getStatus() == Status.ACTIVE) {
            processActiveUser(user);
        } else {
            processInactiveUser(user);
        }
    }
    
    // switch 必须有 default
    public String getRoleName(int role) {
        switch (role) {
            case 1:
                return "ADMIN";
            case 2:
                return "USER";
            default:
                return "UNKNOWN";
        }
    }
}
```

### 2.3 空行使用

```java
// 包声明后空一行
package com.leansofx.qaserviceuser.service;

// import 后空一行
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 用户服务类
 */
// 类注释后空一行
@Service
public class UserService {
    
    // 静态变量和实例变量之间空一行
    private static final int MAX_RETRY = 3;
    
    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    
    // 构造方法和第一个方法之间空一行
    public UserService(UserRepository userRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }
    
    // 方法之间空一行
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public List<User> listUsers() {
        return userRepository.findAll();
    }
}
```

### 2.4 行长度与换行

```java
// 单行不超过 120 个字符，超出时合理换行
public User createUserWithDetails(String username, String email, 
                                   String phone, Integer age, 
                                   String address, String department) {
    // ...
}

// 方法链式调用换行
List<User> activeUsers = userRepository.findAll()
    .stream()
    .filter(User::isActive)
    .sorted(Comparator.comparing(User::getCreateTime).reversed())
    .collect(Collectors.toList());
```

### 2.5 空格使用

```java
// 操作符两侧加空格
int sum = a + b;
boolean flag = (a > b) && (c < d);

// 逗号后加空格
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// 冒号后加空格
for (String name : names) { }

// 小括号内侧不加空格
if (condition) { }  // ✅
if ( condition ) { } // ❌

// 大括号前加空格
public void method() { }  // ✅
public void method(){ }   // ❌
```

---

## 三、注释规范

### 3.1 注释类型

| 类型 | 使用场景 | 格式 |
|------|----------|------|
| **文档注释** | 类、接口、方法、字段 | `/** */` |
| **块注释** | 代码块说明 | `/* */` |
| **行注释** | 单行代码说明 | `//` |
| **TODO 注释** | 待办事项 | `// TODO:` |
| **FIXME 注释** | 需要修复 | `// FIXME:` |

### 3.2 类注释

```java
/**
 * 用户服务类
 * 
 * <p>提供用户相关的业务逻辑处理，包括用户查询、创建、更新、删除等操作</p>
 * 
 * @author      your-name
 * @version     1.0
 * @since       2025-11-03
 * @see         UserRepository
 */
@Service
public class UserService {
    // ...
}
```

### 3.3 方法注释

```java
/**
 * 根据用户ID查询用户信息
 * 
 * <p>优先从缓存中获取，缓存不存在时查询数据库</p>
 * 
 * @param id    用户ID，不能为空
 * @return      用户对象，未找到时返回 null
 * @throws      IllegalArgumentException 当 id 为 null 或小于等于0时抛出
 * @see         User
 */
public User getUserById(Long id) {
    // ...
}

/**
 * 创建新用户
 * 
 * @param dto   用户数据传输对象，包含用户基本信息
 * @return      创建成功的用户对象
 * @throws      BusinessException 当用户名已存在时抛出
 */
@Transactional
public User createUser(UserDTO dto) {
    // ...
}
```

### 3.4 字段注释

```java
public class User {
    
    /** 用户ID，主键 */
    private Long id;
    
    /** 用户名，唯一标识，长度2-20字符 */
    private String username;
    
    /** 用户状态：0-禁用，1-启用，2-锁定 */
    private Integer status;
}
```

### 3.5 代码注释

```java
// ✅ 好的注释：说明为什么这么做
// 使用双重检查锁定实现线程安全的单例
public static Singleton getInstance() {
    if (instance == null) {
        synchronized (Singleton.class) {
            if (instance == null) {
                instance = new Singleton();
            }
        }
    }
    return instance;
}

// ❌ 坏的注释：重复代码做了什么
// 将 a 和 b 相加赋值给 sum
int sum = a + b;

// ✅ 好的 TODO 注释
// TODO: 2025-11-03 需要添加缓存逻辑，避免频繁查询数据库
public List<User> listAllUsers() {
    return userRepository.findAll();
}
```

---

## 四、REST API 规范

### 4.1 URL 设计

```java
@RestController
@RequestMapping("/api/v1/users")  // 版本控制 + 资源名复数
public class UserController {
    
    // ✅ 正确示例
    
    // 查询列表：GET /api/v1/users
    @GetMapping
    public List<UserVO> listUsers() { }
    
    // 查询单个：GET /api/v1/users/{id}
    @GetMapping("/{id}")
    public UserVO getUser(@PathVariable Long id) { }
    
    // 分页查询：GET /api/v1/users?page=0&size=10
    @GetMapping("/page")
    public Page<UserVO> pageUsers(@RequestParam int page, 
                                   @RequestParam int size) { }
    
    // 创建：POST /api/v1/users
    @PostMapping
    public UserVO createUser(@RequestBody @Valid UserDTO dto) { }
    
    // 更新：PUT /api/v1/users/{id}
    @PutMapping("/{id}")
    public UserVO updateUser(@PathVariable Long id, 
                              @RequestBody @Valid UserDTO dto) { }
    
    // 删除：DELETE /api/v1/users/{id}
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) { }
}
```

### 4.2 HTTP 方法使用

| HTTP 方法 | 用途 | 幂等性 | 示例 |
|-----------|------|--------|------|
| GET | 查询资源 | 是 | `GET /users/1` 查询用户 |
| POST | 创建资源 | 否 | `POST /users` 创建用户 |
| PUT | 全量更新 | 是 | `PUT /users/1` 更新用户 |
| PATCH | 部分更新 | 是 | `PATCH /users/1` 部分更新 |
| DELETE | 删除资源 | 是 | `DELETE /users/1` 删除用户 |

### 4.3 状态码使用

```java
@RestController
public class UserController {
    
    // 200 OK - 成功
    @GetMapping("/{id}")
    public ResponseEntity<UserVO> getUser(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        return ResponseEntity.ok(user);  // HTTP 200
    }
    
    // 201 Created - 创建成功
    @PostMapping
    public ResponseEntity<UserVO> createUser(@RequestBody UserDTO dto) {
        UserVO user = userService.createUser(dto);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(user.getId())
            .toUri();
        return ResponseEntity.created(location).body(user);  // HTTP 201
    }
    
    // 204 No Content - 删除成功
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();  // HTTP 204
    }
    
    // 404 Not Found - 资源不存在
    @GetMapping("/{id}")
    public ResponseEntity<UserVO> getUser(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();  // HTTP 404
        }
        return ResponseEntity.ok(user);
    }
}
```

### 4.4 响应格式规范

```java
/**
 * 统一响应包装类
 */
public class ApiResponse<T> {
    
    private int code;           // 业务状态码
    private String message;     // 提示信息
    private T data;             // 数据
    private long timestamp;     // 时间戳
    
    // 成功响应
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = 200;
        response.message = "success";
        response.data = data;
        response.timestamp = System.currentTimeMillis();
        return response;
    }
    
    // 错误响应
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = code;
        response.message = message;
        response.timestamp = System.currentTimeMillis();
        return response;
    }
}

// 使用示例
@RestController
public class UserController {
    
    @GetMapping("/{id}")
    public ApiResponse<UserVO> getUser(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        return ApiResponse.success(user);
    }
}

// 响应示例
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "zhangsan"
    },
    "timestamp": 1699000000000
}
```

---

## 五、异常处理规范

### 5.1 自定义异常

```java
/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {
    
    private final int code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }
    
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}

/**
 * 资源不存在异常
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String resourceName, Long id) {
        super(404, String.format("%s not found with id: %d", resourceName, id));
    }
}
```

### 5.2 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    // 业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        logger.warn("Business exception: {}", e.getMessage());
        ApiResponse<Void> response = ApiResponse.error(e.getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    // 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        ApiResponse<Void> response = ApiResponse.error(400, message);
        return ResponseEntity.badRequest().body(response);
    }
    
    // 其他异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        logger.error("Unexpected error: ", e);
        ApiResponse<Void> response = ApiResponse.error(500, "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

---

## 六、日志规范

### 6.1 日志级别使用

| 级别 | 使用场景 | 示例 |
|------|----------|------|
| ERROR | 系统错误，需要立即处理 | 数据库连接失败、空指针异常 |
| WARN | 警告信息，需要注意 | 参数校验失败、资源未找到 |
| INFO | 业务信息，正常流程记录 | 用户登录、数据操作成功 |
| DEBUG | 调试信息，开发环境使用 | 方法入参、执行过程 |
| TRACE | 更详细的调试信息 | SQL 语句、详细执行路径 |

### 6.2 日志规范

```java
@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    public User getUserById(Long id) {
        // ✅ 正确的日志输出
        logger.debug("Getting user by id: {}", id);
        
        User user = userRepository.findById(id).orElse(null);
        
        if (user == null) {
            logger.warn("User not found, id: {}", id);
            return null;
        }
        
        logger.info("Successfully retrieved user: {}", user.getUsername());
        return user;
    }
    
    public void processUser(User user) {
        // ✅ 使用占位符，避免字符串拼接
        logger.debug("Processing user: {}, status: {}", user.getId(), user.getStatus());
        
        try {
            // 业务逻辑
            logger.info("User processed successfully: {}", user.getId());
        } catch (Exception e) {
            // ✅ 记录异常堆栈
            logger.error("Failed to process user: {}", user.getId(), e);
            throw new BusinessException("Processing failed", e);
        }
    }
    
    // ❌ 错误的日志使用
    public void badExample(User user) {
        // 不要这样写
        logger.debug("User: " + user);  // 字符串拼接，性能差
        
        if (logger.isDebugEnabled()) {  // 不需要判断，SLF4J 自动处理
            logger.debug("User: {}", user);
        }
    }
}
```

---

## 七、数据库相关规范

### 7.1 实体类规范

```java
@Entity
@Table(name = "t_user")  // 表名 t_ 前缀
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "status", nullable = false)
    private Integer status;
    
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
    
    // 乐观锁版本号
    @Version
    @Column(name = "version")
    private Integer version;
    
    // 逻辑删除标志
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    // 自动填充时间
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
```

### 7.2 Repository 规范

```java
// 接口命名：实体名 + Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 方法命名遵循 Spring Data JPA 规范
    
    // 根据用户名查询
    Optional<User> findByUsername(String username);
    
    // 根据状态查询列表
    List<User> findByStatusOrderByCreateTimeDesc(Integer status);
    
    // 根据邮箱模糊查询
    List<User> findByEmailContaining(String email);
    
    // 检查用户名是否存在
    boolean existsByUsername(String username);
    
    // 分页查询
    Page<User> findByStatus(Integer status, Pageable pageable);
    
    // 自定义查询使用 @Query
    @Query("SELECT u FROM User u WHERE u.status = :status AND u.createTime > :createTime")
    List<User> findActiveUsersAfter(@Param("status") Integer status, 
                                     @Param("createTime") LocalDateTime createTime);
}
```

---

## 八、测试规范

### 8.1 测试类规范

```java
// 命名：被测类名 + Test
// 位置：src/test/java 下相同的包路径
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @MockBean
    private UserRepository userRepository;
    
    // 测试方法命名：被测方法名 + _ + 场景 + _ + 预期结果
    
    @Test
    public void getUserById_existingId_returnsUser() {
        // given
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setUsername("test");
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        
        // when
        User actualUser = userService.getUserById(userId);
        
        // then
        assertNotNull(actualUser);
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        verify(userRepository).findById(userId);
    }
    
    @Test
    public void getUserById_nonExistingId_returnsNull() {
        // given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // when
        User actualUser = userService.getUserById(userId);
        
        // then
        assertNull(actualUser);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createUser_nullDto_throwsException() {
        userService.createUser(null);
    }
}
```

### 8.2 测试原则

- **AAA 模式**：Arrange（准备）、Act（执行）、Assert（断言）
- **独立性**：每个测试方法应该独立，不依赖其他测试
- **可重复性**：测试应该可以在任何环境下重复运行
- **单一职责**：每个测试方法只测试一个概念

---

## 九、最佳实践

### 9.1 代码优化

```java
// ✅ 使用 Stream API 简化集合操作
List<String> activeUserNames = users.stream()
    .filter(User::isActive)
    .map(User::getUsername)
    .collect(Collectors.toList());

// ✅ 使用 Optional 避免空指针
Optional<User> userOpt = userRepository.findById(id);
userOpt.ifPresent(user -> {
    // 处理 user
});

// ✅ 使用 StringBuilder 进行字符串拼接（循环内）
StringBuilder sb = new StringBuilder();
for (String item : items) {
    sb.append(item).append(",");
}

// ✅ 使用 try-with-resources 自动关闭资源
try (InputStream is = new FileInputStream(file)) {
    // 使用 is
} catch (IOException e) {
    logger.error("Failed to read file", e);
}
```

### 9.2 避免常见错误

```java
// ❌ 不要在循环中查询数据库
for (Long userId : userIds) {
    userRepository.findById(userId);  // 每次循环都查询
}

// ✅ 使用批量查询
List<User> users = userRepository.findAllById(userIds);

// ❌ 不要捕获异常后不处理
try {
    // 业务逻辑
} catch (Exception e) {
    // 什么都不做
}

// ✅ 至少记录日志或抛出异常
try {
    // 业务逻辑
} catch (Exception e) {
    logger.error("Operation failed", e);
    throw new BusinessException("Operation failed", e);
}

// ❌ 不要返回 null，使用 Optional 或空集合
public List<User> findUsers() {
    // ...
    return null;  // ❌
}

// ✅ 返回空集合
public List<User> findUsers() {
    // ...
    return Collections.emptyList();  // ✅
}
```

---

## 十、IDE 配置

### 10.1 推荐 IDE

- **IntelliJ IDEA**（首选）
- Eclipse
- VS Code + Java Extension Pack

### 10.2 推荐插件

| 插件 | 用途 |
|------|------|
| CheckStyle-IDEA | 代码规范检查 |
| SonarLint | 代码质量分析 |
| Lombok | 简化代码 |
| Save Actions | 自动格式化 |

### 10.3 代码格式化配置

建议导入统一代码格式化配置：
- 缩进：4 个空格
- 换行符：LF（Unix 风格）
- 编码：UTF-8
- 行长度：120 字符

---

## 附录

### A. 代码审查清单

- [ ] 命名是否符合规范
- [ ] 是否包含适当的注释
- [ ] 是否有未处理的异常
- [ ] 是否有适当的日志记录
- [ ] 是否包含单元测试
- [ ] 是否存在明显的性能问题
- [ ] 是否遵循 REST API 规范
- [ ] 是否有潜在的空指针风险

### B. 参考文档

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [阿里巴巴 Java 开发手册](https://github.com/alibaba/p3c)

### C. 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2025-11-03 | 初始版本，基于业界标准制定 |
