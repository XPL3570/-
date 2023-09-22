var util = require('../../utils/util');
const defaultAvatarUrl = 'https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0';
import Dialog from '@vant/weapp/dialog/dialog';
Page({
	data: {
		userName: '',
		avatarUrl: defaultAvatarUrl,
		schoolName: ''
	},
	onInputSchoolName(event) {
		this.setData({
			schoolName: event.detail
		});
	},
	onInputUserName(event) {
		this.setData({
			userName: event.detail
		});
	},
	onChooseAvatar(event) {
		const tempAvatarUrl = event.detail.avatarUrl;
		console.log(tempAvatarUrl)
		try {
			util.uploadAndRetrieveImageUrl(tempAvatarUrl).then(url => {
				console.log('上传成功，服务器返回的图片地址为:', url);
				this.setData({
					avatarUrl: url,
				});
			})
		} catch (error) {
			console.error('上传失败:', error);
		}
	},
	onSubmit() {
		wx.login({
			success: (res) => {
				const schoolName = this.data.schoolName;
				const userName = this.data.userName;
				const avatarUrl = this.data.avatarUrl;
				const data = {
					code: res.code,
					userName,
					avatarUrl,
					schoolName
				};
				wx.request({
					url: 'http://localhost:2204/api/user/register',
					method: 'POST',
					header: {
						'content-type': 'application/json'
					},
					data: JSON.stringify(data),
					success: (res) => {
						if (res.data.code === 200) {
							// 学校已入驻，跳转到其他页面
							wx.setStorageSync('token', res.data.token);
							wx.setStorageSync('userInfo', res.data.userInfo);
							wx.switchTab({
								url: '/pages/index/index'
							});
							wx.showToast({
								title: '绑定学校成功！',
								icon: 'success',
								duration: 2000
							});
						} else if (res.data.code === 211) {
							wx.setStorageSync('userId', res.data.data);
							// 学校未入驻，提示用户重新输入还是跳转到 注册学校的页面
							Dialog.confirm({
								title: '是否注册该学校',
								message: '您想绑定的学校未入驻或者还未通过审核，是否注册该学校？',
							})
								.then(() => {
									wx.navigateTo({
										url: '/pages/selectSchool/registerSchool/registerSchool?schoolName=' + schoolName
									});
								})
								.catch(() => {
									// on cancel
								});
						} else {
							wx.showToast({
								title: res.data.messag,
								icon: 'none',
								duration: 2000
							});
							console.log(res.data.message);
						}
					},
					fail: (res) => {
						console.log('注册失败')
					}
				})
			},
		})

	},
});