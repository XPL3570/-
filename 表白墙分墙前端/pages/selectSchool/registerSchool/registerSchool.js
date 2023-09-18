

Page({
	data: {
	  schoolName: '',
	  wechat: '',
	  phone: ''
	},

	onLoad(options) {
		const schoolName = options.schoolName;
		this.setData({
			schoolName
		})
	  },
  
	onInputSchoolName(event) {
	  this.setData({
		schoolName: event.detail.value
	  });
	},
  
	onInputWechat(event) {
	  this.setData({
		wechat: event.detail.value
	  });
	},
  
	onInputPhone(event) {
	  this.setData({
		phone: event.detail.value
	  });
	},
	
	onSubmit() {
		if (!this.data.schoolName) {
		  wx.showToast({
			title: '请输入学校名称',
			icon: 'none'
		  });
		  return;
		}
	  
		if (!this.data.wechat) {
		  wx.showToast({
			title: '请输入微信号',
			icon: 'none'
		  });
		  return;
		}
	  
		if (!/^1[3456789]\d{9}$/.test(this.data.phone)) {
		  wx.showToast({
			title: '请输入正确的手机号',
			icon: 'none'
		  });
		  return;
		}
	  
		// 提交注册学校的请求
		wx.request({
		  url: 'https://api.example.com/registerSchool',
		  method: 'POST',
		  data: {
			schoolName: this.data.schoolName,
			wechat: this.data.wechat,
			phone: this.data.phone
		  },
		  success: (res) => {
			if (res.data.success) {
			  wx.showToast({
				title: '注册成功',
				icon: 'success'
			  });
			} else {
			  wx.showToast({
				title: '注册失败',
				icon: 'none'
			  });
			}
		  },
		  fail: (err) => {
			wx.showToast({
			  title: '网络请求失败',
			  icon: 'none'
			});
		  }
		});
	  }
  });