/**
* 左边菜单
*/
<template>
  <div>
    <el-menu default-active="2" :collapse="collapsed" collapse-transition router :default-active="$route.path"
             unique-opened class="el-menu-vertical-demo" background-color="#334157" text-color="#fff"
             active-text-color="#ffd04b">
      <div class="logoBox">
        <img class="logoImg" src="../assets/img/logo.png" alt="">
      </div>
      <el-submenu v-for="menu in allMenu" :key="menu.menuId" :index="menu.menuName">
        <template slot="title">
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
              menuId: 1,
              icon: '设置',
              menuName: '设置',
              hasThird: null,
              url: null,
              menus: [
                {
                  menuId: 2,
                  icon: '首页设置',
                  menuName: '首页设置',
                  hasThird: 'N',
                  url: 'school/schoolSettings',
                  menus: null
                }
              ]
            },
            {
              menuId: 71,
              icon: '学校',
              menuName: '学校管理',
              hasThird: null,
              url: null,
              menus: [
                {
                  menuId: 72,
                  icon: '审核',
                  menuName: '学校入驻审核',
                  hasThird: 'N',
                  url: 'school/schoolReview',
                  menus: null
                },
                {
                  menuId: 174,
                  icon: '墙',
                  menuName: '表白墙',
                  hasThird: 'N',
                  url: 'wall/confessionRelease',
                  menus: null
                },
                {
                  menuId: 72,
                  icon: '用户',
                  menuName: '用户管理',
                  hasThird: 'N',
                  url: 'user/userManagement',
                  menus: null
                },
              ]
            },
          ],
    }
  },
  // 创建完毕状态(里面是操作)
  created() {
    // 获取图形验证码
    let res = {
      success: true,
      data: [
        {
          menuId: 1,
          icon: '设置',
          menuName: '设置',
          hasThird: null,
          url: null,
          menus: [
            {
              menuId: 2,
              icon: '首页设置',
              menuName: '首页设置',
              hasThird: 'N',
              url: 'school/schoolSettings',
              menus: null
            }
          ]
        },
        {
          menuId: 71,
          icon: '学校',
          menuName: '学校管理',
          hasThird: null,
          url: null,
          menus: [
            {
              menuId: 72,
              icon: '审核',
              menuName: '学校入驻审核',
              hasThird: 'N',
              url: 'school/schoolReview',
              menus: null
            },
            {
              menuId: 174,
              icon: '墙',
              menuName: '表白墙',
              hasThird: 'N',
              url: 'wall/confessionRelease',
              menus: null
            },
            {
              menuId: 72,
              icon: '用户',
              menuName: '用户管理',
              hasThird: 'N',
              url: 'user/userManagement',
              menus: null
            },
          ]
        },
      ],
      msg: 'success'
    }
    this.allMenu = res.data

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
