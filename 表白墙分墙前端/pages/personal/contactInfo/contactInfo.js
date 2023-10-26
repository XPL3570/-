var request = require('../../../utils/request');
import Dialog from '@vant/weapp/dialog/dialog';

Page({
	data: {
		value: '',
		checked: false,
		isShowButton: false,
		yourOwnContact: [], //申请添加我的记录
		youApplicationSent: [], //你发送的申请
	},
	onLoad() {
		this.setData({
			value: wx.getStorageSync('userInfo').wxaccount,
			checked: wx.getStorageSync('userInfo').canObtainWeChat
		});
		this.getYourOwnContact();
		this.youApplicationSent();
	},
	//获取添加我的申请
	getYourOwnContact() {
		request.requestWithToken('/api/userContact/getYourOwnContact', 'GET', null, (res) => {
			// console.log(res.data.data);
			if (res.data.code === 200) {
				this.setData({
					yourOwnContact: res.data.data
				});
			} else {
				console.log(res);
			}
		}, (res) => {
			console.error(res);
		});
	},
	//获取我添加的申请
	youApplicationSent() {
		request.requestWithToken('/api/userContact/youApplicationSent', 'GET', null, (res) => {
			// console.log(res.data.data);
			if (res.data.code === 200) {
				this.setData({
					youApplicationSent: res.data.data
				});
			} else {
				console.log(res);
			}
		}, (res) => {
			console.error(res);
		});
	},
	//同意申请理由
	agreeApply(e) {
		console.log(e.currentTarget.dataset.id);
		  Dialog.confirm({
			message: '确定同意该申请吗？同意之后该校友或用户可以获取到您当前设置的联系方式',
		  })
			.then(() => {
				let zj={
					userContactId:e.currentTarget.dataset.id,
					isAgree:true
				}
				request.requestWithToken('/api/userContact/setObtainWxAgree','POST',zj,(res)=>{
					if(res.data.code===200){
						var temp=this.data.yourOwnContact;
						temp[e.currentTarget.dataset.index].status=1;
						this.setData({
							yourOwnContact:temp
						});
					}else{
						console.log(res.data);
					}
				},(res)=>{
					console.error(res);
				})
			})
			.catch(() => {});
	},
	// 和不同意申请理由
	refuseApplication(e) {
		Dialog.confirm({
			message: '确定不同意该申请吗？',
		  })
			.then(() => {
				let zj={
					userContactId:e.currentTarget.dataset.id,
					isAgree:false
				}
				request.requestWithToken('/api/userContact/setObtainWxAgree','POST',zj,(res)=>{
					if(res.data.code===200){
						var temp=this.data.yourOwnContact;
						temp[e.currentTarget.dataset.index].status=2;
						this.setData({
							yourOwnContact:temp
						});
					}else{
						console.log(res.data);
					}
				},(res)=>{
					console.error(res);
				})
			})
			.catch(() => {});
	},

	modifyContactInfo() {
		if (this.data.value.length < 6) {
			wx.showToast({
				title: '请设置6位及以上的联系方式哦！',
				icon: 'none'
			})
			return;
		}
		// console.log(wx.getStorageSync('userInfo'));
		if (this.data.value === wx.getStorageSync('userInfo').wxaccount && this.data.checked === wx.getStorageSync('userInfo').canObtainWeChat) {
			wx.showToast({ icon: 'success' });
			this.setData({
				isShowButton: false
			});
		} else {
			let zj = {
				canObtainWeChat: this.data.checked,
				wxAccount: this.data.value
			};
			//提交修改请求
			request.requestWithToken('/api/user/setWeChat', 'POST', zj, (res) => {
				if (res.data.code === 200) {
					wx.showToast({
						title: '修改成功',
						icon: 'success'
					});
					let userInfo = wx.getStorageSync('userInfo');
					userInfo.wxaccount = zj.wxAccount;
					userInfo.canObtainWeChat = zj.canObtainWeChat;
					wx.setStorageSync('userInfo', userInfo);
				} else if (res.data.code > 200) {
					wx.showToast({
						title: res.data.message,
						icon: 'none',
					});
					this.setData({
						value: wx.getStorageSync('userInfo').wxaccount,
						checked: wx.getStorageSync('userInfo').canObtainWeChat
					});
				}
				this.setData({ isShowButton: false })
			}, (res) => {
				console.error(res);
			});
		}
	},
	onChange(event) {
		this.setData({
			isShowButton: true,
			value: event.detail
		});
	},
	onChangeCzj({ detail }) {
		this.setData({ isShowButton: true, checked: detail });
	},
})