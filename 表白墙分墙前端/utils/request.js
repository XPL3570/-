function requestWithToken(url, method, data, successCallback, failCallback) {
	const globalData = getApp().globalData;
	var apiUrl = globalData.apiUrl;
	const token = wx.getStorageSync('token');
	wx.request({
	  url: apiUrl + url,
	  method: method,
	  header: {
		'authentication': token,
		'content-type': 'application/json'
	  },
	  data:  method === 'GET' ? data : JSON.stringify(data), // 根据请求方法决定如何处理数据
	  success: function(res){
		  if(res.data.code===206){
			  this.LoginorRegister();
			  return;
		  }
		  successCallback(res);
	  },
	  fail: failCallback
	});
	}


  function LoginorRegister(){
	wx.login({
		success: res => {	
			const code=res.code
		  // 发送 res.code 到后台获取openid查询用户是否登录过  没有则跳转好选择学校处注册
		  wx.request({
			url: 'https://www.txbbq.xyz/api/user/login',
			// url: 'http://localhost:2204/api/user/login',
		
			method:'POST',
			header: {
				'content-type': 'application/json'
			  },
			data:JSON.stringify({code}),
			success: res => {
				// console.log(res.data)
			  if (res.data.code===200) {
				const { token, userInfo,wall,isAdmin}= res.data.data;  //拿到数据保存到本地变量 
				wx.setStorageSync('token',token);
				wx.setStorageSync('userInfo',userInfo);
				wx.setStorageSync('wall',wall);  //这里后端是返回一个id
				wx.setStorageSync('isAdmin',isAdmin); 
				// 跳转到主页面
				// wx.reLaunch({
				//   url: '/pages/index/index'
				// });
			
				wx.showToast({
					title: '自动登录成功!',
					icon: 'none', // success, loading, none
					duration: 3000
				  });
			  }else if(res.data.code===206){ //206表示用户没有选择学校注册
				wx.redirectTo({
					url: '/pages/selectSchool/selectSchool'
				  });
				  wx.showToast({
					title: '请绑定您的学校',
					icon: 'none', 
					duration: 3000
				  });
			  }else{
				wx.showToast({
					title: '后台错误，获取openId失败',
					icon: 'none', 
					duration: 10000
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
}
  module.exports = {
	requestWithToken: requestWithToken,
	LoginorRegister:LoginorRegister
  };