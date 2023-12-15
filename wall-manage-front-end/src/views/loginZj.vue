<template>
  <div class="login-wrap">
    <el-form label-position="left" :model="ruleForm" :rules="rules" ref="ruleForm" label-width="0px" class="demo-ruleForm login-container">
      <h3 class="title">用户登录</h3>
      <el-form-item prop="account">
        <el-input type="text" v-model="ruleForm.account" auto-complete="off" placeholder="账号"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input type="password" v-model="ruleForm.password" auto-complete="off" placeholder="密码"></el-input>
      </el-form-item>

      <el-checkbox class="remember" v-model="rememberUser">记住我</el-checkbox>
      <el-form-item style="width:100%;">
        <el-button type="primary" style="width:100%;" @click="submitForm('ruleForm')" :loading="loginIng">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script type="text/ecmascript-6">
import { delCookie, getCookie ,setCookie } from '@/utils/util'
import api from "@/axios";
// import md5 from 'js-md5'
export default {
  name: 'loginZj',
  data() {
    return {
      //定义loading默认为false
      loginIng: false,
      // 记住密码
      rememberUser: false,
      ruleForm: {
        //username和password默认为空
        account: '',
        password: '',
      },
      //rules前端验证
      rules: {
        account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
      }
    }
  },
  // 创建完毕状态(里面是操作)
  created() {
    this.$message({
      message: '请输入您的账号密码',
      type: 'success'
    })
    // 获取存在本地的用户名密码
    this.getUsername()

  },
  // 里面的函数只有调用才会执行
  methods: {
    // 获取用户名密码
    getUsername() {
      if (getCookie('user') !== '') {
        this.ruleForm.account = getCookie('user')
        this.rememberUser = true
      }
    },
    //获取info列表
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.loginIng = true
          api.post('/admin/login',this.ruleForm).then(res => {
            // console.log(res.data.data.token);
            if (res.data.code===200) {
              if (this.rememberUser) {
                //保存帐号到cookie，有效期7天
                setCookie('user', this.ruleForm.account, 7)
                //保存密码到cookie，有效期7天
              } else {
                delCookie('user')
              }
              //如果请求成功就让延迟跳转路由
              setTimeout(() => {
                this.loginIng = false
                // 缓存token
                localStorage.setItem('token', res.data.data.token)
                // 缓存用户个人信息
                localStorage.setItem('userdata', JSON.stringify(res.data.data.admin))
                this.$store.commit('login', 'true')
                this.$router.push({ path:'/webApi/user/userManagement'})
              }, 404)
            } else {
              this.loginIng = false
              this.$message.error(res.data.message);
              return false
            }
          })
        } else {
          this.$message.error('请输入用户名密码！')
          this.loginIng = false
          return false
        }
      })
    },
  }
}
</script>

<style scoped>
.login-wrap {
  box-sizing: border-box;
  width: 100%;
  height: 100%;
  padding-top: 10%;
  background-image: url('@/assets/img/yqy.png');
  background-repeat: no-repeat;
  background-position: center right;
  background-size:100%;
}
.login-container {
  border-radius: 10px;
  margin: 0px auto;
  width: 350px;
  padding: 30px 35px 15px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  text-align: left;
  box-shadow: 0 0 20px 2px rgba(0, 0, 0, 0.1);
}
.title {
  margin: 0px auto 40px auto;
  text-align: center;
  color: #505458;
}
.remember {
  margin: 0px 0px 35px 0px;
}
</style>
