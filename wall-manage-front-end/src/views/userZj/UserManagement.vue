<template>
  <div>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="formInline" class="user-search">
      <el-form-item label="搜索：">
        <el-select size="small" v-model="formInline.status" placeholder="选择要查询用户状态">
          <el-option label="全部" value=""></el-option>
          <el-option label="正常" value="0"></el-option>
          <el-option label="异常" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="">
        <el-input size="small" v-model="formInline.schoolName" placeholder="请输入学校"></el-input>
      </el-form-item>
      <el-form-item label="">
        <el-input size="small" v-model="formInline.userName" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="search">搜索</el-button>
      </el-form-item>
    </el-form>
    <el-table
        :data="tableData"
        style="width: 100%"
        border
        :row-class-name="tableRowClassName">
      <el-table-column
          prop="id"
          label="用户ID"
          width="100">
      </el-table-column>
      <el-table-column
          prop="username"
          label="用户名"
          width="180">
      </el-table-column>

      <el-table-column
          prop="avatarURL"
          label="头像"
          width="120">
        <template v-slot:default="scope">
          <div style="display: flex; justify-content: center;">
            <img :src="scope.row['avatarURL']" alt="avatar" width="72"/>
          </div>
        </template>
      </el-table-column>

      <el-table-column
          width="120"
          prop="schoolName"
          label="学校名称">
      </el-table-column>
      <el-table-column
          prop="openId"
          width="150"
          label="微信唯一ID">
      </el-table-column>
      <el-table-column
          width="157"
          prop="createTime"
          label="创建时间">
      </el-table-column>
      <el-table-column
          prop="wXAccount"
          width="120"
          label="微信账号">
      </el-table-column>
      <el-table-column
          prop="gender"
          width="50"
          label="性别">
      </el-table-column>

      <el-table-column
          prop="status"
          label="用户状态">
        <template v-slot:default="scope">
          <span v-if="scope.row.status === 0">正常</span>
          <span v-else-if="scope.row.status === 1">禁止投稿</span>
          <span v-else-if="scope.row.status === 2">禁止评论</span>
          <span v-else-if="scope.row.status === 3">禁止投稿和评论</span>
        </template>
      </el-table-column>
      <el-table-column
          fixed="right"
          label="操作"
          width="157">
        <template v-slot:default="scope">
          <el-button @click="handleClick(scope.row)" type="text">修改状态</el-button>
          <el-button type="text" >修改用户名</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <PageView v-bind:childMsg="page" @callFather="callFather"></PageView>

  </div>

</template>


<script>
import api from "@/axios";
import PageView from "@/components/PageView.vue";
export default {
  components: {PageView},
  data() {
    return {
      page: {  //分页参数 每次都是传递他给组件得到
        page: 1,  //第几页
        limit: 5,
        total: null
      },
      formInline: { //分页参数，每次都是调用他来获取后台然后
        page: 1,
        limit: 5,
        status:'',
        schoolName:'',
        userName:''
      },
      tableData: [], // 你需要在这里填入你的用户数据
    }
  },
  // 假设你有一个获取用户数据的方法
  created() {
    this.fetchData();
  },
  methods: {
    tableRowClassName({row}) {
      // 这是一个函数，用于根据行的状态返回一个类名
      if (row.status===0){
        return null;
      }else {
        return 'warning-row';
      }
    },
    callFather(param) {
      this.formInline.page = param.page
      this.formInline.limit = param.limit

      console.log(this.formInline)
      this.fetchData(this.formInline)
    },
    fetchData() {
      // 在这里获取你的用户数据，并将其赋值给tableData
      api.get('/api/user/admin/userList',this.formInline)
          .then(res=>{
                  console.log(res.data)
                  if (res.data.code===200){
                    this.tableData=res.data.data.data
                    this.page.total=res.data.data.total
                  }
              }
          )
    },
    search(){
      this.fetchData();
    }
  }
}
</script>

<style scoped>
.el-table .warning-row {
  background: oldlace;
}

.el-table .success-row {
  background: #f0f9eb;
}
</style>
