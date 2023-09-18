
Page({

	data: {
		isAdmin:false,
		userName: '',
		avatarUrl: '',
		showShare: false,
		options: [
			{ name: '微信', icon: 'wechat', openType: 'share' },
			{ name: 'QQ', icon: 'qq' },
			{ name: '微博', icon: 'weibo' },
			{ name: '复制链接', icon: 'link' },
			{ name: '二维码', icon: 'qrcode' },
		],
	},
	onLoad(){
		this.setData({
			isAdmin	:wx.getStorageSync('isAdmin')
		})
	},
	onShow() {
		const userInfo = wx.getStorageSync('userInfo');
		if (userInfo) {
			// console.log(userInfo);
			this.setData({
				userName: userInfo.username,
				avatarUrl: userInfo.avatarURL
			});
		}
	},
	SubmitApplicationView() {
		wx.navigateTo({
			url: './submitApplication/submitApplication',
		})
	},
	AdministratorReviewView() {
		wx.navigateTo({
			url: './administratorReview/administratorReview',
		})
	},
	CommentResponseView() {
		wx.navigateTo({
			url: './commentResponse/commentResponse',
		})
	},
	AvatarModView() {
		wx.navigateTo({
			url: './avatarMod/avatarMod',
		})
	},
	NameModView() {
		wx.navigateTo({
			url: './nameMod/nameMod',
		})
	},
	SubmitRecordView() {
		wx.navigateTo({
			url: './submitRecord/submitRecord',
		})
	},

	shareFriends() {
		this.setData({ showShare: true });
	},
	onClose() {
		this.setData({ showShare: false });
	},

	onSelect(event) {
		//这个是选择之后的操作，这里先不写
	},
})