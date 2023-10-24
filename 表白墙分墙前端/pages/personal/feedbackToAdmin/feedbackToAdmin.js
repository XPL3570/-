var request = require('../../../utils/request');
Page({
	data: {
		score:50,
		message:''
	},
	onChangeScore(event) {
		wx.showToast({
		  icon: 'none',
		  title: `您设置的评分：${event.detail}`,
		});
		this.setData({
			score:event.detail
		})
	  },
	  submitSuggestions(){
		  if (!this.data.message) {
			wx.showToast({
				icon:'error',
				title: '请输入您的建议',
			  });
		  }	  
		  let zj={
			score:this.data.score,
			message:this.data.message
		  };
		  request.requestWithToken('/info/feedback/submit','POST',zj,(res)=>{
				if (res.data.code===200) {
					setTimeout(function() {
						this.setData({
						  score: 50,
						  message: ''
						});
						wx.navigateBack({
						  delta: 1 // 返回的页面数，如果 delta 大于现有页面数，则返回到首页
						});
						wx.showToast({
						  title: '感谢您的反馈！',
						  icon: 'success',
						  duration: 3000 // 显示时长为3秒
						});
					  }.bind(this), 224); // 延迟224ms跳转
				}else if(res.data.code>200){
					wx.showToast({
					  title: res.data.message,
					  icon:'none',
					  duration: 3000 // 显示时长为3秒
					});
				}
		  },(res)=>{
			console.error(res.data);	
		  });
			
	  }
	  


})