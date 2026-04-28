<template>
  <div class="tp-page">
    <el-card>
      <h2>个人中心</h2>
      <!-- 钱包与累计收益（模拟） -->
      <div v-if="user" class="wallet-banner">
        <div class="wallet-item wallet-balance-row">
          <span class="wallet-label">我的余额</span>
          <div class="wallet-value-row">
            <span class="wallet-value wallet-balance-text">¥ {{ formatMoney(user.wallet_balance) }}</span>
            <el-button type="primary" size="mini" icon="el-icon-coin" @click="openRechargeDialog">充值</el-button>
          </div>
        </div>
        <div class="wallet-item">
          <span class="wallet-label">预计收益</span>
          <span class="wallet-value earnings">¥ {{ formatMoney(user.total_earnings) }}</span>
          <span class="wallet-hint">（卖出材料包所得，买家确认收货后转入余额）</span>
        </div>
      </div>
      <!-- 账号管理：从顶部「设置」或双击头像/昵称进入，不再占用 Tab 标签 -->
      <div v-show="activeTab === 'account'" class="account-manage-section">
        <h3 class="account-section-title">账号管理</h3>
        <el-row :gutter="16">
          <el-col :span="10">
            <el-form :model="loginForm" label-width="80px">
              <el-form-item label="账号">
                <el-input v-model="loginForm.username" :readonly="!!user" />
              </el-form-item>
              <el-form-item label="密码" v-if="!user">
                <el-input type="password" v-model="loginForm.password" />
              </el-form-item>
              <el-form-item>
                <template v-if="!user">
                  <el-button type="primary" @click="login">登录</el-button>
                  <el-button @click="registerVisible = true">注册</el-button>
                </template>
                <template v-else>
                  <el-tag type="success" style="margin-right: 10px;">已登录</el-tag>
                  <el-button type="danger" @click="logout">退出</el-button>
                </template>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="14" v-if="user">
            <el-form :model="profileForm" label-width="100px">
              <el-form-item label="头像">
                <div class="avatar-row">
                  <el-avatar :size="88" :src="avatarDisplaySrc" icon="el-icon-user-solid" />
                  <div class="avatar-actions">
                    <tp-image-picker
                      :value="profileForm.avatarUrl"
                      hide-preview
                      compact
                      placeholder-text="未设置头像"
                      @input="onProfileAvatarInput"
                    />
                    <el-button v-if="profileForm.avatarUrl" size="small" @click="clearAvatarToDefault">恢复默认头像</el-button>
                  </div>
                </div>
              </el-form-item>
              <el-form-item label="昵称"><el-input v-model="profileForm.nickname" /></el-form-item>
              <el-form-item label="手机号"><el-input v-model="profileForm.phone" /></el-form-item>
              <el-form-item><el-button type="success" @click="updateProfile">保存资料</el-button></el-form-item>
            </el-form>
            <el-divider />
            <el-form :model="passwordForm" label-width="100px">
              <el-form-item label="原密码"><el-input type="password" v-model="passwordForm.oldPassword" /></el-form-item>
              <el-form-item label="新密码"><el-input type="password" v-model="passwordForm.newPassword" /></el-form-item>
              <el-form-item><el-button type="warning" @click="updatePassword">修改密码</el-button></el-form-item>
            </el-form>
          </el-col>
        </el-row>
        <div v-if="user" class="account-back-row">
          <el-button type="text" icon="el-icon-arrow-left" @click="goToMineTab">返回我的内容</el-button>
        </div>
      </div>

      <el-tabs v-if="user && activeTab !== 'account'" v-model="activeTab" @tab-click="onCenterTabClick">
        <el-tab-pane label="我的内容" name="mine">
          <el-row :gutter="12">
            <el-col :span="4"><el-tag>收藏 {{ center.myFavorites }}</el-tag></el-col>
            <el-col :span="4"><el-tag type="success">教程 {{ center.myTutorials }}</el-tag></el-col>
            <el-col :span="4"><el-tag type="warning">帖子 {{ center.myPosts }}</el-tag></el-col>
            <el-col :span="4"><el-tag type="info">买入 {{ center.myBuyOrders }}</el-tag></el-col>
            <el-col :span="4"><el-tag type="danger">卖出 {{ center.mySellOrders }}</el-tag></el-col>
          </el-row>
          <el-divider />
          <el-tabs>
            <el-tab-pane label="我的收藏">
              <el-row v-if="(center.favoriteList || []).length" :gutter="16" class="fav-grid">
                <el-col v-for="item in center.favoriteList" :key="'fav-' + item.id" :xs="24" :sm="12" :md="8">
                  <div class="fav-card-wrap">
                    <el-button
                      type="danger"
                      icon="el-icon-close"
                      circle
                      size="mini"
                      class="fav-remove"
                      title="取消收藏"
                      @click.stop="removeFavorite(item)"
                    />
                    <el-card shadow="hover" class="fav-card" @click.native="$router.push('/tutorial/detail/' + item.id)">
                      <img
                        :src="item.cover_image_url ? '/api' + item.cover_image_url : defaultFavCover"
                        class="fav-cover"
                        alt=""
                      />
                      <h4 class="fav-title">{{ item.title }}</h4>
                      <p class="fav-author">作者：{{ item.author_name || '-' }}</p>
                    </el-card>
                  </div>
                </el-col>
              </el-row>
              <p v-else class="fav-empty">暂无收藏，去教程库逛逛吧</p>
            </el-tab-pane>
            <el-tab-pane label="我的教程">
              <el-table :data="myTutorials" size="mini">
                <el-table-column prop="title" label="标题" />
                <el-table-column prop="status" label="状态" width="80" />
                <el-table-column prop="kit_name" label="材料包" />
                <el-table-column label="操作" width="220">
                  <template slot-scope="scope">
                    <el-button size="mini" @click="editTutorial(scope.row)">编辑</el-button>
                    <el-button size="mini" type="danger" @click="deleteTutorial(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="我的帖子">
              <el-table :data="center.postList || []" size="mini">
                <el-table-column prop="title" label="标题" />
                <el-table-column prop="created_at" label="发布时间" width="180" />
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-tab-pane>

        <el-tab-pane label="我的订单" name="orders">
          <div class="order-pane">
            <div class="order-pane-toolbar">
              <el-button type="primary" size="small" @click="loadOrders">刷新订单</el-button>
              <el-radio-group v-model="orderSubTab" size="small" class="order-sub-switch">
                <el-radio-button label="buy">我购买的</el-radio-button>
                <el-radio-button label="sell">我卖出的</el-radio-button>
              </el-radio-group>
            </div>
            <p class="order-tip">订单状态：待发货 → 运输中（卖家发货后约 10 秒自动进入待取货）→ 待取货 → 已取货（买家确认收货）</p>
            <div class="order-table-wrap">
              <el-table
                v-show="orderSubTab === 'buy'"
                :data="orders.buyOrders || []"
                size="mini"
                class="order-table"
                @row-click="openOrderDetail"
              >
                <el-table-column prop="order_no" label="订单号" min-width="160" />
                <el-table-column prop="kit_name_snapshot" label="材料包" min-width="120" />
                <el-table-column prop="unit_price" label="单价" width="80" />
                <el-table-column prop="quantity" label="数量" width="60" />
                <el-table-column prop="total_amount" label="总金额" width="88" />
                <el-table-column label="状态" width="100">
                  <template slot-scope="scope">{{ orderStatusText(scope.row.order_status) }}</template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                  <template slot-scope="scope">
                    <el-button size="mini" type="text" @click.stop="openOrderDetail(scope.row)">详情</el-button>
                    <el-button
                      v-if="Number(scope.row.order_status) === 3"
                      size="mini"
                      type="success"
                      @click.stop="updateOrder(scope.row, 4)"
                    >确认收货</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-table
                v-show="orderSubTab === 'sell'"
                :data="orders.sellOrders || []"
                size="mini"
                class="order-table"
                @row-click="openOrderDetail"
              >
                <el-table-column prop="order_no" label="订单号" min-width="160" />
                <el-table-column prop="kit_name_snapshot" label="材料包" min-width="120" />
                <!-- 与接口 sellOrders 字段一致：unit_price、quantity（与 buyOrders 相同 SQL 列） -->
                <el-table-column prop="unit_price" label="单价" width="80" />
                <el-table-column prop="quantity" label="数量" width="60" />
                <el-table-column prop="total_amount" label="总金额" width="88" />
                <el-table-column label="状态" width="100">
                  <template slot-scope="scope">{{ orderStatusText(scope.row.order_status) }}</template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                  <template slot-scope="scope">
                    <el-button size="mini" type="text" @click.stop="openOrderDetail(scope.row)">详情</el-button>
                    <el-button
                      v-if="Number(scope.row.order_status) === 1"
                      size="mini"
                      type="primary"
                      @click.stop="updateOrder(scope.row, 2)"
                    >已发货</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog title="注册" :visible.sync="registerVisible" width="40%">
      <el-form :model="registerForm" label-width="80px">
        <el-form-item label="账号"><el-input v-model="registerForm.username" /></el-form-item>
        <el-form-item label="密码"><el-input type="password" v-model="registerForm.password" /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="registerForm.nickname" /></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button :disabled="registerLoading" @click="registerVisible = false">取消</el-button>
        <el-button type="primary" :loading="registerLoading" :disabled="registerLoading" @click="register">
          {{ registerLoading ? '注册中...' : '提交' }}
        </el-button>
      </span>
    </el-dialog>

    <el-dialog title="编辑我的教程" :visible.sync="editVisible" width="60%" top="5vh">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="标题"><el-input v-model="editForm.title" /></el-form-item>
        <el-form-item label="封面图"><tp-image-picker v-model="editForm.coverImageUrl" /></el-form-item>
        <el-form-item label="摘要"><el-input type="textarea" v-model="editForm.summary" /></el-form-item>
        <el-form-item label="难度"><el-input-number v-model="editForm.difficultyLevel" :min="1" :max="4" /></el-form-item>
        <el-form-item label="时长(分钟)"><el-input-number v-model="editForm.productionTimeMinutes" :min="1" /></el-form-item>
        <el-divider>图文步骤</el-divider>
        <div v-for="(s, idx) in editForm.steps" :key="'es'+idx" class="edit-step-box">
          <el-form-item :label="'步骤' + (idx + 1) + '标题'"><el-input v-model="s.stepTitle" /></el-form-item>
          <el-form-item :label="'步骤' + (idx + 1) + '内容'"><el-input type="textarea" :rows="2" v-model="s.stepContent" /></el-form-item>
          <el-form-item :label="'步骤' + (idx + 1) + '配图'"><tp-image-picker v-model="s.stepImageUrl" /></el-form-item>
        </div>
        <el-form-item label="材料包名称"><el-input v-model="editForm.kitName" /></el-form-item>
        <el-form-item label="材料包描述">
          <el-input v-model="editForm.kitDesc" type="textarea" :rows="4" placeholder="补充材料包清单、规格、注意事项等，买家下单前可在教程页看到" />
        </el-form-item>
        <el-form-item label="材料包价格"><el-input-number v-model="editForm.kitPrice" :min="0" /></el-form-item>
        <el-form-item label="材料包库存"><el-input-number v-model="editForm.kitStock" :min="0" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="editVisible=false">取消</el-button><el-button type="primary" @click="saveTutorial">保存</el-button></span>
    </el-dialog>

    <el-dialog title="模拟充值" :visible.sync="rechargeVisible" width="400px" append-to-body @closed="rechargeAmount = '';rechargeSubmitting = false">
      <div style="text-align:center">
        <div style="font-size:48px;color:#10b981;margin:16px 0">
          <i class="el-icon-coin" style="font-size:48px" />
        </div>
        <p>当前余额：¥ {{ formatMoney(user.wallet_balance) }}</p>
        <el-input-number v-model="rechargeAmount" :min="0.01" :step="100" :precision="2" style="width:260px;margin-top:12px" placeholder="请输入充值金额" controls-position="right" />
        <p style="margin-top:16px;color:#909399;font-size:12px">预计余额：¥ {{ rechargePreview }}</p>
      </div>
      <span slot="footer">
        <el-button @click="rechargeVisible = false">取消</el-button>
        <el-button type="primary" :loading="rechargeSubmitting" @click="confirmRecharge">确认充值</el-button>
      </span>
    </el-dialog>

    <el-dialog title="订单详情" :visible.sync="orderDetailVisible" width="520px" append-to-body @closed="orderDetail = null">
      <div v-if="orderDetail" class="order-detail-body">
        <img
          v-if="orderCoverSrc"
          :src="orderCoverSrc"
          class="order-detail-cover"
          alt="封面"
        />
        <p><strong>订单号</strong>：{{ orderDetail.order_no }}</p>
        <p><strong>状态</strong>：{{ orderStatusText(orderDetail.order_status) }}</p>
        <p><strong>商品名称</strong>：{{ orderDetail.kit_name_snapshot }}</p>
        <p v-if="orderDetail.kit_desc_snapshot"><strong>商品描述</strong>：{{ orderDetail.kit_desc_snapshot }}</p>
        <p><strong>单价</strong>：¥ {{ formatMoney(orderDetail.unit_price) }}　<strong>数量</strong>：{{ orderDetail.quantity }}</p>
        <p class="order-detail-total"><strong>实付总额</strong>：¥ {{ formatMoney(orderDetail.total_amount) }}</p>
        <p v-if="orderDetail.receiver_name"><strong>收货人</strong>：{{ orderDetail.receiver_name }} {{ orderDetail.receiver_phone }}</p>
        <p v-if="orderDetail.receiver_address"><strong>地址</strong>：{{ orderDetail.receiver_address }}</p>
        <p v-if="orderDetail.tutorial_title"><strong>关联教程</strong>：{{ orderDetail.tutorial_title }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'UserCenterV2',
  data() {
    return {
      activeTab: 'account',
      user: JSON.parse(localStorage.getItem('tp_user') || 'null'),
      loginForm: { username: '', password: '' },
      registerVisible: false,
      registerForm: { username: '', password: '', nickname: '' },
      profileForm: { nickname: '', avatarUrl: '', phone: '' },
      passwordForm: { oldPassword: '', newPassword: '' },
      center: {},
      orders: {},
      myTutorials: [],
      editVisible: false,
      editForm: {},
      editSource: null,
      defaultFavCover: '/api/files/download/31',
      registerLoading: false,
      rechargeVisible: false,
      rechargeAmount: '',
      rechargeSubmitting: false,
      walletEditing: false,
      walletEditValue: '',
      walletSaving: false,
      orderDetailVisible: false,
      orderDetail: null,
      /** 我的订单内：购买 / 卖出切换 */
      orderSubTab: 'buy'
    }
  },
  computed: {
    avatarDisplaySrc() {
      const u = (this.profileForm.avatarUrl || '').trim()
      return u ? '/api' + u : undefined
    },
    orderCoverSrc() {
      if (!this.orderDetail) return ''
      const c = this.orderDetail.cover_image_snapshot || this.orderDetail.tutorial_cover_live
      return c ? '/api' + c : ''
    },
    rechargePreview() {
      const cur = parseFloat(this.user ? this.user.wallet_balance : 0) || 0
      const add = parseFloat(this.rechargeAmount) || 0
      return (cur + add).toFixed(2)
    }
  },
  watch: {
    '$route.query.tab'() {
      this.applyRouteTab()
    }
  },
  created() {
    if (this.user) {
      this.loginForm.username = this.user.username || ''
      this.loadProfileFromServer()
      this.refreshAll()
    }
    this.applyRouteTab()
  },
  methods: {
    /** 根据路由 query.tab 切换：账号管理 / 我的内容 / 我的订单 */
    applyRouteTab() {
      if (!this.user) {
        this.activeTab = 'account'
        return
      }
      const t = this.$route.query.tab
      if (t === 'account') {
        this.activeTab = 'account'
      } else if (t === 'orders') {
        this.activeTab = 'orders'
      } else {
        this.activeTab = 'mine'
      }
    },
    /** 从账号管理返回「我的内容」 */
    goToMineTab() {
      this.activeTab = 'mine'
      this.$router.replace({ path: '/tutorial/user-center', query: { tab: 'mine' } }).catch(() => {})
    },
    /** 主 Tab 切换时同步地址栏 */
    onCenterTabClick(tab) {
      const name = tab.name
      if (name === 'mine' || name === 'orders') {
        this.$router.replace({ path: '/tutorial/user-center', query: { tab: name } }).catch(() => {})
      }
    },
    formatMoney(v) {
      const n = parseFloat(v)
      return (isNaN(n) ? 0 : n).toFixed(2)
    },
    /** 订单状态：1待发货 2运输中 3待取货 4已取货 5已取消 */
    orderStatusText(s) {
      const m = { 1: '待发货', 2: '运输中', 3: '待取货', 4: '已取货', 5: '已取消' }
      const k = Number(s)
      return m[k] != null ? m[k] : String(s)
    },
    mergeUserWalletFromCenter() {
      if (!this.user || !this.center) return
      const wb = this.center.walletBalance
      const te = this.center.totalEarnings
      if (wb === undefined && te === undefined) return
      const u = {
        ...this.user,
        wallet_balance: wb !== undefined ? wb : this.user.wallet_balance,
        total_earnings: te !== undefined ? te : this.user.total_earnings
      }
      this.user = u
      localStorage.setItem('tp_user', JSON.stringify(u))
      window.dispatchEvent(new Event('tp-user-changed'))
    },
    /** 双击余额进入编辑（隐蔽入口，无额外按钮） */
    startWalletEdit() {
      if (!this.user || this.walletEditing || this.walletSaving) return
      this.walletEditing = true
      this.walletEditValue = this.formatMoney(this.user.wallet_balance)
      this.$nextTick(() => {
        const comp = this.$refs.walletInput
        if (comp && comp.focus) comp.focus()
        if (comp && comp.$refs && comp.$refs.input) {
          comp.$refs.input.select()
        }
      })
    },
    cancelWalletEdit() {
      this.walletEditing = false
      this.walletEditValue = ''
    },
    /** 回车只触发失焦，由 blur 统一提交，避免 enter+blur 重复调用 */
    onWalletEnterKey() {
      const comp = this.$refs.walletInput
      if (comp && comp.$refs && comp.$refs.input) {
        comp.$refs.input.blur()
      } else if (comp && comp.blur) {
        comp.blur()
      }
    },
    /** 回车或失焦提交；与当前值相同则静默退出 */
    commitWalletEdit() {
      if (!this.user || !this.walletEditing || this.walletSaving) return
      const raw = String(this.walletEditValue == null ? '' : this.walletEditValue).trim()
      const v = parseFloat(raw)
      if (isNaN(v) || v < 0) {
        this.$message.error('请输入有效的金额（≥0）')
        this.walletEditing = false
        return
      }
      const cur = parseFloat(this.user.wallet_balance)
      if (!isNaN(cur) && Math.abs(v - cur) < 0.005) {
        this.walletEditing = false
        return
      }
      this.walletSaving = true
      this.$axios
        .put('/api/front/v2/user/wallet', { userId: this.user.id, balance: v })
        .then(res => {
          if (!res.data || res.data.code !== '0') {
            this.$message.error((res.data && res.data.msg) || '更新失败')
            return
          }
          this.$message.success('余额已更新')
          return this.syncTpUserFromServer().then(() => this.loadCenter())
        })
        .catch(() => {
          this.$message.error('网络异常，请稍后重试')
        })
        .finally(() => {
          this.walletSaving = false
          this.walletEditing = false
        })
    },
    openOrderDetail(row) {
      if (!row || !this.user) return
      this.$axios.get('/api/front/v2/order/' + row.id, { params: { userId: this.user.id } }).then(res => {
        if (!res.data || res.data.code !== '0') {
          this.$message.error((res.data && res.data.msg) || '加载失败')
          return
        }
        this.orderDetail = res.data.data || {}
        this.orderDetailVisible = true
      })
    },
    unwrapResult(res, fallbackMsg) {
      const payload = (res && res.data) || {}
      if (payload.code !== '0') {
        this.$message.error(payload.msg || fallbackMsg || '操作失败')
        return null
      }
      // 成功但 data 为 null 时仍返回可判真的占位，避免误判为失败（如注册接口无返回体）
      return payload.data != null ? payload.data : {}
    },
    login() {
      this.$axios.post('/api/front/v2/auth/login', this.loginForm).then(res => {
        const data = this.unwrapResult(res, '登录失败')
        if (!data || data.id == null) return
        this.user = data
        localStorage.setItem('tp_user', JSON.stringify(this.user))
        window.dispatchEvent(new Event('tp-user-changed'))
        this.loginForm.username = this.user.username || ''
        this.loginForm.password = ''
        this.profileForm = { nickname: this.user.nickname || '', avatarUrl: this.user.avatar_url || '', phone: this.user.phone || '' }
        this.activeTab = 'mine'
        this.$router.replace({ path: '/tutorial/user-center', query: { tab: 'mine' } }).catch(() => {})
        this.refreshAll()
      })
    },
    register() {
      if (this.registerLoading) return
      const registerPayload = {
        ...this.registerForm,
        nickname: (this.registerForm.nickname || this.registerForm.username || '').trim()
      }
      if (!registerPayload.username || !registerPayload.password) {
        this.$message.warning('请填写账号和密码')
        return
      }
      this.registerLoading = true
      this.$axios
        .post('/api/front/v2/auth/register', registerPayload)
        .then(res => {
          const payload = (res && res.data) || {}
          if (payload.code !== '0') {
            this.$message.error(payload.msg || '注册失败')
            return
          }
          this.registerVisible = false
          this.loginForm.username = registerPayload.username || ''
          this.registerForm = { username: '', password: '', nickname: '' }
          this.$message.success('注册成功，即将跳转登录')
          setTimeout(() => {
            this.activeTab = 'account'
            this.$router.push({ path: '/tutorial/user-center', query: { tab: 'account' } }).catch(() => {})
          }, 1500)
        })
        .catch(err => {
          const d = err.response && err.response.data
          let msg = '网络异常，请稍后重试'
          if (d && typeof d === 'object' && d.msg) msg = d.msg
          this.$message.error(msg)
        })
        .finally(() => {
          this.registerLoading = false
        })
    },
    logout() {
      localStorage.removeItem('tp_user')
      window.dispatchEvent(new Event('tp-user-changed'))
      this.user = null
      this.loginForm.password = ''
      this.center = {}
      this.orders = {}
      this.myTutorials = []
      this.activeTab = 'account'
      this.$router.replace({ path: '/tutorial/user-center', query: {} }).catch(() => {})
    },
    /** 从服务器拉取资料表单（勿在 refreshAll 中覆盖，避免晚到的请求冲掉用户刚上传的头像） */
    loadProfileFromServer() {
      if (!this.user) return
      this.$axios.get('/api/front/v2/user/' + this.user.id).then(res => {
        if (!res.data || res.data.code !== '0') return
        const p = res.data.data || {}
        this.profileForm = { nickname: p.nickname || '', avatarUrl: p.avatar_url || '', phone: p.phone || '' }
      })
    },
    refreshAll() {
      this.loadCenter()
      this.loadOrders()
      this.loadMyTutorials()
    },
    /** 将当前 profileForm 写入 tp_user（头像/昵称/手机） */
    persistProfile(options = {}) {
      const { successMsg = null } = options
      if (!this.user) return Promise.resolve()
      return this.$axios
        .put('/api/front/v2/user/profile', {
          userId: this.user.id,
          nickname: this.profileForm.nickname,
          avatarUrl: this.profileForm.avatarUrl || '',
          phone: this.profileForm.phone || ''
        })
        .then(res => {
          if (!res.data || res.data.code !== '0') {
            this.$message.error((res.data && res.data.msg) || '保存失败')
            return
          }
          if (successMsg) this.$message.success(successMsg)
          return this.syncTpUserFromServer()
        })
        .catch(() => {
          this.$message.error('网络异常，保存失败')
        })
    },
    /** 头像组件回写路径后立即入库，避免仅预览未点「保存资料」 */
    onProfileAvatarInput(val) {
      this.profileForm.avatarUrl = val || ''
      if (!this.user) return
      this.persistProfile({ successMsg: '头像已保存并绑定当前账号' })
    },
    clearAvatarToDefault() {
      this.profileForm.avatarUrl = ''
      if (!this.user) return
      this.persistProfile({ successMsg: '已恢复默认头像' })
    },
    syncTpUserFromServer() {
      if (!this.user) return Promise.resolve()
      return this.$axios.get('/api/front/v2/user/' + this.user.id).then(res => {
        if (!res.data || res.data.code !== '0') return
        const p = res.data.data || {}
        this.profileForm = { nickname: p.nickname || '', avatarUrl: p.avatar_url || '', phone: p.phone || '' }
        const next = {
          ...this.user,
          nickname: p.nickname,
          avatar_url: p.avatar_url,
          phone: p.phone,
          wallet_balance: p.wallet_balance,
          total_earnings: p.total_earnings
        }
        localStorage.setItem('tp_user', JSON.stringify(next))
        this.user = next
        window.dispatchEvent(new Event('tp-user-changed'))
      })
    },
    updateProfile() {
      this.persistProfile({ successMsg: '资料已更新' })
    },
    updatePassword() {
      if (!this.passwordForm.oldPassword || !this.passwordForm.newPassword) {
        this.$message.warning('请填写原密码和新密码')
        return
      }
      this.$axios.put('/api/front/v2/user/password', { userId: this.user.id, ...this.passwordForm }).then(res => {
        if (!res.data || res.data.code !== '0') {
          this.$message.error((res.data && res.data.msg) || '密码修改失败，请检查原密码是否正确')
          return
        }
        this.passwordForm = { oldPassword: '', newPassword: '' }
        this.$message.success('密码修改成功')
      }).catch(() => {
        this.$message.error('网络异常，请稍后重试')
      })
    },
    loadCenter() {
      this.$axios.get('/api/front/v2/user/center', { params: { userId: this.user.id } }).then(res => {
        this.center = res.data.data || {}
        this.mergeUserWalletFromCenter()
      })
    },
    /** 取消收藏：确认后调接口并刷新列表 */
    removeFavorite(item) {
      if (!item || !item.id) return
      this.$confirm('确定取消收藏该教程？', '提示', { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' })
        .then(() => {
          return this.$axios.post('/api/front/v2/tutorial/' + item.id + '/unfavorite', { userId: this.user.id })
        })
        .then(res => {
          if (!res || !res.data || res.data.code !== '0') {
            this.$message.error((res && res.data && res.data.msg) || '操作失败')
            return
          }
          this.$message.success('已取消收藏')
          this.loadCenter()
        })
        .catch(() => {})
    },
    loadOrders() {
      this.$axios.get('/api/front/v2/order/my', { params: { userId: this.user.id } }).then(res => {
        this.orders = res.data.data || {}
      })
    },
    loadMyTutorials() {
      this.$axios.get('/api/front/v2/user/tutorials', { params: { userId: this.user.id } }).then(res => {
        this.myTutorials = res.data.data || []
      })
    },
    editTutorial(row) {
      this.editSource = row
      this.$axios.get('/api/front/v2/tutorial/' + row.id).then(res => {
        const data = res.data.data || {}
        const t = data.tutorial || {}
        const kit = data.materialKit || {}
        const stepsRaw = data.steps || []
        const steps = stepsRaw.map(s => ({
          stepTitle: s.step_title || '',
          stepContent: s.step_content || '',
          stepImageUrl: s.step_image_url || ''
        }))
        if (steps.length === 0) {
          steps.push({ stepTitle: '', stepContent: '', stepImageUrl: '' })
        }
        this.editForm = {
          id: row.id,
          title: t.title,
          summary: t.summary,
          difficultyLevel: t.difficulty_level,
          productionTimeMinutes: t.production_time_minutes,
          materialSummary: t.material_summary,
          coverImageUrl: t.cover_image_url || '',
          steps,
          materialIds: (data.materials || []).map(m => m.id),
          tagIds: (data.tags || []).map(tg => tg.id),
          bindMaterialKit: !!data.materialKit,
          kitName: kit.kit_name,
          kitPrice: kit.price,
          kitStock: kit.stock,
          kitDesc: kit.kit_desc
        }
        this.editVisible = true
      })
    },
    saveTutorial() {
      this.$axios.put('/api/front/v2/tutorial/' + this.editForm.id, { userId: this.user.id, ...this.editForm }).then(() => {
        this.editVisible = false
        this.$message.success('教程已更新')
        this.refreshAll()
      })
    },
    deleteTutorial(row) {
      this.$confirm('确定删除该教程吗？', '提示', { type: 'warning' }).then(() => {
        this.$axios.delete('/api/front/v2/tutorial/' + row.id, { params: { userId: this.user.id } }).then(() => {
          this.$message.success('已删除')
          this.refreshAll()
        })
      })
    },
    openRechargeDialog() {
      if (!this.user) return
      this.rechargeAmount = ''
      this.rechargeVisible = true
    },
    confirmRecharge() {
      if (!this.user || this.rechargeSubmitting) return
      const add = parseFloat(this.rechargeAmount)
      if (isNaN(add) || add <= 0) {
        this.$message.warning('请输入有效的充值金额')
        return
      }
      const cur = parseFloat(this.user.wallet_balance) || 0
      const newBalance = cur + add
      this.rechargeSubmitting = true
      this.$axios
        .put('/api/front/v2/user/wallet', { userId: this.user.id, balance: newBalance })
        .then(res => {
          if (!res.data || res.data.code !== '0') {
            this.$message.error((res.data && res.data.msg) || '充值失败')
            return
          }
          this.$message.success('充值成功！余额已更新')
          this.rechargeVisible = false
          return this.syncTpUserFromServer().then(() => this.loadCenter())
        })
        .catch(() => {
          this.$message.error('网络异常，请稍后重试')
        })
        .finally(() => {
          this.rechargeSubmitting = false
        })
    },
    updateOrder(order, status) {
      this.$axios.post('/api/front/v2/order/' + order.id + '/status', { userId: this.user.id, status }).then(res => {
        if (!res.data || res.data.code !== '0') {
          this.$message.error((res.data && res.data.msg) || '操作失败')
          return
        }
        this.$message.success(status === 2 ? '已发货，系统将在约 10 秒后自动进入「待取货」' : '已确认收货')
        this.loadOrders()
      })
    }
  }
}
</script>

<style scoped>
.tp-page { padding: 10px 20px; }
.account-manage-section {
  margin-bottom: 8px;
}
.account-section-title {
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.account-back-row {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #ebeef5;
}
.avatar-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}
.avatar-actions {
  flex: 1;
  min-width: 0;
}
.avatar-tip {
  margin: 8px 0 0;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}
.edit-step-box {
  border: 1px dashed #e4e7ed;
  border-radius: 8px;
  padding: 8px;
  margin-bottom: 10px;
}
.fav-grid {
  margin-top: 8px;
}
.fav-card-wrap {
  position: relative;
  margin-bottom: 16px;
}
.fav-remove {
  position: absolute;
  right: 8px;
  top: 8px;
  z-index: 2;
  padding: 6px !important;
}
.fav-card {
  cursor: pointer;
  border-radius: 8px;
}
.fav-cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
  display: block;
}
.fav-title {
  margin: 10px 0 6px;
  font-size: 15px;
  line-height: 1.35;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.fav-author {
  margin: 0;
  font-size: 12px;
  color: #909399;
}
.fav-empty {
  text-align: center;
  color: #909399;
  padding: 32px 0;
}
.wallet-banner {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 20px 32px;
  padding: 14px 16px;
  margin-bottom: 16px;
  background: linear-gradient(100deg, #f0f9ff 0%, #fefce8 100%);
  border-radius: 10px;
  border: 1px solid #e4e7ed;
}
.wallet-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.wallet-label {
  font-size: 12px;
  color: #909399;
}
.wallet-value {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}
.wallet-value.earnings {
  color: #e6a23c;
}
.wallet-hint {
  font-size: 11px;
  color: #c0c4cc;
}
.wallet-balance-row {
  min-height: 44px;
  justify-content: center;
}
.wallet-balance-text {
  cursor: default;
  user-select: none;
}
.wallet-balance-input {
  width: 168px;
  max-width: 100%;
}
.wallet-balance-input >>> .el-input__inner {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}
/* 我的订单：工具栏 + 购买/卖出切换 + 单栏表格，间距统一 */
.order-pane {
  padding: 0 2px;
}
.order-pane-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 12px;
}
.order-sub-switch >>> .el-radio-button__inner {
  min-width: 104px;
  padding-left: 18px;
  padding-right: 18px;
}
.order-table-wrap {
  margin-top: 0;
}
.order-table {
  width: 100%;
}
.order-tip {
  font-size: 12px;
  color: #909399;
  margin: 0 0 12px;
  line-height: 1.5;
}
.order-detail-body p {
  margin: 8px 0;
  font-size: 14px;
  line-height: 1.5;
}
.order-detail-cover {
  width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 12px;
}
.order-detail-total {
  font-size: 16px;
  color: #f56c6c;
}
</style>