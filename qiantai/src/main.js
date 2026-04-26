// ============================================================
// 前台入口文件
// 注册 ElementUI、axios 拦截器、全局过滤器、公用组件
// ============================================================
import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
Vue.use(ElementUI);

// 全局函数及变量
import Global from './Global';
Vue.use(Global);

import Axios from 'axios';
Vue.prototype.$axios = Axios;
// 全局请求拦截器
Axios.interceptors.request.use(
  config => {
    return config;
  },
  error => {
    // 跳转error页面
    router.push({ path: "/error" });
    return Promise.reject(error);
  }
);
// 全局响应拦截器（仅处理后端统一 JSON：含 code 字段；非 JSON/二进制响应直接放行，避免访问 res.data.code 抛错）
Axios.interceptors.response.use(
  res => {
    const data = res && res.data
    if (!data || typeof data !== 'object' || typeof data.code === 'undefined') {
      return res
    }
    if (data.code === '401') {
      Vue.prototype.notifyError(data.msg || '请先登录')
      router.push({ path: '/tutorial/user-center' })
    }
    if (data.code === '500') {
      router.push({ path: '/error' })
    }
    return res
  },
  error => {
    // 网络错误、4xx/5xx：保留 reject 供页面 catch；仅非取消类错误时可选跳转（避免掩盖具体提示）
    if (error.response && error.response.status >= 500) {
      router.push({ path: '/error' })
    }
    return Promise.reject(error)
  }
)

// 相对时间过滤器,把时间戳转换成时间
// 格式: 2026-02-25 21:43:23
Vue.filter('dateFormat', (dataStr) => {
  var time = new Date(dataStr);
/**
 * 为小于10的数字前面添加0，用于格式化时间显示
 * @param {number|string} str - 需要格式化的数字或数字字符串
 * @returns {string} 格式化后的字符串，如果输入小于10则在前面添加0
 */
  function timeAdd0 (str) {
    if (str < 10) {  // 判断输入是否小于10
      str = '0' + str;  // 如果小于10，在前面添加0
    }
    return str;  // 返回格式化后的字符串
  }
  var y = time.getFullYear();
  var m = time.getMonth() + 1;
  var d = time.getDate();
  var h = time.getHours();
  var mm = time.getMinutes();
  var s = time.getSeconds();
  return y + '-' + timeAdd0(m) + '-' + timeAdd0(d) + ' ' + timeAdd0(h) + ':' + timeAdd0(mm) + ':' + timeAdd0(s);
});

// 注册全局组件




import TpImagePicker from './components/TpImagePicker.vue'
Vue.component('TpImagePicker', TpImagePicker)
import TutorialCaseCard from './components/TutorialCaseCard.vue'
Vue.component('TutorialCaseCard', TutorialCaseCard)

Vue.config.productionTip = false;

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
