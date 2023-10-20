var request = require('../../utils/request')
var util = require('../../utils/util')
var oos = require('../../utils/oosRequest')
import Toast from '@vant/weapp/toast/toast';
Page({

	data: {
		show: false,
		buttonDisabled: false,  //四个按钮是否可以点击，这里设置每4.4秒可以点击发起请求一次
		showZj: false, //展示纸条
		carton: '', //显示当前的纸箱
		gender: -1,  //性别 0男 1女
		Introduction: '', //介绍
		ContactInfo: '', //微信号
		fileList: [],      //地址
		paper: {},   //抽取到的纸条
		activeTab: 0, // 默认选中我的纸条的tab
		lastLoadTime01: 0, // 我的抽到纸条上一次加载数据的时间  
		lastLoadTime02: 0, // 我的放入纸条上一次加载数据的时间  
		drawnNote: [], //用户抽到的纸条
		insertNote: [], //用户放入的纸条
	},
	disableButton: function() {
		this.setData({ buttonDisabled: true });
		setTimeout(() => {
		  this.setData({ buttonDisabled: false });
		}, 4400);
	  },

	handleOuterTabTap() {
		// 根据activeTab来判断应该调用哪个方法
		if (this.data.activeTab === 0) {
			this.loadData1();
		} else if (this.data.activeTab === 1) {
			this.loadData2();
		}
	},
	loadData1() { //我抽到的纸条
		var currentTime = new Date().getTime();
		if (currentTime - this.data.lastLoadTime02 < 5000) {
			return;
		}
		request.requestWithToken('/api/lotteryRecord/obtainedNote', "GET", null, (res) => {
			if (res.data.code === 200) {
				var dataList = res.data.data.map(function (item) {
					var imageUrl = item.imageUrl;
					var sbzj = imageUrl.split(';');
					item.sbzj = sbzj;
					return item;
				});
				this.setData({
					drawnNote: dataList
				});
				// console.log(this.data.drawnNote);
			} else {
				console.log(res.data);
			}
		}, (res) => {
			console.log(res.data);
		})
		this.setData({
			lastLoadTime02: currentTime
		});
	},
	loadData2() {//我放入的纸条
		var currentTime = new Date().getTime();
		if (currentTime - this.data.lastLoadTime01 < 5000) {
			return;
		}
		request.requestWithToken('/api/lottery/postedNote', 'GET', null, (res) => {
			if (res.data.code === 200) {
				var dataList = res.data.data.map((item) => {
					var imageUrl = item.imageUrl;
					var sbzj = imageUrl.split(';');
					item.sbzj = sbzj;
					return item;
				});
				this.setData({
					insertNote: dataList
				});
				// console.log(this.data.insertNote)
			} else {
				console.log(res.data);
			}
		}, (res) => {
			console.log(res.data);
		})
		this.setData({
			lastLoadTime01: currentTime
		});
	},
	handleTabChange: function (e) {
		// 更新activeTab
		this.setData({
			activeTab: e.detail.index
		});
		this.handleOuterTabTap();
	},
	handleOuterTabChange(e) {
		// 当切换外部 van-tab 时调用的方法
		// e.detail.index 是当前选中的 van-tab 的索引
		if (e.detail.index == 2) { // 判断如果切换到的是"我的纸条"，那么就加载"我抽到的纸条"的内容
			this.handleOuterTabTap();
		}
	},

	handleUserInfoInput(event) {
		this.setData({
			ContactInfo: event.detail
		})
	},
	handleIntroductionInput(event) {
		this.setData({
			Introduction: event.detail
		});
	},
	showPopup() {
		this.setData({ show: true });
	},
	onClose() {
		//调用函数把图片删除了
		var fileList = this.data.fileList;
		if (fileList.length > 0) {
			fileList.forEach(file => {
				util.URLDeleteImage(file.url);
			});
		}
		this.setData({
			show: false,
			carton: '',
			Introduction: '', //介绍
			ContactInfo: '', //微信号
			fileList: []    //地址
		});
	},
	onCloseZj() {
		this.setData({
			showZj: false
		})
	},
	takeOutX() { // 抽男生纸条
		if (this.data.buttonDisabled) {
			return;
		  }
		  this.disableButton();
		this.extractTape(0, '男生');
	},

	takeOutZ() { // 抽女生纸条
		if (this.data.buttonDisabled) {
			return;
		  }
		  this.disableButton();
		this.extractTape(1, '女生');
	},
	extractTape(gender, carton) {
		this.setData({
			gender: gender,
			carton: carton
		});
		var data = {
			schoolId: wx.getStorageSync('userInfo').schoolId,
			gender: this.data.gender
		};
		request.requestWithToken("/api/lotteryRecord/extractTape", "GET", data, (res) => {
			if (res.data.code === 200) {
				var imageUrl = res.data.data.imageUrl;
				var sbzj = imageUrl.split(';');
				res.data.data.sbzj = sbzj;
				this.setData({
					paper: res.data.data,
					showZj: true
				});
				//加载对应的抽到的纸条的数据
				setTimeout(() => {
					this.loadData1();
				  }, 500);
				// console.log(this.data.paper);
			} else {
				Toast.fail(res.data.message);
			}
		}, (res) => {
			console.log(res.data);
		})
	},

	previewImage(event) {
		var index = event.currentTarget.dataset.index;
		wx.previewImage({
			current: this.data.paper.sbzj[index], // 当前显示图片的http链接
			urls: this.data.paper.sbzj // 需要预览的图片http链接列表
		})
	},

	previewImage01(event) {  //用户抽到纸条 图片展示
		var { outerIndex, innerIndex } = event.currentTarget.dataset;
		wx.previewImage({
			current: this.data.drawnNote[outerIndex].sbzj[innerIndex], // 当前显示图片的http链接
			urls: this.data.drawnNote[outerIndex].sbzj // 需要预览的图片http链接列表
		})
	},
	previewImage02(event) {  //用户放入的纸条 图片展示
		var { outerIndex, innerIndex } = event.currentTarget.dataset;
		wx.previewImage({
			current: this.data.insertNote[outerIndex].sbzj[innerIndex], // 当前显示图片的http链接
			urls: this.data.insertNote[outerIndex].sbzj // 需要预览的图片http链接列表
		})
	},

	drawPaperX() {
		this.setData({
			show: true,
			gender: 0,
			carton: '男生'
		});
	},
	drawPaperZ() {
		this.setData({
			show: true,
			gender: 1,
			carton: '女生'
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
		//调用方法删除图片
		util.URLDeleteImage(event.detail.file.url);
		// console.log(this.data.fileList);
	},

	handleSubmit() {
		if (this.data.buttonDisabled) {
			return;
		  }
		  this.disableButton();
		var data = {
			schoolId: wx.getStorageSync('userInfo').schoolId,
			gender: this.data.gender,
			contactInfo: this.data.ContactInfo,
			introduction: this.data.Introduction,
			imageUrl: this.data.fileList.map(item => item.url).join(';')
		};
		if (data.contactInfo.length <= 5) {
			Toast('请输入正确的联系方式');
			return;
		}

		if (data.introduction.length <= 10) {
			Toast('介绍内容长度必须大于10个字符');
			return;
		}
		request.requestWithToken("/api/lottery/paperTape", "POST", data, (res) => {
			if (res.data.code === 200) {
				this.setData({
					show: false,
					carton: '',
					Introduction: '', //介绍
					ContactInfo: '', //微信号
					fileList: [],   //地址
				});
				Toast.success('放入成功!');
				setTimeout(() => {
					this.loadData2();
				  }, 300);
			} else {
				Toast.fail(res.data.message);
			}
		}, (res) => {
			console.log(res);
		})

	}
})