<template>
<div>
  <el-table :data="reportRecords" border style="width: 100%">
    <el-table-column prop="userId" label="用户ID"></el-table-column>
    <el-table-column prop="reportId" label="举报投稿ID"></el-table-column>
    <el-table-column prop="userIdForSubmissionPublisher" label="投稿发布者ID"></el-table-column>
    <el-table-column prop="createTime" label="创建时间"></el-table-column>
    <el-table-column prop="message" label="举报建议"></el-table-column>
    <el-table-column prop="title" label="发布标题"></el-table-column>
    <el-table-column prop="textContent" label="发布内容文字"></el-table-column>
    <el-table-column prop="imageURL" label="发布内容图片"></el-table-column>
  </el-table>
</div>
</template>

<script>
export default {
  name: "SubmissionReportList",
  data(){
    return{
      reportRecords:[],
    }
  },
  created() {
  },methods:{
    getData(){

    },
  }
}
</script>

<style scoped>

</style>
