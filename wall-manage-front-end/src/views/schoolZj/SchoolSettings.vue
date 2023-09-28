<template>
  <div>
    <el-table :data="schoolList" stripe>
      <el-table-column prop="id" label="学校ID" width="80"></el-table-column>
      <el-table-column prop="schoolName" label="学校名称" width="120"></el-table-column>
      <el-table-column prop="avatarURL" label="头像地址" width="120"></el-table-column>
      <el-table-column prop="description" label="描述内容" width="120"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="120"></el-table-column>
      <el-table-column prop="carouselImages" label="轮播图地址" width="120"></el-table-column>
      <el-table-column prop="prompt" label="提示语" width="120"></el-table-column>
      <el-table-column prop="isVerified" width="120" label="审核状态">
        <template v-slot="scope">{{ getStatusText(scope.row.isVerified) }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      schoolList: [] // 假设你已经从后端获取到了学校数据并赋值给schoolList
    };
  },
  methods: {
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
