import Vue from 'vue';
import Router from 'vue-router';


// 启用路由
Vue.use(Router);

export default new Router({
        // mode: 'history', // 使用history模式，去掉URL中的#
    routes: [
        {
            path: '/',
            name: '',
            component: () => import('../views/loginZj.vue'),
            hidden: true,
            meta: {
                requireAuth: false
            }
         },
        {
            path: '/login',
            name: '登录',
            component: () => import('../views/loginZj.vue'),
            hidden: true,
            meta: {
                requireAuth: false
            }
        },
        {
            path: '/index',
            name: '首页',
            component: ()=>import('../views/index.vue'),
            iconCls: 'el-icon-tickets',
            children:[
                {
                    path: '/school/schoolReview',
                    name: '学校审核',
                    component: () => import('../views/schoolZj/SchoolReview.vue'),
                    meta: {
                        requireAuth: true
                    }
                },
                {
                    path: '/school/schoolSettings',
                    name: '学校管理',
                    component: () => import('../views/schoolZj/SchoolSettings.vue'),
                    meta: {
                        requireAuth: true
                    }
                },    {
                    path: '/user/userManagement',
                    name: '用户管理',
                    component: () => import('../views/userZj/UserManagement.vue'),
                    meta: {
                        requireAuth: true
                    }
                },    {
                    path: '/wall/wallManagement',
                    name: '表白墙管理',
                    component: () => import('../views/wallZj/WallManagement.vue'),
                    meta: {
                        requireAuth: true
                    }
                },    {
                    path: '/wall/confessionRelease',
                    name: '表白墙投稿',
                    component: () => import('../views/wallZj/ConfessionRelease.vue'),
                    meta: {
                        requireAuth: true
                    }
                },    {
                    path: '/wall/wallPublishingManagement',
                    name: '表白墙发布内容管理',
                    component: () => import('../views/wallZj/WallPublishingManagement.vue'),
                    meta: {
                        requireAuth: true
                    }
                },
                {
                    path: '/user/userManagement',
                    name: '用户管理',
                    component: () => import('../views/userZj/UserManagement.vue'),
                    meta: {
                        requireAuth: true
                    }
                }
            ]
        },

    ]
});
