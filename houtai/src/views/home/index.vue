<template>
  <div class="tp-admin-home">
    <el-card shadow="never" class="welcome-card">
      <div class="welcome-row">
        <div>
          <h2>旧物改造教程分享平台 · 管理后台</h2>
          <p>管理员可在此统一处理用户、内容和材料包商品治理。</p>
        </div>
        <el-button type="primary" size="small" icon="el-icon-refresh" @click="loadOverview">刷新数据</el-button>
      </div>
    </el-card>

    <el-row :gutter="12" class="stat-row">
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-label">用户总数</div>
          <div class="stat-value">{{ stats.userCount }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-label">教程总数</div>
          <div class="stat-value">{{ stats.tutorialCount }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-label">帖子总数</div>
          <div class="stat-value">{{ stats.postCount }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-label">评论总数</div>
          <div class="stat-value">{{ stats.commentCount }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-label">材料包总数</div>
          <div class="stat-value">{{ stats.kitCount }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-label">异常用户</div>
          <div class="stat-value danger">{{ stats.disabledUserCount }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="panel-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never">
          <div slot="header" class="panel-title">近期高热帖子</div>
          <el-table :data="topPosts" size="mini" border>
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
            <el-table-column prop="author_name" label="作者" width="120" />
            <el-table-column prop="hot_score" label="热度" width="90" />
            <el-table-column prop="comment_count" label="评论" width="80" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="never">
          <div slot="header" class="panel-title">待处理风险提醒</div>
          <el-alert :title="`禁用账号：${stats.disabledUserCount} 个`" type="warning" :closable="false" show-icon style="margin-bottom:8px" />
          <el-alert :title="`已删除违规教程：${stats.deletedTutorialCount} 个`" type="error" :closable="false" show-icon style="margin-bottom:8px" />
          <el-alert :title="`已删除违规帖子：${stats.deletedPostCount} 个`" type="error" :closable="false" show-icon style="margin-bottom:8px" />
          <el-alert :title="`已删除违规评论：${stats.deletedCommentCount} 条（教程评论+帖子评论）`" type="error" :closable="false" show-icon style="margin-bottom:8px" />
          <el-alert :title="`已删除违规材料包：${stats.deletedKitCount} 个`" type="error" :closable="false" show-icon style="margin-bottom:8px" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { Session } from '@/utils/storage';
import {
  tpAdminDashboardStats
} from '@/api/tpadmin/tpAdminApi';

export default {
  name: 'home',
  data() {
    return {
      stats: {
        userCount: 0,
        disabledUserCount: 0,
        tutorialCount: 0,
        deletedTutorialCount: 0,
        postCount: 0,
        deletedPostCount: 0,
        commentCount: 0,
        deletedCommentCount: 0,
        kitCount: 0,
        deletedKitCount: 0
      },
      topPosts: []
    }
  },
  created() {
    this.loadOverview();
  },
  methods: {
    adminUserId() {
      const user = Session.get('userInfo');
      return user ? user.id : null;
    },
    loadOverview() {
      const adminUserId = this.adminUserId();
      if (!adminUserId) return;
      tpAdminDashboardStats(adminUserId).then(res => {
        const data = res.data || {};
        this.stats.userCount = data.userCount || 0;
        this.stats.disabledUserCount = data.disabledUserCount || 0;
        this.stats.tutorialCount = data.tutorialCount || 0;
        this.stats.deletedTutorialCount = data.deletedTutorialCount || 0;
        this.stats.postCount = data.postCount || 0;
        this.stats.deletedPostCount = data.deletedPostCount || 0;
        this.stats.commentCount = data.commentCount || 0;
        this.stats.deletedCommentCount = data.deletedCommentCount || 0;
        this.stats.kitCount = data.kitCount || 0;
        this.stats.deletedKitCount = data.deletedKitCount || 0;
        this.topPosts = data.hotPosts || [];
      }).catch(() => {});
    }
  }
}
</script>

<style scoped>
.tp-admin-home {
  padding: 2px;
}

.welcome-card {
  margin-bottom: 12px;
}

.welcome-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.welcome-row h2 {
  margin: 0;
  font-size: 22px;
  color: #243042;
}

.welcome-row p {
  margin: 6px 0 0;
  font-size: 13px;
  color: #728095;
}

.stat-row {
  margin-bottom: 12px;
}

.stat-card {
  margin-bottom: 12px;
}

.stat-label {
  font-size: 12px;
  color: #8091a7;
}

.stat-value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 700;
  color: #1f2d3d;
}

.stat-value.danger {
  color: #d9534f;
}

.panel-title {
  font-weight: 600;
}
</style>
