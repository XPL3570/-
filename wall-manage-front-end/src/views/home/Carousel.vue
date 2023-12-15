<template>
  <div>
    <span class="demonstration">默认 Hover 指示器触发</span>
    <el-carousel height="150px">
      <el-carousel-item v-for="item in 4" :key="item">
        <h3 class="small">{{ item }}</h3>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script>
import api from "@/axios";
export default {
  name: 'Carousel',
  data() {
    return {

    }
  },
  created() {
    this.ObtainCarouselMap();
  },
  methods: {
    ObtainCarouselMap() {
      api.get('your-api-url')
          .then(response => {
            this.addresses = response.data;
          })
          .catch(error => {
            console.error(error);
          });
    }
  }
}

</script>

<style scoped>

</style>
