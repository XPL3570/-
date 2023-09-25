<template>
  <div>
    <el-input
        placeholder="请输入30字以内标题"
        maxlength="30"
        v-model="title"
        clearable>
    </el-input>
    <div style="margin: 20px 0;"></div>
    <el-input
        type="textarea"
        maxlength="1000"
        :autosize="{ minRows: 2, maxRows: 4}"
        placeholder="请输入投稿内容（maxlength=1000）"
        v-model="textContent">
    </el-input>
    <div style="margin: 20px 0;"></div>
    <el-upload
        :action="uploadUrl"
        list-type="picture-card"
        :file-list="fileList"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload"
        :on-preview="handlePictureCardPreview"
        :limit="3"
        :on-remove="handleRemove"
        :headers="{
          authentication:token
        }">
      <i class="el-icon-plus"></i>
    </el-upload>
    <div slot="tip">只能上传jpg/png/gif/bmp格式的图片，且不超过3张</div>
    <el-button type="primary" round>投稿到所有表白墙</el-button>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
  </div>
</template>

<script>
import axios from "@/axios";

export default {
  name: "ConfessionRelease",
  data() {
    return {
      wallId:'', //墙id ，这里就设置成0，为了通过校验
      title:'', //投稿标题
      textContent:'',     //投稿内容
      isAnonymous:'',    //是否匿名
      dialogImageUrl: '',  //显示图片
      dialogVisible: false,  //是否显示
      uploadUrl: 'http://127.0.0.1:2204/admin/upload', //这是上传图片的地址
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
    }
  }
}
</script>

<style scoped>

</style>
