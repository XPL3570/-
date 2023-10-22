<template>
<div>
  <div style="display: flex; justify-content: space-between;">
    <div>所有反馈数未读数：{{total}}</div>
    <el-row style="margin: 10px;">
      <el-button plain type="primary" round size="small" @click=getData>刷新</el-button>
    </el-row>
  </div>
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
        <el-tag v-if="scope.row.score < 50" type="danger">{{scope.row.score}}分</el-tag>
        <el-tag v-else-if="scope.row.score < 80" >{{scope.row.score}}分</el-tag>
        <el-tag v-else  type="success">{{scope.row.score}}分</el-tag>
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
        <el-tag v-if="scope.row.isRead === true" >已读</el-tag>
        <el-tag v-else  type="warning">未读</el-tag>
      </template>
    </el-table-column>
    <el-table-column
        label="操作"
        width="157">
      <template v-slot:default="scope">
        <el-button @click="handleClickState(scope.$index, scope.row.id)" plain size="mini" type="primary"
                   style="margin-bottom: 4px;margin-left: 18px; width: 100px;">修改已读
        </el-button>
      </template>
    </el-table-column>
  </el-table>
</div>
</template>

<script>
import api from "@/axios";
export default {
  name: "UnreadFeedback",
  data() {
    return {
      total:0,
      tableData:[],
    }
  },created() {
    this.getData();
  },methods:{
    getData(){
      api.get('/info/feedback/admin/noReadObtaining',null).then(
          res=>{
            if (res.data.code===200){
              this.tableData=res.data.data;
              // this.$message.success('加载反馈成功！');// todo 优化，可以把table组件加上正在加载中
            }
          }
      ).catch(res=>{
        console.error(res.data);
      });
      api.get('/info/feedback/admin/noReadObCount',null).then(
          res=>{if(res.data.code===200){this.total=res.data.data}}
      ).catch(res=>{console.error(res)})
    },handleClickState(index,id){
      let zj={
        requestId:id
      }
      api.post('/info/feedback/admin/modifyRead',zj)
          .then(
              res=>{
                if (res.data.code===200){
                  this.tableData.splice(index, 1);
                  this.total=this.total-1;
                  this.$message.info('修改已读状态成功');
                }else {
                  this.$message.error('修改已读状态失败');
                }
              }
          ).catch(res=>{console.error(res)}
      )
    },
  }

//
}
</script>

<style scoped>

</style>
