<template>
  <div class="layout home-layout">
    <!-- 现代导航栏 -->
    <header class="navbar layout-header">
      <div class="navbar-container">
        <a href="/" class="navbar-brand gradient-text">Gang's Blog</a>

        <nav class="navbar-nav">
          <a href="/" class="active">首页</a>
          <a href="/articles">文章</a>
          <a href="/categories">分类</a>
          <a href="/tags">标签</a>
          <a href="/about">关于</a>
        </nav>

        <div class="navbar-actions">
          <button class="btn btn-ghost btn-sm" @click="toggleTheme">
            <el-icon><Sunny v-if="isDark" /><Moon v-else /></el-icon>
          </button>
          <router-link to="/auth/login" class="btn btn-ghost btn-sm">
            登录
          </router-link>
          <router-link to="/auth/register" class="btn btn-primary btn-sm">
            注册
          </router-link>
        </div>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="layout-main">
      <!-- Hero区域 -->
      <section class="hero-section">
        <div class="container">
          <h1 class="hero-title fade-in">欢迎来到我的博客</h1>
          <p class="hero-description slide-up">
            分享技术见解，记录思考历程，探索代码之美。
            这里是关于Spring Boot、Vue.js和现代Web开发的技术博客。
          </p>
          <div class="flex justify-center gap-4 scale-in">
            <button class="btn btn-primary btn-lg" @click="viewArticles">
              <el-icon><Reading /></el-icon>
              开始阅读
            </button>
            <button class="btn btn-secondary btn-lg" @click="viewDocs">
              <el-icon><Document /></el-icon>
              API文档
            </button>
          </div>
        </div>
      </section>

      <!-- 特色内容 -->
      <section class="featured-section">
        <div class="container">
          <h2 class="section-title">技术栈特色</h2>
          <div class="card-grid">
            <div class="card hover-lift">
              <div class="card-body">
                <div class="flex items-center gap-3 mb-4">
                  <div class="tag tag-lg" style="background: #e0f2fe; color: #0369a1; border-color: #7dd3fc;">
                    🚀
                  </div>
                  <h3 class="h4 mb-0">现代后端</h3>
                </div>
                <p class="text-secondary">
                  Spring Boot 3 + MyBatis Plus + MySQL + Redis + RabbitMQ
                  构建高性能、可扩展的后端服务
                </p>
              </div>
            </div>

            <div class="card hover-lift">
              <div class="card-body">
                <div class="flex items-center gap-3 mb-4">
                  <div class="tag tag-lg" style="background: #f0fdf4; color: #166534; border-color: #86efac;">
                    🎨
                  </div>
                  <h3 class="h4 mb-0">现代前端</h3>
                </div>
                <p class="text-secondary">
                  Vue 3 + Element Plus + Vite
                  打造流畅的用户体验和现代化界面
                </p>
              </div>
            </div>

            <div class="card hover-lift">
              <div class="card-body">
                <div class="flex items-center gap-3 mb-4">
                  <div class="tag tag-lg" style="background: #fef3c7; color: #92400e; border-color: #fde68a;">
                    🔐
                  </div>
                  <h3 class="h4 mb-0">企业级安全</h3>
                </div>
                <p class="text-secondary">
                  JWT认证、权限控制、XSS防护
                  确保系统安全可靠
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 最新文章 -->
      <section class="latest-section bg-secondary">
        <div class="container">
          <h2 class="section-title">最新文章</h2>
          <div class="card-grid-large">
            <article class="card card-article hover-lift">
              <div class="card-body">
                <div class="card-meta">
                  <span>2024-01-15</span>
                  <span>•</span>
                  <span>技术分享</span>
                </div>
                <h3 class="card-title">
                  <a href="/article/1" class="article-title">Spring Boot 3.0 新特性详解</a>
                </h3>
                <p class="text-secondary">
                  Spring Boot 3.0 带来了许多令人兴奋的新特性，本文将详细介绍这些新功能...
                </p>
                <div class="card-tags">
                  <span class="tag">Java</span>
                  <span class="tag">Spring Boot</span>
                </div>
              </div>
            </article>

            <article class="card card-article hover-lift">
              <div class="card-body">
                <div class="card-meta">
                  <span>2024-01-14</span>
                  <span>•</span>
                  <span>技术分享</span>
                </div>
                <h3 class="card-title">
                  <a href="/article/2" class="article-title">Vue 3 Composition API 实战指南</a>
                </h3>
                <p class="text-secondary">
                  Vue 3 的 Composition API 为我们提供了更加灵活的代码组织方式...
                </p>
                <div class="card-tags">
                  <span class="tag tag-success">Vue.js</span>
                  <span class="tag tag-gray">前端</span>
                </div>
              </div>
            </article>

            <article class="card card-article hover-lift">
              <div class="card-body">
                <div class="card-meta">
                  <span>2024-01-13</span>
                  <span>•</span>
                  <span>生活随笔</span>
                </div>
                <h3 class="card-title">
                  <a href="/article/3" class="article-title">我的编程学习心得</a>
                </h3>
                <p class="text-secondary">
                  分享一下我在编程学习路上的一些心得和体会，希望对大家有所帮助...
                </p>
                <div class="card-tags">
                  <span class="tag tag-warning">学习</span>
                  <span class="tag tag-gray">心得</span>
                </div>
              </div>
            </article>
          </div>

          <div class="text-center mt-8">
            <button class="btn btn-ghost" @click="viewAllArticles">
              查看更多文章
              <el-icon><ArrowRight /></el-icon>
            </button>
          </div>
        </div>
      </section>

      <!-- API测试结果 -->
      <div v-if="apiResult" class="container mt-8">
        <div class="card glass-effect">
          <div class="card-header">
            <h3 class="card-title">API连接测试结果</h3>
            <span class="tag" :class="apiResult.success ? 'tag-success' : 'tag-error'">
              {{ apiResult.success ? '连接成功' : '连接失败' }}
            </span>
          </div>
          <div class="card-body">
            <pre><code>{{ apiResult.data }}</code></pre>
          </div>
        </div>
      </div>
    </main>

    <!-- 现代页脚 -->
    <footer class="layout-footer bg-secondary border-t">
      <div class="container py-8">
        <div class="text-center">
          <p class="text-secondary">
            &copy; 2024 Gang的个人博客 - 使用
            <span class="gradient-text font-semibold">Spring Boot 3</span>
            &
            <span class="gradient-text font-semibold">Vue 3</span>
            构建
          </p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Connection, Document, Reading, Sunny, Moon, ArrowRight } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const loading = ref(false)
const apiResult = ref(null)
const isDark = ref(false)

// 主题切换
const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.setAttribute('data-theme', isDark.value ? 'dark' : 'light')
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}

// 初始化主题
onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches

  isDark.value = savedTheme === 'dark' || (!savedTheme && prefersDark)
  document.documentElement.setAttribute('data-theme', isDark.value ? 'dark' : 'light')
})

// 测试后端连接
const testConnection = async () => {
  loading.value = true
  apiResult.value = null

  try {
    const response = await axios.get('/api/v1/test/hello', { timeout: 5000 })
    apiResult.value = {
      success: true,
      data: JSON.stringify(response.data, null, 2)
    }
  } catch (error) {
    apiResult.value = {
      success: false,
      data: `连接失败: ${error.message}\n\n后端服务可能未启动，请确保:\n1. 数据库服务已启动\n2. 后端Spring Boot应用已启动\n3. 端口8080未被占用`
    }
  } finally {
    loading.value = false
  }
}

// 导航方法
const viewDocs = () => {
  window.open('http://localhost:8080/doc.html', '_blank')
}

const viewArticles = () => {
  router.push('/articles')
}

const viewAllArticles = () => {
  router.push('/articles')
}
</script>

