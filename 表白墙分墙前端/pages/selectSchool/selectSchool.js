var oos = require('../../utils/oosRequest');
var request = require('../../utils/request');
const defaultAvatarUrl = 'https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0';
import Dialog from '@vant/weapp/dialog/dialog';
Page({
	data: {
		userName: '',
		avatarUrl: defaultAvatarUrl,
		schoolName: '',
		userNameError:'',
		schoolNameError:'' 
	},
	onInputSchoolName(event) {
		var schoolName = event.detail;
		var schoolNameError = '';
		if (!schoolName) {
			schoolNameError = '学校名字不能为空';
		} else if (schoolName.length < 4 || schoolName.length > 50) {
			schoolNameError = '请注意学校名字的长度哦';
		}
		this.setData({
			schoolName: event.detail,
			schoolNameError:schoolNameError
		});
	},
	onInputUserName(event) {
		var userName = event.detail;
		var userNameError = '';
		if (!userName) {
			userNameError = '您的名字不能为空';
		} else if (userName.length < 2 || userName.length > 8) {
			userNameError = '长度必须在2到8个字符之间';
		}
		this.setData({
			userName: event.detail,
			userNameError:userNameError
		});
	},
	onSubmit() {
		// console.log(this.data.userName);
		// console.log(this.data.schoolName);
		// console.log(!this.data.userNameError);
		// console.log(this.data.schoolNameError);
		if(!this.data.userName||!this.data.schoolName||this.data.userNameError||this.data.schoolNameError){
			Dialog.alert({
				message: '请输入完整信息！',
			  }).then(() => {
				// on close
			  });
			return;
		}
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
				request.requestWithToken('/api/user/register','POST',data, (res) => {
						if (res.data.code === 200) {
							// 学校已入驻，跳转到其他页面
							wx.setStorageSync('token', res.data.data.token);
							wx.setStorageSync('userInfo', res.data.data.userInfo);
							wx.setStorageSync('wall', res.data.data.wall);  //返回的是一个对象
						wx.setStorageSync('isAdmin', res.data.data.isAdmin); //这里必然是false 直接选择好学校了
							// wx.setStorageSync('initializeHomepageIdentification', 1);
							
							setTimeout(() => {
								wx.switchTab({
									url: '/pages/index/index'
								});
							  }, 224); // 300 毫秒为延迟的时间
							wx.showToast({
								title: '绑定学校成功！',
								icon: 'success',
								duration: 2000
							});
						} else if (res.data.code === 211) {
							wx.setStorageSync('token', res.data.data);
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
								title: res.data.message,
								icon: 'none',
								duration: 2000
							});
							console.log(res.data.message);
						}
					},
					 (res) => {
						console.log('注册失败')
					}
				);
			
			},
		})

	},
});