App({
	globalData: {
	  token: null
	},
	onLaunch: function () {
	  // 登录
	  wx.login({
		success: res => {
		  // 发送 res.code 到后台换取 openId, sessionKey, unionId   没有成功发现是第一次登录就会跳转页面拿着code+学校重新发起注册请求
		  wx.request({
			url: 'http://localhost/login',
			method: 'POST',
			data: {
			  code: res.code
			},
			success: res => {
			  if (res.data.token) {
				// 保存 token 到全局变量
				this.globalData.token = res.data.token;
				// 跳转到主页面
				wx.switchTab({
				  url: '/pages/index/index'
				});
			  } else {
				// 跳转到选择学校页面
				wx.navigateTo({
				  url: '/pages/selectSchool/selectSchool?code=' + code
				});
			  }
			},
			fail: err => {
			  console.error(err);
			}
		  });
		},
		fail: err => {
		  console.error(err);
		}
	  });
	},
  globalData: {
	userInfo: null,
	token: null
  }
})
