var request = require('../../utils/request')
var uril = require('../../utils/util')

Page({
	data: {
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
	// 加载和处理数据
	onLoad() {
		const data = {
			wallId: wx.getStorageSync('wall').id,
			recordAfterTime: Math.floor(Date.now() / 1000), // 获取当前时间的时间戳
			pageSize: 2
		};
		request.requestWithToken("/api/confessionPost/readConfessionWall", "POST", data, (res) => {
			if (res.data.code === 200) {
				const updatedConfession = res.data.data.map((item) => { //遍历源数据
					const subCommentsExist = item.mainComments.map((mainComment) => { //拿到主评论去找子评论找有没有匹配的
						const matchingSubComments = item.subComments.filter(subComment => subComment.parentCommentId === mainComment.id);
						return matchingSubComments.length > 0;
					});
					const updatedMainComments = item.mainComments.map((mainComment) => ({
						...mainComment,
						commentContentVisible: false,
					}));
					return {
						...item,
						mainComments: updatedMainComments,
						subCommentsExist,
					};
				});
				this.setData({
					confession: updatedConfession,
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
				// console.log(res.data);
			}
		})
	},
	//处理展开数据
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
			wx.showToast({
				title: '请输入评论',
				icon: 'none'
			});
			return;
		} else if (content.length > 20) {
			wx.showToast({
				title: '请输入20个字以内的评论',
				icon: 'none'
			});
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
				wx.showToast({
					title: '评论成功',
				});
				//构建评论对象 
				var zj = {
					id: res.data.data,   //这里后端返回一个id
					commentContent: this.data.confession[outIndex].submitMainComment,
					commentTime: uril.currentTime(),
					userName: wx.getStorageSync('userInfo').username,
					avatarURL: wx.getStorageSync('userInfo').avatarURL
				};
				var confession = this.data.confession;
				confession[outIndex].mainComments.push(zj);
				this.setData({
					confession,
					[`confession[${outIndex}].submitMainComment`]: ''
				});
			} else if (res.data.code === 217) {
				wx.showToast({
					title: res.data.message,
					icon: 'none'
				})
			} else {
				wx.showToast({
					title: '请稍后重试',
				});
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
					commentTime: uril.currentTime(),
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
			}else if (res.data.code === 217) {
				wx.showToast({
					title: res.data.message,
					icon: 'none'
				})
			} else {
				wx.showToast({
					title: '请稍后重试',
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

})

