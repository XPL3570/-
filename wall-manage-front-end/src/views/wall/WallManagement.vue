<template>
  <div>
    <el-table :data="confessionWallData"
              border
              v-loading="loading"
              element-loading-text="拼命加载中"
              highlight-current-row
              style="width: 100%">
      <el-table-column prop="id" label="表白墙ID" width="62"></el-table-column>
      <el-table-column prop="wallName" label="表白墙名字"></el-table-column>
      <el-table-column prop="schoolId" label="学校id" width="62"></el-table-column>
      <el-table-column prop="schoolName" label="学校名字"></el-table-column>
      <el-table-column prop="avatarURL" label="表白墙头像" width="120">
        <template v-slot:default="scope">
          <div style="display: flex; justify-content: center;">
            <img :src="scope.row['avatarURL']" alt="avatar" width="72" @click="showDetail(scope.row['avatarURL'])"/>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="wallName" label="表白墙名字"></el-table-column>
      <el-table-column prop="description" label="表白墙描述(或备注)"></el-table-column>
      <el-table-column prop="createTime" label="创建时间"></el-table-column>

      <el-table-column prop="status" width="60" label="状态">
        <template v-slot="scope">{{ getStatusText(scope.row.status) }}</template>
      </el-table-column>

      <el-table-column
          label="操作"
          width="100"
      >
        <template v-slot:default="scope" >
          <el-button @click="editWall(scope.row)" plain type="primary" size="small">编辑</el-button>
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

    <el-dialog title="表白墙设置" width="50%" :visible.sync="isDialogWallOpen">

      <el-form :model="wallDataToBeModified" label-width="100px">
        <el-form-item label="表白墙id:"><span>{{ wallDataToBeModified.id }}</span></el-form-item>
        <el-form-item label="表白墙名字:">
          <el-input v-model="wallDataToBeModified.wallName"></el-input>
        </el-form-item>
          <el-form-item label="关联学校id:"><span>{{ wallDataToBeModified.schoolId }}</span></el-form-item>
          <el-form-item label="关联学校名字:"><span>{{ wallDataToBeModified.schoolName }}</span></el-form-item>
          <el-form-item label="创建时间:"><span>{{ wallDataToBeModified.createTime }}</span></el-form-item>
        <el-form-item label="描述(或备注):">
          <el-input v-model="wallDataToBeModified.description"></el-input>
        </el-form-item>
        <el-form-item label="是否被禁用:">
          <el-switch
              v-model="wallDataToBeModified.status"
              active-color="#ff4949"
              inactive-color="#13ce66">
          </el-switch>
        </el-form-item>
      </el-form>


      <div slot="footer" class="dialog-footer">
        <el-button @click="CancelWallMod">取 消</el-button>
        <el-button type="primary" @click="confirmDialogWallInfo">确 定</el-button>
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
      isDialogWallOpen:false, //是否显示编辑表白墙
      wallDataToBeModified:{}, //编辑表白墙缓存数据
      showImageDialog: false,
      selectedImage: '', // 存储选中的图片URL
      confessionWallData: [] // 将实际的数据赋值给这个数组
    }
  },
  created() {
    this.getData(this.formInline);
  },
  methods: {
    editWall(row) {
      console.log(row);
      this.wallDataToBeModified={}; //重置
      this.isDialogWallOpen = true;
      this.wallDataToBeModified = {...row};
      // console.log(this.wallDataToBeModified);
    },
    CancelWallMod(){
      this.wallDataToBeModified={};
      this.isDialogWallOpen = false;
    },
    confirmDialogWallInfo(){ //提交修改
        let zj={
          wallId:this.wallDataToBeModified.id,
          wallName:this.wallDataToBeModified.wallName,
          description:this.wallDataToBeModified.description,
          status:this.wallDataToBeModified.status,
        };
        api.post('/api/confession/admin/modifyWall',zj).then(
            (res)=>{
              if (res.data.code===200){
                this.$message.success('修改表白墙信息成功');
                this.CancelWallMod();
                this.getData(this.formInline);
              }else {
                this.$message.error(res.data.message);
              }
            }
        ).catch((res)=>{console.error(res)})
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
        case false:
          return '正常';
        case true:
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

