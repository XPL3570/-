<template>
  <div>
    <el-table :data="schoolList"
              border
              highlight-current-row
    >
      <el-table-column prop="id" label="ID" width="45"></el-table-column>
      <el-table-column prop="schoolName" label="学校名称" width="120"></el-table-column>
      <el-table-column prop="avatarURL" label="学校头像" width="120">
        <template v-slot:default="scope">
          <div style="display: flex; justify-content: center;">
            <img :src="scope.row['avatarURL']" alt="avatar" width="72" @click="showDetail(scope.row['avatarURL'])"/>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述内容" width="120"></el-table-column>


      <el-table-column prop="createTime" label="创建时间" width="132"></el-table-column>
      <el-table-column prop="creatorName" label="创建者名字" width="90"></el-table-column>
      <el-table-column prop="creatorWeChat" label="创建者微信号" width="133"></el-table-column>
      <el-table-column prop="creatorPhone" label="创建者手机号" width="108"></el-table-column>
      <el-table-column prop="carouselImages" label="首页轮播图" width="244">
        <template v-slot:default="scope">
          <div style="display: flex;">
            <img v-for="(url, index) in scope.row['carouselImages']"
                 :src="url" alt="avatar" width="100" :key="index"
                 @click="showDetail(url)"/>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="prompt" label="首页提示语" width="120"></el-table-column>
      <el-table-column prop="isVerified" width="50" label="审核状态">
        <template v-slot="scope">{{ getStatusText(scope.row.isVerified) }}</template>
      </el-table-column>
      <el-table-column
          label="操作"
          width="100">
        <template v-slot:default="scope">
          <el-button @click="showSchoolSetting(scope.row)" plain type="primary" size="small">编辑</el-button>
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

    <el-dialog title="学校设置" width="60%" :visible.sync="isDialogSchoolOpen">

      <el-form :model="schoolDataToBeModified" label-width="100px">
        <el-form-item label="学校id">
          <span>{{schoolDataToBeModified.id}}</span>
        </el-form-item>
        <el-form-item label="学校名称">
          <el-input v-model="schoolDataToBeModified.schoolName"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="schoolDataToBeModified.description"></el-input>
        </el-form-item>
        <el-form-item label="提示语">
          <el-input v-model="schoolDataToBeModified.prompt"></el-input>
        </el-form-item>

        <el-form-item label="学校轮播图">
          <el-upload
              :action="uploadUrl"
              list-type="picture-card"
              :file-list="schoolDataToBeModified.carouselImages"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
              :on-preview="showDetail"
              :limit="3"
              :on-remove="handleRemove"
              :headers="{
          authentication:token
        }">
            <i class="el-icon-plus"></i>
          </el-upload>
        </el-form-item>


      </el-form>


      <div slot="footer" class="dialog-footer">
        <el-button @click="isDialogSchoolOpen = false">取 消</el-button>
        <el-button type="primary" @click="confirmDialogSchoolInfo">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import api from "@/axios";
import PageView from '@/components/PageView.vue'
import index from "vuex";
import axios from "@/axios";

export default {
  computed: {
    index() {
      return index
    }
  },
  components: {PageView},
  data() {
    return {
      isDialogSchoolOpen: false,
      showImageDialog: false,
      selectedImage: '', // 存储选中的图片URL
      schoolDataToBeModified:{},//每次查看的学校数据
      page: {  //分页参数
        page: 1,  //第几页
        limit: 5,
        total: 0
      },
      formInline: { //分页参数，每次都是调用他来获取后台然后
        page: 1,
        limit: 5
      },
      schoolList: [], // 假设你已经从后端获取到了学校数据并赋值给schoolList
      uploadUrl: 'http://127.0.0.1:2204/admin/upload', //这是上传图片的地址  超过4张小程序显示有点问题，后面优化
      token: localStorage.getItem('token'), //从本地变量中获取token
    };
  },
  created() {
    this.getData(this.formInline);
  },
  methods: {
    handleAvatarSuccess(res) {
      if (res.code === 200) {
        this.schoolDataToBeModified.carouselImages.push( res.data)
      } else {
        console.log(res)
        this.$message.error('图片上传失败！' + res)
      }
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif' || file.type === 'image/bmp';
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isJPG) {
        this.$message.error('上传头像图片只能是 jpg或png或gif和bmp 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    },
    handleRemove(url) {
      let zj = {
        deleteUrl: url
      }
      axios.post('/deleteImage', zj).then(
          res => {
            if (res.data.code === 200) {
              this.$message.success('移除图片成功！');
            } else {
              this.$message.error('移除图片失败！')
            }
          }
      ).catch(res=>{
        console.log(res)
      })
    },
    showSchoolSetting(row) {  //打开编辑学校
      this.isDialogSchoolOpen=true;
      this.schoolDataToBeModified=row;
      console.log(row);
      console.log(this.schoolDataToBeModified.carouselImages);
    },
    confirmDialogSchoolInfo() { //提交学校修改

    },
    showDetail(image) {
      this.selectedImage = image; // 将选中的图片URL赋值给selectedImage变量
      this.showImageDialog = true; // 打开弹窗
    },
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
