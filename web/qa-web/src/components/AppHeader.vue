<template>
  <a-layout-header class="header">
    <div class="header-content">
      <div class="logo" @click="navigateTo('/')">
        <img src="https://images.pexels.com/photos/40568/medical-appointment-doctor-healthcare-40568.jpeg?auto=compress&cs=tinysrgb&w=100" alt="QA Live Healthcare" />
        <span>QA Live Healthcare</span>
      </div>
      
      <!-- 桌面端菜单 -->
      <a-menu v-model:selectedKeys="selectedKeys" mode="horizontal" class="nav-menu desktop-menu">
        <a-menu-item key="home" @click="navigateTo('/')">
          <HomeOutlined />
          首页
        </a-menu-item>
        <a-menu-item key="consultation" @click="navigateTo('/consultation')">
          <MessageOutlined />
          问诊
        </a-menu-item>
        <a-menu-item key="doctors" @click="navigateTo('/doctors')">
          <TeamOutlined />
          医生
        </a-menu-item>
        <a-menu-item key="about" @click="navigateTo('/about')">
          <InfoCircleOutlined />
          关于
        </a-menu-item>
      </a-menu>
      
      <!-- 桌面端登录按钮 -->
      <a-button type="primary" class="login-btn desktop-menu" @click="navigateTo('/doctor/login')">
        <UserOutlined />
        医生登录
      </a-button>
      
      <!-- 移动端汉堡包按钮 -->
      <a-button type="text" class="hamburger-btn mobile-menu" @click="showMobileMenu = true">
        <MenuOutlined />
      </a-button>
      
      <!-- 移动端抽屉菜单 -->
      <a-drawer
        v-model:open="showMobileMenu"
        placement="right"
        :closable="false"
        :width="280"
        class="mobile-drawer"
      >
        <div class="drawer-header">
          <span class="drawer-title">菜单</span>
          <a-button type="text" class="close-btn" @click="showMobileMenu = false">
            <CloseOutlined />
          </a-button>
        </div>
        
        <div class="drawer-content">
          <!-- 菜单项 -->
          <div 
            class="drawer-menu-item" 
            :class="{ active: selectedKeys.includes('home') }"
            @click="navigateAndClose('/')"
          >
            <HomeOutlined />
            <span>首页</span>
          </div>
          
          <div 
            class="drawer-menu-item" 
            :class="{ active: selectedKeys.includes('consultation') }"
            @click="navigateAndClose('/consultation')"
          >
            <MessageOutlined />
            <span>问诊</span>
          </div>
          
          <div 
            class="drawer-menu-item" 
            :class="{ active: selectedKeys.includes('doctors') }"
            @click="navigateAndClose('/doctors')"
          >
            <TeamOutlined />
            <span>医生</span>
          </div>
          
          <div 
            class="drawer-menu-item" 
            :class="{ active: selectedKeys.includes('about') }"
            @click="navigateAndClose('/about')"
          >
            <InfoCircleOutlined />
            <span>关于</span>
          </div>
          
          <a-divider />
          
          <!-- 医生登录按钮 -->
          <a-button 
            type="primary" 
            class="drawer-login-btn" 
            @click="navigateAndClose('/doctor/login')"
          >
            <UserOutlined />
            医生登录
          </a-button>
        </div>
      </a-drawer>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { HomeOutlined, MessageOutlined, TeamOutlined, InfoCircleOutlined, UserOutlined, MenuOutlined, CloseOutlined } from '@ant-design/icons-vue';

const router = useRouter();
const route = useRoute();
const selectedKeys = ref<string[]>(['home']);
const showMobileMenu = ref(false);

watch(() => route.path, (newPath) => {
  if (newPath === '/') {
    selectedKeys.value = ['home'];
  } else if (newPath.startsWith('/consultation')) {
    selectedKeys.value = ['consultation'];
  } else if (newPath.startsWith('/doctors')) {
    selectedKeys.value = ['doctors'];
  } else if (newPath.startsWith('/about')) {
    selectedKeys.value = ['about'];
  }
}, { immediate: true });

const navigateTo = (path: string) => {
  router.push(path);
};

const navigateAndClose = (path: string) => {
  router.push(path);
  showMobileMenu.value = false;
};
</script>

<style scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 0;
  height: 64px;
  line-height: 64px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo img {
  height: 40px;
  width: 40px;
  border-radius: 8px;
  object-fit: cover;
}

.logo span {
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

.nav-menu {
  flex: 1;
  border: none;
  margin: 0 40px;
  line-height: 64px;
}

.login-btn {
  background: #52c41a;
  border-color: #52c41a;
}

.login-btn:hover {
  background: #73d13d;
  border-color: #73d13d;
}

/* 移动端菜单样式 */
.hamburger-btn {
  font-size: 20px;
  color: #1890ff;
  display: none;
}

.mobile-menu {
  display: none;
}

.desktop-menu {
  display: flex;
}

/* 抽屉菜单样式 */
.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.drawer-title {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.close-btn {
  font-size: 18px;
  color: #666;
}

.drawer-content {
  display: flex;
  flex-direction: column;
}

.drawer-menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  margin: 4px 0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: #333;
  font-size: 15px;
}

.drawer-menu-item:hover {
  background-color: #f5f5f5;
  color: #1890ff;
}

.drawer-menu-item.active {
  background-color: #e6f7ff;
  color: #1890ff;
}

.drawer-menu-item :deep(.anticon) {
  font-size: 18px;
}

.drawer-login-btn {
  background: #52c41a;
  border-color: #52c41a;
  width: 100%;
  margin-top: 8px;
  height: 44px;
  font-size: 15px;
}

.drawer-login-btn:hover {
  background: #73d13d;
  border-color: #73d13d;
}

/* 响应式布局 - 手机端和iPad */
@media screen and (max-width: 1024px) {
  .desktop-menu {
    display: none !important;
  }
  
  .mobile-menu {
    display: flex !important;
  }
  
  .hamburger-btn {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .logo span {
    font-size: 16px;
  }
}
</style>
