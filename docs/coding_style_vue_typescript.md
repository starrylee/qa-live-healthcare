# Vue.js + TypeScript 编码规范

## 1. 项目结构规范

### 1.1 目录结构

```
src/
├── api/                    # API 接口定义
│   ├── user.ts
│   └── types.ts
├── assets/                 # 静态资源
│   ├── images/
│   ├── icons/
│   └── styles/
│       ├── variables.scss
│       ├── mixins.scss
│       └── global.scss
├── components/             # 公共组件
│   ├── common/             # 通用组件
│   │   ├── AppButton.vue
│   │   └── AppModal.vue
│   ├── business/           # 业务组件
│   │   ├── UserCard.vue
│   │   └── OrderList.vue
│   └── layout/             # 布局组件
│       ├── AppHeader.vue
│       ├── AppSidebar.vue
│       └── AppFooter.vue
├── composables/            # 组合式函数
│   ├── useUser.ts
│   ├── useAuth.ts
│   └── useForm.ts
├── directives/             # 自定义指令
│   ├── permission.ts
│   └── loading.ts
├── layouts/                # 页面布局
│   ├── DefaultLayout.vue
│   └── AuthLayout.vue
├── router/                 # 路由配置
│   ├── index.ts
│   └── routes.ts
├── stores/                 # Pinia 状态管理
│   ├── index.ts
│   ├── modules/
│   │   ├── user.ts
│   │   └── app.ts
│   └── plugins/
│       └── persist.ts
├── utils/                  # 工具函数
│   ├── request.ts          # HTTP 请求封装
│   ├── storage.ts          # 本地存储封装
│   ├── validate.ts         # 表单验证
│   └── format.ts           # 格式化工具
├── views/                  # 页面视图
│   ├── home/
│   │   └── Index.vue
│   ├── user/
│   │   ├── List.vue
│   │   ├── Detail.vue
│   │   └── components/     # 页面私有组件
│   │       └── UserForm.vue
│   └── login/
│       └── Index.vue
├── App.vue
└── main.ts
```

### 1.2 文件命名规范

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| **组件** | PascalCase，多单词大写开头 | `UserList.vue`, `AppHeader.vue` |
| **组合式函数** | camelCase，以 `use` 开头 | `useUser.ts`, `useAuth.ts` |
| **工具函数** | camelCase | `formatDate.ts`, `validateEmail.ts` |
| **常量** | UPPER_SNAKE_CASE | `API_BASE_URL`, `DEFAULT_PAGE_SIZE` |
| **类型定义** | PascalCase，类型后缀可选 | `User.ts`, `UserTypes.ts` |
| **样式文件** | kebab-case 或 camelCase | `variables.scss`, `global.scss` |
| **页面视图** | Index.vue 或具体名称 | `Index.vue`, `UserList.vue` |

## 2. Vue 组件规范

### 2.1 单文件组件结构

```vue
<script setup lang="ts">
// 1. 导入
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import type { User } from '@/types'

// 2. 类型定义
interface Props {
  userId: string
  editable?: boolean
}

interface Emits {
  (e: 'update', user: User): void
  (e: 'delete', id: string): void
}

// 3. 组件选项
defineOptions({
  name: 'UserCard'
})

// 4. Props 和 Emits
const props = withDefaults(defineProps<Props>(), {
  editable: false
})

const emit = defineEmits<Emits>()

// 5. 依赖注入
const route = useRoute()

// 6. 状态定义
const user = ref<User | null>(null)
const loading = ref(false)
const errorMessage = ref('')

// 7. 计算属性
const userName = computed(() => user.value?.name ?? 'Unknown')
const isAdmin = computed(() => user.value?.role === 'admin')

// 8. 方法定义
async function fetchUser() {
  loading.value = true
  try {
    user.value = await getUserById(props.userId)
  } catch (error) {
    errorMessage.value = 'Failed to fetch user'
    console.error(error)
  } finally {
    loading.value = false
  }
}

function handleUpdate() {
  if (user.value) {
    emit('update', user.value)
  }
}

// 9. 生命周期钩子
onMounted(() => {
  fetchUser()
})
</script>

<template>
  <div class="user-card">
    <div v-if="loading" class="user-card__loading">
      Loading...
    </div>
    <div v-else-if="errorMessage" class="user-card__error">
      {{ errorMessage }}
    </div>
    <div v-else class="user-card__content">
      <h3 class="user-card__name">{{ userName }}</h3>
      <span v-if="isAdmin" class="user-card__badge">Admin</span>
      <button v-if="editable" @click="handleUpdate">
        Edit
      </button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.user-card {
  padding: 16px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  &__loading {
    color: #999;
    text-align: center;
  }

  &__error {
    color: #f56c6c;
  }

  &__name {
    margin: 0 0 8px;
    font-size: 18px;
    font-weight: 600;
  }

  &__badge {
    display: inline-block;
    padding: 2px 8px;
    font-size: 12px;
    color: #fff;
    background: #409eff;
    border-radius: 4px;
  }
}
</style>
```

### 2.2 Props 定义规范

```typescript
// 推荐：使用类型定义
interface Props {
  // 基本类型
  title: string
  count: number
  visible: boolean
  
  // 可选属性
  description?: string
  
  // 默认值
  pageSize?: number
  
  // 复杂类型
  user: User
  items: string[]
  
  // 函数类型
  onSubmit: (data: FormData) => void
  
  // 联合类型
  size?: 'small' | 'medium' | 'large'
  
  // 任意对象
  payload?: Record<string, unknown>
}

const props = withDefaults(defineProps<Props>(), {
  description: '',
  pageSize: 10,
  size: 'medium',
  payload: () => ({})
})
```

### 2.3 Emits 定义规范

```typescript
// 推荐：使用类型定义
interface Emits {
  (e: 'update', value: string): void
  (e: 'change', value: string, oldValue: string): void
  (e: 'submit', data: FormData): Promise<boolean>
  (e: 'delete', id: string): void
}

const emit = defineEmits<Emits>()

// 使用
function handleSubmit() {
  emit('submit', formData)
}
```

### 2.4 组件选项顺序

```vue
<script setup lang="ts">
// 1. 组件名
defineOptions({ name: 'ComponentName' })

// 2. 导入
import { ... } from 'vue'

// 3. 类型定义
interface Props { ... }
interface Emits { ... }

// 4. Props/Emits
defineProps<Props>()
defineEmits<Emits>()

// 5. 组合式函数
const { user } = useUser()

// 6. 响应式状态
const count = ref(0)
const double = computed(() => count.value * 2)

// 7. 方法
function increment() {
  count.value++
}

// 8. 生命周期
onMounted(() => { ... })
</script>
```

## 3. TypeScript 规范

### 3.1 类型定义

```typescript
// 接口定义
interface User {
  id: string
  name: string
  email: string
  avatar?: string
  status: UserStatus
  createdAt: Date
  updatedAt: Date
}

// 类型别名
type UserStatus = 'active' | 'inactive' | 'banned'
type ApiResponse<T> = {
  code: number
  data: T
  message: string
}

// 枚举（推荐使用常量对象替代）
const UserRole = {
  ADMIN: 'admin',
  USER: 'user',
  GUEST: 'guest'
} as const

type UserRole = typeof UserRole[keyof typeof UserRole]

// 工具类型
type PartialUser = Partial<User>
type RequiredUser = Required<User>
type UserKeys = keyof User
type UserValues = User[keyof User]
```

### 3.2 函数定义

```typescript
// 命名函数
function calculateTotal(items: CartItem[]): number {
  return items.reduce((sum, item) => sum + item.price * item.quantity, 0)
}

// 箭头函数
const formatDate = (date: Date | string, format = 'YYYY-MM-DD'): string => {
  return dayjs(date).format(format)
}

// 异步函数
async function fetchUser(id: string): Promise<User | null> {
  try {
    const response = await api.get<ApiResponse<User>>(`/users/${id}`)
    return response.data.data
  } catch (error) {
    handleApiError(error)
    return null
  }
}

// 函数重载
function process(input: string): string
function process(input: number): number
function process(input: string | number): string | number {
  if (typeof input === 'string') {
    return input.toUpperCase()
  }
  return input * 2
}
```

### 3.3 泛型使用

```typescript
// 通用请求函数
async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(url, options)
  return response.json() as Promise<T>
}

// 使用
const user = await request<User>('/api/user/1')
const users = await request<User[]>('/api/users')

// 泛型组件 Props
interface ListProps<T> {
  items: T[]
  keyField: keyof T
  renderItem: (item: T) => VNode
}

// 泛型 Hook
function useList<T>(fetcher: () => Promise<T[]>) {
  const list = ref<T[]>([])
  const loading = ref(false)
  
  async function refresh() {
    loading.value = true
    list.value = await fetcher()
    loading.value = false
  }
  
  return { list, loading, refresh }
}
```

## 4. 组合式函数规范

### 4.1 命名与结构

```typescript
// useUser.ts
import { ref, computed } from 'vue'
import type { User, UserUpdateRequest } from '@/types'

export function useUser(userId: string) {
  // 状态
  const user = ref<User | null>(null)
  const loading = ref(false)
  const error = ref<Error | null>(null)
  
  // 计算属性
  const isLoggedIn = computed(() => !!user.value)
  const displayName = computed(() => user.value?.name ?? 'Guest')
  
  // 方法
  async function fetchUser() {
    loading.value = true
    error.value = null
    try {
      user.value = await getUserById(userId)
    } catch (err) {
      error.value = err as Error
    } finally {
      loading.value = false
    }
  }
  
  async function updateUser(data: UserUpdateRequest) {
    if (!user.value) return
    loading.value = true
    try {
      user.value = await updateUserById(user.value.id, data)
    } catch (err) {
      error.value = err as Error
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 生命周期
  onMounted(fetchUser)
  
  // 返回值
  return {
    user: readonly(user),
    loading: readonly(loading),
    error: readonly(error),
    isLoggedIn,
    displayName,
    fetchUser,
    updateUser
  }
}
```

### 4.2 最佳实践

```typescript
// 推荐：状态只读导出
export function useCounter() {
  const count = ref(0)
  
  function increment() {
    count.value++
  }
  
  return {
    count: readonly(count),  // 防止外部直接修改
    increment
  }
}

// 推荐：参数选项对象
interface UseFetchOptions {
  immediate?: boolean
  initialData?: unknown
  onError?: (error: Error) => void
}

export function useFetch<T>(url: string, options: UseFetchOptions = {}) {
  const { immediate = true, initialData, onError } = options
  // ...
}

// 使用
const { data, loading } = useFetch<User[]>('/api/users', {
  immediate: false,
  onError: (err) => message.error(err.message)
})
```

## 5. Pinia Store 规范

### 5.1 Store 结构

```typescript
// stores/modules/user.ts
import { defineStore } from 'pinia'
import type { User, UserLoginRequest } from '@/types'

export const useUserStore = defineStore('user', () => {
  // ============ State ============
  const user = ref<User | null>(null)
  const token = ref<string>('')
  const loading = ref(false)
  
  // ============ Getters ============
  const isLoggedIn = computed(() => !!token.value)
  const userName = computed(() => user.value?.name ?? '')
  const permissions = computed(() => user.value?.permissions ?? [])
  
  const hasPermission = computed(() => (permission: string) => {
    return permissions.value.includes(permission)
  })
  
  // ============ Actions ============
  async function login(credentials: UserLoginRequest) {
    loading.value = true
    try {
      const response = await api.login(credentials)
      user.value = response.user
      token.value = response.token
      storage.set('token', response.token)
      return true
    } catch (error) {
      return false
    } finally {
      loading.value = false
    }
  }
  
  function logout() {
    user.value = null
    token.value = ''
    storage.remove('token')
  }
  
  function updateUserInfo(data: Partial<User>) {
    if (user.value) {
      Object.assign(user.value, data)
    }
  }
  
  // ============ Return ============
  return {
    // State
    user,
    token,
    loading,
    // Getters
    isLoggedIn,
    userName,
    permissions,
    hasPermission,
    // Actions
    login,
    logout,
    updateUserInfo
  }
})
```

### 5.2 Store 使用

```vue
<script setup lang="ts">
import { useUserStore } from '@/stores/modules/user'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()

// 使用 storeToRefs 解构响应式状态
const { user, isLoggedIn, userName } = storeToRefs(userStore)

// 直接解构方法
const { login, logout } = userStore

// 监听状态变化
watch(isLoggedIn, (newVal) => {
  if (newVal) {
    message.success('Login successful')
  }
})
</script>
```

## 6. 样式规范

### 6.1 SCSS/BEM 规范

```vue
<template>
  <div class="user-card">
    <div class="user-card__header">
      <img class="user-card__avatar" :src="user.avatar" />
      <span class="user-card__name">{{ user.name }}</span>
    </div>
    <div class="user-card__body">
      <p class="user-card__bio">{{ user.bio }}</p>
    </div>
    <div class="user-card__footer">
      <button class="user-card__btn user-card__btn--primary">
        Follow
      </button>
      <button class="user-card__btn user-card__btn--ghost">
        Message
      </button>
    </div>
  </div>
</template>

<style scoped lang="scss">
// 使用 BEM 命名规范
.user-card {
  padding: 16px;
  background: #fff;
  border-radius: 8px;

  &__header {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
  }

  &__avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
  }

  &__name {
    margin-left: 12px;
    font-size: 16px;
    font-weight: 600;
  }

  &__body {
    margin-bottom: 16px;
  }

  &__bio {
    color: #666;
    line-height: 1.5;
  }

  &__footer {
    display: flex;
    gap: 8px;
  }

  &__btn {
    flex: 1;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;

    &--primary {
      color: #fff;
      background: #409eff;
      border: none;
    }

    &--ghost {
      color: #409eff;
      background: transparent;
      border: 1px solid #409eff;
    }
  }

  // 修饰符
  &--compact {
    padding: 12px;
  }

  &--dark {
    background: #1a1a1a;
    color: #fff;
  }
}
</style>
```

### 6.2 变量与 Mixins

```scss
// assets/styles/variables.scss
// 颜色
$primary-color: #409eff;
$success-color: #67c23a;
$warning-color: #e6a23c;
$danger-color: #f56c6c;
$info-color: #909399;

// 文字颜色
$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$text-placeholder: #c0c4cc;

// 边框颜色
$border-base: #dcdfe6;
$border-light: #e4e7ed;
$border-lighter: #ebeef5;

// 间距
$spacing-xs: 4px;
$spacing-sm: 8px;
$spacing-md: 16px;
$spacing-lg: 24px;
$spacing-xl: 32px;

// 断点
$breakpoint-xs: 480px;
$breakpoint-sm: 768px;
$breakpoint-md: 992px;
$breakpoint-lg: 1200px;
$breakpoint-xl: 1600px;

// assets/styles/mixins.scss
@mixin flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

@mixin text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@mixin multi-ellipsis($lines: 2) {
  display: -webkit-box;
  -webkit-line-clamp: $lines;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@mixin responsive($breakpoint) {
  @media screen and (max-width: $breakpoint) {
    @content;
  }
}
```

## 7. API 请求规范

### 7.1 请求封装

```typescript
// utils/request.ts
import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'

const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = storage.get('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<unknown>>) => {
    const { code, data, message } = response.data
    
    if (code === 200) {
      return data
    }
    
    // 业务错误
    throw new BusinessError(message, code)
  },
  (error) => {
    // HTTP 错误
    if (error.response) {
      const { status } = error.response
      
      switch (status) {
        case 401:
          // 未授权，清除 token 并跳转登录
          useUserStore().logout()
          router.push('/login')
          break
        case 403:
          message.error('没有权限访问')
          break
        case 404:
          message.error('请求的资源不存在')
          break
        case 500:
          message.error('服务器内部错误')
          break
        default:
          message.error('网络错误')
      }
    }
    
    return Promise.reject(error)
  }
)

export default request
```

### 7.2 API 定义

```typescript
// api/user.ts
import request from '@/utils/request'
import type { 
  User, 
  UserQueryParams, 
  UserCreateRequest, 
  UserUpdateRequest,
  PageResult 
} from './types'

export const userApi = {
  // 获取用户列表
  getList(params: UserQueryParams) {
    return request.get<PageResult<User>>('/users', { params })
  },
  
  // 获取用户详情
  getById(id: string) {
    return request.get<User>(`/users/${id}`)
  },
  
  // 创建用户
  create(data: UserCreateRequest) {
    return request.post<User>('/users', data)
  },
  
  // 更新用户
  update(id: string, data: UserUpdateRequest) {
    return request.put<User>(`/users/${id}`, data)
  },
  
  // 删除用户
  delete(id: string) {
    return request.delete<void>(`/users/${id}`)
  },
  
  // 批量删除
  batchDelete(ids: string[]) {
    return request.post<void>('/users/batch-delete', { ids })
  }
}
```

### 7.3 类型定义

```typescript
// api/types.ts
// 通用响应类型
export interface ApiResponse<T> {
  code: number
  data: T
  message: string
}

// 分页参数
export interface PageParams {
  page: number
  size: number
}

// 分页结果
export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  size: number
}

// 用户相关类型
export interface User {
  id: string
  name: string
  email: string
  avatar?: string
  status: 'active' | 'inactive'
  createdAt: string
  updatedAt: string
}

export interface UserQueryParams extends PageParams {
  keyword?: string
  status?: string
  startDate?: string
  endDate?: string
}

export interface UserCreateRequest {
  name: string
  email: string
  password: string
}

export interface UserUpdateRequest {
  name?: string
  email?: string
  avatar?: string
  status?: string
}
```

## 8. 路由规范

### 8.1 路由配置

```typescript
// router/routes.ts
import type { RouteRecordRaw } from 'vue-router'

export const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/Index.vue'),
    meta: {
      title: '首页',
      requiresAuth: false
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Index.vue'),
    meta: {
      title: '登录',
      requiresAuth: false,
      hideHeader: true
    }
  },
  {
    path: '/users',
    name: 'UserList',
    component: () => import('@/views/user/List.vue'),
    meta: {
      title: '用户管理',
      requiresAuth: true,
      permission: 'user:view'
    }
  },
  {
    path: '/users/:id',
    name: 'UserDetail',
    component: () => import('@/views/user/Detail.vue'),
    props: true,
    meta: {
      title: '用户详情',
      requiresAuth: true,
      activeMenu: '/users'
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在'
    }
  }
]
```

### 8.2 路由守卫

```typescript
// router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { routes } from './routes'
import { useUserStore } from '@/stores/modules/user'

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 系统名称`
  }
  
  // 权限验证
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login?redirect=' + encodeURIComponent(to.fullPath))
    return
  }
  
  // 权限码验证
  if (to.meta.permission && !userStore.hasPermission(to.meta.permission)) {
    next('/403')
    return
  }
  
  next()
})

export default router
```

## 9. 工具函数规范

### 9.1 日期处理

```typescript
// utils/date.ts
import dayjs from 'dayjs'

export function formatDate(
  date: Date | string | number,
  format = 'YYYY-MM-DD'
): string {
  return dayjs(date).format(format)
}

export function formatDateTime(date: Date | string | number): string {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

export function relativeTime(date: Date | string | number): string {
  return dayjs(date).fromNow()
}

export function isToday(date: Date | string | number): boolean {
  return dayjs(date).isSame(dayjs(), 'day')
}

export function addDays(date: Date | string | number, days: number): Date {
  return dayjs(date).add(days, 'day').toDate()
}
```

### 9.2 表单验证

```typescript
// utils/validate.ts
export const validators = {
  // 邮箱验证
  email: (value: string): boolean => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)
  },
  
  // 手机号验证（中国大陆）
  phone: (value: string): boolean => {
    return /^1[3-9]\d{9}$/.test(value)
  },
  
  // URL 验证
  url: (value: string): boolean => {
    try {
      new URL(value)
      return true
    } catch {
      return false
    }
  },
  
  // 身份证验证
  idCard: (value: string): boolean => {
    return /^\d{15}|\d{18}$/.test(value)
  },
  
  // 非空验证
  required: (value: unknown): boolean => {
    if (Array.isArray(value)) {
      return value.length > 0
    }
    if (typeof value === 'string') {
      return value.trim() !== ''
    }
    return value !== null && value !== undefined
  },
  
  // 长度验证
  length: (value: string, min: number, max: number): boolean => {
    const len = value.length
    return len >= min && len <= max
  }
}

// 使用
if (!validators.email(email)) {
  errors.push('请输入有效的邮箱地址')
}
```

### 9.3 存储封装

```typescript
// utils/storage.ts
interface StorageOptions {
  prefix?: string
  expires?: number // 过期时间（毫秒）
}

class Storage {
  private prefix: string

  constructor(options: StorageOptions = {}) {
    this.prefix = options.prefix ?? 'app_'
  }

  private getKey(key: string): string {
    return `${this.prefix}${key}`
  }

  set<T>(key: string, value: T, expires?: number): void {
    const data = {
      value,
      expires: expires ? Date.now() + expires : null
    }
    localStorage.setItem(this.getKey(key), JSON.stringify(data))
  }

  get<T>(key: string): T | null {
    const item = localStorage.getItem(this.getKey(key))
    if (!item) return null

    try {
      const data = JSON.parse(item)
      if (data.expires && Date.now() > data.expires) {
        this.remove(key)
        return null
      }
      return data.value as T
    } catch {
      return null
    }
  }

  remove(key: string): void {
    localStorage.removeItem(this.getKey(key))
  }

  clear(): void {
    localStorage.clear()
  }
}

export const storage = new Storage({ prefix: 'qa_' })
```

## 10. ESLint 配置

```javascript
// .eslintrc.cjs
module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true,
    node: true
  },
  extends: [
    'eslint:recommended',
    '@vue/typescript/recommended',
    'plugin:vue/vue3-recommended',
    'plugin:@typescript-eslint/recommended',
    'prettier'
  ],
  parser: 'vue-eslint-parser',
  parserOptions: {
    parser: '@typescript-eslint/parser',
    ecmaVersion: 2021,
    sourceType: 'module'
  },
  plugins: ['@typescript-eslint', 'vue'],
  rules: {
    // Vue 规则
    'vue/multi-word-component-names': 'off',
    'vue/component-tags-order': [
      'error',
      {
        order: ['script', 'template', 'style']
      }
    ],
    'vue/block-lang': [
      'error',
      {
        script: { lang: 'ts' },
        style: { lang: 'scss' }
      }
    ],
    
    // TypeScript 规则
    '@typescript-eslint/explicit-function-return-type': 'off',
    '@typescript-eslint/no-explicit-any': 'warn',
    '@typescript-eslint/no-unused-vars': [
      'error',
      { argsIgnorePattern: '^_' }
    ],
    
    // 通用规则
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off'
  }
}
```

---

**最后更新**: 2026-03-20
