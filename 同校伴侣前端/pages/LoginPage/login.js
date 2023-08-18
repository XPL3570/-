var api=require('../../utils/api')
Page({
  data: {
    username: '',
    password: ''
  },
  onLoad: function() {
    // const userInfo = wx.getStorageSync('userInfo');
    // if (userInfo.userName) {
    //   this.setData({
    //     username: userInfo.userName
    //   });
    // }
  },
  handleAccountInput(e) {
    this.setData({
      username: e.detail.value
    });
  },
  handlePasswordInput(e) {
    this.setData({
      password: e.detail.value
    });
  },
  handleLogin() {
	// 校验账号和密码是否为空
	if (this.data.username === '' || this.data.password === '') {
	  wx.showToast({
		title: '请输入账号和密码',
		icon: 'none'
	  });
	  return;
	}
	const { username, password } = this.data;
	const app = getApp();
	// const self = this; // 保存组件实例的引用
  
	// 发送登录请求
	wx.request({
	  url: app.globalData.apiUrl + '/webapi/LoginApi.ashx',
	  method: 'POST',
	  header: {
		'Content-Type': 'application/json'
	  },
	  data: JSON.stringify({
		'businessType': 'api.web.login',
		username,
		password
	  }),
	  success(res) {
		if (res.data.code === 0) {
		  const { token, userInfo,tokenExpireDate } = res.data.result;   
		  wx.setStorageSync('userInfo', userInfo);
		  wx.setStorageSync('token', token);
		  wx.setStorageSync('tokenExpireDate', tokenExpireDate);

		  // 跳转到其他页面
		  wx.switchTab({
			url: '/pages/index/index'
		  });
		} else {
		  // 登录失败
		  wx.showToast({
			title: '账号或密码错误',
			icon: 'none'
		  });
		}
	  },
	  fail(res) {
		console.log(res)
		wx.showToast({
		  title: '登录失败',
		  icon: 'none'
		});
	  }
	});
  }
});