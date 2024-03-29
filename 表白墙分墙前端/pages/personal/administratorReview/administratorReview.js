var request = require('../../../utils/request');
var util = require('../../../utils/util');
import Dialog from '@vant/weapp/dialog/dialog';
Page({
	data: {
		list: [],
		canLoadMore: true, // 是否可以继续加载更多数据
		page: 1, // 当前页数
		limit: 10, // 每页数据条数
		rejectDialogVisible: false
	},
	onLoad() {
		this.loadData();
	},
	loadData() {
		if (!this.data.canLoadMore) {
			wx.showToast({
				title: '没有更多数据了哦！',
				icon: 'none'
			});
			return; // 如果不能继续加载更多数据，则直接返回
		};
		let data = {
			wallId: wx.getStorageSync('wall').id,
			page: this.data.page,
			limit: this.data.limit
		}
		request.requestWithToken('/api/confessionPost/userAdmin/pending', 'GET', data, (res) => {
			if(res.data.code===200){
				const newData = res.data.data;
				// util.formatDate
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
				console.log(this.data.list);
			}else{
				console.error(res)
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
	},
	handlePass (e) {
		Dialog.confirm({
      message: '确定通过这条投稿吗？'
    })
      .then(() => {
		var {id,index}=e.currentTarget.dataset;
		var data={
			id, //记录id
			postUserId:this.data.list[index].userId,
			wallId:wx.getStorageSync('wall').id,
			postStatus:1,
		}
		console.log(data)
		request.requestWithToken("/api/confessionPost/userAdmin/submissionReview","POST",data,(res)=>{
			console.log(res.data);
				if(res.data.code===200){
					var zj=this.data.list;
					zj.splice(index, 1);
					this.setData({
						list:zj
					});
				}else{
					console.log(res.data);
					wx.showToast({
						title: res.data.message,
						icon:'error'
					})
				}
		},(res)=>{
			wx.showToast({
				title: '请求失败，请稍后重试',
				icon:'error'
			})
		});
	})
	.catch(() => {
		// 处理取消按钮的逻辑
	});
	},
	handleReject (e) {
		Dialog.confirm({
			title: '提示',
      message: '确定不通过吗？'
    })
      .then(() => {
				var {id,index}=e.currentTarget.dataset;
				var data={
					id, //记录id
					postUserId:this.data.list[index].userId,
					wallId:wx.getStorageSync('wall').id,
					postStatus:2,
				}
				request.requestWithToken("/api/confessionPost/userAdmin/submissionReview","POST",data,(res)=>{
					console.log(res.data);
						if(res.data.code===200){
							var zj=this.data.list;
							zj.splice(index, 1);
							this.setData({
								list:zj
							});
						}else{
							// console.log(res.data);
							wx.showToast({
								title: res.data.message,
								icon:'error'
							})
						}
				},(res)=>{
					wx.showToast({
						title: '请求失败，请稍后重试',
						icon:'error'
					})
				});
				
      })
      .catch(() => {
        // 处理取消按钮的逻辑
      });
	},

})