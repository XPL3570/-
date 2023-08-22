var request=require('./utils/request')

App({
	onLaunch: function () {
	  wx.login({
		success: res => {
		  // 发送 res.code 到后台获取openid查询用户是否登录过  没有则跳转好选择学校处注册
		  wx.request({
			  url: 'http://localhost:2204/api/user/login',
			  method:'POST',
			  header: {
				'content-type': 'application/json'
			  },
			data:JSON.stringify(res.code),
			success: res => {
			  if (res.data.code===200) {
				// 保存 token 到全局变量
				this.globalData.token = res.data.token;
				this.globalData.token = res.data.userInfo;
				// 跳转到主页面
				wx.switchTab({
				  url: '/pages/index/index'
				});
			  }
			  if(res.data.code===206){ //206表示用户没有选择学校注册
				wx.redirectTo({
					url: '/pages/selectSchool/selectSchool'
				  });
				  wx.showToast({
					title: '选择好您的学校之后会自动绑定贵校表白墙',
					icon: 'none', // success, loading, none
					duration: 3000
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
	apiUrl: 'http://localhost:2204',
	token:null,
	userInfo:{}
  }
})
