# Java 编码规范

## 1. 代码格式规范

### 1.1 缩进与换行

- 使用 **4 个空格** 缩进，禁止使用 Tab
- 每行代码不超过 **120 个字符**
- 左大括号 `{` 不换行，与声明语句末尾同一行
- 右大括号 `}` 独占一行
- 方法之间保留一个空行

```java
// 正确
public class UserService {
    private static final int MAX_RETRY = 3;
    
    public User getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(id).orElse(null);
    }
}

// 错误
public class UserService
{
    private static final int MAX_RETRY=3;
    public User getUserById(Long id)
    {
        if(id==null){
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(id).orElse(null);
    }
}
```

### 1.2 命名规范

#### 类名
- 使用大驼峰命名法（UpperCamelCase）
- 名词或名词短语
- 避免缩写，除非是广泛认可的（如 `DTO`, `DAO`）

```java
// 正确
public class UserService { }
public class OrderRepository { }
public class UserDTO { }

// 错误
public class userService { }
public class Order_Repo { }
public class USvc { }
```

#### 方法名
- 使用小驼峰命名法（lowerCamelCase）
- 动词或动词短语
- 查询方法以 `get`, `find`, `query` 开头
- 修改方法以 `save`, `update`, `delete` 开头

```java
// 正确
public User getUserById(Long id) { }
public List<User> findUsersByStatus(String status) { }
public void saveUser(User user) { }
public void updateUserStatus(Long id, String status) { }

// 错误
public User GetUser(Long id) { }
public void SAVE_USER(User user) { }
public void upd_status(Long id) { }
```

#### 变量名
- 使用小驼峰命名法（lowerCamelCase）
- 有意义的命名，避免单字母（循环变量除外）
- 布尔变量使用 `is`, `has`, `can` 等前缀

```java
// 正确
private String userName;
private boolean isActive;
private boolean hasPermission;
private int retryCount;

// 错误
private String username;  // userName 更清晰
private boolean active;
private int rc;  // 不清晰
```

#### 常量名
- 全大写，单词间用下划线分隔
- 使用 `static final` 修饰

```java
// 正确
public static final int MAX_RETRY_COUNT = 3;
public static final String DEFAULT_ENCODING = "UTF-8";

// 错误
public static final int maxRetry = 3;
public static final String defaultEncoding = "UTF-8";
```

### 1.3 注释规范

#### 类注释
```java
/**
 * 用户服务类
 * 
 * 提供用户相关的业务逻辑处理，包括用户查询、创建、更新、删除等操作
 * 
 * @author Zhang San
 * @since 1.0.0
 */
public class UserService {
    // ...
}
```

#### 方法注释
```java
/**
 * 根据用户ID查询用户信息
 * 
 * @param id 用户ID，不能为空
 * @return 用户信息，如果不存在返回null
 * @throws IllegalArgumentException 当id为空时抛出
 * @throws NotFoundException 当用户不存在时抛出
 */
public User getUserById(Long id) {
    // ...
}
```

#### 字段注释
```java
/** 最大重试次数 */
private static final int MAX_RETRY = 3;

/** 用户名 */
private String userName;
```

#### 行内注释
```java
// 校验用户ID
if (id == null) {
    throw new IllegalArgumentException("User ID cannot be null");
}

// TODO: 添加缓存逻辑
User user = userRepository.findById(id).orElse(null);
```

## 2. 代码结构规范

### 2.1 类成员顺序

```java
public class UserService {
    // 1. 静态常量
    private static final int MAX_RETRY = 3;
    
    // 2. 实例常量
    private final UserRepository userRepository;
    
    // 3. 普通字段
    private String defaultStatus;
    
    // 4. 构造函数
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // 5. 公共方法
    public User getUserById(Long id) {
        // ...
    }
    
    // 6. 受保护方法
    protected void validateUser(User user) {
        // ...
    }
    
    // 7. 私有方法
    private void logOperation(String operation) {
        // ...
    }
    
    // 8. Getter/Setter
    public String getDefaultStatus() {
        return defaultStatus;
    }
}
```

### 2.2 导入语句顺序

```java
// 1. java.*
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// 2. javax.*
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

// 3. 第三方库
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

// 4. 项目内部
import com.company.project.dto.UserDTO;
import com.company.project.entity.User;
import com.company.project.repository.UserRepository;
```

## 3. 最佳实践

### 3.1 空值处理

```java
// 推荐：使用 Optional
public Optional<User> findUserById(Long id) {
    return userRepository.findById(id);
}

// 推荐：使用 Objects.requireNonNull
public void processUser(User user) {
    Objects.requireNonNull(user, "User cannot be null");
    // ...
}

// 推荐：空集合返回空列表而非 null
public List<User> findUsersByStatus(String status) {
    List<User> users = userRepository.findByStatus(status);
    return users == null ? Collections.emptyList() : users;
}

// 不推荐
public User findUser(Long id) {
    if (id == null) return null;  // 应该抛异常
    // ...
}
```

### 3.2 字符串处理

```java
// 推荐：使用 StringBuilder 进行字符串拼接
StringBuilder sb = new StringBuilder();
for (String item : items) {
    sb.append(item).append(",");
}
String result = sb.toString();

// 推荐：使用 String.format 或 MessageFormat
String message = String.format("User %s has %d orders", userName, orderCount);

// 推荐：使用 Apache Commons Lang3 的 StringUtils
if (StringUtils.isNotBlank(userName)) {
    // ...
}
```

### 3.3 集合处理

```java
// 推荐：使用钻石操作符
List<String> names = new ArrayList<>();
Map<String, User> userMap = new HashMap<>();

// 推荐：使用 Java 8 Stream API
List<String> activeUserNames = users.stream()
    .filter(User::isActive)
    .map(User::getName)
    .collect(Collectors.toList());

// 推荐：使用不可变集合
public List<String> getSupportedTypes() {
    return Collections.unmodifiableList(SUPPORTED_TYPES);
}
```

### 3.4 异常处理

```java
// 推荐：使用自定义业务异常
public User getUserById(Long id) {
    if (id == null) {
        throw new BusinessException(ErrorCode.INVALID_PARAMETER, "User ID cannot be null");
    }
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("User not found: " + id));
}

// 推荐：异常日志记录
try {
    processOrder(order);
} catch (BusinessException e) {
    log.warn("Business exception: {}", e.getMessage());
    throw e;
} catch (Exception e) {
    log.error("Unexpected error processing order: {}", orderId, e);
    throw new SystemException("Order processing failed", e);
}

// 不推荐：吞没异常
try {
    processOrder(order);
} catch (Exception e) {
    // 不要这样做！
}
```

### 3.5 日志规范

```java
// 使用 SLF4J + Logback
@Slf4j
public class UserService {
    
    public void processUser(User user) {
        // 调试日志
        log.debug("Processing user: {}", user.getId());
        
        // 信息日志
        log.info("User {} logged in from {}", user.getName(), clientIp);
        
        // 警告日志
        if (retryCount > MAX_RETRY / 2) {
            log.warn("Retry count is high: {}/{} for user {}", 
                    retryCount, MAX_RETRY, user.getId());
        }
        
        // 错误日志
        try {
            saveToDatabase(user);
        } catch (SQLException e) {
            log.error("Failed to save user: {}", user.getId(), e);
        }
    }
}
```

## 4. Spring Boot 规范

### 4.1 依赖注入

```java
// 推荐：使用构造函数注入（Spring 4.3+）
@Service
public class UserService {
    private final UserRepository userRepository;
    private final OrderService orderService;
    
    public UserService(UserRepository userRepository, OrderService orderService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
    }
}

// 推荐：使用 Lombok 简化
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final OrderService orderService;
}

// 不推荐：字段注入
@Service
public class UserService {
    @Autowired  // 不推荐
    private UserRepository userRepository;
}
```

### 4.2 Controller 规范

```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserConverter.toResponse(user));
    }
    
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(request);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(user.getId())
            .toUri();
        
        return ResponseEntity.created(location)
            .body(UserConverter.toResponse(user));
    }
    
    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<User> users = userService.findUsers(PageRequest.of(page, size));
        return ResponseEntity.ok(PageResponse.of(users.map(UserConverter::toResponse)));
    }
}
```

### 4.3 Service 规范

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }
    
    @Transactional
    public User createUser(UserCreateRequest request) {
        validateCreateRequest(request);
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setCreatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void updateUserStatus(Long id, UserStatus status) {
        User user = getUserById(id);
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
    }
}
```

### 4.4 Repository 规范

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 方法名派生查询
    Optional<User> findByEmail(String email);
    
    List<User> findByStatusAndCreatedAtAfter(String status, LocalDateTime date);
    
    boolean existsByEmail(String email);
    
    // 自定义 JPQL 查询
    @Query("SELECT u FROM User u WHERE u.status = :status ORDER BY u.createdAt DESC")
    List<User> findActiveUsers(@Param("status") String status);
    
    // 原生 SQL 查询
    @Query(value = "SELECT * FROM users WHERE created_at > :date", nativeQuery = true)
    List<User> findRecentUsers(@Param("date") LocalDateTime date);
    
    // 分页查询
    Page<User> findByStatus(String status, Pageable pageable);
    
    // 更新操作
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
```

## 5. 单元测试规范

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("根据ID查询用户 - 用户存在")
    void getUserById_WhenUserExists_ReturnsUser() {
        // Given
        Long userId = 1L;
        User expectedUser = User.builder()
            .id(userId)
            .name("张三")
            .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        
        // When
        User actualUser = userService.getUserById(userId);
        
        // Then
        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getId()).isEqualTo(userId);
        assertThat(actualUser.getName()).isEqualTo("张三");
        verify(userRepository).findById(userId);
    }
    
    @Test
    @DisplayName("根据ID查询用户 - 用户不存在")
    void getUserById_WhenUserNotExists_ThrowsNotFoundException() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.getUserById(userId))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("User not found");
    }
}
```

## 6. Checkstyle 配置

推荐使用以下 Checkstyle 规则：

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">
    <module name="TreeWalker">
        <!-- 命名规范 -->
        <module name="TypeName"/>
        <module name="MethodName"/>
        <module name="LocalVariableName"/>
        <module name="ConstantName"/>
        <module name="MemberName"/>
        <module name="ParameterName"/>
        
        <!-- 代码格式 -->
        <module name="LeftCurly"/>
        <module name="RightCurly"/>
        <module name="NeedBraces"/>
        <module name="Indentation"/>
        
        <!-- 导入 -->
        <module name="UnusedImports"/>
        <module name="RedundantImport"/>
        <module name="IllegalImport"/>
        
        <!-- 其他 -->
        <module name="LineLength">
            <property name="max" value="120"/>
        </module>
        <module name="MethodLength">
            <property name="max" value="50"/>
        </module>
    </module>
</module>
```

---

**最后更新**: 2026-03-20
