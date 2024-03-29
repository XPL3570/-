var request = require('../../../utils/request');
var util = require('../../../utils/util');


Page({
	data: {
		activeNames: ['1'],
		list: [],
		canLoadMore: true, // 是否可以继续加载更多数据
		page: 1, // 当前页数
		limit: 10, // 每页数据条数
	},
	onChange(event) {
		this.setData({
			activeNames: event.detail,
		});
	},
	onLoad() {
		this.loadData();
	},

	loadData() {
		if (!this.data.canLoadMore) {
			wx.showToast({
				title: '没有更多您的投稿了！',
				icon: 'none'
			});
			return; // 如果不能继续加载更多数据，则直接返回
		}
		request.requestWithToken('/api/confessionPost/pending', 'GET', { page: this.data.page, limit: this.data.limit }, (res) => {
			if (res.data.code === 200) {
				const newData = res.data.data;

				const formattedData = newData.map(obj => {
					const formattedCreateTime = util.formatDate(obj.createTime);
					return {
						...obj,
						createTime: formattedCreateTime
					};
				});

				const newList = this.data.list.concat(formattedData);
				this.setData({
					list: newList,
					canLoadMore: newData.length >= this.data.limit, // 判断是否可以继续加载更多数据
					page: this.data.page + 1, // 更新页数
				});
			}
		}, (res) => {
			console.log(res)
		})
	},
	onReachBottom() { //注意这个函数的触发时机，后面要触底，上拉才会触发，如果数据不够是不会调用的
		this.loadData();
	},
	handleImageTap(event) {
		const index = event.currentTarget.dataset.index;
		const image = event.currentTarget.dataset.image;
		const images = this.data.list[index].imageURL;
		wx.previewImage({
			current: image, // 当前显示图片的链接
			urls: images // 需要预览的图片链接列表
		});
	}
})