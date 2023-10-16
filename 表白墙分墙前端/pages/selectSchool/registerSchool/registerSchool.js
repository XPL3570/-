var util = require('../../../utils/util')
var oos = require('../../../utils/oosRequest');
var request = require('../../../utils/request')
import Dialog from '@vant/weapp/dialog/dialog';

Page({
	data: {
		schoolName: '',
		description: '',
		wechatNumber: '',
		phoneNumber: '',
		schoolNameError: '',
		descriptionError: '',
		wechatNumberError: '',
		phoneNumberError: '',
		fileList: []
	},

	onLoad(options) {
		const schoolName = options.schoolName;
		this.setData({
			schoolName
		})
	},
	onInputSchoolName(event) {
		var schoolName = event.detail;
		var schoolNameError = '';
		if (schoolName.length < 4) {
			schoolNameError = '学校名称的长度不能少于4';
		} else if (schoolName.length > 50) {
			schoolNameError = '学校名称的长度不能超过50';
		}
		this.setData({
			schoolName: schoolName,
			schoolNameError: schoolNameError,
		});
	},

	onInputDescription(event) {
		this.setData({
			description: event.detail,
			descriptionError: event.detail.length > 1000 ? '学校描述的长度不能超过1000' : '',
		});
	},
	onInputWechatNumber(event) {
		this.setData({
			wechatNumber: event.detail,
			wechatNumberError: event.detail.length < 6 || event.detail.length > 20 ? '微信号的长度必须在6到20之间' : '',
		});
	},
	onInputPhoneNumber(event) {
		var phoneNumber = event.detail;
		var phoneNumberError = '';
		if (phoneNumber.length !== 11) {
			phoneNumberError = '请输入完整的手机号';
		} else if (!/^1[34578]\d{9}$/.test(phoneNumber)) {
			phoneNumberError = '手机号格式不正确';
		}
		this.setData({
			phoneNumber: phoneNumber,
			phoneNumberError: phoneNumberError,
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
	async afterRead(event) {
		// console.log('afterRead调用')
		const {
			file
		} = event.detail;
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
	},
	onSubmit() {
		if (this.data.schoolNameError || this.data.descriptionError || this.data.wechatNumberError || this.data.phoneNumberError || !this.data.fileList[0]) {
			wx.showToast({
				title: '请填写数据完整后再提交',
				icon: 'none',
			});
			return;
		}
		var data = {
			avatarURL: this.data.fileList[0].url,
			schoolName: this.data.schoolName,
			description: this.data.description,
			wechatNumber: this.data.wechatNumber,
			phoneNumber: this.data.phoneNumber,
		};
		// 获取全局变量
		request.requestWithToken('/api/school/register', 'POST', data, (res) => {
			if (res.data.code === 200) {
				//保存schoolId并跳转到表白墙
				try {
					wx.setStorageSync('schoolId', res.data.data);
					console.log('学校id存储成功');
				} catch (e) {
					console.log('学校id数据存储失败');
				}
				Dialog.alert({
					title: '请注册表白墙',
					message: '提交入驻申请成功，等待管理员审核，请注册学校的表白墙',
					theme: 'round-button',
				}).then(() => {
					wx.navigateTo({
						url: '/pages/selectSchool/registerWall/registerWall',
					});
				});
			} else if (res.data.code === 210) {
				Dialog.alert({
					message: '学校已经入驻或者正在等待审核! 您可以入驻其他学校或者选择其他学校登录',
				}).then(() => {
					// on close
				});
			} else if (res.data.code === 257) {
				Dialog.alert({
					message: res.data.message,
				}).then(() => {
				});
			} else {
				console.log(res.data)
			}
		}, (res) => {
			console.log(res.data);
		});

	},
});
