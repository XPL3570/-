var request = require('../../../utils/request');
var util = require('../../../utils/util');
Page({
	data: {
		list: [],
		canLoadMore: true, // 是否可以继续加载更多数据
		page: 1, // 当前页数
		limit: 10, // 每页数据条数
	},
	onLoad() {
		this.loadData();
	},
	loadData() {
		if (!this.data.canLoadMore) {
			wx.showToast({
				title: '没有更多评论回复了哦！',
				icon: 'none'
			});
			return; // 如果不能继续加载更多数据，则直接返回
		}
		// console.log(this.data.page);
		request.requestWithToken('/api/comment/repliesWithComments', 'GET', { page: this.data.page, limit: this.data.limit }, (res) => {
			// console.log(res.data);
			if (res.data.code === 200) {
				const newData = res.data.data;
				const formattedData = newData.map(obj => {
					const formattedCreateTime = util.formatDate(obj.commentTime);
					return {
						...obj,
						commentTime: formattedCreateTime
					};
				});
				console.log(formattedData);
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

})