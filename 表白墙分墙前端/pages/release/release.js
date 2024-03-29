var util = require('../../utils/util')
var request = require('../../utils/request')
var oos = require('../../utils/oosRequest')
/*可以优化点，数据可以提交然后保存到本地变量，把图片每次都保存，不然后台可能都有无效的图片 */
Page({
	data: {
		// buttonDisabled: false, //按钮是否禁用
		title: '', // 存储投稿标题
		TitleErrorMsg: '',
		content: '', // 存储投稿内容
		fileList: [],
		checked: false, //匿名发布
		isAnonymous: 0, //是否匿名
		maxNumberImageUploads: 2, //最大图片上传数量,默认2，管理员可以3张
		lastClickTime: 0,//上次点击提交时间
	},
	onLoad() {
		if (wx.getStorageSync('isAdmin')) {
			this.setData({
				maxNumberImageUploads: 3
			});
		}
		this.setData({
			title: '', // 初始化投稿标题
			TitleErrorMsg: '',
			content: '', // 初始化投稿内容
			fileList: [] // 初始化文件列表
		});
	},
	onChange(event) {
		const value = event.detail;
		this.setData({
			checked: value,
			isAnonymous: value ? 1 : 0
		});
	},
	handleTitleInput(event) {
		var msg;
		if (event.detail.length > 20) {
			msg = '请输入20字以内的标题';
		} else {
			msg = '';
		}
		this.setData({
			title: event.detail,
			TitleErrorMsg: msg
		});
	},
	handleContentInput(event) {
		this.setData({
			content: event.detail
		});
	},
	// 在选择文件之前触发,判断大小
	beforeRead(event) {
		const { file, callback } = event.detail;
		if (file.type !== 'image') {
			wx.showToast({
				title: '文件类型异常',
				icon: 'error',
			});
			callback(false);
		}
		// 检查文件大小是否超过限制，单位为字节（例如，限制为 2MB）
		if (file.size > 2 * 1024 * 1024) {
			wx.showToast({
				title: '文件大小超过2M',
				icon: 'error',
			});
			// 可以根据需要返回 false 阻止文件上传
			callback(false);
		}
		callback(true);
	},
	// 文件上传完成后的回调函数 这里要往后端发起请求保存返回地址，
	// async afterRead(event) {
	// 	// console.log('afterRead调用')
	// 	const {
	// 		file  
	// 	} = event.detail;   //这个url应该就是本地的图片temp地址 
	// 	const avatarUrl = await util.uploadAndRetrieveImageUrl(file.url);
	// 	const newRecord = {
	// 		url: avatarUrl
	// 	};
	// 	this.data.fileList.push(newRecord);
	// 	this.setData({
	// 		fileList: this.data.fileList,
	// 	});
	// },
	async afterRead(event) { //修改代码
		// console.log('afterRead调用')
		const {
			file
		} = event.detail;   //这个url应该就是本地的图片temp地址 
		oos.uploadImagesAlibabaCloud(file.url, (url) => {
			if (url) {
				const newRecord = {
					url: url
				};
				this.data.fileList.push(newRecord);
				this.setData({
					fileList: this.data.fileList,
				});
			}
		});
	},
	// 删除图片，拿到地址信息往后端发起删除操作
	handleDeleteImage(event) {
		const {
			index
		} = event.detail;
		const fileLists = [...this.data.fileList];
		fileLists.splice(index, 1);
		this.setData({
			fileList: fileLists
		});
		//调用方法删除图片
		util.URLDeleteImage(event.detail.file.url);
		// console.log(this.data.fileList);
	},
	handleSubmit() { //提交
		if (this.validateForm()) { //校验标题和投稿内容
			// this.setData({ //校验通过直接禁用
			// 	buttonDisabled: true
			// });
			//通过校验之后判断是否超过两秒点击的
			const currentTime = Date.now();
			if (currentTime - this.data.lastClickTime < 2000) {
				// 如果距离上次点击的时间小于2秒，则不执行后续操作
				return;
			}
			this.setData({
				lastClickTime:currentTime
			})
			var data = {
				isAnonymous: this.data.isAnonymous,
				wallId: wx.getStorageSync('wall').id,
				title: this.data.title,
				textContent: this.data.content,
				imageURL: this.processFileList(this.data.fileList)
			};

			//todo 这个接口地址修改一下就是管理员的接口了，后面再加，不确定是在这里发布
			request.requestWithToken('/api/confessionPost/submit', 'POST', data, (res) => {
				if (res.data.code === 200) {
					this.setData({
						title: '', // 初始化投稿标题
						TitleErrorMsg: '',
						content: '', // 初始化投稿内容
						fileList: [] // 初始化文件列表
					});
					console.log(res.data);
					wx.setStorageSync('initializeHomepageIdentification', 1);
					setTimeout(() => {
						// 在跳转页面时，将提示信息存储在本地缓存中
						wx.setStorageSync('message', res.data.message);
						// wx.switchTab({
						// 	url: '/pages/index/index'
						// });
						wx.showToast({
							title: res.data.message,
							icon: 'success'
						});
					}, 404);
				} else if (res.data.code === 216) {
					console.log(res.data.message);
					wx.showToast({
						title: '今日投稿已上限',
						icon: 'error'
					});
				} else {
					wx.showToast({
						title: res.data.message,
						icon: 'none'
					});
					console.log(res.data.message);
				}
			}, (res) => {
				console.log('投稿发布异常' + res.data);
				wx.showToast({
					title:'投稿异常，请稍后重试',
					icon:"none"
				})
			})
		}
	},
	// 校验标题和内容
	validateForm: function () {
		// 获取标题和内容
		let title = this.data.title;
		let content = this.data.content;
		// 校验标题长度
		if (title.length <= 3 || title.length > 20) {
			this.setData({
				TitleErrorMsg: '标题长度应在4到20个字哦'
			});
			return false; // 返回校验失败
		}
		// 校验内容是否为空
		if (content.trim() === '') {
			wx.showToast({
				title: '投稿内容不能为空',
				icon: 'none'
			});
			return false; // 返回校验失败
		}
		return true; // 返回校验通过
	},
	processFileList(fileList) {
		return fileList.map(item => item.url).join(';');
	}
});