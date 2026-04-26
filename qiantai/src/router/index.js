// ============================================================
// 前台路由配置
// ============================================================
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

const routes = [

  {
    path: '/',
    name: 'TutorialHomeV2',
    component: () => import('../views/TutorialHomeV2.vue')
  },
  {
    path: '/login',
    name: 'authLogin',
    component: () => import('../views/auth/login.vue')
  },
  {
    path: '/register',
    name: 'authRegister',
    component: () => import('../views/auth/register.vue')
  },
  {
    path: '/error',
    name: 'Error',
    component: () => import('../components/Error.vue')
  },
  {
    path: '/tutorial/hub',
    name: 'TutorialHub',
    component: () => import('../views/TutorialHub.vue')
  },
  {
    path: '/tutorial/detail/:id',
    name: 'TutorialDetailV2',
    component: () => import('../views/TutorialDetailV2.vue')
  },
  {
    path: '/tutorial/publish',
    name: 'TutorialPublishV2',
    component: () => import('../views/TutorialPublishV2.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/tutorial/community',
    name: 'CommunityV2',
    component: () => import('../views/CommunityV2.vue')
  },
  {
    path: '/tutorial/user-center',
    name: 'UserCenterV2',
    component: () => import('../views/UserCenterV2.vue')
  },
  {
    path: '/tutorial/static/:key',
    name: 'StaticPageV2',
    component: () => import('../views/StaticPageV2.vue')
  },


]

const router = new Router({
  // base: '/dist',
  // mode: 'history',
  routes
})

/* 由于Vue-router在3.1之后把$router.push()方法改为了Promise。所以假如没有回调函数，错误信息就会交给全局的路由错误处理。
vue-router先报了一个Uncaught(in promise)的错误(因为push没加回调) ，然后再点击路由的时候才会触发NavigationDuplicated的错误(路由出现的错误，全局错误处理打印了出来)。*/
// 禁止全局路由错误处理打印
const originalPush = Router.prototype.push;
Router.prototype.push = function push (location, onResolve, onReject) {
  if (onResolve || onReject)
    return originalPush.call(this, location, onResolve, onReject)
  return originalPush.call(this, location).catch(err => err)
}

// 路由守卫：未登录访问需登录页面时跳转登录页
router.beforeEach((to, from, next) => {
  if (to.meta && to.meta.requiresAuth) {
    const user = JSON.parse(localStorage.getItem('tp_user') || 'null')
    if (!user) {
      next({ name: 'authLogin', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router
