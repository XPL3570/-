import Vue from 'vue'
import App from './App.vue'

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

import router from './router';
// 引入状态管理
import store from './vuex/store';


Vue.use(ElementUI);
Vue.config.productionTip = false

// 路由拦截器
router.beforeEach((to, from, next) => {
  if (to.matched.length != 0) {
    if (to.meta.requireAuth) { // 判断该路由是否需要登录权限
      if (localStorage.getItem("userInfo")) { // 通过vuex state获取当前的user是否存在
        next();
      } else {
        next({
          path: '/login',
          query: { redirect: to.fullPath } // 将跳转的路由path作为参数，登录成功后跳转到该路由
        })
      }
    } else {
      if (localStorage.getItem("userInfo")) { // 判断是否登录
        if (to.path != "/" && to.path != "/login") { //判断是否要跳到登录界面
          next();
        } else {
          /**
           * 防刷新，如果登录，修改路由跳转到登录页面，修改路由为登录后的首页
           */
          next({
            path: '/school/schoolSettings'
          })
        }
      } else {
        next();
      }
    }
  } else {
    next({
      path: '/login',
      query: { redirect: to.fullPath } // 将跳转的路由path作为参数，登录成功后跳转到该路由
    })
  }
})

new Vue({
  router,
  store,//使用store vuex状态管理
  render: h => h(App),
}).$mount('#app')


new Vue({
  router,
  store, //使用store vuex状态管理
  render: h => h(App),
  components: { App },
  template: '<App/>',
  data: {
    // 空的实例放到根组件下，所有的子组件都能调用
    Bus: new Vue()
  }

}).$mount('#app')
