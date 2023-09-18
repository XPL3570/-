var request = require('../../../utils/request');
import Notify from '@vant/weapp/notify/notify';
Page({
	data: {
		newUsername: ''
	},
	handleInput(event) {
		this.setData({
			newUsername: event.detail
		});
	},
	handleUpdateUsername() {
		var zjName = this.data.newUsername;
		if (zjName.length < 2 || zjName.length > 8) {
			Notify({ type: 'warning', message: '请输入2-8位的用户名哦' });
			return;
		}

		const data = {
			username: this.data.newUsername
		};
		request.requestWithToken('/api/user/name', 'POST', data,
			(res) => {
				if (res.data.code === 200) {
					const userInfo = wx.getStorageSync('userInfo');
					userInfo.username = this.data.newUsername;
					wx.setStorageSync('userInfo', userInfo);
					wx.navigateBack({
						delta: 1,
					});
					wx.showToast({
						title: '用户名更改成功',
						icon: 'success',
						duration: 2000,
					});
				} else if (res.data.code === 400) {
					wx.showModal({
						title: '提示',
						content: res.data.message,
						showCancel: false,
					});
				} else {
					console.log(res.data)
				}
			}, (res) => {
				wx.showToast({
					title: '用户名更改失败',
					icon: 'error',
					duration: 2000,
				});
			});
	}
})