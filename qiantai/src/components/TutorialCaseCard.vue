<template>
  <el-card shadow="hover" class="tutorial-case-card">
    <img :src="coverSrc" class="cover" alt="" />
    <h3>{{ item.title }}</h3>
    <p class="summary">{{ item.summary || '暂无摘要' }}</p>
    <p class="meta">作者：{{ item.author_name || '-' }} | 难度：{{ difficultyText }}</p>
    <p class="meta">材料：{{ item.material_summary || '-' }}</p>
    <div class="tags-row">
      <el-tag size="mini">浏览 {{ item.view_count }}</el-tag>
      <el-tag size="mini" type="success">点赞 {{ item.like_count }}</el-tag>
      <el-tag size="mini" type="warning">收藏 {{ item.favorite_count }}</el-tag>
    </div>
    <div class="card-actions">
      <el-button type="text" @click="$emit('preview', item)">教程预览</el-button>
    </div>
  </el-card>
</template>

<script>
export default {
  name: 'TutorialCaseCard',
  props: {
    item: { type: Object, required: true },
    defaultCover: { type: String, default: '/api/files/download/31' }
  },
  computed: {
    coverSrc() {
      return this.item.cover_image_url ? '/api' + this.item.cover_image_url : this.defaultCover
    },
    difficultyText() {
      const map = { 1: '入门', 2: '普通', 3: '进阶', 4: '困难' }
      return map[this.item.difficulty_level] || '普通'
    }
  }
}
</script>

<style scoped>
.tutorial-case-card .cover {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 8px;
  display: block;
}
.tutorial-case-card h3 {
  margin: 10px 0 6px;
  font-size: 16px;
}
.summary {
  color: #666;
  min-height: 42px;
  font-size: 13px;
  line-height: 1.5;
}
.meta {
  color: #999;
  font-size: 12px;
  margin: 4px 0;
}
.tags-row {
  margin-top: 8px;
}
.tags-row .el-tag {
  margin-right: 6px;
}
.card-actions {
  margin-top: 8px;
}
</style>
