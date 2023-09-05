
const app = getApp()

Page({
	data: {
		title:'',
		swiperOptions: {  //轮播图参数
			indicatorDots: true,
			autoplay: true,
			interval: 2000,
			duration: 1000,
		},
		swiperData: [
			'../../image/Carousel/1.jpg',
			'../../image/Carousel/2.jpg',
			'../../image/Carousel/3.jpg'
		]
	},
	onLoad: function () {
		this.setData({
			title:wx.getStorageSync('wall').wallName
		});
	},
	// 在目标页面的onShow生命周期函数中获取并显示提示信息
	onShow: function () {
		// 获取本地缓存中的提示信息
		var message = wx.getStorageSync('message');
		// console.log(message)
		if (message) {
			// 显示提示信息，持续3秒
			wx.showToast({
				title: message,
				icon: 'success',
				duration: 2404
			});
			// 清除本地缓存中的提示信息
			wx.removeStorageSync('message');
		}
	}
})
