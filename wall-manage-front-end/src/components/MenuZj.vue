<template>
  <div>
    <el-menu default-active="2" :collapse="collapsed" collapse-transition router :default-active="$route.path"
             unique-opened class="el-menu-vertical-demo" background-color="#334157" text-color="#fff"
             active-text-color="#ffd04b">
      <div class="logoBox">
        <img class="logoImg" src="../assets/img/logo.png" alt="">
      </div>
      <el-submenu v-for="menu in allMenu" :key="menu.menuId" :index="menu.menuName">
        <template slot="title" >
          <img :src="require(`@/assets/icons/${menu.icon}.png`)" alt="icon" style="height: 24px;margin-right: 10px"/>
          <span>{{ menu.menuName }}</span>
        </template>
        <el-menu-item-group>
          <el-menu-item v-for="menu in menu.menus" :index="'/'+menu.url" :key="menu.menuId">
            <img :src="require(`@/assets/icons/${menu.icon}.png`)" alt="icon" style="height: 24px;margin-right: 10px"/>
            <span>{{ menu.menuName }}</span>
          </el-menu-item>
        </el-menu-item-group>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>

export default {
  name: 'MenuView',
  data() {
    return {
      collapsed: false,
      allMenu:
          [
            {
              menuId: 10,
              icon: '设置',
              menuName: '首页设置',
              hasThird: null,
              url: null,
              menus: [
                {
                  menuId: 11,
                  icon: '首页设置',
                  menuName: '首页设置',
                  hasThird: 'N',
                  url: 'webApi/home/homeSettings',
                  menus: null
                }
              ]
            },
            {
              menuId: 20,
              icon: '学校',
              menuName: '学校管理',
              hasThird: null,
              url: null,
              menus: [
                {
                  menuId: 21,
                  icon: '审核',
                  menuName: '学校入驻审核',
                  hasThird: 'N',
                  url: 'webApi/school/schoolReview',
                  menus: null
                },
                {
                  menuId: 22,
                  icon: '列表',
                  menuName: '学校管理列表',
                  hasThird: 'N',
                  url: 'webApi/school/schoolSettings',
                  menus: null
                },
              ]
            },
            {
              menuId: 30,
              icon: '墙',
              menuName: '表白墙管理',
              hasThird: null,
              url: null,
              menus: [
                {
                  menuId: 31,
                  icon: '全部发布',
                  menuName: '内容发布（所有人可见）',
                  hasThird: 'N',
                  url: 'webApi/wall/confessionRelease',
                  menus: null
                },
                {
                  menuId: 32,
                  icon: '列表模式',
                  menuName: '表白墙列表',
                  hasThird: 'N',
                  url: 'webApi/wall/wallManagement',
                  menus: null
                },
                {
                  menuId: 33,
                  icon: '表白墙发布内容管理',
                  menuName: '发布内容管理',
                  hasThird: 'N',
                  url: 'webApi/wall/WallPublishingContentManagement',
                  menus: null
                },
                {
                  menuId: 34,
                  icon: '投稿举报列表',
                  menuName: '投稿举报列表',
                  hasThird: 'N',
                  url: 'webApi/wall/SubmissionReportList',
                  menus: null
                },
              ]
            },
            {
              menuId: 40,
              icon: '用户',
              menuName: '用户管理',
              hasThird: null,
              url: null,
              menus: [
                {
                  menuId: 41,
                  icon: '用户列表',
                  menuName: '用户管理',
                  hasThird: 'N',
                  url: 'webApi/user/userManagement',
                  menus: null
                },
                {
                  menuId: 42,
                  icon: '管理员列表',
                  menuName: '管理员列表管理',
                  hasThird: 'N',
                  url: 'webApi/user/administrators',
                  menus: null
                },
              ]
            },
            {
              menuId: 50,
              icon: '用户反馈',
              menuName: '用户反馈',
              hasThird: null,
              url: null,
              menus: [
                {
                  menuId: 51,
                  icon: '未读反馈',
                  menuName: '未读反馈',
                  hasThird: 'N',
                  url: 'webApi/feedback/UnreadFeedback',
                  menus: null
                },
                {
                  menuId: 52,
                  icon: '所有反馈',
                  menuName: '所有反馈信息',
                  hasThird: 'N',
                  url: 'webApi/feedback/allFeedback',
                  menus: null
                },
              ]
            },
          ],
    }
  },
  // 创建完毕状态(里面是操作)
  created() {
    // 监听
    this.$root.Bus.$on('toggle', value => {
      this.collapsed = !value
    })
  }
}
</script>
<style>
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 240px;
  min-height: 400px;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  border: none;
  text-align: left;
}

.el-menu-item-group__title {
  padding: 0px;
}

.el-menu-bg {
  background-color: #1f2d3d !important;
}

.el-menu {
  border: none;
}

.logoBox {
  height: 50px;
  line-height: 40px;
  color: #9d9d9d;
  font-size: 20px;
  text-align: center;
  padding: 20px 0px;
}

.logoImg {
  height: 44px;
  border-radius: 20%;
}

</style>
