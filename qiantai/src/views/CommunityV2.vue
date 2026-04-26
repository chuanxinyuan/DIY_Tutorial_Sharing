<template>
  <div class="community-page">
    <el-card shadow="never" class="hero-card">
      <div class="hero-head">
        <div>
          <h2>社区板块</h2>
          <p>交流改造经验、分享踩坑心得、讨论材料包使用方案。</p>
        </div>
        <el-button type="primary" icon="el-icon-edit" @click="openPublish">发布帖子</el-button>
      </div>

      <div class="toolbar">
        <el-radio-group v-model="sort" size="small" @change="onSortChange">
          <el-radio-button label="hot">热门排序</el-radio-button>
          <el-radio-button label="new">最新排序</el-radio-button>
        </el-radio-group>

        <el-input
          v-model.trim="keyword"
          clearable
          size="small"
          placeholder="搜索帖子标题或内容"
          class="search-input"
          @clear="pageNo = 1"
          @input="pageNo = 1"
        >
          <i slot="prefix" class="el-input__icon el-icon-search" />
        </el-input>
      </div>
    </el-card>

    <el-skeleton :loading="loading" animated :rows="6" class="list-skeleton" />

    <div v-if="!loading" class="post-list">
      <el-empty v-if="filteredList.length === 0" description="暂无匹配帖子" />

      <el-card v-for="post in pagedList" :key="post.id" class="post-card" shadow="hover">
        <div class="post-main" @click="openDetail(post.id)">
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-content">{{ post.preview }}</p>
          <div class="meta-row">
            <span><i class="el-icon-user" /> {{ post.author_name || '匿名用户' }}</span>
            <span><i class="el-icon-chat-dot-round" /> {{ post.comment_count || 0 }} 回复</span>
            <span><i class="el-icon-view" /> {{ post.view_count || 0 }} 浏览</span>
            <span><i class="el-icon-time" /> {{ formatDate(post.created_at) }}</span>
          </div>
        </div>
        <div class="post-actions">
          <el-button type="text" @click="openDetail(post.id)">查看详情</el-button>
          <el-button v-if="isMyPost(post)" type="text" @click="openEditPost(post)">编辑</el-button>
          <el-button v-if="isMyPost(post)" type="text" style="color:#f56c6c" @click="deletePost(post)">删除</el-button>
        </div>
      </el-card>

      <div class="pager-wrap" v-if="filteredList.length > pageSize">
        <el-pagination
          background
          layout="prev, pager, next"
          :current-page.sync="pageNo"
          :page-size="pageSize"
          :total="filteredList.length"
        />
      </div>
    </div>

    <el-drawer
      title="帖子详情"
      :visible.sync="detailVisible"
      size="52%"
      :with-header="true"
      destroy-on-close
    >
      <div class="detail-wrap" v-if="detail.post">
        <div class="detail-scroll">
          <h3 class="detail-title">{{ detail.post.title }}</h3>
          <div class="detail-meta">
            <span>作者：{{ detail.post.author_name || '匿名用户' }}</span>
            <span>浏览：{{ detail.post.view_count || 0 }}</span>
            <span>发布时间：{{ formatDate(detail.post.created_at) }}</span>
          </div>
          <div class="detail-content">{{ detail.post.content }}</div>

          <el-divider content-position="left">回复（{{ (detail.comments || []).length }}）</el-divider>

          <el-empty v-if="(detail.comments || []).length === 0" description="还没有回复，来抢沙发" />
          <div v-else class="reply-list">
            <div
              v-for="comment in commentDisplayList"
              :key="comment.id"
              class="reply-item"
              :class="{ 'nested-reply': comment.level > 0 }"
              :style="{ marginLeft: (Math.min(comment.level, 6) * 16) + 'px' }"
            >
              <div class="reply-user">{{ comment.user_name || '用户' }}</div>
              <div class="reply-content">{{ comment.content }}</div>
              <div class="reply-time">
                <span>{{ formatDate(comment.created_at) }}</span>
                <el-button type="text" size="mini" @click="setReplyTarget(comment)">回复TA</el-button>
                <el-button v-if="isMyComment(comment)" type="text" size="mini" style="color:#f56c6c" @click="deleteComment(comment)">删除</el-button>
              </div>
            </div>
          </div>
        </div>

        <el-form :model="replyForm" ref="replyForm" :rules="replyRules" label-width="0" class="reply-form-panel">
          <div v-if="replyTarget" class="reply-target-tip">
            正在回复：{{ replyTarget.user_name }}
            <el-button type="text" size="mini" @click="clearReplyTarget">取消</el-button>
          </div>
          <el-form-item prop="content">
            <el-input
              type="textarea"
              v-model.trim="replyForm.content"
              :rows="4"
              maxlength="500"
              show-word-limit
              placeholder="写下你的回复（最多500字）"
            />
          </el-form-item>
          <div class="reply-action">
            <el-button @click="detailVisible = false">关闭</el-button>
            <el-button type="primary" :loading="replySubmitting" @click="submitReply">发表回复</el-button>
          </div>
        </el-form>
      </div>
    </el-drawer>

    <el-dialog title="发布帖子" :visible.sync="publishVisible" width="640px" destroy-on-close>
      <el-form ref="postFormRef" :model="postForm" :rules="postRules" label-width="70px">
        <el-form-item label="标题" prop="title">
          <el-input v-model.trim="postForm.title" maxlength="60" show-word-limit placeholder="一句话概括你的问题或经验" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            type="textarea"
            v-model.trim="postForm.content"
            :rows="7"
            maxlength="2000"
            show-word-limit
            placeholder="请描述具体场景、改造材料和遇到的问题，这样更容易获得高质量回复"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="publishVisible = false">取消</el-button>
        <el-button type="primary" :loading="postSubmitting" @click="submitPost">发布</el-button>
      </span>
    </el-dialog>

    <el-dialog title="编辑帖子" :visible.sync="editVisible" width="640px" destroy-on-close>
      <el-form ref="editFormRef" :model="editForm" :rules="postRules" label-width="70px">
        <el-form-item label="标题" prop="title">
          <el-input v-model.trim="editForm.title" maxlength="60" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" v-model.trim="editForm.content" :rows="7" maxlength="2000" show-word-limit />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editSubmitting" @click="submitEditPost">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'CommunityV2',
  data() {
    return {
      sort: 'hot',
      keyword: '',
      loading: false,
      list: [],
      pageNo: 1,
      pageSize: 8,

      publishVisible: false,
      postSubmitting: false,
      postForm: { title: '', content: '' },
      postRules: {
        title: [
          { required: true, message: '请输入帖子标题', trigger: 'blur' },
          { min: 4, max: 60, message: '标题长度 4-60 字', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入帖子内容', trigger: 'blur' },
          { min: 10, max: 2000, message: '内容长度 10-2000 字', trigger: 'blur' }
        ]
      },

      detailVisible: false,
      detail: {},
      currentPostId: null,
      replyTarget: null,
      replySubmitting: false,
      replyForm: { content: '' },
      replyRules: {
        content: [
          { required: true, message: '请输入回复内容', trigger: 'blur' },
          { min: 2, max: 500, message: '回复长度 2-500 字', trigger: 'blur' }
        ]
      },

      editVisible: false,
      editSubmitting: false,
      editPostId: null,
      editForm: { title: '', content: '' }
    }
  },
  computed: {
    filteredList() {
      const kw = (this.keyword || '').toLowerCase()
      if (!kw) return this.list
      return this.list.filter(item => {
        const title = (item.title || '').toLowerCase()
        const content = (item.preview || item.content || '').toLowerCase()
        return title.includes(kw) || content.includes(kw)
      })
    },
    pagedList() {
      const start = (this.pageNo - 1) * this.pageSize
      return this.filteredList.slice(start, start + this.pageSize)
    },
    commentDisplayList() {
      const comments = (this.detail.comments || []).map(c => ({ ...c, children: [] }))
      if (!comments.length) return []
      const byId = {}
      comments.forEach(c => {
        byId[c.id] = c
      })
      const roots = []
      comments.forEach(c => {
        const pid = c.parent_comment_id
        if (pid && byId[pid]) {
          byId[pid].children.push(c)
        } else {
          roots.push(c)
        }
      })
      const sortByTime = (a, b) => {
        const ta = new Date(a.created_at || 0).getTime()
        const tb = new Date(b.created_at || 0).getTime()
        return ta - tb
      }
      const result = []
      const walk = (nodes, level) => {
        nodes.sort(sortByTime).forEach(node => {
          result.push({ ...node, level })
          if (node.children && node.children.length) {
            walk(node.children, level + 1)
          }
        })
      }
      walk(roots.sort(sortByTime), 0)
      return result
    }
  },
  watch: {
    filteredList() {
      const maxPage = Math.max(1, Math.ceil(this.filteredList.length / this.pageSize))
      if (this.pageNo > maxPage) this.pageNo = maxPage
    }
  },
  created() {
    this.loadList()
  },
  methods: {
    getUser() {
      return JSON.parse(localStorage.getItem('tp_user') || 'null')
    },
    getUserId() {
      const u = this.getUser()
      return u ? u.id : null
    },
    ensureLogin() {
      if (this.getUserId()) return true
      this.$message.warning('请先登录后再操作')
      this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } })
      return false
    },
    isMyPost(post) {
      const uid = this.getUserId()
      return !!uid && Number(post.author_user_id) === Number(uid)
    },
    isMyComment(comment) {
      const uid = this.getUserId()
      return !!uid && Number(comment.user_id) === Number(uid)
    },
    formatDate(v) {
      if (!v) return '-'
      return String(v).replace('T', ' ').slice(0, 19)
    },
    onSortChange() {
      this.pageNo = 1
      this.loadList()
    },
    loadList() {
      this.loading = true
      this.$axios.get('/api/front/v2/community/page', { params: { sort: this.sort } }).then(res => {
        this.list = (res.data.data || {}).list || []
      }).finally(() => {
        this.loading = false
      })
    },
    openPublish() {
      if (!this.ensureLogin()) return
      this.publishVisible = true
    },
    submitPost() {
      if (!this.ensureLogin()) return
      this.$refs.postFormRef.validate(valid => {
        if (!valid) return
        this.postSubmitting = true
        this.$axios.post('/api/front/v2/community/post', {
          userId: this.getUserId(),
          title: this.postForm.title,
          content: this.postForm.content
        }).then(() => {
          this.$message.success('帖子发布成功')
          this.publishVisible = false
          this.postForm = { title: '', content: '' }
          this.pageNo = 1
          this.loadList()
        }).finally(() => {
          this.postSubmitting = false
        })
      })
    },
    openDetail(id) {
      this.currentPostId = id
      this.replyForm.content = ''
      this.replyTarget = null
      this.$axios.get('/api/front/v2/community/' + id).then(res => {
        this.detail = res.data.data || {}
        this.detailVisible = true
      })
    },
    setReplyTarget(comment) {
      this.replyTarget = comment
      if (!this.replyForm.content) {
        this.replyForm.content = '@' + (comment.user_name || '用户') + ' '
      }
    },
    clearReplyTarget() {
      this.replyTarget = null
    },
    submitReply() {
      if (!this.ensureLogin()) return
      this.$refs.replyForm.validate(valid => {
        if (!valid) return
        this.replySubmitting = true
        this.$axios.post('/api/front/v2/community/' + this.currentPostId + '/reply', {
          userId: this.getUserId(),
          content: this.replyForm.content,
          parentCommentId: this.replyTarget ? this.replyTarget.id : null
        }).then(() => {
          this.$message.success('回复成功')
          this.replyForm.content = ''
          this.replyTarget = null
          this.openDetail(this.currentPostId)
          this.loadList()
        }).finally(() => {
          this.replySubmitting = false
        })
      })
    },
    deleteComment(comment) {
      this.$confirm('确定删除这条评论吗？', '提示', { type: 'warning' }).then(() => {
        this.$axios.delete('/api/front/v2/community/comment/' + comment.id, {
          params: { userId: this.getUserId() }
        }).then(() => {
          this.$message.success('评论已删除')
          this.openDetail(this.currentPostId)
          this.loadList()
        })
      }).catch(() => {})
    },
    openEditPost(post) {
      this.$axios.get('/api/front/v2/community/' + post.id).then(res => {
        const p = (res.data.data || {}).post || {}
        this.editPostId = post.id
        this.editForm = { title: p.title || '', content: p.content || '' }
        this.editVisible = true
      })
    },
    submitEditPost() {
      this.$refs.editFormRef.validate(valid => {
        if (!valid) return
        this.editSubmitting = true
        this.$axios.put('/api/front/v2/community/post/' + this.editPostId, {
          userId: this.getUserId(),
          title: this.editForm.title,
          content: this.editForm.content
        }).then(() => {
          this.$message.success('帖子已更新')
          this.editVisible = false
          this.loadList()
          if (this.detailVisible && this.currentPostId === this.editPostId) {
            this.openDetail(this.currentPostId)
          }
        }).finally(() => {
          this.editSubmitting = false
        })
      })
    },
    deletePost(post) {
      this.$confirm('确定删除该帖子吗？删除后不可恢复。', '提示', { type: 'warning' }).then(() => {
        this.$axios.delete('/api/front/v2/community/post/' + post.id, {
          params: { userId: this.getUserId() }
        }).then(() => {
          this.$message.success('帖子已删除')
          this.loadList()
          if (this.detailVisible && this.currentPostId === post.id) {
            this.detailVisible = false
          }
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.community-page {
  padding: 10px 20px;
}

.hero-card {
  margin-bottom: 14px;
  border-radius: 10px;
}

.hero-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.hero-head h2 {
  margin: 0;
  font-size: 24px;
  color: #1f2d3d;
}

.hero-head p {
  margin: 6px 0 0;
  color: #66788a;
  font-size: 13px;
}

.toolbar {
  margin-top: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 320px;
}

.post-list {
  display: grid;
  gap: 12px;
}

.post-card {
  border-radius: 10px;
}

.post-main {
  cursor: pointer;
}

.post-title {
  margin: 0 0 8px;
  font-size: 18px;
  color: #1f2d3d;
}

.post-content {
  margin: 0;
  color: #4f6072;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta-row {
  margin-top: 10px;
  color: #7d8b99;
  font-size: 12px;
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.post-actions {
  margin-top: 6px;
  text-align: right;
}

.pager-wrap {
  display: flex;
  justify-content: center;
  margin: 8px 0 2px;
}

.detail-wrap {
  height: calc(100vh - 94px);
  display: flex;
  flex-direction: column;
  padding: 4px 8px 8px;
  box-sizing: border-box;
}

.detail-scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-right: 6px;
}

.detail-title {
  margin: 0;
  font-size: 22px;
  color: #1f2d3d;
}

.detail-meta {
  margin-top: 10px;
  color: #7d8b99;
  font-size: 12px;
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.detail-content {
  margin-top: 12px;
  white-space: pre-wrap;
  color: #2c3e50;
  line-height: 1.8;
}

.reply-list {
  display: grid;
  gap: 10px;
}

.reply-item {
  border: 1px solid #edf2f7;
  border-radius: 8px;
  padding: 10px 12px;
  background: #fbfdff;
}

.nested-reply {
  border-left: 3px solid #dce8fb;
  background: #f7fbff;
}

.reply-user {
  font-size: 13px;
  font-weight: 600;
  color: #1f2d3d;
}

.reply-content {
  margin-top: 6px;
  color: #2c3e50;
  line-height: 1.6;
  white-space: pre-wrap;
}

.reply-time {
  margin-top: 6px;
  font-size: 12px;
  color: #8ea0b2;
  display: flex;
  align-items: center;
  gap: 8px;
}

.reply-target-tip {
  margin-bottom: 8px;
  font-size: 12px;
  color: #5f6f82;
  background: #f4f8ff;
  border: 1px solid #dce8fb;
  border-radius: 6px;
  padding: 6px 10px;
}

.reply-form-panel {
  flex: 0 0 auto;
  margin-top: 10px;
  padding: 10px 0 calc(10px + env(safe-area-inset-bottom));
  border-top: 1px solid #edf2f7;
  background: #fff;
}

.reply-action {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

@media (max-width: 900px) {
  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }
}
</style>
