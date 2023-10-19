<template>
  <div>
    <el-table
        :data="tableData"
        border
        highlight-current-row
        v-loading="loading"
        element-loading-text="拼命加载中"
        style="width: 100%">
      <el-table-column
          fixed
          prop="schoolName"
          label="学校名称"
          width="120">
      </el-table-column>
      <el-table-column
          prop="schoolId"
          label="学校ID"
          width="72">
      </el-table-column>
      <el-table-column
          prop="creatorUsername"
          label="创建者用户名"
          width="120">
      </el-table-column>
      <el-table-column
          prop="creatorUserAvatarURL"
          label="创建者头像"
          width="120">
        <template v-slot:default="scope">
          <div style="display: flex; justify-content: center;">
            <img :src="scope.row['creatorUserAvatarURL']" alt="avatar" width="72"  @click="showDetail(scope.row['creatorUserAvatarURL'])"/>
          </div>
        </template>
      </el-table-column>
      <el-table-column
          prop="createTime"
          label="申请时间"
          width="157">
      </el-table-column>
<!--      <el-table-column-->
<!--          prop="avatarURL"-->
<!--          label="学校头像"-->
<!--          width="115">-->
<!--        <template v-slot:default="scope">-->
<!--          <div style="display: flex; justify-content: center;">-->
<!--            <img :src="scope.row['avatarURL']" alt="avatar"  height="72"  @click="showDetail(scope.row['avatarURL'])"/>-->
<!--          </div>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column-->
<!--          prop="description"-->
<!--          label="描述内容"-->
<!--          width="300">-->
<!--      </el-table-column>-->
      <el-table-column
          prop="wechatNumber"
          label="微信号"
          width="120">
      </el-table-column>
      <el-table-column
          prop="phoneNumber"
          label="手机号"
          width="120">
      </el-table-column>
      <el-table-column

          label="操作"
          width="200">
        <!-- 使用 v-slot:default="scope" 来获取当前行的数据 -->
        <template v-slot:default="scope">
          <!-- 使用 div 和 CSS 来使按钮居中 -->
          <div style="display: flex; justify-content: center;">
            <!-- type="primary" 使按钮显示为蓝色 -->
            <!-- style="margin-right: 10px;" 在按钮右侧添加 10px 的外边距 -->
            <el-button @click="showConfirm(scope.row, 'approve')" type="primary" size="small"
                       style="margin-right: 10px;">
              通过审核
            </el-button>
            <!-- type="danger" 使按钮显示为红色 -->
            <el-button @click="showConfirm(scope.row, 'reject')" type="danger" size="small">不通过审核</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <PageView v-bind:childMsg="page" @callFather="callFather"></PageView>

    <el-dialog :visible.sync="dialogVisible" width="30%">
      <span v-if="currentAction === 'approve'">你确定要通过审核吗？</span>
      <span v-else-if="currentAction === 'reject'">确定不通过审核吗？</span>
      <!-- 对话框的底部按钮 -->
      <span slot="footer" class="dialog-footer">
    <!-- "取消" 按钮，点击时隐藏对话框 -->
    <el-button @click="dialogVisible = false">取 消</el-button>
        <!-- "确定" 按钮，点击时调用 confirmAction 方法 -->
    <el-button type="primary" @click="confirmAction">确 定</el-button>
  </span>
    </el-dialog>

    <el-dialog title="图片详情" :visible.sync="showImageDialog" width="40%">
      <div style="text-align: center;">
        <img :src="selectedImage" alt="avatar" width="90%" height="90%"/>
      </div>
    </el-dialog>
  </div>

</template>

<script>
import api from "@/axios";
import PageView from '@/components/PageView.vue'

export default {
  components: {PageView},

  data() {
    return {
      showImageDialog: false,
      selectedImage: '', // 存储选中的图片URL
      loading:false,
      page: {  //页面分页参数
        page: 1,  //第几页
        limit: 5,
        total:0
      },
      formInline: { //分页参数，每次都是调用他来获取后台然后
        page: 1,
        limit: 5
      },
      // 控制确认提示对话框的显示和隐藏
      dialogVisible: false,
      // 存储当前行的数据
      currentRow: null,
      // 存储当前的操作（'approve' 或 'reject'）
      currentAction: null,
      tableData: []
    };
  },
  // 创建完毕状态(里面是操作)
  created() {
    this.getData(this.formInline)
  },

  methods: {
    showDetail(image) {
      this.selectedImage = image; // 将选中的图片URL赋值给selectedImage变量
      this.showImageDialog = true; // 打开弹窗
    },
    getData(param){
      this.loading = true
      api.get('/api/school/admin/viewNoReview',param)
          .then(res=>{
            if (!res){
              this.$message.error('审核数据加载失败！');
              return;
            }
            // console.log(res.data.data)
            if (res.data.code===200){
              this.loading = true
              this.tableData=res.data.data.data
              this.page.page = this.formInline.page
              this.page.limit = this.formInline.limit
              this.page.total = res.data.data.total
              this.loading = false
              // console.log(this.tableData)
            }else {
              //直接给错误提示
              this.$message.error(res.data.message);
            }
          })
    },
    callFather(param) {
      this.formInline.page = param.page
      this.formInline.limit = param.limit

      this.getData(this.formInline)
    },
    // 显示确认提示对话框，并存储当前行的数据和当前的操作
    showConfirm(row, action) {
      this.currentRow = row;
      this.currentAction = action;
      this.dialogVisible = true;
    },
    // 执行当前的操作，并隐藏确认提示对话框
    confirmAction() {
      this.dialogVisible = false;
      if (this.currentAction === 'approve') {
        this.handleApprove(this.currentRow);
      } else if (this.currentAction === 'reject') {
        this.handleReject(this.currentRow);
      }
    },
    // 处理 "通过审核" 的操作
    handleApprove(row) {
      // 在这里添加你的代码来处理 "通过审核" 的操作
      let zj = {
        schoolId:row.schoolId,
        isVerified:1
      };
      api.post('api/school/admin/examine',zj).then(
          res=>{
            if (res.data.code===200){
              this.getData(this.formInline);
              this.$message.success('通过 ' + row.schoolName + ' 的审核成功');
            }else {
              console.error(res.data)
              this.$message.error('提交' + row.schoolName + ' 的审核申请失败了');
            }
          }
      )

    },

    // 处理 "不通过审核" 的操作
    handleReject(row) {
      let zj = {
        schoolId:row.schoolId,
        isVerified:2
      };
      api.post('api/school/admin/examine',zj).then(
          res=>{
            if (res.data.code===200){
              this.getData(this.formInline);
              this.$message.warning('没有通过 ' + row.schoolName + ' 的审核哦 ! ! !');
            }else {
              console.error(res.data)
              this.$message.error('提交' + row.schoolName + ' 的审核申请失败了');
            }
          }
      )
    }
  }
}
</script>
