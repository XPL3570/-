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
    </el-table-column>
    <el-table-column
        prop="message"
        label="反馈信息">
    </el-table-column>
    <el-table-column
        prop="createTime"
        label="反馈时间"
        width="144"
    >
    </el-table-column>
    <el-table-column
        prop="isRead"
        label="是否已读"
        width="80">
    </el-table-column>
  </el-table>
</div>
</template>

<script>
import api from "@/axios";
export default {
  name: "AllFeedback",
  data() {
    return {
      tableData:[],
    }
  },created() {

  },methods:{
    getData(){
      api.get('/',null);
    }
  }
}
</script>

<style scoped>

</style>
