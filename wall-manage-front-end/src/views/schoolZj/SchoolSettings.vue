<template>
  <div>
    <el-table :data="schoolList"
              border
              highlight-current-row
    >
      <el-table-column prop="id" label="学校ID" width="80"></el-table-column>
      <el-table-column prop="schoolName" label="学校名称" width="120"></el-table-column>
      <el-table-column prop="avatarURL" label="学校头像" width="120">
        <template v-slot:default="scope">
          <div style="display: flex; justify-content: center;">
            <img :src="scope.row['avatarURL']" alt="avatar" width="72"/>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述内容" width="120"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="133"></el-table-column>
      <el-table-column prop="carouselImages" label="轮播图地址"></el-table-column>
      <el-table-column prop="prompt" label="首页提示语" width="120"></el-table-column>
      <el-table-column prop="isVerified" width="80" label="审核状态">
        <template v-slot="scope">{{ getStatusText(scope.row.isVerified) }}</template>
      </el-table-column>
      <el-table-column
          label="操作"
          width="100">
        <template v-slot:default="scope">
          <el-button @click="showConfirm(scope.row)" plain type="primary" size="small">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <PageView v-bind:childMsg="page" @callFather="callFather"></PageView>
  </div>
</template>

<script>
import api from "@/axios";
import PageView from '@/components/PageView.vue'

export default {
  components: {PageView},
  data() {
    return {
      page: {  //分页参数
        page: 1,  //第几页
        limit: 5,
        total: 0
      },
      formInline: { //分页参数，每次都是调用他来获取后台然后
        page: 1,
        limit: 5
      },
      schoolList: [] // 假设你已经从后端获取到了学校数据并赋值给schoolList
    };
  },
  created() {
    this.getData(this.formInline);
  },
  methods: {
    getData(param) {
      api.get('/api/school/admin/viewSchool', param)
          .then(res => {
                if (res.data.code === 200) {
                  this.schoolList = res.data.data.data;
                  this.page.page = this.formInline.page;
                  this.page.limit = this.formInline.limit;
                  this.page.total = res.data.data.total;
                }
                console.log(res.data);

              }
          ).catch(
          error => {
            console.log(error)
          }
      )
    },
    callFather(param) {
      this.formInline.page = param.page
      this.formInline.limit = param.limit
      this.getData(this.formInline);
    },
    getStatusText(status) {
      switch (status) {
        case 0:
          return '未审核';
        case 1:
          return '通过';
        case 2:
          return '未通过';
        default:
          return '';
      }
    }
  }
};
</script>
