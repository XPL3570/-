<template>
  <div >
    <div class="container" >
    <el-input
        placeholder="请输入30字以内标题"
        maxlength="30"
        v-model="title"
        clearable>
    </el-input>
    <div style="margin: 20px 0; width: 100%">

    <el-input
        type="textarea"
        maxlength="1000"
        :autosize="{ minRows: 2, maxRows: 4}"
        placeholder="请输入投稿内容（maxlength=1000）"
        v-model="textContent">
    </el-input>
    </div>
      <div slot="tip">图片上传:只能上传jpg/png/gif/bmp格式的图片，且不超过4张</div>
    <div style="margin: 20px 0;">

    <el-upload
        :action="uploadUrl"
        list-type="picture-card"
        :file-list="fileList"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload"
        :on-preview="handlePictureCardPreview"
        :limit="4"
        :on-remove="handleRemove"
        :headers="{
          authentication:token
        }">
      <i class="el-icon-plus"></i>
    </el-upload>

    </div>

    <div style="margin: 20px 0;">
    <el-switch
        style="display: block"
        v-model="isAnonymous"
        active-color="#13ce66"
        active-text="匿名发布"
        inactive-text="实名发布">
    </el-switch>
    </div>
    <div style="margin: 20px 0;">
    <el-button type="primary" round @click="submitPost">投稿到所有表白墙</el-button>
    </div>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
  </div>
  </div>
</template>

<script>
import axios from "@/axios";

export default {
  name: "ConfessionRelease",
  data() {
    return {
      wallId:0, //墙id ，这里就设置成0，为了通过校验
      title:'', //投稿标题
      textContent:'',     //投稿内容
      isAnonymous:false,    //是否匿名
      dialogImageUrl: '',  //显示图片
      dialogVisible: false,  //是否显示
      uploadUrl: 'http://127.0.0.1:2204/admin/upload', //这是上传图片的地址  超过4张小程序显示有点问题，后面优化
      token: localStorage.getItem('token'), //从本地变量中获取token
      fileList: [],
    };
  },
  created() {
  },
  methods: {
    handleAvatarSuccess(res) {
      if (res.code === 200) {
        this.fileList.push({url: res.data})
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
    handleRemove(file) {
      let zj = {
        deleteUrl: file.url
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
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    submitPost() {
      // 校验所有的值是否为空
      if (!this.title || !this.textContent ) {
        // 如果有任何一个值为空，进行相应的提示或返回
        this.$message.warning("请填写完整的投稿信息哦")
        return;
      }
      // 拼接fileList中每个对象的url值
      const urls = this.fileList.map(file => file.url).join(';');
      // 发送请求
      axios.post('/admin/allSubmitPost', {
        wallId: 0,
        title: this.title,
        textContent: this.textContent,
        isAnonymous: this.isAnonymous,
        imageURL: urls
      })
          .then(response => {
            if (response.data.code===200){
                  this.title=''; //投稿标题
                  this.textContent='';     //投稿内容
                  this.isAnonymous=false;   //是否匿名
                  this.fileList= [];
              this.$message.success("投稿到所有表白墙成功！")
            }else if(response.data.code===224){
              this.$message.error(response.data.message);
            }
            else {
              this.$message.error('投稿失败！')
            }
          })
          .catch(error => {
            console.error(error)
            // 请求失败处理逻辑
          });
    }
  }
}
</script>

<style scoped>
.container {
  margin: 0 auto;
  width: 50%; /* 可根据需要调整宽度 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  /*height: 100vh; !* 可根据需要调整高度 *!*/
}
</style>
