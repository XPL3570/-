
Page({
	/**
	 * 页面的初始数据
	 */
	data: {
		
	  ico:"" ,	 //头像地址
	  userName:"",
	  realName:"",   //姓名
	
	},
	onShow: function() {
	  // 获取全局变量中的头像地址
	 
	  const userInfo =wx.getStorageSync('userInfo');
	  // 更新页面数据
	  this.setData({
		  ico: userInfo.ico
	  });
  
	},
  
	onLoad: function() {
	  const userInfo =wx.getStorageSync('userInfo');
	  this.setData({
		ico: userInfo.ico,
		realName: userInfo.realName,
		userName: userInfo.userName
	  })
	},
	PatrolRecordView: function (event) {
	  wx.navigateTo({
	  });
	},
	PatrolListView: function (event) {
	  wx.navigateTo({

	  });
	},
	CheckOutView: function (event) {
	  wx.navigateTo({

	  });
	},
	LeaveRecordView: function (event) {
	  wx.navigateTo({

	  });
	},
	PasswordModView: function (event) {
	  wx.navigateTo({

	  });
	},
	AvatarModView: function (event) {
	  wx.navigateTo({

	  });
	},
	LoginOut:function (event) {
		wx.removeStorageSync('token'); 
		wx.removeStorageSync('userInfo'); 
		wx.removeStorageSync('tokenExpireDate'); 
		  wx.reLaunch({
			url: '/pages/LoginPage/login' // 重新登录页面的路径
		  });
		  wx.showToast({
			title: '已经退出登录，请重新登录',
			icon: 'none'
		  });

		wx.navigateBack({
			// delta: 1, // 返回的层数，如果首页在页面栈的第一层，则为1
			success: function() {
			  wx.redirectTo({
				url: 'pages/LoginPage/login',
			  });
			},
		  });
	  }
	  
})