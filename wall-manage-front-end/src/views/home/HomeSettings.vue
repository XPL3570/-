<template>
<div class="container">

  <div style="margin-bottom: 5px">全局提示语信息</div>
  <div style="width: 60%;">

  <el-input v-model="promptText" placeholder="当前全局提示语为空" disabled ></el-input>
  </div>
  <div style="margin:10px"></div>
  <el-switch
      v-model="promptSwitch"
      active-text="开启全局提示语"
      inactive-text="关闭全部提示语"
      disabled>
  </el-switch>

  <div style="margin:10px"></div>

  <el-button type="primary" @click="clickSetPrompt">设置全局提示语</el-button>

  <div style="margin:50px"></div>

  <div style="margin-bottom: 5px">全局轮播图信息</div>
  <div class="image-row">
      <div v-for="imageUrl in fileList" :key="imageUrl.url" class="image-container" @click="showDetailFile(imageUrl)">
        <img :src="imageUrl.url" alt="Image" class="image"/>
      </div>
  </div>
  <div style="margin:10px"></div>
  <el-switch
      disabled
      v-model="carouselSwitch"
      active-text="开启全局轮播图"
      inactive-text="关闭全部轮播图">
  </el-switch>
  <div style="margin:10px"></div>

  <el-button type="primary" @click="clickSetCarousel">设置全局轮播图</el-button>


  <el-dialog :visible.sync="dialogVisiblePrompt" width="50%">
    <el-input v-model="promptTextSub" :rows="3" placeholder="请输入全局提示语"></el-input>
    <div style="margin:20px"></div>
    <el-switch
        v-model="promptSwitchSub"
        active-text="开启全局提示语"
        inactive-text="关闭全部提示语">
    </el-switch>
    <div style="margin:30px"></div>
    <!-- 对话框的底部按钮 -->
    <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisiblePrompt = false">取 消</el-button>
    <el-button type="primary" @click="SubmitGlobalPromptModification">确 定</el-button>
  </span>
  </el-dialog>

  <el-dialog :visible.sync="dialogCarousel" width="40%">
    <div>首页轮播图</div>
    <div style="margin:10px"></div>
    <el-upload
        :before-upload="ossPolicy"
        :action="host"
        list-type="picture-card"
        :file-list="fileListSub"
        :data="objectData"
        :on-preview="showDetailFile"
        :on-success="handleImageSuccess"
        :limit="3"
        :on-remove="handleRemove"
    >
      <i class="el-icon-plus"></i>
    </el-upload>
    <div style="margin:20px"></div>
    <el-switch
        v-model="carouselSwitchSub"
        active-text="开启全局轮播图"
        inactive-text="关闭全部轮播图">
    </el-switch>
    <div style="margin:30px"></div>
    <!-- 对话框的底部按钮 -->
    <span slot="footer" class="dialog-footer">
    <el-button @click="dialogCarousel = false">取 消</el-button>
    <el-button type="primary" @click="submitGlobalCarouselModification">确 定</el-button>
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
import api from "@/axios";

export default {
  name: "HomeSettings",
  data() {
    return {
      showImageDialog:false,
      dialogVisiblePrompt:false, //显示编辑全局提示语
      dialogCarousel:false,//显示编辑全局轮播图
      selectedImage:'',
      // uploadUrl: 'http://127.0.0.1:2204/admin/upload', //这是上传图片的地址  超过4张小程序显示有点问题，后面优化
      token: localStorage.getItem('token'), //从本地变量中获取token
      fileList:[],
      fileListSub:[],
      carouselSwitch:null,
      carouselSwitchSub:null,
      promptSwitch:null,
      promptSwitchSub:null,
      promptText:'没有加载到数据哦，这里是页面默认初始数据！',
      promptTextSub:'',
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
      host:'',
    }
  },
  created() {
   this.getData();
  }
  ,methods:{
    async getData() {
      try {
        const [promptResponse, switchResponse, carouselResponse] = await Promise.all([
          api.get('/msg/admin/getGlobalPrompt', null),
          api.get('/carouselImage/admin/getCarouselIsDisabled', null),
          api.get('/carouselImage/admin/getAllCarouselImage', null)
        ]);

        if (promptResponse.data.code === 200) {
          this.promptText = promptResponse.data.data.message;
          this.promptSwitch = promptResponse.data.data.mainSwitch;
        } else {
          this.$message.error('获取全局提示语失败！');
        }

        if (switchResponse.data.code === 200) {
          this.carouselSwitch = switchResponse.data.data;
        } else {
          this.$message.error('获取全局轮播图开关失败');
        }

        if (carouselResponse.data.code === 200) {
          console.log(carouselResponse.data)
          this.fileList = carouselResponse.data.data.map(url => {
            return { url: url.carouselImage };
          });
        } else {
          this.$message.error('获取全局轮播图图片失败');
        }
      } catch (error) {
        console.error(error);
      }
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
    SubmitGlobalPromptModification(){
        let zj={
          message:this.promptTextSub,
          mainSwitch:this.promptSwitchSub
        }
        api.post('/msg/admin/setGlobalPrompts',zj)
            .then(
                res=>{
                  if (res.data.code===200){
                    this.promptText=zj.message;
                    this.promptSwitch=zj.mainSwitch;
                    this.dialogVisiblePrompt=false;
                    this.$message.success('修改全局提示语成功！');
                  }else {
                    this.$message.error('修改全局提示语失败！');
                  }
                }
            )
    },
    submitGlobalCarouselModification(){
      let zj={
        carouselList:this.fileListSub.map(url=>{return url.url}),
        carouselIsDisabled:this.carouselSwitchSub
      };
      api.post('/carouselImage/admin/setGlobalCarousel',zj)
          .then(
              res=>{
                if(res.data.code===200){
                  this.fileList=this.fileListSub.slice();
                  this.carouselSwitch=this.carouselSwitchSub;
                  this.dialogCarousel=false;
                  this.$message.success('修改全局轮播图成功！')
                }else {
                  this.$message.error('修改全局轮播图信息失败！')
                }
              }
          )
    },
    clickSetPrompt(){
      this.dialogVisiblePrompt=true;
      this.promptSwitchSub=this.promptSwitch;
      this.promptTextSub=this.promptText;
    },
    clickSetCarousel(){
      this.fileListSub=[];
      this.dialogCarousel=true;
      this.carouselSwitchSub=this.carouselSwitch;
      this.fileListSub = this.fileList.slice();
    },
    handleImageSuccess() { //不写回调这个就不用了
      this.fileListSub.push({
            url: this.host + '/' + this.objectData.key,
          });


      // console.log(res)
      // if (res){
      //   console.log('res存在')
      // }
      // if (res.code === 200||res.code===204) {
      //   this.fileListSub.push({url: res.data})
      // } else {
      //   console.log(res)
      //   this.$message.error('图片上传失败！' + res)
      // }
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
    handleRemove(file, fileListSub) { //这里删除就不调用后端的方法了，因为可能删除之后不会提交，增加容错，误操作
      console.log(file);
      console.log(fileListSub);
      const index = fileListSub.findIndex(item => item.url === file.url);
      if (index !== -1) {
        fileListSub.splice(index, 1);
      }
      this.fileListSub = fileListSub;
    },
    showDetailFile(file) {
      this.selectedImage = file.url; // 将选中的图片URL赋值给selectedImage变量
      this.showImageDialog = true; // 打开弹窗
    },

  }
}
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80vh; /* 将容器高度设置为视口高度来实现垂直居中 */

}

.image-row {
  display: flex;
  flex-direction: row;
  /*justify-content: space-between;*/
}
.image-container {
  margin-right: 10px;
}

.image {
  width: 240px;
  height: 180px;
  object-fit: cover;
}
</style>
