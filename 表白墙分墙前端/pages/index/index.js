var request = require('../../utils/request')
var util = require('../../utils/util')
import Notify from '@vant/weapp/notify/notify';

Page({
	data: {
		startTimeStamp:0,
		title: '', //墙名字
		prompt: '欢迎来到同校表白墙！        您的投稿是我不懈的动力！',
		swiperOptions: {  //轮播图参数
			indicatorDots: true,
			autoplay: true,
			interval: 2000,
			duration: 1000,
		},
		reportPanelShow:false,
		postId:null, //举报id
		reportInfo:'', //举报信息
		swiperData: [
			'../../image/Carousel/1.jpg',
			'../../image/Carousel/2.jpg',
			'../../image/Carousel/3.jpg'
		],
		canLoadMore: true, // 是否可以继续加载更多数据
		page: 1, // 当前页数
		limit: 10, // 每页数据条数
		activeNames: ['1'],
		confession: [],
		//下面的都放到页面变量里面是因为不针对单个的回复内容做保存了，因为弹出框在的位置不太好拿下标
		showPopup: false, // 控制输入框的显示状态
		replyName: '', //待回复人的姓名
		replyCommentId: null, //回复人id
		replyContent: '',
		replyIndex: -1,  //要回复的评论的投稿索引 在大集合下面
		mainIndex: -1, //如果回复的是主评论，这就是记录主评论的下标
	},
	onLoad() { //获取首页数据和表白数据，分开哦
		// console.log('index的onLoad函数触发')
		this.setData({
			title: wx.getStorageSync('wall').wallName
		});
		// console.log(wx.getStorageSync('userInfo').schoolId)
		//获取轮播图和提示语
		if (wx.getStorageSync('userInfo').schoolId === undefined) {
			return;
		}
		var sbzj = {
			schoolId: wx.getStorageSync('userInfo').schoolId
		}
		console.log(sbzj);

		request.requestWithToken("/api/school/getIndexInfo", "GET", sbzj,
			(res) => {
				if (res.data.code === 200) {
					this.setData({
						prompt: res.data.data.prompt,
						swiperData: res.data.data.carouselImages
					});
				} else {
					console.error('获取首页信息失败了')
					console.log(res);
				}
			}, (res) => {
				console.error(res);
			});
		this.loadData();
	},
	loadData() {
		// console.log(this.data.confession.length);
		if (!this.data.canLoadMore) {
			wx.showToast({
				title: '没有更多投稿数据了哦！',
				icon: 'none'
			});
			return;
		}
		const data = {
			wallId: wx.getStorageSync('wall').id,
			page: this.data.page,
			limit: this.data.limit
		};
		request.requestWithToken("/api/confessionPost/readConfessionWall", "GET", data, (res) => {
			if (res.data.code === 200) {
				this.setData({
					//判断是否可以
					canLoadMore: res.data.data.length >= this.data.limit,
					page: this.data.page + 1, // 更新页数
				});
				// console.log(res.data.data);
				const updatedConfession = res.data.data.map((item) => { //遍历源数据
					const subCommentsExist = item.mainComments.map((mainComment) => { //拿到主评论去找子评论找有没有匹配的
						const matchingSubComments = item.subComments.filter(subComment => subComment.parentCommentId === mainComment.id);
						return matchingSubComments.length > 0;
					});
					//优化点，这里是把所有的日志在这里过滤了，但是不展开的数据可能不要用，这里不管
					const updatedMainComments = item.mainComments.map((mainComment) => ({
						...mainComment, 
						commentTime:util.formatDate(mainComment.commentTime), 
						commentContentVisible: false,
					}));
					const updatedSubComments = item.subComments.map((subComment) => ({
						...subComment, 
						commentTime:util.formatDate(subComment.commentTime), 
					}));
					let formattedpublishTime = util.formatDate(item.publishTime);
					return {
						...item,
						publishTime:formattedpublishTime,
						subComments:updatedSubComments,
						mainComments: updatedMainComments,
						subCommentsExist,
					};
				});
				// 将加载到的数据合并到原始数据
				const allConfession = this.data.confession.concat(updatedConfession);
				this.setData({
					confession: allConfession,
				});
				// console.log(this.data.confession);
				wx.showToast({
					title: '获取数据成功'
				})
				console.log('获取数据成功了ok')
			} else {
				wx.showToast({
					title: '数据获取异常',
					icon: 'error'
				});
				// console.log(res.data);
			}
		})
	},
	onReachBottom() {
		this.loadData();
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
	},
	onChange(event) {
		this.setData({
			activeNames: event.detail
		});
	},
	handleImageTap(e) {
		const { outIndex, image } = e.currentTarget.dataset;
		const images = this.data.confession[outIndex].imageURL;
		wx.previewImage({
			current: image, // 当前显示图片的链接
			urls: images // 需要预览的图片链接列表
		});
	},
	//处理展开子评论数据
	expandComments(event) {
		const { outIndex, zjIndex } = event.currentTarget.dataset;
		const updatedConfession = this.LoadComments(outIndex, zjIndex, this.data.confession, true);
		this.setData({
			confession: updatedConfession
		});
	},
	/**
	 * 将评论展开的方法
	 * @param {number} outIndex - 外层索引
	 * @param {number} zjIndex - 内层索引
	 * @param {Array} confession - 原始数据
	 * @param {boolean} updateVisible - 是否更新 commentContentVisible 属性,就是变化展开的数据
	 * @returns {Array} - 更新后的数据
	 */
	LoadComments(outIndex, zjIndex, confession, updateVisible) {
		// 如果没有子评论，则直接返回
		if (!confession[outIndex].subCommentsExist[zjIndex]) {
			return confession;
		}
		// 清空 showComments 数组
		confession[outIndex].mainComments[zjIndex].showComments = [];
		// 获取主评论的 id
		const mainCommentId = confession[outIndex].mainComments[zjIndex].id;
		// 定义递归函数，将父评论的用户名添加到对应的子评论中
		const addParentNameToSubComments = (commentId, subComments, parentName) => {
			for (let i = 0; i < subComments.length; i++) {
				if (subComments[i].parentCommentId === commentId) { // 判断子评论的 parentCommentId 是否与主评论 id 匹配
					subComments[i].parentName = parentName; // 将父评论的用户名赋值给子评论的 parentName 属性
					// console.log(subComments[i]);
					this.data.confession[outIndex].mainComments[zjIndex].showComments.push(subComments[i]);  //这里是把下面的子节点放到父评论下面
					let name = subComments[i].userName;  //拿到这个的名字调用下面找子函数
					addParentNameToSubComments(subComments[i].id, subComments, name); // 递归调用函数，处理当前子评论的子评论，传入父评论的用户名
				}
			}
		};
		// 获取所有子评论
		const subComments = confession[outIndex].subComments;
		// 获取主评论的用户名作为父评论名称
		const parentName = confession[outIndex].mainComments[zjIndex].userName;
		// 调用递归函数，将父评论名称添加到子评论中
		addParentNameToSubComments(mainCommentId, subComments, parentName);
		// 如果需要更新 commentContentVisible 属性，则更新
		if (updateVisible) {
			confession[outIndex].mainComments[zjIndex].commentContentVisible = !confession[outIndex].mainComments[zjIndex].commentContentVisible;
		}
		return confession;
	},

	//输入主评论
	handleInput(e) {
		const { outIndex } = e.currentTarget.dataset;
		this.setData({
			[`confession[${outIndex}].submitMainComment`]: e.detail
		});
	},
	//发布主评论
	handleSubmit(e) {
		const { outIndex } = e.currentTarget.dataset;
		var content = this.data.confession[outIndex].submitMainComment;
		if (content === undefined || content.trim() === "") { //判断是否都是空字符串和空
			// 警告通知
			Notify({ type: 'primary', message: '请输入评论' });
			return;
		} else if (content.length > 50) {
			Notify({ type: 'primary', message: '请输入50字以内的评论' });
			return;
		}
		var data = {  //发送请求构建的对象
			confessionPostReviewId: this.data.confession[outIndex].id,
			parentCommentId: null,
			commentContent: content
		}
		// console.log(data);
		request.requestWithToken('/api/comment/publishReply', 'POST', data, (res) => {
			if (res.data.code === 200) {
				Notify({ type: 'success', message: '评论成功' });
				//构建评论对象 
				var zj = {
					id: res.data.data,   //这里后端返回一个id
					commentContent: this.data.confession[outIndex].submitMainComment,
					commentTime: util.currentTime(),
					userName: wx.getStorageSync('userInfo').username,
					avatarURL: wx.getStorageSync('userInfo').avatarURL
				};
				var confession = this.data.confession;
				confession[outIndex].mainComments.push(zj);
				this.setData({
					confession,
					[`confession[${outIndex}].submitMainComment`]: ''
				});
			} else if (res.data.code === 217 || 243) {
				wx.showToast({
					title: res.data.message,
					icon: 'none'
				})
			}
			else {
				Notify({ type: 'danger', message: res.data.message });
				console.log(res);
			}
		}, (res) => {
			console.log(res);
			console.log('评论请求失败');
		});
	},
	//输入评论回复内容
	replyChange(e) {
		this.setData({
			replyContent: e.detail
		});
	},
	//发布子评论
	handleTap(e) {
		const { outIndex, zjId, replyName, mainIndex } = e.currentTarget.dataset;
		//拿到用户名字放到页面data渲染
		this.setData({
			showPopup: true,
			replyName,          //要回复人名
			replyCommentId: zjId, //回复人id
			replyIndex: outIndex,  //要回复的评论的投稿索引
			mainIndex: mainIndex   //要回复的投稿的主评论索引
		})
	},
	// 输入框发送的回调函数
	onConfirm(event) {
		var content = this.data.replyContent;
		if (content === undefined || content.trim() === "") {
			Notify({ type: 'primary', message: '请输入评论' });
			return;
		} else if (content.length > 50) {
			Notify({ type: 'primary', message: '请输入50字以内的评论' });
			return;
		}
		this.setData({ showPopup: false });

		var data = {  //发送请求构建的对象
			confessionPostReviewId: this.data.confession[this.data.replyIndex].id,
			parentCommentId: this.data.replyCommentId,
			commentContent: this.data.replyContent
		}
		// console.log(data);
		request.requestWithToken('/api/comment/publishReply', 'POST', data, (res) => {
			if (res.data.code === 200) {
				wx.showToast({
					title: '评论成功',
				});
				var resId = res.data.data;
				// 可在此处执行提交操作或其他逻辑处理
				var zj = {
					id: resId, //这里后端返回一个id
					parentCommentId: this.data.replyCommentId,
					parentName: this.data.replyName,
					commentContent: this.data.replyContent,
					commentTime: util.currentTime(),
					userName: wx.getStorageSync('userInfo').username,
					avatarURL: wx.getStorageSync('userInfo').avatarURL
				};
				var confession = this.data.confession;

				var mainIndex = this.data.mainIndex;

				if (mainIndex !== -1 && mainIndex !== undefined) {
					confession[this.data.replyIndex].subCommentsExist[mainIndex] = true;
				}
				confession[this.data.replyIndex].subComments.push(zj);//让子评论列表放进去，下次渲染
				// 确保 showComments 是一个数组
				if (!Array.isArray(confession[this.data.replyIndex].mainComments[this.data.mainIndex].showComments)) {
					confession[this.data.replyIndex].mainComments[this.data.mainIndex].showComments = [];
					confession[this.data.replyIndex].mainComments[this.data.mainIndex].commentContentVisible = true;
				}

				// 将 zj 添加到 showComments 数组中
				confession[this.data.replyIndex].mainComments[this.data.mainIndex].showComments.push(zj);

				//重新加载评论
				const updatedConfession = this.LoadComments(this.data.replyIndex, this.data.mainIndex, this.data.confession, false);
				this.setData({ //重构数据+收缩+初始化数据
					confession: updatedConfession,
					showPopup: false,
					replyCommentId: null,
					replyContent: '',
					replyName: '',
					mainIndex: -1,
					replyIndex: -1,
				});
			} else if (res.data.code === 217 || 243) {
				wx.showToast({
					title: res.data.message,
					icon: 'none'
				});
				this.setData({
					replyContent: ''
				})
			} else {
				Notify({ type: 'danger', message: res.data.message });
				this.setData({ //重构数据+收缩+初始化数据
					showPopup: false,
					replyCommentId: null,
					replyContent: '',
					replyName: '',
					mainIndex: -1,
					replyIndex: -1,
				});
				console.log(res);
			}
		}, (res) => {
			console.log(res);
			console.log('评论请求失败');
		});
	},
	onClose() {
		this.setData({
			showPopup: false,
			replyContent: '',
			replyCommentId: null,
			replyName: '',
			mainIndex: -1,
			replyIndex: -1,
		});
	},
	touchstart(e) {
		// console.log('点击开始：',e.timeStamp)
	  this.setData({startTimeStamp:e.timeStamp})
	},
	touchend(e) {
		// console.log('点击结束',e.timeStamp);
	  if(e.timeStamp - this.data.startTimeStamp < 404) {
		  return;
		} else {
		  this.setData({
			postId:e.currentTarget.dataset.postId,
			reportPanelShow:true,
		  });
		}
	},
	onCloseReportPanel(){
		this.setData({
			reportPanelShow:false,
			postId:null,
			reportInfo:''
		});
	},
	submitReportPanel(){
		console.log('提交举报')
		console.log(this.data.postId);
		console.log(this.data.reportInfo);
		let zj={
			reportId:this.data.postId,
			message:this.data.reportInfo
		}
		request.requestWithToken('/api/report/sendReport','POST',zj,(res)=>{
			if (res.data.code===200) {
				Notify({ 
					type: 'success',message: '提交举报成功!',   duration: 2204,
				});
				this.onCloseReportPanel();
			}else if(res.data.code>200){
				Notify({ 
					type: 'warning', message: res.data.message, duration: 2204,
				});
				console.error(res.data);
			}
		},(res)=>{
			console.log(res);
		});
	}
})
