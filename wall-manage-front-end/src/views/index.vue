<template>

  <el-container class="index-con">
    <el-aside :class="showclass">
      <menuZj></menuZj>
    </el-aside>
    <el-container class="main-con">
      <el-header class="index-header">
        <headZj></headZj>
      </el-header>
      <el-main clss="index-main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>
<script>
// 导入组件
import headZj from '../components/HeadZj.vue'
import menuZj from '../components/MenuZj.vue'
export default {
  name: 'indexPage',
  data() {
    return {
      showclass: 'asideshow',
      showtype: false
    }
  },
  // 注册组件
  components: {
    headZj,
    menuZj
  },
  methods: {},
  created() {
    // 监听
    this.$root.Bus.$on('toggle', value => {
      if (value) {
        this.showclass = 'asideshow'
      } else {
        setTimeout(() => {
          this.showclass = 'aside'
        }, 300)
      }
    })
  },
  beforeUpdate() {},
  // 挂载前状态(里面是操作)
  beforeMount() {
    // 弹出登录成功
    this.$message({
      message: '登录成功',
      type: 'success'
    })
  }
}
</script>
<style >
.index-con {
  height: 100%;
  width: 100%;
  box-sizing: border-box;
}

.aside {
  width: 64px !important;
  height: 100%;
  background-color: #334157;
  margin: 0px;
}
.asideshow {
  width: 240px !important;
  height: 100%;
  background-color: #334157;
  margin: 0px;
}
.index-header,
.index-main {
  padding: 0px;
  border-left: 2px solid #333;
}
</style>
