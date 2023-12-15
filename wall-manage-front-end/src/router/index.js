import Vue from 'vue';
import Router from 'vue-router';

// 启用路由
Vue.use(Router);

export default new Router({
    mode: 'history', // 使用history模式，去掉URL中的#
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
            path: '/webApi',
            name: '',
            component: () => import('../views/loginZj.vue'),
            hidden: true,
            meta: {
                requireAuth: false
            }
        },
        {
            path: '/webApi/login',
            name: '登录',
            component: () => import('../views/loginZj.vue'),
            hidden: true,
            meta: {
                requireAuth: false
            }
        },
        {
            path: '/webApi/carouselChart',
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
            component: () => import('../views/index.vue'),
            iconCls: 'el-icon-tickets',
            children: [
                {
                    path: '/webApi/school/schoolReview',
                    name: '学校审核',
                    component: () => import('../views/school/SchoolReview.vue'),
                    meta: {
                        requireAuth: true
                    }
                },
                {
                    path: '/webApi/school/schoolSettings',
                    name: '学校管理',
                    component: () => import('../views/school/SchoolSettings.vue'),
                    meta: {
                        requireAuth: true
                    }
                }, {
                    path: '/webApi/user/userManagement',
                    name: '用户管理',
                    component: () => import('../views/user/UserManagement.vue'),
                    meta: {
                        requireAuth: true
                    }
                }, {
                    path: '/webApi/wall/wallManagement',
                    name: '表白墙管理',
                    component: () => import('../views/wall/WallManagement.vue'),
                    meta: {
                        requireAuth: true
                    }
                }, {
                    path: '/webApi/wall/confessionRelease',
                    name: '表白墙投稿',
                    component: () => import('../views/wall/ConfessionRelease.vue'),
                    meta: {
                        requireAuth: true
                    }
                }, {
                    path: '/webApi/wall/WallPublishingContentManagement',
                    name: '表白墙发布内容管理',
                    component: () => import('../views/wall/WallPublishingContentManagement.vue'),
                    meta: {
                        requireAuth: true
                    }
                },
                {
                    path: '/webApi/user/userManagement',
                    name: '用户管理',
                    component: () => import('../views/user/UserManagement.vue'),
                    meta: {
                        requireAuth: true
                    }
                },
                {
                    path: '/webApi/user/administrators',
                    name: '管理员列表管理',
                    component: () => import('../views/user/ListOfRegularAdministrators.vue'),
                    meta: {
                        requireAuth: true
                    }
                },
                {
                    path: '/webApi/home/homeSettings',
                    name: '管理员列表管理',
                    component: () => import('../views/home/HomeSettings.vue'),
                    meta: {
                        requireAuth: true
                    }
                },
                {
                    path: '/webApi/feedback/allFeedback',
                    name: '所有反馈信息',
                    component: () => import('../views/feedback/AllFeedback.vue'),
                    meta: {
                        requireAuth: true
                    }
                },
                {
                    path: '/webApi/feedback/UnreadFeedback',
                    name: '未读反馈信息',
                    component: () => import('../views/feedback/UnreadFeedback.vue'),
                    meta: {
                        requireAuth: true
                    }
                },
                {
                    path: '/webApi/wall/SubmissionReportList',
                    name: '投稿举报列表',
                    component: () => import('../views/wall/SubmissionReportList.vue'),
                    meta: {
                        requireAuth: true
                    }
                },

            ]
        },

    ]
});
