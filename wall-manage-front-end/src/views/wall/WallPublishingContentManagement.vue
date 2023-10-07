<template>
  <div>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="formInline" class="user-search">
      <el-form-item label="搜索：">
      </el-form-item>
      <el-form-item label="表白墙：">
        <el-input size="small" v-model="formInline.wallName" placeholder="请输入表白墙名字"></el-input>
      </el-form-item>
      <el-form-item label="内容查询：">
        <el-input size="small" v-model="formInline.fuzzyQueryContent" placeholder="请输入投稿内容"></el-input>
      </el-form-item>
      <el-form-item label="用户名：">
        <el-input size="small" v-model="formInline.userName" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="发布状态：">
        <el-select size="small" v-model="formInline.postStatus" placeholder="请选择发布状态">
          <el-option label="全部" value=""></el-option>
          <el-option label="待审核" value="0"></el-option>
          <el-option label="已经发布" value="1"></el-option>
          <el-option label="审核未通过" value="2"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="匿名状态：">
      <el-select size="small" v-model="formInline.isAnonymous" placeholder="请选择要查询是否匿名">
        <el-option label="全部" value=""></el-option>
        <el-option label="正常" value=false></el-option>
        <el-option label="匿名" value=true></el-option>
      </el-select>
      </el-form-item>
      <el-form-item label="是否超级管理员发布：">
        <el-select size="small" v-model="formInline.isAdminPost" placeholder="请选择要查询是否超级管理员发布">
          <el-option label="全部" value=""></el-option>
          <el-option label="是" value=true></el-option>
          <el-option label="否" value=false></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="是否倒序排列：">
        <el-select size="small" v-model="formInline.reverseOrder" placeholder="请选择是否倒序查询">
          <el-option label="是" value=true></el-option>
          <el-option label="否" value=false></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="getData">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table
        :data="tableData"
        style="width: 100%"
        border
        v-loading="loading"
        element-loading-text="拼命加载中"
    >
      <el-table-column prop="id" label="用户ID" width="70"></el-table-column>
      <el-table-column prop="wallName" label="墙名称"></el-table-column>
      <el-table-column prop="userName" label="用户名" width="100"></el-table-column>
      <el-table-column prop="userAvatar" label="用户头像" width="100">
        <template v-slot:default="scope">
          <div style="display: flex; justify-content: center;">
            <img :src="scope.row['userAvatar']" alt="avatar" width="80" height="80" @click="showDetail(scope.row['userAvatar'])"/>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="投稿标题" width="157"></el-table-column>
      <el-table-column prop="textContent" label="投稿内容" width="244">
        <template v-slot:default="scope">
          <el-input type="textarea"  :value="scope.row.textContent" :autosize="{ minRows: 2, maxRows: 3 }"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="imageURL" label="投稿图片" width="240">
        <template v-slot:default="scope">
          <div style="display: flex; justify-content: center;">
            <template v-if="scope.row['imageURL'].length > 0 && scope.row['imageURL'][0]">
              <img v-for="(url, index) in scope.row['imageURL']"
                   :src="url" alt="avatar" class="thumbnail" :key="index"
                   @click="showDetail(url)"/>
            </template>
            <template v-else>
              <div>未上传图片</div>
            </template>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" ></el-table-column>
      <el-table-column prop="publishTime" label="发布时间" ></el-table-column>
      <el-table-column prop="postStatus" label="帖子状态" :formatter="formatPostStatus" width="100"></el-table-column>
      <el-table-column prop="isAnonymous" label="是否匿名" :formatter="formatBoolean" width="80"></el-table-column>
      <el-table-column prop="isAdminPost" label="是否超级管理员发布" :formatter="formatBoolean" width="80"></el-table-column>
      <el-table-column
          fixed="right"
          label="操作"
          width="157">
        <template >
          <el-button plain size="mini" type="danger"
                     style="margin-bottom: 4px;margin-left: 18px; width: 100px;">修改状态
          </el-button>
          <el-button plain size="mini" type="primary"
                     style="width: 100px; margin-left: 18px">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <PageView v-bind:childMsg="page" @callFather="callFather"></PageView>

    <el-dialog title="图片详情" :visible.sync="showImageDialog" width="44%">
      <div style="text-align: center;">
        <img :src="selectedImage" alt="avatar" width="90%" height="90%"/>
      </div>
    </el-dialog>

  </div>
</template>
<script>
import api from "@/axios";
import PageView from "@/components/PageView.vue";
export default {
  components:{PageView},
  data() {
    return {
      showImageDialog: false,
      selectedImage: '', // 存储选中的图片URL
      loading:false,
      page: {  //分页参数 每次都是传递他给组件得到
        page: 1,  //第几页
        limit: 5,
        total: null
      },
      formInline: { //分页参数，每次都是调用他来获取后台然后
        page: 1,
        limit: 5,
        fuzzyQueryContent:'',
        wallName:'',
        userName:'',
        isAdminPost:null,
        isAnonymous:null,
        postStatus:null,
        reverseOrder:null,
      },
      tableData: [], // 表格数据
    };
  },
  created() {
    this.getData();
  },
  methods: {
    getData(){
      console.log(this.formInline)
      this.loading=true;
      api.get('/api/confessionPost/admin/userList',this.formInline)
          .then(
              res=>{
                if (res.data.code===200){
                  this.tableData=res.data.data.data.map(item=>{
                    item.imageURL=item.imageURL.split(';');
                    return item;
                  });
                  console.log(this.tableData)
                  this.page.total=res.data.data.total;
                  this.loading=false;
                }else {
                  this.$message.error('加载数据投稿失败!');
                }
              }
          )
    },
    showDetail(image) {
      this.selectedImage = image; // 将选中的图片URL赋值给selectedImage变量
      this.showImageDialog = true; // 打开弹窗
    },
    callFather(param) {
      this.formInline.page = param.page
      this.formInline.limit = param.limit
      // console.log(this.formInline)
      this.getData(this.formInline)
    },
    formatPostStatus(row, column) {
      const value = row[column.property];
      if (value === 0) {
        return '未审核';
      } else if (value === 1) {
        return '已经发布';
      } else if (value === 2) {
        return '未通过审核';
      }
      return '';
    },
    formatBoolean(row, column) {
      const value = row[column.property];
      return value ? '是' : '否';
    },
  }
}
</script>

<style>
.thumbnail {
  width: 70px;
  height: 80px;
  object-fit: cover;
}
</style>
