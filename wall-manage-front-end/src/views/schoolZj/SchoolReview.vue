<template>
  <div>
  <el-table
      :data="tableData"
      border
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
          <img :src="scope.row.creatorUserAvatarURL" alt="avatar" width="72"/>
        </div>
      </template>
    </el-table-column>
    <el-table-column
        prop="createTime"
        label="创建时间"
        width="150">
    </el-table-column>
    <el-table-column
        prop="avatarURL"
        label="学校头像"
        width="120">
      <template v-slot:default="scope">
        <div style="display: flex; justify-content: center;">
          <img :src="scope.row.avatarURL" alt="avatar" width="72"/>
        </div>
      </template>
    </el-table-column>
    <el-table-column
        prop="description"
        label="描述内容"
        width="300">
    </el-table-column>
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
          fixed="right"
          label="操作"
          width="200">
        <!-- 使用 v-slot:default="scope" 来获取当前行的数据 -->
        <template v-slot:default="scope">
          <!-- 使用 div 和 CSS 来使按钮居中 -->
          <div style="display: flex; justify-content: center;">
            <!-- type="primary" 使按钮显示为蓝色 -->
            <!-- style="margin-right: 10px;" 在按钮右侧添加 10px 的外边距 -->
            <el-button @click="showConfirm(scope.row, 'approve')" type="primary" size="small" style="margin-right: 10px;">
              通过审核
            </el-button>
            <!-- type="danger" 使按钮显示为红色 -->
            <el-button @click="showConfirm(scope.row, 'reject')" type="danger" size="small">不通过审核</el-button>

          </div>
        </template>
      </el-table-column>


    </el-table>
    <el-dialog :visible.sync="dialogVisible" width="30%" @before-close="handleClose">
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

  </div>


</template>


<script>
export default {
  data() {
    return {
      // 控制确认提示对话框的显示和隐藏
      dialogVisible: false,
      // 存储当前行的数据
      currentRow: null,
      // 存储当前的操作（'approve' 或 'reject'）
      currentAction: null,
      tableData: [
        {
          creatorUsername: 'John Doe',
          creatorUserAvatarURL: 'http://127.0.0.1:2204/upload/20230922152608AbvYMQ.jpg',
          schoolId: 1,
          schoolName: 'School 1',
          createTime: '2022-02-01 10:00:00',
          avatarURL: 'https://example.com/avatar2.jpg',
          description: 'This is a description',
          wechatNumber: 'wechat123',
          phoneNumber: '1234567890'
        },
        {
          creatorUsername: 'Jane Doe',
          creatorUserAvatarURL: 'https://example.com/avatar3.jpg',
          schoolId: 2,
          schoolName: 'School 2',
          createTime: '2022-02-02 11:00:00',
          avatarURL: 'https://example.com/avatar4.jpg',
          description: 'This is another description',
          wechatNumber: 'wechat456',
          phoneNumber: '0987654321'
        },
        // 更多的假数据...
      ]
    };
  },
  methods: {
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
    // 在对话框关闭前显示一个确认提示
    handleClose(done) {
      this.$confirm('你确定要关闭吗？')
          .then(() => {
            done(); // 关闭对话框
          })
          .catch(() => {
          });
    },

    // 处理 "通过审核" 的操作
    handleApprove(row) {
      // 在这里添加你的代码来处理 "通过审核" 的操作

      console.log('通过审核', row.schoolId);
    },

    // 处理 "不通过审核" 的操作
    handleReject(row) {
      // 在这里添加你的代码来处理 "不通过审核" 的操作
      console.log('不通过审核', row);
    }
  }
}
</script>
