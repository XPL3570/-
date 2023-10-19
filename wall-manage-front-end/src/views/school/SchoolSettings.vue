<template>
  <div>
    <el-table :data="schoolList"
              v-loading="loading"
              element-loading-text="拼命加载中"
              border
              highlight-current-row
              style="width:100%"
    >
      <el-table-column prop="id" fixed label="ID" width="45"></el-table-column>
      <el-table-column prop="schoolName" fixed label="学校名称" width="120"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="132"></el-table-column>
      <el-table-column prop="creatorName" label="创建者名字" width="90"></el-table-column>
      <el-table-column prop="numberLuckyDraws" label="恋爱墙可抽奖次数" width="90"></el-table-column>
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

      <el-table-column prop="prompt" label="首页提示语" width="144"></el-table-column>
      <el-table-column prop="isVerified" width="50" label="审核状态">
        <template v-slot="scope">{{ getStatusText(scope.row.isVerified) }}</template>
      </el-table-column>
      <el-table-column
          label="操作"
          width="80">
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

    <el-dialog title="学校设置" width="50%" :visible.sync="isDialogSchoolOpen">

      <el-form :model="schoolDataToBeModified" label-width="100px">
        <el-form-item label="学校id">
          <span>{{ schoolDataToBeModified.id }}</span>
        </el-form-item>
        <el-form-item label="学校名称">
          <el-input v-model="schoolDataToBeModified.schoolName"></el-input>
        </el-form-item>
        <el-form-item label="提示语">
          <el-input v-model="schoolDataToBeModified.prompt"></el-input>
        </el-form-item>

        <el-form-item label="可抽奖次数">
          <el-input v-model="schoolDataToBeModified.numberLuckyDraws" type="number"></el-input>
        </el-form-item>

        <el-form-item label="学校轮播图">
          <el-upload
              :action="host"
              list-type="picture-card"
              :file-list="fileList"
              :on-success="handleAvatarSuccess"
              :before-upload="ossPolicy"
              :on-preview="showDetailFile"
              :limit="3"
              :data="objectData"
              :on-remove="handleRemove">
            <i class="el-icon-plus"></i>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-select v-model="schoolDataToBeModified.isVerified" placeholder="请选择">
            <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
                :disabled="item.disabled">
            </el-option>
          </el-select>
        </el-form-item>

      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="CancelSchoolMod">取 消</el-button>
        <el-button type="primary" @click="confirmDialogSchoolInfo">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

import api from "@/axios";
import PageView from '@/components/PageView.vue'

export default {

  components: {PageView},
  data() {
    return {
      loading: false,
      fileList: [],  //临时数组，对应是要展示的编辑弹出框轮播图对象，格式不一样，所以要单独存
      isDialogSchoolOpen: false,
      showImageDialog: false,
      selectedImage: '', // 存储选中的图片URL
      schoolDataToBeModified: {},//每次查看的学校数据
      // schoolAvatarCache: [], //学校头像地址，因为要固定的格式，所有用数组存对象
      options: [{
        value: 0,
        label: '未审核'
      }, {
        value: 1,
        label: '通过审核'
      }, {
        value: 2,
        label: '未通过审核',
        disabled: true
      }],
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
      // uploadUrl: 'http://127.0.0.1:2204/admin/upload', //这是上传图片的地址  超过4张小程序显示有点问题，后面优化
      // token: localStorage.getItem('token'), //从本地变量中获取token
      host:'', //上传地址
      objectData:{  //临时票据信息
        //访问keyId
        OSSAccessKeyId:'',
        //临时秘钥签名
        Signature:'',
        //过期时间
        expire:'',
        //文件存放的相对路劲
        key:'',
        //转码之后的权限标识
        policy:'',
        'x-oss-security-token': '', // security-token 字段
        name:'file'
      },
    };
  },
  created() {
    this.getData(this.formInline);
  },
  methods: {
    CancelSchoolMod() {
      this.isDialogSchoolOpen = false;
      this.fileList = [];
      this.schoolAvatarCache = [];
      this.schoolDataToBeModified = {};
    },
    handleAvatarSuccess() {
      this.fileList.push({
        url: this.host + '/' + this.objectData.key,
      });
    },
    handleAvatarSuccessAvatar() {
      // if (res.code === 200) {
      //   this.schoolAvatarCache.push({url: res.data})
      // } else {
      //   console.log(res)
      //   this.$message.error('图片上传失败！' + res)
      // }
      this.schoolAvatarCache.push({
        url: this.host + '/' + this.objectData.key,
      });
    },
    ossPolicy(file){ //上传前进行服务器签名
      const zj=this.beforeAvatarUpload(file);
      if (zj===false){
        return false;
      }
      return new Promise((resolve,reject)=>{
        let _self=this;
        //请求后端
        api.get('/admin/getStsToken',null)
            .then(response=>{
              // console.log(response.data);
              _self.objectData.OSSAccessKeyId=response.data.data.accessid;
              _self.objectData.Signature=response.data.data.signature;
              // _self.objectData.securityToken=response.data.data.securityToken;
              _self.host=response.data.data.host;
              _self.objectData.key=response.data.data.dir;
              _self.objectData.policy=response.data.data.policy;
              _self.objectData.expire=response.data.data.expire;
              _self.objectData['x-oss-security-token']=response.data.data.securityToken;
              // console.log(_self.objectData);
              //   setTimeout(function() {
              //   _self.fileListSub.push({
              //     url: _self.host + '/' + _self.objectData.key,
              //   });
              // }, 200);
              //   console.log(_self.fileListSub)
              resolve(true);//继续
            }).catch(function (error){
          console.error(error);
          reject(false);
        })
      });
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
    handleRemove(file, fileList) { //这里删除就不调用后端的方法了，因为可能删除之后不会提交，增加容错，误操作
      const index = fileList.findIndex(item => item.url === file.url);
      if (index !== -1) {
        fileList.splice(index, 1);
      }
      this.fileList = fileList;
    },
    handleRemoveAvatar(file, schoolAvatarCache) { //这里删除就不调用后端的方法了，因为可能删除之后不会提交，增加容错，误操作
      const index = schoolAvatarCache.findIndex(item => item.url === file.url);
      if (index !== -1) {
        schoolAvatarCache.splice(index, 1);
      }
      this.schoolAvatarCache = schoolAvatarCache;
    },

    showSchoolSetting(row) {  //打开编辑学校
      this.fileList = [];//先重置图片数据
      this.isDialogSchoolOpen = true;
      this.schoolDataToBeModified = row;
      // console.log(this.schoolDataToBeModified.avatarURL);
      // console.log(row);
      if (this.schoolDataToBeModified.carouselImages) {
        this.fileList = this.schoolDataToBeModified.carouselImages.map(url => {
          return {url};
        });
      }
      this.schoolAvatarCache = [];
      // if (this.schoolDataToBeModified.avatarURL) {
      //   this.schoolAvatarCache.push({
      //     url: this.schoolDataToBeModified.avatarURL
      //   })
      // }
      console.log(this.schoolDataToBeModified);
    },
    confirmDialogSchoolInfo() { //提交学校修改
      console.log(this.schoolDataToBeModified);
      let zj = {
        id: this.schoolDataToBeModified.id,
        schoolName: this.schoolDataToBeModified.schoolName,
        numberLuckyDraws:this.schoolDataToBeModified.numberLuckyDraws,
        carouselImages: this.fileList.map(item => item.url).join(';'),
        prompt: this.schoolDataToBeModified.prompt,
        isVerified: this.schoolDataToBeModified.isVerified
      };
      api.post('/api/school/admin/modifySchool', zj)
          .then(res => {
            if (res.data.code === 200) {
              this.$message.success('修改学校成功！')
              this.CancelSchoolMod(); //清空数据
              this.getData(this.formInline);
            } else {
              this.$message.error('修改学校失败！')
            }
          })
      console.log(zj)

    },
    showDetail(image) {
      this.selectedImage = image; // 将选中的图片URL赋值给selectedImage变量
      this.showImageDialog = true; // 打开弹窗
    },
    showDetailFile(file) {
      this.selectedImage = file.url; // 将选中的图片URL赋值给selectedImage变量
      this.showImageDialog = true; // 打开弹窗
    },
    getData(param) {
      this.loading = true
      api.get('/api/school/admin/viewSchool', param)
          .then(res => {
                if (!res) {
                  this.$message.error('学校数据加载失败！');
                  return;
                }
                if (res.data.code === 200) {
                  this.schoolList = res.data.data.data;
                  this.page.page = this.formInline.page;
                  this.page.limit = this.formInline.limit;
                  this.page.total = res.data.data.total;
                  this.loading = false
                }
                // console.log(res.data);
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

<style>
.custom-row {
  height: 30px; /* 设置行高为50px */
}
</style>


