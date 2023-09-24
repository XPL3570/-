
<template>
  <el-pagination class="page-box" @size-change="handleSizeChange" @current-change="handleCurrentChange" background :current-page="childMsg.page" :page-sizes="[5, 10, 20, 30]" :page-size="childMsg.limit" layout="total, sizes, prev, pager, next, jumper" :total="childMsg.total">
  </el-pagination>
</template>
<script>
export default {
  name: 'PageView',
  props: ['childMsg'],
  data() {
    return {
      pageParam: {
        page: this.childMsg.page,
        limit: this.childMsg.limit,
        total:this.childMsg.total
      }
    }
  },
  created() {},
  methods: {
    handleSizeChange(val) {
      /**
       * 子传父
       * 参数1 父元素方法
       * 参数2 数据
       *
       */
      console.log(`每页 ${val} 条`);

      this.pageParam.limit = val
      this.$emit('callFather', this.pageParam)
    },
    handleCurrentChange(val) {


      console.log(`当前页: ${val}`);
      /**
       * 子传父
       * 参数1 父元素方法
       * 参数2 数据
       */
      this.pageParam.page = val
      this.$emit('callFather', this.pageParam)
    }
  }
}
</script>

<style>
.page-box {
  margin: 10px auto;
}
</style>
