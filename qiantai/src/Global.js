// ============================================================
// 全局变量及工具方法
// ============================================================
exports.install = function (Vue) {
  // 开发环境多数请求走 vue.config.js 的 /api 代理到 8888；本变量若未使用可忽略
  Vue.prototype.$target = 'http://localhost:8888/'
  // 封装提示成功的弹出框
  Vue.prototype.notifySucceed = function (msg) {
    this.$notify({
      title: "成功",
      message: msg,
      type: "success",
      offset: 100
    });
  };
  // 封装提示失败的弹出框
  Vue.prototype.notifyError = function (msg) {
    this.$notify.error({
      title: "错误",
      message: msg,
      offset: 100
    });
  };
}
