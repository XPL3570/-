<template>
  <div>
    <el-table
        :data="tableData"
        border
        style="width: 100%">
      <el-table-column
          prop="userName"
          label="反馈用户名"
          width="120">
      </el-table-column>
      <el-table-column
          prop="userStatus"
          label="用户状态"
          width="120">
        <template v-slot:default="scope">
          <el-tag v-if="scope.row.userStatus === 0" type="success">正常</el-tag>
          <el-tag v-else-if="scope.row.userStatus === 1" type="danger">禁止投稿</el-tag>
          <el-tag v-else-if="scope.row.userStatus === 2" type="danger">禁止评论</el-tag>
          <el-tag v-else-if="scope.row.userStatus === 3" type="danger">禁止投稿和评论</el-tag>
        </template>
      </el-table-column>
      <el-table-column
          prop="schoolId"
          label="用户学校id"
          width="100"
      >
      </el-table-column>
      <el-table-column
          prop="score"
          label="反馈评分"
          width="80">
        <template v-slot:default="scope">
          <el-tag v-if="scope.row.score < 50" type="danger">{{ scope.row.score }}分</el-tag>
          <el-tag v-else-if="scope.row.score < 80">{{ scope.row.score }}分</el-tag>
          <el-tag v-else type="success">{{ scope.row.score }}分</el-tag>
        </template>
      </el-table-column>
      <el-table-column
          prop="message"
          label="反馈信息">
      </el-table-column>
      <el-table-column
          prop="createTime"
          label="反馈时间"
          width="172"
      >
      </el-table-column>
      <el-table-column
          prop="isRead"
          label="是否已读"
          width="78">
        <template v-slot:default="scope">
          <el-tag v-if="scope.row.isRead === true">已读</el-tag>
          <el-tag v-else type="warning">未读</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <PageView v-bind:childMsg="page" @callFather="callFather"></PageView>
  </div>
</template>

<script>
import PageView from '@/components/PageView.vue'
import api from "@/axios";

export default {
  components: {PageView},
  name: "AllFeedback",
  data() {
    return {
      tableData: [],
      page: {  //分页参数
        page: 1,  //第几页
        limit: 5,
        total: 0
      },
      formInline: { //分页参数，每次都是调用他来获取后台然后
        page: 1,
        limit: 5
      },
    }
  }, created() {
    this.getData(this.param);
  }, methods: {
    getData() {
      api.get('/info/feedback/admin/allObtaining', this.formInline)
          .then(
              res => {
                if (!res) {
                  this.$message.error('反馈数据加载失败！');
                  return;
                }
                if (res.data.code === 200) {
                  this.tableData = res.data.data.data;
                  this.page.page = this.formInline.page;
                  this.page.limit = this.formInline.limit;
                  this.page.total = res.data.data.total;
                  this.loading = false
                }
              }
          ).catch(
              res=>{console.error(res);}
      )
    },
    callFather(param) {
      this.formInline.page = param.page
      this.formInline.limit = param.limit
      this.getData(this.formInline);
    },
  }
}
</script>

<style scoped>

</style>
