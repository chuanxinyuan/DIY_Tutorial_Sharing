// ============================================================
// Vuex 全局状态管理入口
// 管理用户登录状态
// ============================================================
import Vue from 'vue'
import Vuex from 'vuex'

import user from './modules/user'

Vue.use(Vuex)

export default new Vuex.Store({
  strict: process.env.NODE_ENV !== 'production',
  modules: {
    user
  }
})
