<template>
<div>


  <div>设置全局提示语</div>

  <el-input v-model="promptTextSub" placeholder="当前全局提示语为空" disabled></el-input>

  <el-switch
      v-model="promptSwitch"
      active-text="开启全局提示语"
      inactive-text="关闭全部提示语"
      disabled>
  </el-switch>

  <div>设置全局轮播图</div>
  <template>
    <div>
      <div v-for="imageUrl in fileList" :key="imageUrl">
        <img :src="imageUrl" alt="Image" />
      </div>
    </div>
  </template>
  <el-switch
      disabled
      v-model="carouselSwitch"
      active-text="开启全局轮播图"
      inactive-text="关闭全部轮播图">
  </el-switch>
  <div style="margin: 10px"></div>


  <el-dialog :visible.sync="dialogCarousel" width="40%">
    <div>首页轮播图</div>
    <el-upload
        :action="uploadUrl"
        list-type="picture-card"
        :file-list="fileList"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload"
        :on-preview="showDetailFile"
        :limit="3"
        :on-remove="handleRemove"
        :headers="{
          authentication:token
        }">
      <i class="el-icon-plus"></i>
    </el-upload>
    <el-switch
        v-model="carouselSwitchSub"
        active-text="开启全局轮播图"
        inactive-text="关闭全部轮播图">
    </el-switch>
    <!-- 对话框的底部按钮 -->
    <span slot="footer" class="dialog-footer">
    <el-button @click="dialogCarousel = false">取 消</el-button>
    <el-button type="primary" @click="a">确 定</el-button>
  </span>
  </el-dialog>

  <el-dialog :visible.sync="dialogVisiblePrompt" width="30%">
    <el-input v-model="promptTextSub" :rows="6" placeholder="请输入全局提示语"></el-input>
    <el-switch
        v-model="promptSwitchSub"
        active-text="开启全局提示语"
        inactive-text="关闭全部提示语">
    </el-switch>
    <!-- 对话框的底部按钮 -->
    <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisiblePrompt = false">取 消</el-button>
    <el-button type="primary" @click="a">确 定</el-button>
  </span>
  </el-dialog>

  <el-dialog title="图片详情" :visible.sync="showImageDialog" width="40%">
    <div style="text-align: center;">
      <img :src="selectedImage" alt="avatar" width="90%" height="90%"/>
    </div>
  </el-dialog>

</div>
</template>

<!--设置学校首页轮播图，还有全局提示语，全局提示语的开启和关闭-->
<script>
export default {
  name: "HomeSettings",
  data() {
    return {
      showImageDialog:false,
      dialogVisiblePrompt:false, //显示编辑全局提示语
      dialogCarousel:false,//显示编辑全局轮播图
      selectedImage:'',
      uploadUrl: 'http://127.0.0.1:2204/admin/upload', //这是上传图片的地址  超过4张小程序显示有点问题，后面优化
      token: localStorage.getItem('token'), //从本地变量中获取token
      fileList:['http://127.0.0.1:2204/upload/20231003005702ECxztV.png','http://127.0.0.1:2204/upload/20231003005704Bqrhrb.png'],
      carouselSwitch:true,
      carouselSwitchSub:null,
      promptSwitch:true,
      promptSwitchSub:null,
      promptText:'',
      promptTextSub:'',

    }
  },methods:{
    a(){
      console.log('a方法调用')
    },
    handleAvatarSuccess(res) {
      if (res.code === 200) {
        this.fileList.push({url: res.data})
      } else {
        console.log(res)
        this.$message.error('图片上传失败！' + res)
      }
    },
    handleAvatarSuccessAvatar(res) {
      if (res.code === 200) {
        this.schoolAvatarCache.push({url: res.data})
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
    handleRemove(file, fileList) { //这里删除就不调用后端的方法了，因为可能删除之后不会提交，增加容错，误操作
      const index = fileList.findIndex(item => item.url === file.url);
      if (index !== -1) {
        fileList.splice(index, 1);
      }
      this.fileList = fileList;
    },
    showDetailFile(file) {
      this.selectedImage = file.url; // 将选中的图片URL赋值给selectedImage变量
      this.showImageDialog = true; // 打开弹窗
    },

  }
}
</script>

<style scoped>

</style>
