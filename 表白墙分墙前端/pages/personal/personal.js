var request = require('../../utils/request');
Page({
	data: {
		isAdmin:false,
		showCommentUnread:true, //未读评论显示
		userName: '',
		avatarUrl: '',
		showShare: false,
		numberUnreadComments:0, //未读评论数量
		options: [
			{ name: '微信', icon: 'wechat', openType: 'share' },
			{ name: 'QQ', icon: 'qq',openType: 'share' },
			{ name: '微博', icon: 'weibo' },
			{ name: '复制链接', icon: 'link', openType: 'copyLink' },
			{ name: '二维码', icon: 'qrcode' },
		],
	},
	onLoad(){
		this.onCopyLink();
		this.setData({
			isAdmin	:wx.getStorageSync('isAdmin')
		});
		 // 从本地缓存中获取上次更新的时间戳
		 var lastUpdateTime;
		 var localLastUpdateTime=wx.getStorageSync('lastUpdateTime');
		 if (localLastUpdateTime !== '' &&localLastUpdateTime!==null && localLastUpdateTime!=undefined) {
				lastUpdateTime=localLastUpdateTime;
		 }else{
			   // 获取当前时间
				 var currentTime = new Date();
				 // 计算一个月前的时间
				 var oneMonthAgo = new Date(currentTime.getTime() - 30 * 24 * 60 * 60 );
				 // 获取一个月前的时间戳（单位：秒）
				 lastUpdateTime = Math.floor(oneMonthAgo.getTime()/1000);
		 }
		//  console.log({timestamp: parseInt(lastUpdateTime, 10)})
		 request.requestWithToken('/api/comment/numberUnreadComments','GET',{timestamp: parseInt(lastUpdateTime, 10)},
		 (res)=>{
			//  console.log(res.data);
				if(res.data.code===200){
					this.setData({
						numberUnreadComments:res.data.data
					});
					wx.setStorageSync('lastUpdateTime',Math.floor(Date.now() / 1000));
				}else{
					console.error('获取用户未读评论数失败',res);
				}
		 },(res)=>{
			 console.error(res);
		 });
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
	handleAvatarTap() {
		wx.showToast({
		  title: '长按头像修改哦！',
		  icon:'none'
		})
	  },
	  handleLongAvatarTap(){
		this.AvatarModView();
	  },
	handleNameTap() {
		wx.showToast({
		  title: '长按用户名修改哦！',
		  icon:'none'
		})
	  },
	  handleLongNameTap() {
		this.NameModView();
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
	feedbackReviewView() {
		wx.navigateTo({
			url: './feedbackToAdmin/feedbackToAdmin',
		})
	},
	CommentResponseView(event) {
		  // 判断是否执行事件逻辑
			if (event.target.dataset.ignore) {
				return;
			}
		wx.navigateTo({
			url: './commentResponse/commentResponse',
		})
		this.setData({
			numberUnreadComments:0
		})
	},
	LoveWallView() {
		wx.navigateTo({
			url: '../loveWall/loveWall',
		})
	},
	AvatarModView() {
		wx.navigateTo({
			url: './avatarMod/avatarMod',
		})
	},
	ContactInfoView() {
		wx.navigateTo({
			url: './contactInfo/contactInfo',
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
	onCloseUnread(e){
		this.setData({
			showCommentUnread: false,
			numberUnreadComments:0
		});
	},
	shareFriends() {
		this.setData({ showShare: true });
	},
	onClose() {
		this.setData({ showShare: false });
	},
	onCopyLink() {
	// 	wx.setClipboardData({
	// 	  data: '小程序链接',
	// 	  success: function () {
	// 		wx.showToast({
	// 		  title: '链接已复制',
	// 		  icon: 'success',
	// 		  duration: 2000
	// 		});
	// 	  }
	// 	});
	//   },
	//   onShareAppMessage() {
	// 	return {
	// 	  title: '分享标题',
	// 	  path: '/pages/index/index?param1=value1&param2=value2',
	// 	  imageUrl: '/path/to/share-image.jpg'
	// 	};
	  },
	  onShareTimeline() {
		// return {
		//   title: '分享标题',
		//   query: 'param1=value1&param2=value2',
		//   imageUrl: '/path/to/share-image.jpg'
		// };
	  },

	onSelect(event) {
		//这个是选择之后的操作，这里先不写
		console.log(event);
	},
})