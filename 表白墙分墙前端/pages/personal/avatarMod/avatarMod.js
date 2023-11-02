var util = require('../../../utils/util');
var oos = require('../../../utils/oosRequest');
var request = require('../../../utils/request');

Page({
  data: {
    avatarUrl: '', // 当前用户的头像URL   可以在页面加载的时候获取，后面做
  },
  onLoad: function () {
	const userInfo =wx.getStorageSync('userInfo');
    if (userInfo) {
      this.setData({
        avatarUrl: userInfo.avatarURL
      });
    }
  },
  uploadAvatar() {
	request.requestWithToken('/api/user/canModifyAvatar', 'GET', null,
	(res) => {
	  if (res.data.code === 400) {
		wx.showModal({
			title: '提示',
			content: res.data.message,
			showCancel: false,
		  });
	  }else{
		wx.chooseMedia({
			count: 1,
			mediaType: ['image'],
			sourceType: ['album', 'camera'],
			success: (res) => {
			  const tempFilePath = res.tempFiles[0].tempFilePath;
			  oos.uploadImagesAlibabaCloud(tempFilePath,(url)=>{
				// console.log('上传成功，服务器返回的图片地址为:', url);
				this.setData({
				  avatarUrl: url,
				});
				const data = {
				  avatarUrl: url
				};
				request.requestWithToken('/api/user/avatar', 'POST', data,
				  (res) => {
					// console.log(res.data.code)
					if (res.data.code === 200) {
					const userInfo = wx.getStorageSync('userInfo');
					 // 修改 avatarURL 属性
   					 userInfo.avatarURL = this.data.avatarUrl; 
  					  // 更新本地存储中的 userInfo
   					 wx.setStorageSync('userInfo', userInfo);
					  wx.navigateBack({
						delta: 1,
					  });
					  wx.showToast({
						  title: '更换头像成功',
						  icon: 'success',
						  duration: 2404,
						});
					} else if (res.data.code === 400) {
				   
					}
				  }, (res) => {
					wx.showToast({
					  title: '更换头像失败',
					  icon: 'error',
					  duration: 2000,
					});
				  });
			  })
			}
		  });
	  }
	},()=>{
		console.log('请求更换头像失败')
	}
 )
},
   


});