<template>
  <div class="app">
    <!-- 头部导航 -->
    <header class="header">
      <div class="container">
        <h1 class="logo">Gang的个人博客</h1>
        <nav class="nav">
          <el-menu mode="horizontal" :ellipsis="false" background-color="transparent" text-color="#333">
            <el-menu-item index="1">首页</el-menu-item>
            <el-menu-item index="2">技术分享</el-menu-item>
            <el-menu-item index="3">生活随笔</el-menu-item>
            <el-menu-item index="4">关于我</el-menu-item>
          </el-menu>
        </nav>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="main">
      <div class="container">
        <div class="content-wrapper">
          <!-- 左侧内容区 -->
          <div class="content">
            <div class="welcome-card">
              <el-card shadow="hover">
                <h2>欢迎来到我的个人博客！</h2>
                <p>这是一个基于 <strong>Spring Boot 3 + Vue 3 + Element Plus</strong> 构建的现代化个人博客系统。</p>

                <div class="features">
                  <h3>✨ 技术特色</h3>
                  <ul>
                    <li>🚀 <strong>后端</strong>：Spring Boot 3 + MyBatis Plus + MySQL + Redis + RabbitMQ</li>
                    <li>🎨 <strong>前端</strong>：Vue 3 + Element Plus + Vite</li>
                    <li>📝 <strong>功能</strong>：文章管理、分类标签、评论系统、搜索功能</li>
                    <li>🔐 <strong>安全</strong>：JWT认证、权限控制、XSS防护</li>
                  </ul>
                </div>

                <div class="actions">
                  <el-button type="primary" @click="testConnection" :loading="loading">
                    <el-icon><Connection /></el-icon>
                    测试后端连接
                  </el-button>
                  <el-button type="success" @click="viewDocs">
                    <el-icon><Document /></el-icon>
                    查看API文档
                  </el-button>
                </div>
              </el-card>
            </div>

            <!-- API测试结果 -->
            <div v-if="apiResult" class="api-result">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>API连接测试结果</span>
                    <el-tag :type="apiResult.success ? 'success' : 'danger'">
                      {{ apiResult.success ? '连接成功' : '连接失败' }}
                    </el-tag>
                  </div>
                </template>
                <pre>{{ apiResult.data }}</pre>
              </el-card>
            </div>
          </div>

          <!-- 右侧边栏 -->
          <aside class="sidebar">
            <el-card shadow="hover" class="sidebar-card">
              <template #header>
                <span>📊 博客统计</span>
              </template>
              <div class="stats">
                <div class="stat-item">
                  <span class="label">文章总数:</span>
                  <span class="value">12</span>
                </div>
                <div class="stat-item">
                  <span class="label">分类数量:</span>
                  <span class="value">3</span>
                </div>
                <div class="stat-item">
                  <span class="label">总浏览量:</span>
                  <span class="value">1,234</span>
                </div>
              </div>
            </el-card>

            <el-card shadow="hover" class="sidebar-card">
              <template #header>
                <span>🏷️ 热门标签</span>
              </template>
              <div class="tags">
                <el-tag class="tag">Spring Boot</el-tag>
                <el-tag class="tag" type="success">Vue.js</el-tag>
                <el-tag class="tag" type="warning">Java</el-tag>
                <el-tag class="tag" type="danger">MySQL</el-tag>
              </div>
            </el-card>
          </aside>
        </div>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <p>&copy; 2024 Gang的个人博客. Powered by Spring Boot 3 & Vue 3</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Connection, Document } from '@element-plus/icons-vue'
import axios from 'axios'

const loading = ref(false)
const apiResult = ref(null)

const testConnection = async () => {
  loading.value = true
  apiResult.value = null

  try {
    // 尝试连接后端API
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

const viewDocs = () => {
  window.open('http://localhost:8080/api/doc.html', '_blank')
}
</script>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  margin: 0;
  font-size: 1.8rem;
  font-weight: bold;
}

.main {
  flex: 1;
  padding: 2rem 0;
  background-color: #f5f7fa;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.welcome-card {
  margin-bottom: 2rem;
}

.features {
  margin: 1.5rem 0;
}

.features ul {
  list-style: none;
  padding: 0;
}

.features li {
  padding: 0.5rem 0;
  border-left: 3px solid #409eff;
  padding-left: 1rem;
  margin: 0.5rem 0;
}

.actions {
  margin-top: 2rem;
}

.actions .el-button {
  margin-right: 1rem;
}

.api-result {
  margin-top: 2rem;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.api-result pre {
  background-color: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  overflow-x: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.sidebar-card {
  margin-bottom: 2rem;
}

.stats {
  padding: 0;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item:last-child {
  border-bottom: none;
}

.value {
  font-weight: bold;
  color: #409eff;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  cursor: pointer;
}

.footer {
  background-color: #2c3e50;
  color: white;
  text-align: center;
  padding: 2rem 0;
}

@media (max-width: 768px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }

  .container {
    flex-direction: column;
    text-align: center;
  }

  .nav {
    margin-top: 1rem;
  }
}
</style>