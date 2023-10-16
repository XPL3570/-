var util = require('../../../utils/util')
var request = require('../../../utils/request')
import Dialog from '@vant/weapp/dialog/dialog';

Page({
	data: {
		schoolId: null,
		ConfessionWallName: '',
		ConfessionWallNameError: '',
		description: '',
		descriptionError: '',
		fileList: []
	},
	onLoad() {
		//这里登录的时候学校id会在userInfo里面
		this.setData({
			schoolId: wx.getStorageSync('schoolId') 
		})
	},
	onInputConfessionWallName: function (event) {
		var ConfessionWallName = event.detail;
		var ConfessionWallNameError = '';
		if (!ConfessionWallName) {
			ConfessionWallNameError = '表白墙名字不能为空';
		} else if (ConfessionWallName.length < 5 || ConfessionWallName.length > 20) {
			ConfessionWallNameError = '长度必须在5到20个字符之间';
		}
		this.setData({
			ConfessionWallName: ConfessionWallName,
			ConfessionWallNameError: ConfessionWallNameError
		});
	},

	onInputDescription: function (event) {
		var description = event.detail;
		var descriptionError = '';
		if (!description) {
			descriptionError = '表白墙描述不能为空';
		}
		this.setData({
			description: description,
			descriptionError: descriptionError
		});
	},
	beforeRead(event) {
		const { file, callback } = event.detail;
		if (file.type !== 'image') {
			wx.showToast({
				title: '文件类型异常',
				icon: 'error',
			});
			callback(false);
		}
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
	async afterRead(event) {
		const {
			file
		} = event.detail;

		oos.uploadImagesAlibabaCloud(file.url,(url)=>{
			if (url) {
				const newRecord = {
					url: url
				};
				this.data.fileList.push(newRecord);
				this.setData({
					fileList: this.data.fileList,
				});
			}
		})		
	},
	handleDeleteImage(event) {
		const {
			index
		} = event.detail;
		const fileLists = [...this.data.fileList];
		fileLists.splice(index, 1);
		this.setData({
			fileList: fileLists
		});
		util.URLDeleteImage(event.detail.file.url);
	},

	onSubmit: function () {
		if (!this.data.schoolId || this.data.ConfessionWallNameError || this.data.descriptionError || !this.data.fileList[0]) {
			console.log(this.data)
			wx.showToast({
				title: '请完整填写数据',
				icon: 'none',
				duration: 2000
			});
			return;
		}
		var data = {
			schoolId: wx.getStorageSync('schoolId'),
			avatarURL: this.data.fileList[0].url,
			confessionWallName: this.data.ConfessionWallName,
			description: this.data.description
		};
		// console.log(data);
		request.requestWithToken('/api/confession/register','POST',data,(res) => {
			if (res.data.code === 200) {
				const beforeClose = (action) =>
					new Promise((resolve) => {
						setTimeout(() => {
							if (action === 'confirm') {
								resolve(true);
							} else {
								// 拦截取消操作
								resolve(false);
							}
						}, 2404);
					});
				Dialog.alert({
					title: '注册表白墙成功！',
					message: '注册表白墙成功，请耐心等待管理员审核',
					theme: 'round-button',
					beforeClose,
				}).then(() => {
					this.setData({
						schoolId: null,
						ConfessionWallName: '',
						ConfessionWallNameError: '',
						description: '',
						descriptionError: '',
						fileList: []
					});
					wx.switchTab({
						url: '/pages/index/index'
					});
				});
			} else {
				console.log(res.data)
			}
		},(res) => {
			console.log(res.data);
		});
	}
});
