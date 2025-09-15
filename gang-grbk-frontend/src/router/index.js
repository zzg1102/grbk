import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import ArticleDetail from '@/views/ArticleDetail.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/articles',
    name: 'Articles',
    component: () => import('@/views/ArticleList.vue')
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: ArticleDetail,
    props: true
  },
  {
    path: '/auth/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue')
  },
  {
    path: '/auth/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue')
  },
  {
    path: '/auth/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/auth/Login.vue') // 暂时使用登录页面
  },
  {
    path: '/categories',
    name: 'Categories',
    component: () => import('@/views/Categories.vue')
  },
  {
    path: '/category/:id',
    name: 'CategoryDetail',
    component: () => import('@/views/CategoryDetail.vue'),
    props: true
  },
  {
    path: '/tags',
    name: 'Tags',
    component: () => import('@/views/Tags.vue')
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/About.vue')
  },
  // 用户写文章路由
  {
    path: '/write',
    name: 'WriteArticle',
    component: () => import('@/views/WriteArticle.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/write/:id',
    name: 'EditArticle',
    component: () => import('@/views/WriteArticle.vue'),
    props: true,
    meta: { requiresAuth: true }
  },
  // 管理后台路由
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/Dashboard.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/articles',
    name: 'ArticleManagement',
    component: () => import('@/views/admin/ArticleManagement.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/articles/create',
    name: 'CreateArticle',
    component: () => import('@/views/admin/ArticleEditor.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/articles/edit/:id',
    name: 'EditArticle',
    component: () => import('@/views/admin/ArticleEditor.vue'),
    props: true,
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('auth_token')
  const userInfo = JSON.parse(localStorage.getItem('user_info') || '{}')

  // 检查是否需要登录
  if (to.meta.requiresAuth && !token) {
    next({
      path: '/auth/login',
      query: { redirect: to.fullPath }
    })
    return
  }

  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin && userInfo.userType !== 1) {
    next({
      path: '/',
      query: { error: 'access_denied' }
    })
    return
  }

  next()
})

export default router