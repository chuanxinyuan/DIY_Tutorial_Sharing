// ============================================================
// Vuex 全局状态管理入口
// 管理用户登录状态和购物车（购物车模块已不再使用）
// ============================================================
import Vue from 'vue'
import Vuex from 'vuex'

import user from './modules/user'
import shoppingCart from './modules/shoppingCart'

Vue.use(Vuex)

export default new Vuex.Store({
  strict: true,
  modules: {
    user,
    shoppingCart
  }
})
