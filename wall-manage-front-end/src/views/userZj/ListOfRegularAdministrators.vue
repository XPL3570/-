<template>
  <div>
    <el-table :data="admins" style="width: 100%">
      <el-table-column prop="id" label="管理员ID"></el-table-column>
      <el-table-column prop="schoolId" label="学校ID"></el-table-column>
      <el-table-column prop="userId" label="用户ID"></el-table-column>
      <el-table-column prop="confessionWallId" label="表白墙ID"></el-table-column>
      <el-table-column prop="phoneNumber" label="手机号"></el-table-column>
      <el-table-column prop="weChatId" label="微信号"></el-table-column>
      <el-table-column prop="createTime" label="创建时间"></el-table-column>
      <el-table-column prop="permission" label="权限标识"></el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <PageView v-bind:childMsg="page" @callFather="callFather"></PageView>
  </div>
</template>

<script>
import api from "@/axios";
import PageView from "@/components/PageView.vue";

export default {
  name: "ListOfRegularAdministrators",
  components: {PageView},
  data(){
    return{
      page: {  //分页参数 每次都是传递他给组件得到
        page: 1,  //第几页
        limit: 5,
        total: null
      },
      formInline: { //分页参数，每次都是调用他来获取后台然后
        page: 1,
        limit: 5,
      },
      admins: [
        // 假设这里是从后端获取到的Admin对象数组
        // 将每个对象的Password字段清空后的数组
      ],
    }
  },
  created() {
    this.fetchData();
  },
  methods:{
    callFather(param) {
      this.formInline.page = param.page
      this.formInline.limit = param.limit

      console.log(this.formInline)
      this.fetchData(this.formInline)
    },
    fetchData() {
      // 在这里获取你的用户数据，并将其赋值给tableData
      api.get('/admin/list', this.formInline)
          .then(res => {
                console.log(res.data)
                if (res.data.code === 200) {
                  this.admins = res.data.data.data
                  this.page.total = res.data.data.total
                }
              }
          )
    },
  }
}
</script>

<style scoped>

</style>
