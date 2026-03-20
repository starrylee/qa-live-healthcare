# QA Healthcare 前端编码规范

## 概述

本文档定义了 QA Healthcare 前端项目的编码规范，基于 Vue 3 + TypeScript + Vite 技术栈，参考业界标准（Vue.js 官方风格指南、Airbnb JavaScript Style Guide、Google TypeScript Style Guide）制定。

**适用范围：**
- 所有 TypeScript/Vue 源代码文件（*.ts, *.vue）
- Vue 3 Composition API 项目
- Ant Design Vue 组件库

**目标：**
- 提高代码可读性和可维护性
- 保证代码质量一致性
- 充分发挥 TypeScript 类型安全优势
- 便于团队协作和 Code Review

---

## 一、命名规范

### 1.1 文件命名

| 类型 | 规范 | 示例 |
|------|------|------|
| **Vue 组件** | PascalCase，多单词大驼峰 | `UserProfile.vue`, `TheHeader.vue` |
| **TypeScript 文件** | camelCase 或 PascalCase | `userService.ts`, `UserDTO.ts` |
| **常量/配置** | camelCase 或 kebab-case | `api-config.ts`, `constants.ts` |
| **类型定义** | PascalCase，.d.ts 后缀 | `types/user.d.ts` |
| **样式文件** | kebab-case，与组件同名 | `user-profile.scss` |
| **工具函数** | camelCase | `dateUtils.ts`, `formatters.ts` |
| **测试文件** | 被测文件名 + .spec.ts | `UserProfile.spec.ts` |

```
src/
├── components/                      # 【组件目录】
│   ├── AppHeader.vue               # ✅ 大驼峰，The/App 前缀表示基础组件
│   ├── UserCard.vue                # ✅ 业务组件
│   └── common/
│       └── BaseButton.vue          # ✅ Base 前缀表示通用基础组件
│
├── views/                          # 【页面目录】
│   ├── Home.vue                    # ✅ 页面使用大驼峰
│   ├── UserLogin.vue               # ✅ 动词+名词
│   └── UserProfile.vue             # ✅ 名词组合
│
├── composables/                    # 【组合式函数】
│   ├── useAuth.ts                  # ✅ use 前缀
│   ├── useUser.ts                  # ✅ use 前缀
│   └── useFetch.ts                 # ✅ use 前缀
│
├── utils/                          # 【工具函数】
│   ├── dateUtils.ts                # ✅ camelCase
│   ├── formatters.ts               # ✅ camelCase
│   └── validators.ts               # ✅ camelCase
│
├── types/                          # 【类型定义】
│   ├── user.d.ts                   # ✅ 类型声明文件
│   ├── api.d.ts                    # ✅ API 类型
│   └── index.ts                    # ✅ 类型导出
│
└── store/                          # 【状态管理】
    └── index.ts                    # ✅ camelCase
```

### 1.2 组件命名

#### Vue 组件名规范

```vue
<!-- ✅ 正确示例 -->
<!-- 基础组件使用 The/App 前缀 -->
<template>
  <AppHeader />
  <AppFooter />
  <TheModal />
</template>

<!-- 业务组件使用名词 -->
<template>
  <UserCard />
  <DoctorList />
  <QuestionItem />
</template>

<!-- 组件名应该完整单词，避免缩写 -->
<template>
  <UserProfile />   <!-- ✅ -->
  <UserProf />      <!-- ❌ 缩写不清晰 -->
</template>
```

#### 组件名大小写

```vue
<!-- ✅ 在模板中使用 PascalCase（推荐） -->
<template>
  <UserProfile :user="currentUser" />
  <DoctorList :doctors="doctors" />
</template>

<!-- ✅ 也可以使用 kebab-case -->
<template>
  <user-profile :user="currentUser" />
  <doctor-list :doctors="doctors" />
</template>
```

### 1.3 变量命名

#### TypeScript 变量

```typescript
// ✅ 正确示例

// 基础类型
const userName: string = 'zhangsan';
const userAge: number = 25;
const isActive: boolean = true;
const createdAt: Date = new Date();

// 引用类型
const userList: User[] = [];
const userMap: Map<string, User> = new Map();
const userSet: Set<string> = new Set();

// 对象
const currentUser: User = { id: 1, name: '张三' };

// 函数
const getUserById = (id: number): User | null => { };
const fetchUserList = async (): Promise<User[]> => { };
const handleSubmit = (): void => { };

// 布尔值使用 is/has/can/should 前缀
const isLoading = ref(false);
const hasError = ref(false);
const canSubmit = computed(() => formValid.value);
const shouldShow = computed(() => items.value.length > 0);

// 常量
const MAX_RETRY_COUNT = 3;
const API_BASE_URL = '/api/v1';
const DEFAULT_PAGE_SIZE = 10;

// ❌ 错误示例
const user_name = 'zhangsan';     // 蛇形命名
const UserName = 'zhangsan';      // 大驼峰用于变量
const getuserbyid = () => { };    // 未驼峰
const getUserByID = () => { };    // 缩写 ID 应该为 Id
const loading = ref(false);       // 布尔值无前缀
```

### 1.4 类型命名

```typescript
// ✅ 正确示例

// 接口使用 PascalCase
interface User {
  id: number;
  name: string;
  email: string;
}

interface UserDTO {
  username: string;
  password: string;
}

// 类型别名使用 PascalCase
type UserId = number | string;
type UserStatus = 'active' | 'inactive' | 'deleted';

// 枚举使用 PascalCase，值使用全大写
enum UserRole {
  ADMIN = 'ADMIN',
  USER = 'USER',
  GUEST = 'GUEST'
}

enum HttpStatus {
  OK = 200,
  NOT_FOUND = 404,
  SERVER_ERROR = 500
}

// 泛型使用 T, K, V 或具体含义
function getValue<T>(obj: T, key: keyof T): T[keyof T] {
  return obj[key];
}

interface ApiResponse<T> {
  code: number;
  data: T;
  message: string;
}

// ❌ 错误示例
interface user { }              // 小写
interface userDTO { }           // 首字母小写
enum userRole { }               // 小写
```

### 1.5 组合式函数命名

```typescript
// ✅ 正确示例 - 使用 use 前缀

// useAuth.ts - 认证相关
export function useAuth() {
  const token = ref('');
  const login = async (credentials: LoginDTO) => { };
  const logout = () => { };
  
  return { token, login, logout };
}

// useUser.ts - 用户数据
export function useUser() {
  const user = ref<User | null>(null);
  const fetchUser = async (id: number) => { };
  const updateUser = async (data: UserDTO) => { };
  
  return { user, fetchUser, updateUser };
}

// useFetch.ts - 数据获取
export function useFetch<T>(url: string) {
  const data = ref<T | null>(null);
  const loading = ref(false);
  const error = ref<Error | null>(null);
  
  const execute = async () => { };
  
  return { data, loading, error, execute };
}

// ❌ 错误示例
export function auth() { }       // 缺少 use 前缀
export function userHook() { }   // Hook 是 React 概念
```

---

## 二、Vue 组件规范

### 2.1 组件结构（SFC）

```vue
<!-- ✅ 推荐顺序：script -> template -> style -->
<script setup lang="ts">
// 1. 导入（按类型分组，按字母排序）
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { UserOutlined } from '@ant-design/icons-vue';

import { useUser } from '@/composables/useUser';
import { formatDate } from '@/utils/dateUtils';
import type { User } from '@/types/user';

// 2. 类型定义（如需要）
interface Props {
  userId: number;
  editable?: boolean;
}

interface Emits {
  (e: 'update', user: User): void;
  (e: 'delete', id: number): void;
}

// 3. 组件配置
defineOptions({
  name: 'UserProfile'
});

// 4. Props 和 Emits
const props = withDefaults(defineProps<Props>(), {
  editable: false
});

const emit = defineEmits<Emits>();

// 5. 组合式函数
const route = useRoute();
const router = useRouter();
const { user, fetchUser } = useUser();

// 6. 响应式数据
const loading = ref(false);
const error = ref<string | null>(null);
const formData = reactive<User>({
  id: 0,
  name: '',
  email: ''
});

// 7. 计算属性
const isValid = computed(() => {
  return formData.name.length > 0 && formData.email.includes('@');
});

const displayName = computed(() => {
  return user.value?.name || '未知用户';
});

// 8. 方法
const handleSubmit = async (): Promise<void> => {
  if (!isValid.value) return;
  
  try {
    loading.value = true;
    await updateUser(formData);
    message.success('更新成功');
    emit('update', formData);
  } catch (err) {
    error.value = err instanceof Error ? err.message : '更新失败';
    message.error(error.value);
  } finally {
    loading.value = false;
  }
};

const handleDelete = (): void => {
  emit('delete', props.userId);
};

// 9. 生命周期钩子
onMounted(async () => {
  await fetchUser(props.userId);
});

// 10. Watch
watch(() => props.userId, (newId) => {
  fetchUser(newId);
});
</script>

<template>
  <!-- 使用语义化标签 -->
  <section class="user-profile">
    <header class="profile-header">
      <h2>{{ displayName }}</h2>
    </header>
    
    <main class="profile-content">
      <!-- v-if/v-for 避免同时使用，如需则使用 template -->
      <template v-if="loading">
        <a-skeleton active />
      </template>
      
      <template v-else-if="error">
        <a-alert :message="error" type="error" />
      </template>
      
      <template v-else>
        <a-form :model="formData" @finish="handleSubmit">
          <!-- 表单内容 -->
        </a-form>
      </template>
    </main>
    
    <footer v-if="editable" class="profile-footer">
      <a-button type="primary" @click="handleSubmit">
        保存
      </a-button>
    </footer>
  </section>
</template>

<style scoped>
/* 使用 scoped 限制样式作用域 */
.user-profile {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
}

.profile-header {
  margin-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.profile-content {
  min-height: 200px;
}

.profile-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #e8e8e8;
}
</style>
```

### 2.2 Props 定义规范

```vue
<script setup lang="ts">
// ✅ 使用 TypeScript 接口定义 Props
interface Props {
  // 基础类型
  title: string;
  count: number;
  visible: boolean;
  
  // 对象类型
  user: User;
  options?: SelectOption[];
  
  // 联合类型
  status: 'active' | 'inactive' | 'pending';
  size?: 'small' | 'middle' | 'large';
  
  // 函数类型
  onSubmit?: (data: FormData) => void;
  onCancel?: () => void;
  
  // 复杂类型
  formatter?: (value: number) => string;
}

// ✅ 使用 withDefaults 设置默认值
const props = withDefaults(defineProps<Props>(), {
  count: 0,
  visible: false,
  options: () => [],
  size: 'middle',
  onSubmit: undefined,
  onCancel: undefined
});
</script>
```

### 2.3 Emits 定义规范

```vue
<script setup lang="ts">
// ✅ 使用类型定义 Emits
interface Emits {
  // 无参数
  (e: 'close'): void;
  
  // 单参数
  (e: 'update', value: string): void;
  
  // 多参数
  (e: 'change', value: string, oldValue: string): void;
  
  // 对象参数
  (e: 'submit', data: FormData): void;
}

const emit = defineEmits<Emits>();

// ✅ 使用方式
const handleClick = () => {
  emit('close');
};

const handleChange = (value: string) => {
  emit('update', value);
  emit('change', value, oldValue.value);
};
</script>
```

### 2.4 响应式数据规范

```vue
<script setup lang="ts">
import { ref, reactive, computed } from 'vue';

// ✅ ref 用于基础类型和需要替换的对象
const count = ref<number>(0);
const message = ref<string>('');
const isLoading = ref<boolean>(false);
const user = ref<User | null>(null);

// ✅ reactive 用于复杂的对象（不需要替换整个对象）
const formState = reactive<FormState>({
  name: '',
  email: '',
  age: 0
});

// ✅ computed 用于派生状态
const fullName = computed(() => {
  return `${user.value?.firstName} ${user.value?.lastName}`;
});

const isValid = computed(() => {
  return formState.name.length > 0 && formState.email.includes('@');
});

// ✅ 数组操作
const list = ref<User[]>([]);

// 修改数组内容
list.value.push(newUser);           // ✅
list.value = [...list.value, newUser]; // ✅ 也可以

// 替换整个数组
list.value = newList;               // ✅ ref 可以替换

// ❌ 错误示例
// Object.assign(formState, { name: 'new' }); // 不必要，直接修改即可
formState.name = 'new';             // ✅ 直接修改 reactive 对象
</script>
```

---

## 三、TypeScript 规范

### 3.1 类型推断与显式类型

```typescript
// ✅ 简单类型可以推断
const name = 'zhangsan';        // 类型推断为 string
const count = 10;               // 类型推断为 number
const isActive = true;          // 类型推断为 boolean

// ✅ 复杂类型需要显式声明
const user: User = { id: 1, name: '张三' };
const list: string[] = ['a', 'b'];
const map: Map<string, User> = new Map();

// ✅ 函数参数和返回值需要显式类型
function add(a: number, b: number): number {
  return a + b;
}

// ✅ 异步函数返回 Promise
async function fetchUser(id: number): Promise<User> {
  const response = await api.get(`/users/${id}`);
  return response.data;
}

// ✅ Vue ref 需要泛型
const user = ref<User | null>(null);
const list = ref<User[]>([]);
const count = ref<number>(0);

// ❌ 避免使用 any
const data: any = response;     // ❌
const data: unknown = response; // ✅ 使用 unknown 更安全

// ❌ 不必要的类型注解
const name: string = 'zhangsan'; // 可以推断，不需要显式声明
```

### 3.2 接口与类型别名

```typescript
// ✅ 对象结构使用 interface
interface User {
  id: number;
  name: string;
  email: string;
  createdAt: Date;
}

// ✅ 使用 extends 扩展接口
interface AdminUser extends User {
  role: 'admin' | 'superadmin';
  permissions: string[];
}

// ✅ 联合类型使用 type
type Status = 'active' | 'inactive' | 'deleted';
type ID = string | number;

// ✅ 复杂类型使用 type
type ApiResponse<T> = {
  code: number;
  data: T;
  message: string;
};

type PartialUser = Partial<User>;
type RequiredUser = Required<User>;
type UserKeys = keyof User;

// ✅ 函数类型
type Handler<T> = (data: T) => void;
type AsyncHandler<T, R> = (data: T) => Promise<R>;

// ✅ 条件类型
type NonNullable<T> = T extends null | undefined ? never : T;
```

### 3.3 泛型使用

```typescript
// ✅ 函数泛型
function identity<T>(arg: T): T {
  return arg;
}

function getProperty<T, K extends keyof T>(obj: T, key: K): T[K] {
  return obj[key];
}

// ✅ 接口泛型
interface ApiResponse<T> {
  code: number;
  data: T;
  message: string;
}

interface PaginatedResponse<T> {
  list: T[];
  total: number;
  page: number;
  pageSize: number;
}

// ✅ 类泛型
class Store<T> {
  private data: T[] = [];
  
  add(item: T): void {
    this.data.push(item);
  }
  
  getAll(): T[] {
    return this.data;
  }
}

// ✅ 泛型约束
interface HasId {
  id: number;
}

function findById<T extends HasId>(items: T[], id: number): T | undefined {
  return items.find(item => item.id === id);
}
```

---

## 四、代码格式

### 4.1 缩进与换行

```typescript
// ✅ 使用 2 个空格缩进
function processUser(user: User): void {
  if (user.isActive) {
    sendNotification(user);
  }
}

// ✅ 适当换行，单行不超过 100 字符
const result = someArray
  .filter(item => item.isActive)
  .map(item => item.name)
  .sort((a, b) => a.localeCompare(b));

// ✅ 对象/数组适当换行
const user = {
  id: 1,
  name: '张三',
  email: 'zhangsan@example.com',
  address: {
    city: '北京',
    street: '长安街'
  }
};

const list = [
  'item1',
  'item2',
  'item3'
];
```

### 4.2 引号与分号

```typescript
// ✅ 使用单引号
const name = 'zhangsan';
const message = 'Hello, world!';

// ✅ 模板字符串使用反引号
const greeting = `Hello, ${name}!`;
const multiline = `
  Line 1
  Line 2
`;

// ✅ 使用分号
const count = 10;
const name = 'test';

function foo(): void {
  return;
}

// ❌ 避免无分号的风格
const count = 10    // 不推荐
const name = 'test' // 不推荐
```

### 4.3 空格使用

```typescript
// ✅ 操作符两侧加空格
const sum = a + b;
const isValid = age > 18 && name !== '';

// ✅ 逗号后加空格
const list = [1, 2, 3];
const obj = { a: 1, b: 2 };

// ✅ 冒号后加空格（对象）
const user: User = { id: 1, name: '张三' };

// ✅ 函数参数列表括号内不加空格
function add(a: number, b: number): number { }

// ✅ 函数调用括号内不加空格
add(1, 2);

// ❌ 错误示例
const sum=a+b;        // 无空格
function add( a: number ) { }  // 括号内空格
```

---

## 五、注释规范

### 5.1 注释类型

| 类型 | 使用场景 | 示例 |
|------|----------|------|
| **文档注释** | 公共 API、复杂函数 | `/** */` |
| **行注释** | 单行说明 | `//` |
| **TODO/FIXME** | 待办事项 | `// TODO:` / `// FIXME:` |
| **禁用标记** | 临时禁用代码 | `// eslint-disable-next-line` |

### 5.2 文档注释

```typescript
/**
 * 获取用户信息
 * 
 * @param id - 用户ID
 * @returns 用户对象，未找到返回 null
 * @throws {Error} 当网络请求失败时抛出
 * 
 * @example
 * ```ts
 * const user = await getUserById(1);
 * if (user) {
 *   console.log(user.name);
 * }
 * ```
 */
async function getUserById(id: number): Promise<User | null> {
  // ...
}

/**
 * 用户状态枚举
 */
enum UserStatus {
  /** 激活状态 */
  ACTIVE = 'active',
  /** 未激活状态 */
  INACTIVE = 'inactive'
}
```

### 5.3 代码注释

```typescript
// ✅ 解释为什么这么做，而不是做了什么
// 使用 Set 去重比 filter 性能更好
const uniqueIds = [...new Set(ids)];

// ✅ 标记待办事项
// TODO: 添加缓存逻辑，避免重复请求
async function fetchData() {
  return api.get('/data');
}

// ✅ 标记需要修复
// FIXME: 这里存在内存泄漏风险，需要优化
function processLargeData(data: Data[]) {
  // ...
}

// ❌ 不要写无意义的注释
// 将 a 和 b 相加
const sum = a + b;
```

---

## 六、最佳实践

### 6.1 组合式函数最佳实践

```typescript
// ✅ 单一职责
// useAuth.ts - 只处理认证
export function useAuth() {
  const token = ref<string | null>(localStorage.getItem('token'));
  
  const login = async (credentials: LoginDTO) => {
    const { data } = await api.post('/login', credentials);
    token.value = data.token;
    localStorage.setItem('token', data.token);
  };
  
  const logout = () => {
    token.value = null;
    localStorage.removeItem('token');
  };
  
  const isAuthenticated = computed(() => !!token.value);
  
  return {
    token: readonly(token),
    login,
    logout,
    isAuthenticated
  };
}

// ✅ 返回 readonly 防止外部直接修改
import { readonly } from 'vue';

export function useCounter() {
  const count = ref(0);
  
  const increment = () => count.value++;
  const decrement = () => count.value--;
  
  return {
    count: readonly(count),
    increment,
    decrement
  };
}
```

### 6.2 状态管理最佳实践

```typescript
// ✅ store/index.ts
import { reactive, readonly } from 'vue';

// 定义 State 类型
interface State {
  user: User | null;
  isLoggedIn: boolean;
  permissions: string[];
}

// 创建响应式状态
const state = reactive<State>({
  user: null,
  isLoggedIn: false,
  permissions: []
});

// 定义 mutations
const setUser = (user: User | null): void => {
  state.user = user;
  state.isLoggedIn = !!user;
};

const setPermissions = (permissions: string[]): void => {
  state.permissions = permissions;
};

// 导出 readonly 状态和操作方法
export const useStore = () => ({
  state: readonly(state) as State,
  setUser,
  setPermissions
});
```

### 6.3 API 调用最佳实践

```typescript
// ✅ utils/request.ts
import axios, { AxiosInstance, AxiosError } from 'axios';

const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器
request.interceptors.response.use(
  (response) => response.data,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      // 处理未授权
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default request;

// ✅ api/user.ts
import request from '@/utils/request';
import type { User, UserDTO } from '@/types/user';

export const userApi = {
  getById: (id: number): Promise<User> => 
    request.get(`/users/${id}`),
  
  list: (): Promise<User[]> => 
    request.get('/users'),
  
  create: (data: UserDTO): Promise<User> => 
    request.post('/users', data),
  
  update: (id: number, data: UserDTO): Promise<User> => 
    request.put(`/users/${id}`, data),
  
  delete: (id: number): Promise<void> => 
    request.delete(`/users/${id}`)
};
```

### 6.4 错误处理最佳实践

```typescript
// ✅ 统一错误处理
try {
  const user = await userApi.getById(userId);
  userInfo.value = user;
} catch (error) {
  if (error instanceof AxiosError) {
    if (error.response?.status === 404) {
      message.error('用户不存在');
    } else {
      message.error('获取用户信息失败');
    }
  } else {
    message.error('未知错误');
    console.error(error);
  }
} finally {
  loading.value = false;
}

// ✅ 使用 Result 类型处理错误
interface Result<T, E = Error> {
  success: true;
  data: T;
} | {
  success: false;
  error: E;
}

async function fetchUser(id: number): Promise<Result<User>> {
  try {
    const user = await api.get(`/users/${id}`);
    return { success: true, data: user };
  } catch (error) {
    return { 
      success: false, 
      error: error instanceof Error ? error : new Error('Unknown error')
    };
  }
}
```

---

## 七、性能优化

### 7.1 Vue 性能优化

```vue
<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue';

// ✅ 使用 computed 缓存计算结果
const filteredList = computed(() => {
  return list.value.filter(item => item.isActive);
});

// ✅ 使用 watchEffect 自动追踪依赖
import { watchEffect } from 'vue';

watchEffect(() => {
  console.log(user.value?.name);
  // 自动追踪 user.value 的访问
});

// ✅ 使用 nextTick 等待 DOM 更新
const updateHeight = async () => {
  list.value.push(newItem);
  await nextTick();
  // DOM 已更新
  calculateHeight();
};

// ✅ v-for 使用 key
<template>
  <div v-for="item in list" :key="item.id">
    {{ item.name }}
  </div>
</template>

// ✅ 使用 v-once 渲染静态内容
<template>
  <div v-once>
    <!-- 只渲染一次 -->
    <h1>{{ staticTitle }}</h1>
  </div>
</template>
</script>
```

### 7.2 异步组件

```vue
<script setup lang="ts">
import { defineAsyncComponent } from 'vue';

// ✅ 使用异步组件减少首屏加载
const UserModal = defineAsyncComponent(() => 
  import('./components/UserModal.vue')
);

// ✅ 带加载状态
const AsyncComponent = defineAsyncComponent({
  loader: () => import('./components/HeavyComponent.vue'),
  loadingComponent: LoadingSpinner,
  errorComponent: ErrorComponent,
  delay: 200,
  timeout: 3000
});
</script>
```

---

## 八、IDE 配置

### 8.1 推荐配置

**VS Code 推荐扩展：**
- Volar - Vue 3 官方扩展
- TypeScript Vue Plugin (Volar)
- ESLint
- Prettier
- Vue VSCode Snippets

**推荐设置：**
```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "typescript.tsdk": "node_modules/typescript/lib"
}
```

### 8.2 ESLint 配置

```javascript
// .eslintrc.js
module.exports = {
  env: {
    browser: true,
    es2021: true
  },
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:vue/vue3-recommended',
    'prettier'
  ],
  parser: 'vue-eslint-parser',
  parserOptions: {
    parser: '@typescript-eslint/parser',
    sourceType: 'module'
  },
  plugins: ['@typescript-eslint'],
  rules: {
    'vue/multi-word-component-names': 'off',
    '@typescript-eslint/explicit-function-return-type': 'warn',
    '@typescript-eslint/no-explicit-any': 'error'
  }
};
```

### 8.3 Prettier 配置

```javascript
// .prettierrc
{
  "semi": true,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "es5",
  "printWidth": 100,
  "endOfLine": "lf"
}
```

---

## 附录

### A. 代码审查清单

- [ ] 命名是否符合规范（文件、变量、函数、组件）
- [ ] TypeScript 类型是否正确定义（避免 any）
- [ ] Props 和 Emits 是否使用类型定义
- [ ] 是否使用了合适的组合式函数
- [ ] 是否有未处理的异步错误
- [ ] 是否使用了响应式数据的最佳实践（ref vs reactive）
- [ ] 组件是否遵循 SFC 规范
- [ ] 是否有适当的注释
- [ ] 代码格式是否符合 Prettier 配置

### B. 参考文档

- [Vue.js 风格指南](https://vuejs.org/style-guide/)
- [TypeScript 官方文档](https://www.typescriptlang.org/docs/)
- [Vue 3 组合式 API](https://vuejs.org/guide/extras/composition-api-faq.html)
- [Airbnb JavaScript Style Guide](https://github.com/airbnb/javascript)

### C. 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2025-11-03 | 初始版本，基于 Vue 3 + TypeScript 制定 |
