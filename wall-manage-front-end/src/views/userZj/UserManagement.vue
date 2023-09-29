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
          width="100"
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
          <el-button @click="handleClickState(scope.$index, scope.row)" plain size="mini" type="danger"
                     style="margin-bottom: 4px;margin-left: 18px; width: 100px;">修改状态
          </el-button>
          <el-button @click="handleClickUser(scope.$index, scope.row)" plain size="mini" type="primary"
                     style="width: 100px; margin-left: 18px">修改用户名
          </el-button>
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

    <el-dialog title="修改用户名字" width="30%" :visible.sync="isDialogUserOpen">

      <el-input v-model="editForm.userName" placeholder="请输入用户名"></el-input>

      <div slot="footer" class="dialog-footer">
        <el-button @click="isDialogUserOpen = false">取 消</el-button>
        <el-button type="primary" @click="confirmDialogUsername">确 定</el-button>
      </div>
    </el-dialog>

  </div>

</template>


<script>
import api from "@/axios";
import PageView from "@/components/PageView.vue";

export default {
  components: {PageView},
  data() {
    return {
      isDialogStateOpen: false,
      isDialogUserOpen: false,
      page: {  //分页参数 每次都是传递他给组件得到
        page: 1,  //第几页
        limit: 5,
        total: null
      },
      formInline: { //分页参数，每次都是调用他来获取后台然后
        page: 1,
        limit: 5,
        status: '',
        schoolName: '',
        userName: ''
      },
      tableData: [], // 你需要在这里填入你的用户数据
      editForm: { //暂存的用户信息待提交
        userIndex: -1,
        userName: '',
        userId: -1,
        userAvatar: '',
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
  // 假设你有一个获取用户数据的方法
  created() {
    this.fetchData();
  },
  methods: {
    handleClickState(index, row) {
      this.isDialogStateOpen = true;
      this.editForm.userStatus = row.status;
      this.editForm.userId = row.id;
      this.editForm.userIndex = index;
    },
    handleClickUser(index,row) {
      this.isDialogUserOpen = true;
      this.editForm.userName = row.username;
      this.editForm.userId = row.id;
      this.editForm.userIndex = index;
    },
    cancelDialogState() {
      this.isDialogStateOpen = false;
      this.editForm.userId = -1;
      this.editForm.userStatus = -1;
      this.editForm.userIndex = -1;
      // 取消修改状态，清空选中状态
    },
    confirmDialogState() {
      //提交的时候判断一下用户的信息是否修改然后给出对应的提示
      if (this.tableData[this.editForm.userIndex].status === this.editForm.userStatus) {
        this.$message.warning('您想要修改的改用户状态和之前没有变化哦');
        return;
      }

      // console.log(this.editForm.userId)
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
              //把对应的数据清空和里列表上面的数据替换
              this.editForm.userId = -1;
              this.tableData[this.editForm.userIndex].status = this.editForm.userStatus
              this.editForm.userStatus = -1;
              this.editForm.userIndex = -1;
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
    confirmDialogUsername() {
      let temp=this.editForm.userName;
      if (temp.length<3||temp.length>20){
        this.$message.warning('名字要在3-20个字符哦');
        return;
      }
      if (this.tableData[this.editForm.userIndex].username === this.editForm.userName) {
        this.$message.warning('您想要修改的改用户名字和之前没有变化哦');
        return;
      }
      const postData = {
        userId: this.editForm.userId, // 根据实际情况获取用户ID
        username: this.editForm.userName // 选中的状态
      };
      api.post('/api/user/admin/usernameMod', postData)
          .then(res => {
            if (res.data.code === 200) {
              this.$message.success('修改用户名成功!');
              this.editForm.userId = -1;
              this.tableData[this.editForm.userIndex].username = this.editForm.userName;
              this.editForm.userName = '';
            }else {
              this.$message.error('修改用户名失败！');
            }
          }).catch(error => {
        console.error(error);
      });
      this.isDialogUserOpen = false;
    },
    confirmDialogUser() {
      // 确定修改用户名字
      this.isDialogUserOpen = false;

      // 发送请求到后台，更新用户名字
      // const postData = {
      // userId: row.userId, // 根据实际情况获取用户ID
      // userName: row.newUserName // 修改后的用户名字
      // };
      // api.post('/api/update/user/name', postData)
      //     .then(res => {
      //       // 处理响应结果
      //     })
      //     .catch(error => {
      //       // 处理错误
      //     });
    },
    tableRowClassName({row}) {
      // 这是一个函数，用于根据行的状态返回一个类名
      if (row.status === 0) {
        return null;
      } else {
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
      api.get('/api/user/admin/userList', this.formInline)
          .then(res => {
                // console.log(res.data)
                if (res.data.code === 200) {
                  this.tableData = res.data.data.data
                  this.page.total = res.data.data.total
                }
              }
          )
    },
    search() {
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
