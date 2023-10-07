<template>
  <div>
    <el-table :data="confessionWallData"
              border
              v-loading="loading"
              element-loading-text="拼命加载中"
              highlight-current-row
              style="width: 100%">
      <el-table-column prop="id" label="表白墙ID"></el-table-column>
      <el-table-column prop="wallName" label="表白墙名字"></el-table-column>
      <el-table-column prop="schoolId" label="学校id"></el-table-column>
      <el-table-column prop="schoolName" label="学校名字"></el-table-column>


      <el-table-column prop="avatarURL" label="表白墙头像" width="120">
        <template v-slot:default="scope">
          <div style="display: flex; justify-content: center;">
            <img :src="scope.row['avatarURL']" alt="avatar" width="72" @click="showDetail(scope.row['avatarURL'])"/>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="wallName" label="表白墙名字"></el-table-column>
      <el-table-column prop="description" label="表白墙描述"></el-table-column>
      <el-table-column prop="createTime" label="创建时间"></el-table-column>

      <el-table-column prop="status" width="60" label="状态">
        <template v-slot="scope">{{ getStatusText(scope.row.status) }}</template>
      </el-table-column>

      <el-table-column
          label="操作"
          width="100"

      >
        <template v-slot:default="scope" >
          <el-button @click="aaa(scope.row)" plain type="primary" size="small">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <PageView v-bind:childMsg="page" @callFather="callFather"></PageView>

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
  name: "WallManagement",
  data() {
    return {
      loading: false,
      page: {  //分页参数
        page: 1,  //第几页
        limit: 5,
        total: 0
      },
      formInline: { //分页参数，每次都是调用他来获取后台然后
        page: 1,
        limit: 5
      },
      showImageDialog: false,
      selectedImage: '', // 存储选中的图片URL
      confessionWallData: [] // 将实际的数据赋值给这个数组
    }
  },
  created() {
    this.getData(this.formInline);
  },
  methods: {
    aaa() {

    },
    showDetail(image) {
      this.selectedImage = image; // 将选中的图片URL赋值给selectedImage变量
      this.showImageDialog = true; // 打开弹窗
    },
    callFather(param) {
      this.formInline.page = param.page
      this.formInline.limit = param.limit
      this.getData(this.formInline);
    },
    getData(param) {
      this.loading = true
      api.get('/api/confession/admin/list', param)
          .then(res => {
                if (!res) {
                  this.$message.error('表白墙墙数据加载失败！');
                  return;
                }
                if (res.data.code === 200) {
                  this.confessionWallData = res.data.data.data;
                  this.page.page = this.formInline.page;
                  this.page.limit = this.formInline.limit;
                  this.page.total = res.data.data.total;
                  this.loading = false
                }
                console.log(res.data);
              }
          ).catch(
          error => {
            console.log(error)
          }
      )
    },
    getStatusText(status) {
      switch (status) {
        case 0:
          return '正常';
        case 1:
          return '禁用';
        default:
          return '';
      }
    }
  }
}
</script>

<style scoped>

</style>

