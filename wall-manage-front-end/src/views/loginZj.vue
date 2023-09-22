<template>
  <div class="login-wrap">
    <el-form label-position="left" :model="ruleForm" :rules="rules" ref="ruleForm" label-width="0px" class="demo-ruleForm login-container">
      <h3 class="title">用户登录</h3>
      <el-form-item prop="username">
        <el-input type="text" v-model="ruleForm.username" auto-complete="off" placeholder="账号"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input type="password" v-model="ruleForm.password" auto-complete="off" placeholder="密码"></el-input>
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item prop="code">
            <el-input type="text" v-model="ruleForm.code" auto-complete="off" placeholder="图形验证码" @keyup.enter.native="submitForm('ruleForm')"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12" class="code-box">
          <img :src="ruleForm.codeimg" alt="" class="codeimg" @click="getcode()">
        </el-col>
      </el-row>
      <el-checkbox class="remember" v-model="rememberpwd">记住密码</el-checkbox>
      <el-form-item style="width:100%;">
        <el-button type="primary" style="width:100%;" @click="submitForm('ruleForm')" :loading="logining">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script type="text/ecmascript-6">
import {  getCookie  } from '../utils/util'
// import md5 from 'js-md5'
export default {
  name: 'loginZj',
  data() {
    return {
      //定义loading默认为false
      logining: false,
      // 记住密码
      rememberpwd: false,
      ruleForm: {
        //username和password默认为空
        username: '',
        password: '',
        code: '',
        randomStr: '',
        codeimg: ''
      },
      //rules前端验证
      rules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      }
    }
  },
  // 创建完毕状态(里面是操作)
  created() {
    this.$message({
      message: '账号密码及验证码不为空即可',
      type: 'success'
    })
    // 获取图形验证码
    this.getcode()
    // 获取存在本地的用户名密码
    this.getuserpwd()

  },
  // 里面的函数只有调用才会执行
  methods: {
    // 获取用户名密码
    getuserpwd() {
      if (getCookie('user') != '' && getCookie('pwd') != '') {
        this.ruleForm.username = getCookie('user')
        this.ruleForm.password = getCookie('pwd')
        this.rememberpwd = true
      }
    },
    //获取info列表
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.logining = true
          // 测试通道，不为空直接登录
          setTimeout(() => {
            this.logining = false
            this.$store.commit('login', 'true')
            this.$router.push({ path:'/user/userManagement'})
          }, 1000)
          // 注释
          // login(this.ruleForm).then(res => {
          //   if (res.success) {
          //     if (this.rememberpwd) {
          //       //保存帐号到cookie，有效期7天
          //       setCookie('user', this.ruleForm.username, 7)
          //       //保存密码到cookie，有效期7天
          //       setCookie('pwd', this.ruleForm.password, 7)
          //     } else {
          //       delCookie('user')
          //       delCookie('pwd')
          //     }
          //     //如果请求成功就让他2秒跳转路由
          //     setTimeout(() => {
          //       this.logining = false
          //       // 缓存token
          //       localStorage.setItem('logintoken', res.data.token)
          //       // 缓存用户个人信息
          //       localStorage.setItem('userdata', JSON.stringify(res.data))
          //       this.$store.commit('login', 'true')
          //       this.$router.push({ path: '/goods/Goods' })
          //     }, 1000)
          //   } else {
          //     this.$message.error(res.msg)
          //     this.logining = false
          //     return false
          //   }
          // })
        } else {
          // 获取图形验证码
          this.getcode()
          this.$message.error('请输入用户名密码！')
          this.logining = false
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
.code-box {
  text-align: right;
}
.codeimg {
  height: 40px;
}
</style>
