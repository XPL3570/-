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
  // 在这里可以进行路由拦截和控制的逻辑处理
  // 比如判断用户是否登录，是否有权限访问该路由等

  // 如果用户已登录，可以继续访问该路由
  // 否则，重定向到登录页面
  if (to.meta.requiresAuth && !store.state.user) {
    // 用户未登录，重定向到登录页面
    next('/login');
  } else {
    // 用户已登录，继续导航到下一个路由
    next();
  }
});

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
