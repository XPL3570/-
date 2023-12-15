var request = require('../../utils/request')
var util = require('../../utils/util')
import Notify from '@vant/weapp/notify/notify';
import Dialog from '@vant/weapp/dialog/dialog';

Page({
	data: {
		startTimeStampreport: 0, //记录点击投稿 触发举报的时间
		timer: null, //点击投稿的归零定时器
		startTimeStampUser: 0,//点击用户记录时间
		title: '', //墙名字
		prompt: '欢迎来到同校表白墙！        您的投稿是我不懈的动力！',
		swiperOptions: {  //轮播图参数
			indicatorDots: true,
			autoplay: true,
			interval: 2000,
			duration: 1000,
		},
		addUserInfo: {},
		addUserShow: false,//添加好友参数展示
		applicationReason: '', //添加好友申请理由
		reportPanelShow: false,
		postId: null, //举报id
		reportInfo: '', //举报信息
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
	onLoad() {
		console.log('index的onLoad函数触发');
		//  this.initializeHomepageData();
	},
	async initializeHomepageData() {
		// console.log("初始化首页数据方法调用");
		this.setData({
			page: 1,
			limit: 10,
			canLoadMore: true,
			confession: []
		});
		//获取首页数据和表白数据，分开哦
		this.setData({
			title: wx.getStorageSync('wall').wallName
		});

		// 获取轮播图和提示语
		if (wx.getStorageSync('userInfo').schoolId === undefined) {
			return;
		}
		var sbzj = {
			schoolId: wx.getStorageSync('userInfo').schoolId
		};
		try {
			const res = await request.requestWithTokenPromise("/api/school/getIndexInfo", "GET", sbzj);
			if (res.data.code === 200) {
				this.setData({
					prompt: res.data.data.prompt,
					swiperData: res.data.data.carouselImages
				});
			} else {
				console.error('获取首页信息失败了');
				console.log(res);
			}
		} catch (error) {
			console.error(error);
		}
		console.log("初始化首页数据方法调用-zj");
		this.loadData(); // 使用await等待this.loadData()方法的异步操作完成

	},
	loadData() {
		// console.log(this.data.confession);
		// console.log('分页参数页数：', this.data.page);
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
					return this.processConfessionData(item);//这里是处理某一个投稿数据
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
			} else {
				wx.showToast({
					title: '数据获取异常',
					icon: 'error'
				});
				console.log(res.data);
			}
		})
	},
	processConfessionData(item){ //初始化后一个投稿数据的方法
		const subCommentsExist = item.mainComments.map((mainComment) => { //拿到主评论去找子评论找有没有匹配的
			const matchingSubComments = item.subComments.filter(subComment => subComment.parentCommentId === mainComment.id);
			return matchingSubComments.length > 0;
		});
		//优化点，这里是把所有的数据在这里过滤了，但是不展开的数据可能不要用，这里不管
		const updatedMainComments = item.mainComments.map((mainComment) => ({
			...mainComment,
			commentTime: util.formatDate(mainComment.commentTime),
			commentContentVisible: false,
		}));
		const updatedSubComments = item.subComments.map((subComment) => ({
			...subComment,
			commentTime: util.formatDate(subComment.commentTime),
		}));
		let formattedpublishTime = util.formatDate(item.publishTime);
		return {
			...item,
			publishTime: formattedpublishTime,
			subComments: updatedSubComments,
			mainComments: updatedMainComments,
			subCommentsExist,
		};
	},
	onReachBottom() {//上拉获取新的数据
		this.loadData();
	},
	onPullDownRefresh() {//下拉刷新页面
		console.log('下拉方法触发')
		this.initializeHomepageData();
		wx.stopPullDownRefresh();
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
		// 初始化首页标识
		var init = wx.getStorageSync('initializeHomepageIdentification');
		// console.log('init=',init);
		if (init === 1) {
			this.initializeHomepageData();
			// console.log('aaaaaaaa');
			wx.setStorageSync('initializeHomepageIdentification', 0);
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
	//回复主评论
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
	//长按主评论删除
	handleLongTap(event) {
		// 处理长按操作
		let userId = event.currentTarget.dataset.userId;
		if (userId === wx.getStorageSync('userInfo').id) {
			Dialog.confirm({
				title: '评论',
				message: '确定要删除这条评论吗？',
			})
				.then(() => {  //todo 就对接接口了	
					let zjId = event.currentTarget.dataset.zjId;
					let outIndex = event.currentTarget.dataset.outIndex;
					let mainIndex = event.currentTarget.dataset.mainIndex;
					let postId=this.data.confession[outIndex].id;
					let isMain=event.currentTarget.dataset.isMain;
					let commentId=event.currentTarget.dataset.commentId;
					let sbzj={
						postId,commentId,isMain
					};
					// console.log(sbzj);
					request.requestWithToken('/api/comment/deleteComment','POST',sbzj,(res)=>{
						if (res.data.code===200) {
							Notify({ type: 'success', message: '删除评论成功' });
							if(isMain){//判断是否是主评论，删除逻辑会不一样
								let newConfession=this.data.confession;
								newConfession[outIndex].mainComments.splice(mainIndex, 1);
								newConfession[outIndex]=this.processConfessionData(newConfession[outIndex])
								this.setData({
									confession:	newConfession
								});
								console.log("删除评论主成功！");
							}else{//删除子评论
								let newConfession=this.data.confession;
								newConfession[outIndex].subComments = newConfession[outIndex].subComments.filter(comment => comment.id !== zjId);
								newConfession[outIndex]=this.processConfessionData(newConfession[outIndex]);
								console.log(newConfession[outIndex]);
								if(newConfession[outIndex].subCommentsExist[mainIndex]){//有子评论就展开
									this.expandComments({
										currentTarget: {
										  dataset: {
											outIndex:outIndex,
											zjIndex:mainIndex
										  }
										}
									  });
								}
								// console.log(this.datNotify({ type: 'warning', message: '通知内容' });a.confession[outIndex]);
								this.setData({
									confession:	newConfession
								});
							}
						}else if(res.data.code>200){
							Notify({ type: 'warning', message: res.data.message });
						}else{
							console.error(res.data);
						}
					},(res)=>{
						console.error(res);
					});
					// console.log(this.data.confession[outIndex]);
				})
				.catch(() => {console.log('取消删除主评论')});
		}
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
					avatarURL: wx.getStorageSync('userInfo').avatarURL,
					userId:wx.getStorageSync('userInfo').id
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
	touchReport(e) {
		// this.setData({ startTimeStamp: e.timeStamp })
		const currentTime = new Date().getTime();
		const lastTapTime = this.data.startTimeStampreport;
		const interval = currentTime - lastTapTime;
		// 如果两次点击的时间间隔小于300ms，则认为是双击事件
		if (interval < 244) {
			// console.log(e.currentTarget.dataset.outIndex);
			let userId = e.currentTarget.dataset.userId;
			let postId = e.currentTarget.dataset.postId;
			if (userId === wx.getStorageSync('userInfo').id) {
				Dialog.confirm({
					title: '删除投稿',
					message: '确定要删除您发布的这条投稿吗？',
				})
					.then(() => {
						console.log('触发删除接口')
						let zj = {
							postId: postId,
							wallId: e.currentTarget.dataset.wallId
						};
						request.requestWithToken('/api/confessionPost/delete', 'POST', zj, (res) => {
							if (res.data.code === 200) {
								let index = e.currentTarget.dataset.outIndex; // 要删除的下标
								if (index >= 0 && index < this.data.confession.length) {
									let newConfession = this.data.confession.slice(); // 复制一份原数组
									newConfession.splice(index, 1); // 删除指定下标的元素
									this.setData({
										confession: newConfession // 更新data中的array
									});
									console.log(this.data.confession);
									console.log(newConfession)
								}
								wx.showToast({
									title: '删除成功!',
									icon: 'success'
								});
							} else if (res.data.code > 200) {
								wx.showToast({
									title: res.data.message,
									icon: 'none'
								});
							} else {
								console.error(res);
							}
						}, (res) => {
							console.error(res);
						})
					})
					.catch(() => { });
			} else {
				// 设置举报投稿id和展开举报面板
				this.setData({
					postId: postId,
					reportPanelShow: true,
				});
			}
		} else {
			// 如果不是双击事件，则更新lastTapTime的值，并启动一个计时器
			this.setData({
				startTimeStampreport: currentTime
			});

			// 设置一个300ms的定时器，在定时器结束后清空lastTapTime的值
			clearTimeout(this.data.timer);
			this.data.timer = setTimeout(() => {
				this.setData({
					lastTapTime: 0
				});
				// console.log('定时器触发，时间归零');
			}, 300);
		}
	},
	touchend(e) {
		// console.log('点击结束',e.timeStamp);
		if (e.timeStamp - this.data.startTimeStamp < 404) {
			return;
		} else {

		}
	},
	touchUserStart(e) {
		this.setData({ startTimeStampUser: e.timeStamp })
	},
	touchUserEnd(e) {
		if (wx.getStorageSync('userInfo').id === e.currentTarget.dataset.userId) { //判断这是不是自己发的
			return;
		}
		if (e.timeStamp - this.data.startTimeStampUser < 404) {
			return;
		} else {
			this.setData({  //长按用户头像或名字发你获取联系方式的请求
				addUserInfo: {
					userId: e.currentTarget.dataset.userId,
					username: e.currentTarget.dataset.userName
				},
				addUserShow: true
			})
			console.log(this.data.addUserInfo);
		}
	},
	onCloseReportPanel() {
		this.setData({
			reportPanelShow: false,
			postId: null,
			reportInfo: ''
		});
	},
	submitReportPanel() {
		let zj = {
			reportId: this.data.postId,
			message: this.data.reportInfo
		}
		request.requestWithToken('/api/report/sendReport', 'POST', zj, (res) => {
			if (res.data.code === 200) {
				Notify({
					type: 'success', message: '提交举报成功!', duration: 2204,
				});
				this.onCloseReportPanel();
			} else if (res.data.code > 200) {
				Notify({
					type: 'warning', message: res.data.message, duration: 2204,
				});
				console.error(res.data);
			}
			this.onCloseReportPanel();
		}, (res) => {
			console.log(res);
		});
	},
	submitApplication() { //提交好友申请
		//因为输入框配置了输入的长度，这里判断一下输入的最短的就可以了
		if (this.data.applicationReason.length < 4) {
			wx.showToast({
				title: '您要输入最短4个字的申请理由哦！',
				icon: 'none'
			});
			return;
		}
		let zj = {
			receiverId: this.data.addUserInfo.userId,
			applicationReason: this.data.applicationReason
		};
		request.requestWithToken('/api/userContact/obtainContact', 'POST', zj, (res) => {
			// console.log(res.data);
			if (res.data.code === 200) {
				Notify({
					type: 'success', message: '发送成功!', duration: 3500,
				});
				this.onCloseaddUserInfo();

			} else if (res.data.code > 200) {
				Notify({
					type: 'warning', message: res.data.message, duration: 4040,
				});
				this.onCloseaddUserInfo();
			}

		}, (res) => {
			console.error(res);
		});
	},
	onCloseaddUserInfo() {
		this.setData({ addUserShow: false, addUserInfo: {}, applicationReason: '' });
	}
})
