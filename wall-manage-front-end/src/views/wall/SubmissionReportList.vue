<template>
  <div>
    <el-table :data="reportRecords" border style="width: 100%">
      <el-table-column prop="userId" label="举报用户ID" width="80"></el-table-column>
      <el-table-column prop="reportId" label="举报投稿ID" width="80"></el-table-column>
      <el-table-column label="投稿发布者ID" width="114">
        <template v-slot:default="{ row }">
          <span v-if="row.userIdForSubmissionPublisher === -44">该投稿已删除</span>
          <span v-else>{{ row.userIdForSubmissionPublisher }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="举报时间" width="157"></el-table-column>
      <el-table-column prop="message" label="举报建议" width="200"></el-table-column>
      <el-table-column prop="title" label="投稿标题" width="157"></el-table-column>
      <el-table-column prop="textContent" label="投稿内容文字" width="200"></el-table-column>
      <el-table-column prop="imageURL" label="发布内容图片">
        <template v-slot:default="scope">
          <div style="display: flex; justify-content: center;">
            <template v-if="scope.row.imageURL">
              <img v-for="(url, index) in  scope.row.imageURL.split(';')"
                   :src="url" alt="avatar" class="thumbnail" :key="index"
                   @click="showDetail(url)"/>
            </template>
            <template v-else>
              <div style="  height: 50px;">未上传图片</div>
            </template>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="157">
        <template v-slot:default="scope">
          <el-button plain size="mini" type="primary" @click="handleClickState(scope.$index, scope.row)"
                     style="margin-left: 18px; width: 100px;margin-bottom: 8px">修改用户状态</el-button>

          <template>
            <el-popconfirm
                confirm-button-text='删除'
                cancel-button-text='不用了'
                icon="el-icon-info"
                icon-color="red"
                title="确定删除该投稿吗？"
                @confirm="handleDeletePostStatus(scope.row)"
            >
              <el-button plain size="mini" type="danger" slot="reference" style="width: 100px; margin-left: 18px" >删除投稿</el-button>
            </el-popconfirm>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <PageView v-bind:childMsg="page" @callFather="callFather"></PageView>
    <el-dialog title="修改用户状态" width="24%" :visible.sync="isDialogStateOpen">
      <span>请选择用户状态： </span>
      <el-select v-model="editForm.userStatus" placeholder="请选择">
        <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value">
        </el-option>
      </el-select>

      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelDialogState">取 消</el-button>
        <el-button type="primary" @click="confirmDialogState">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="图片详情" :visible.sync="showImageDialog" width="44%">
      <div style="text-align: center;">
        <img :src="selectedImage" alt="avatar" width="100%" height="100%"/>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import PageView from '@/components/PageView.vue'
import api from "@/axios";

export default {
  components: {PageView},
  name: "SubmissionReportList",
  data() {
    return {
      isDialogStateOpen: false,
      showImageDialog: false,
      selectedImage: '',
      reportRecords: [],
      page: {  //分页参数
        page: 1,  //第几页
        limit: 5,
        total: 0
      },
      formInline: { //分页参数，每次都是调用他来获取后台然后
        page: 1,
        limit: 5
      },
      editForm: { //暂存的用户信息待提交
        userId: -1,
        userStatus: -1
      },
      options: [{
        value: 0,
        label: '正常'
      }, {
        value: 1,
        label: '禁止投稿'
      }, {
        value: 2,
        label: '禁止评论'
      }, {
        value: 3,
        label: '禁止投稿和评论'
      }],
    }
  },
  created() {
    this.getData();
  }, methods: {
    getData() {
      api.get('/api/report/admin/getReportList', this.formInline)
          .then(
              res => {
                if (!res) {
                  this.$message.error('举报信息加载失败！');
                  return;
                }
                if (res.data.code === 200) {
                  this.reportRecords = res.data.data.data;
                  this.page.page = this.formInline.page;
                  this.page.limit = this.formInline.limit;
                  this.page.total = res.data.data.total;
                  this.loading = false
                }
              }
          ).catch(
          res => {
            console.error(res);
          }
      )
    },
    callFather(param) {
      this.formInline.page = param.page
      this.formInline.limit = param.limit
      this.getData();
    },
    showDetail(image) {
      this.selectedImage = image; // 将选中的图片URL赋值给selectedImage变量
      this.showImageDialog = true; // 打开弹窗
    },
    handleClickState(index, row) {
      this.isDialogStateOpen = true;
      this.editForm.userId = row.userIdForSubmissionPublisher;

    },
    handleDeletePostStatus(row){
      // console.log(id)
      api.post('/api/confessionPost/admin/delete',{postId:row.reportId,wallId: row.wallId})  //todo 要修改的，删除逻辑
          .then(
              res=>{
                if (res.data.code===200){
                  this.getData();
                  this.$message.success('删除投稿成功！')
                  this.visible=false;
                  // 关闭弹出框
                }else {
                  console.error(res);
                  this.$message.error('删除投稿失败！')
                }
              }
          )
    },
    cancelDialogState() {
      this.isDialogStateOpen = false;
      this.editForm.userId = -1;
      this.editForm.userStatus = -1;
      // 取消修改状态，清空选中状态
    },
    confirmDialogState() {
      // 发送请求到后台，更新用户状态
      const postData = {
        userId: this.editForm.userId, // 根据实际情况获取用户ID
        status: this.editForm.userStatus // 选中的状态
      };
      api.post('/api/user/admin/userStatusMod', postData)
          .then(res => {
            // 处理响应结果
            if (res.data.code === 200) {
              this.$message.success('修改用户状态成功!');
            } else {
              this.$message.error('修改用户状态失败！')
            }
          })
          .catch(error => {
            console.error(error)
            // 处理错误
          });
      // 确定修改状态
      this.isDialogStateOpen = false;
    },
  }
}
</script>

<style scoped>
.thumbnail {
  width: 50px;
  height: 50px;
  object-fit: cover;
}
</style>
