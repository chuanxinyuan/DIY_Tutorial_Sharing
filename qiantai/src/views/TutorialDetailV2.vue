<template>
  <div class="tp-page" v-if="tutorial">
    <el-card>
      <h2>{{ tutorial.title }}</h2>
      <p>{{ tutorial.summary }}</p>
      <p>作者：{{ tutorial.author_name }} | 难度：{{ tutorial.difficulty_level }} | 预计耗时：{{ tutorial.production_time_minutes || '-' }} 分钟</p>
      <p>标签：<el-tag size="mini" v-for="t in tags" :key="t.id" style="margin-right:6px;">{{ t.tag_name }}</el-tag></p>
      <p>材料：<el-tag type="info" size="mini" v-for="m in materials" :key="m.id" style="margin-right:6px;">{{ m.material_name }}</el-tag></p>

      <!-- 仿短视频风格的点赞 / 收藏 -->
      <div class="dy-actions">
        <div class="dy-btn" role="button" @click="toggleLike">
          <span class="dy-icon heart" :class="{ active: userLiked, bump: likeBump }">♥</span>
          <span class="dy-num">{{ likeCountDisplay }}</span>
        </div>
        <div class="dy-btn" role="button" @click="toggleFavorite">
          <span class="dy-icon star" :class="{ active: userFavorited, bump: favBump }">★</span>
          <span class="dy-num">{{ favCountDisplay }}</span>
        </div>
      </div>
    </el-card>

    <el-card style="margin-top: 12px">
      <h3>分步教程</h3>
      <el-timeline>
        <el-timeline-item v-for="step in steps" :key="step.id" :timestamp="'第' + step.step_no + '步'">
          <h4>{{ step.step_title || ('步骤' + step.step_no) }}</h4>
          <p>{{ step.step_content }}</p>
          <img v-if="step.step_image_url" :src="'/api' + step.step_image_url" class="step-img" />
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-card style="margin-top: 12px" v-if="materialKit">
      <h3>材料包购买</h3>
      <p>名称：{{ materialKit.kit_name }}</p>
      <p class="kit-price-line">单价：<span class="price-strong">¥ {{ formatMoney(materialKit.price) }}</span>，库存：{{ materialKit.stock }}</p>
      <el-button type="danger" @click="openBuyDialog">立即购买</el-button>
    </el-card>

    <el-card style="margin-top: 12px">
      <h3>评论</h3>
      <el-input type="textarea" v-model="commentContent" :rows="3" placeholder="写下你的评论" />
      <el-button type="primary" style="margin-top: 8px" @click="comment">发表评论</el-button>
      <div v-for="c in comments" :key="c.id" class="comment">
        <div class="comment-head">
          <strong>{{ c.user_name || '匿名' }}</strong>
          <span class="comment-time">{{ c.created_at || '' }}</span>
          <el-button
            v-if="isMyComment(c)"
            type="text"
            size="mini"
            style="color:#f56c6c;margin-left:auto"
            @click="deleteComment(c)"
          >删除评论</el-button>
        </div>
        <p class="comment-text">{{ c.content }}</p>
      </div>
    </el-card>

    <!-- 购买确认弹窗 -->
    <el-dialog
      title="确认购买材料包"
      :visible.sync="buyDialogVisible"
      width="420px"
      append-to-body
      @closed="onBuyDialogClosed"
    >
      <div v-if="materialKit" class="buy-dialog-body">
        <p class="buy-kit-name">{{ materialKit.kit_name }}</p>
        <p class="buy-kit-desc" v-if="materialKit.kit_desc">{{ materialKit.kit_desc }}</p>
        <p class="buy-row">
          <span>单价</span>
          <span class="price-strong">¥ {{ formatMoney(materialKit.price) }}</span>
        </p>
        <p class="buy-row">
          <span>购买数量</span>
          <el-input-number v-model="buyQty" :min="1" :max="maxBuyQty" size="small" />
        </p>
        <p class="buy-row total">
          <span>总计金额</span>
          <span class="price-total">¥ {{ orderTotalFormatted }}</span>
        </p>
      </div>
      <span slot="footer">
        <el-button @click="buyDialogVisible = false">取消购买</el-button>
        <el-button type="primary" :loading="buySubmitting" @click="confirmBuy">确认购买</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'TutorialDetailV2',
  data() {
    return {
      tutorial: null,
      steps: [],
      tags: [],
      materials: [],
      materialKit: null,
      comments: [],
      commentContent: '',
      userLiked: false,
      userFavorited: false,
      likeBump: false,
      favBump: false,
      buyDialogVisible: false,
      buyQty: 1,
      buySubmitting: false
    }
  },
  computed: {
    likeCountDisplay() {
      return this.tutorial ? Number(this.tutorial.like_count) || 0 : 0
    },
    favCountDisplay() {
      return this.tutorial ? Number(this.tutorial.favorite_count) || 0 : 0
    },
    maxBuyQty() {
      if (!this.materialKit) return 1
      const s = Number(this.materialKit.stock)
      return Math.max(1, isNaN(s) ? 1 : s)
    },
    orderTotalFormatted() {
      if (!this.materialKit) return '0.00'
      const unit = parseFloat(this.materialKit.price) || 0
      const total = unit * (this.buyQty || 1)
      return total.toFixed(2)
    }
  },
  created() {
    this.loadDetail()
  },
  methods: {
    formatMoney(v) {
      const n = parseFloat(v)
      return (isNaN(n) ? 0 : n).toFixed(2)
    },
    getUserId() {
      const user = JSON.parse(localStorage.getItem('tp_user') || 'null')
      return user ? user.id : null
    },
    mustLogin() {
      const userId = this.getUserId()
      if (!userId) {
        this.$message.warning('请先登录后再操作')
        this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } })
        return null
      }
      return userId
    },
    loadDetail() {
      const userId = this.getUserId()
      const params = userId ? { userId } : {}
      this.$axios.get('/api/front/v2/tutorial/' + this.$route.params.id, { params }).then(res => {
        const data = res.data.data || {}
        this.tutorial = data.tutorial
        this.steps = data.steps || []
        this.tags = data.tags || []
        this.materials = data.materials || []
        this.materialKit = data.materialKit
        this.comments = data.comments || []
        this.userLiked = !!data.userLiked
        this.userFavorited = !!data.userFavorited
      })
    },
    triggerBump(kind) {
      if (kind === 'like') {
        this.likeBump = true
        this.$nextTick(() => {
          setTimeout(() => { this.likeBump = false }, 450)
        })
      } else {
        this.favBump = true
        this.$nextTick(() => {
          setTimeout(() => { this.favBump = false }, 450)
        })
      }
    },
    toggleLike() {
      const userId = this.mustLogin()
      if (!userId) return
      this.triggerBump('like')
      const id = this.$route.params.id
      const url = this.userLiked
        ? '/api/front/v2/tutorial/' + id + '/unlike'
        : '/api/front/v2/tutorial/' + id + '/like'
      this.$axios.post(url, { userId }).then(res => {
        if (!res.data || res.data.code !== '0') {
          this.$message.error((res.data && res.data.msg) || '操作失败')
          return
        }
        this.loadDetail()
      })
    },
    toggleFavorite() {
      const userId = this.mustLogin()
      if (!userId) return
      this.triggerBump('fav')
      const id = this.$route.params.id
      const url = this.userFavorited
        ? '/api/front/v2/tutorial/' + id + '/unfavorite'
        : '/api/front/v2/tutorial/' + id + '/favorite'
      this.$axios.post(url, { userId }).then(res => {
        if (!res.data || res.data.code !== '0') {
          this.$message.error((res.data && res.data.msg) || '操作失败')
          return
        }
        this.loadDetail()
      })
    },
    comment() {
      const userId = this.mustLogin()
      if (!userId) return
      if (!this.commentContent) return this.$message.error('请输入评论')
      this.$axios.post('/api/front/v2/tutorial/' + this.$route.params.id + '/comment', {
        userId,
        content: this.commentContent
      }).then(() => {
        this.commentContent = ''
        this.loadDetail()
      })
    },
    isMyComment(c) {
      const userId = this.getUserId()
      return !!userId && Number(c.user_id) === Number(userId)
    },
    deleteComment(c) {
      const userId = this.mustLogin()
      if (!userId) return
      this.$confirm('确定删除该评论吗？', '提示', { type: 'warning' }).then(() => {
        this.$axios.delete('/api/front/v2/tutorial/comment/' + c.id, {
          params: { userId }
        }).then(() => {
          this.$message.success('评论已删除')
          this.loadDetail()
        })
      }).catch(() => {})
    },
    openBuyDialog() {
      if (!this.mustLogin()) return
      if (!this.materialKit) return
      this.buyQty = 1
      this.buyDialogVisible = true
    },
    onBuyDialogClosed() {
      this.buySubmitting = false
    },
    /** 扣款后同步本地 tp_user 中的钱包与收益字段 */
    refreshUserWallet(userId) {
      this.$axios.get('/api/front/v2/user/' + userId).then(res => {
        if (!res.data || res.data.code !== '0') return
        const p = res.data.data || {}
        try {
          const raw = localStorage.getItem('tp_user')
          const u = raw ? JSON.parse(raw) : {}
          u.wallet_balance = p.wallet_balance
          u.total_earnings = p.total_earnings
          localStorage.setItem('tp_user', JSON.stringify(u))
          window.dispatchEvent(new Event('tp-user-changed'))
        } catch (e) { /* ignore */ }
      })
    },
    confirmBuy() {
      const userId = this.getUserId()
      if (!userId || !this.materialKit) return
      const q = Math.min(this.maxBuyQty, Math.max(1, parseInt(this.buyQty, 10) || 1))
      this.buySubmitting = true
      this.$axios
        .post('/api/front/v2/order/create', {
          buyerUserId: userId,
          materialKitId: this.materialKit.id,
          quantity: q
        })
        .then(res => {
          if (!res.data || res.data.code !== '0') {
            this.$message.error((res.data && res.data.msg) || '下单失败')
            return
          }
          const data = res.data.data || {}
          const orderNo = data.orderNo || ''
          this.$message.success(orderNo ? '购买成功，订单号：' + orderNo : '购买成功')
          this.buyDialogVisible = false
          this.loadDetail()
          this.refreshUserWallet(userId)
        })
        .finally(() => {
          this.buySubmitting = false
        })
    }
  }
}
</script>

<style scoped>
.tp-page { padding: 10px 20px; }
.step-img { width: 240px; border-radius: 8px; }
.comment { margin-top: 8px; padding: 8px; background: #f7f7f7; border-radius: 6px; }
.comment-head { display: flex; align-items: center; gap: 10px; margin-bottom: 6px; }
.comment-head strong { flex-shrink: 0; }
.comment-time { font-size: 12px; color: #909399; }
.comment-text { margin: 0; }

.dy-actions {
  display: flex;
  align-items: center;
  gap: 28px;
  margin-top: 16px;
  padding: 12px 0;
}
.dy-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  user-select: none;
}
.dy-btn:active .dy-icon {
  transform: scale(0.92);
}
.dy-icon {
  font-size: 36px;
  line-height: 1;
  transition: color 0.2s, -webkit-text-stroke 0.2s, transform 0.2s;
}
.dy-icon.heart:not(.active) {
  color: transparent;
  -webkit-text-stroke: 1.8px #909399;
  text-shadow: none;
}
.dy-icon.heart.active {
  color: #ff2d55;
  -webkit-text-stroke: 0;
  filter: drop-shadow(0 1px 2px rgba(255, 45, 85, 0.35));
}
.dy-icon.star:not(.active) {
  color: transparent;
  -webkit-text-stroke: 1.6px #909399;
}
.dy-icon.star.active {
  color: #f7b500;
  -webkit-text-stroke: 0;
  filter: drop-shadow(0 1px 2px rgba(247, 181, 0, 0.45));
}
.dy-icon.bump {
  animation: dy-bump 0.45s ease;
}
@keyframes dy-bump {
  0% { transform: scale(1); }
  30% { transform: scale(1.28); }
  55% { transform: scale(0.95); }
  100% { transform: scale(1); }
}
.dy-num {
  margin-top: 4px;
  font-size: 13px;
  color: #606266;
  font-weight: 600;
}

.kit-price-line .price-strong {
  color: #f56c6c;
  font-weight: 700;
}

.buy-dialog-body .buy-kit-name {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px;
}
.buy-dialog-body .buy-kit-desc {
  font-size: 13px;
  color: #909399;
  line-height: 1.5;
  margin: 0 0 16px;
}
.buy-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 10px 0;
}
.buy-row.total {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px dashed #ebeef5;
}
.price-strong {
  color: #e6a23c;
  font-weight: 700;
}
.price-total {
  font-size: 20px;
  color: #f56c6c;
  font-weight: 800;
}
</style>
