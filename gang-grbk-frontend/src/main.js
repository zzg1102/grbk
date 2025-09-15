
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'

// 导入Gang Blog现代简约主题
import './styles/index.scss'

const app = createApp(App)

// 配置axios基础URL - 根据环境自动切换
const isDevelopment = import.meta.env.DEV
axios.defaults.baseURL = isDevelopment ? 'http://localhost:8080' : 'https://api.ymknb.cn'
axios.defaults.timeout = 10000

// 添加请求拦截器 - 自动添加认证头
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 添加响应拦截器 - 统一错误处理
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Token过期，清除本地存储并跳转到登录页
      localStorage.removeItem('auth_token')
      localStorage.removeItem('user_info')
      window.location.href = '/auth/login'
    }
    return Promise.reject(error)
  }
)

// 全局注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(router)
app.use(ElementPlus)

app.mount('#app')