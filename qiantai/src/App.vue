<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="container topbar-inner">
        <div class="brand-wrap" @click="$router.push('/')">
          <div class="brand-logo">改</div>
          <div>
            <div class="brand-title">旧物改造教程分享平台</div>
            <div class="brand-subtitle">教程免费 · 材料包可选交易 · 社区交流</div>
          </div>
        </div>

        <div class="top-actions">
          <el-input
            v-model="keyword"
            placeholder="搜索成品名称或材料"
            size="small"
            class="search-input"
            @keyup.enter.native="goSearch"
          >
            <el-button slot="append" icon="el-icon-search" @click="goSearch" />
          </el-input>

          <template v-if="!user">
            <el-button size="small" type="primary" @click="$router.push('/login')">登录</el-button>
            <el-button size="small" @click="$router.push('/register')">注册</el-button>
          </template>
          <template v-else>
            <el-avatar
              class="nav-avatar nav-user-trigger"
              title="双击进入账号管理"
              :size="32"
              :src="user.avatar_url ? '/api' + user.avatar_url : undefined"
              icon="el-icon-user-solid"
              @dblclick.native="goAccountManagement"
            />
            <span
              class="welcome nav-user-trigger"
              title="双击进入账号管理"
              @dblclick="goAccountManagement"
            >{{ user.nickname || user.username }}</span>
            <el-button size="small" @click="goPublish">发布教程</el-button>
            <el-dropdown trigger="click" class="settings-dropdown" @command="onSettingsCommand">
              <el-button size="small" plain class="settings-trigger">
                <i class="el-icon-setting" /> 设置<i class="el-icon-arrow-down el-icon--right" />
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="account_mgmt">账号管理</el-dropdown-item>
                <el-dropdown-item divided command="about_us">关于我们</el-dropdown-item>
                <el-dropdown-item command="user_agreement">用户协议</el-dropdown-item>
                <el-dropdown-item command="privacy_policy">隐私政策</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </div>
      </div>
    </header>

    <nav class="main-nav">
      <div class="container nav-inner">
        <router-link to="/" class="nav-item" :class="{ active: $route.path === '/' }">首页</router-link>
        <router-link to="/tutorial/hub" class="nav-item" :class="{ active: $route.path.startsWith('/tutorial/hub') }">教程库</router-link>
        <router-link to="/tutorial/community" class="nav-item" :class="{ active: $route.path.startsWith('/tutorial/community') }">社区板块</router-link>
        <router-link
          v-if="user"
          :to="{ path: '/tutorial/user-center', query: { tab: 'mine' } }"
          class="nav-item nav-item-user"
          :class="{ active: $route.path.startsWith('/tutorial/user-center') }"
        >个人中心</router-link>
        <router-link
          v-else
          :to="{ path: '/login', query: { redirect: '/tutorial/user-center' } }"
          class="nav-item nav-item-user"
        >个人中心</router-link>
      </div>
    </nav>

    <main class="main-content container">
      <router-view />
    </main>

    <footer class="footer">
      <div class="container footer-inner">
        <span>© 2026 旧物改造教程分享平台</span>
        <span class="footer-links">
          <router-link to="/tutorial/static/about_us">关于我们</router-link>
          <router-link to="/tutorial/static/user_agreement">用户协议</router-link>
          <router-link to="/tutorial/static/privacy_policy">隐私政策</router-link>
        </span>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      keyword: '',
      user: JSON.parse(localStorage.getItem('tp_user') || 'null')
    }
  },
  watch: {
    $route() {
      this.syncUser()
      this.keyword = this.$route.query.keyword || ''
    }
  },
  created() {
    window.addEventListener('tp-user-changed', this.syncUser)
    this.keyword = this.$route.query.keyword || ''
  },
  beforeDestroy() {
    window.removeEventListener('tp-user-changed', this.syncUser)
  },
  methods: {
    syncUser() {
      this.user = JSON.parse(localStorage.getItem('tp_user') || 'null')
    },
    goSearch() {
      const kw = (this.keyword || '').trim()
      this.$router.push({ path: '/tutorial/hub', query: kw ? { keyword: kw } : {} })
    },
    goPublish() {
      if (!this.user) {
        this.$router.push({ path: '/login', query: { redirect: '/tutorial/publish' } })
        return
      }
      this.$router.push('/tutorial/publish')
    },
    /** 进入个人中心 — 账号管理 */
    goAccountManagement() {
      if (!this.user) return
      this.$router.push({ path: '/tutorial/user-center', query: { tab: 'account' } }).catch(() => {})
    },
    /** 顶部「设置」下拉：账号管理、静态页与退出 */
    onSettingsCommand(cmd) {
      if (cmd === 'logout') {
        this.logout()
        return
      }
      if (cmd === 'account_mgmt') {
        this.goAccountManagement()
        return
      }
      const map = {
        about_us: '/tutorial/static/about_us',
        user_agreement: '/tutorial/static/user_agreement',
        privacy_policy: '/tutorial/static/privacy_policy'
      }
      const path = map[cmd]
      if (path) this.$router.push(path)
    },
    logout() {
      localStorage.removeItem('tp_user')
      this.syncUser()
      window.dispatchEvent(new Event('tp-user-changed'))
      this.$message.success('已退出登录')
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: #f6f8fb;
  color: #243042;
}

.container {
  width: 1200px;
  max-width: calc(100% - 32px);
  margin: 0 auto;
}

.topbar {
  background: linear-gradient(90deg, #1f7a5a, #2b9e75);
  color: #fff;
}

.topbar-inner {
  min-height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.brand-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.brand-logo {
  width: 36px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  background: #ffffff;
  color: #2b9e75;
  border-radius: 8px;
  font-weight: 700;
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
}

.brand-subtitle {
  font-size: 12px;
  opacity: 0.9;
}

.top-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-input {
  width: 320px;
}

.nav-avatar {
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.85);
}

.welcome {
  font-size: 13px;
  background: rgba(255, 255, 255, 0.2);
  padding: 5px 10px;
  border-radius: 14px;
}

.nav-user-trigger {
  cursor: pointer;
}

/* 顶部「设置」：与绿色顶栏协调 */
.settings-dropdown {
  margin-left: 2px;
}
.settings-trigger {
  color: #fff !important;
  border-color: rgba(255, 255, 255, 0.55) !important;
  background: rgba(255, 255, 255, 0.12) !important;
}
.settings-trigger:hover {
  border-color: #fff !important;
  background: rgba(255, 255, 255, 0.22) !important;
}

.main-nav {
  background: #ffffff;
  border-bottom: 1px solid #e9edf5;
}

.nav-inner {
  min-height: 48px;
  display: flex;
  align-items: center;
  gap: 8px;
  overflow-x: auto;
}

.nav-item {
  color: #4a5568;
  text-decoration: none;
  font-size: 14px;
  padding: 8px 14px;
  border-radius: 6px;
  white-space: nowrap;
}

.nav-item:hover,
.nav-item.active {
  color: #1f7a5a;
  background: #e8f7f1;
}

/* 个人中心：主导航中略强调，符合「我的」入口习惯 */
.nav-item-user {
  margin-left: 4px;
  font-weight: 600;
}

.main-content {
  padding: 16px 0 24px;
}

.footer {
  margin-top: 20px;
  background: #1f2937;
  color: #cfd7e3;
}

.footer-inner {
  min-height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.footer-links {
  display: flex;
  gap: 16px;
}

.footer-links a {
  color: #cfd7e3;
  text-decoration: none;
}

.footer-links a:hover {
  color: #ffffff;
}

@media (max-width: 860px) {
  .topbar-inner {
    align-items: flex-start;
    flex-direction: column;
    padding: 10px 0;
  }

  .top-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .search-input {
    width: 100%;
  }
}
</style>
