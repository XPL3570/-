var request = require('./request');

//这个接口是上传头像，和图片都要用的所以没有携带token就可以调用
function uploadAndRetrieveImageUrl(filePath) {
	return new Promise(async (resolve, reject) => {
		try {
			const base64Image = await imageToBase64(filePath);
			const avatarUrl = await uploadImage(base64Image);
			resolve(avatarUrl);
		} catch (error) {
			reject(error);
		}
	});
}
//根据url删除图片，这个接口可以在前端或者后端加一个校验，限制接口调用次数，比较危险
function URLDeleteImage(fileUrl) {
	const data = {
		deleteUrl: fileUrl
	}
	request.requestWithToken('/deleteImage', 'POST', data, (res) => {
		// console.log(res);
		if (res.data.code === 200) {
			// console.log('图片删除成功')
		} else {
			// console.log('图片删除失败')
		}
	}, () => {
		console.log('删除图片调用异常');
	});
}

function imageToBase64(filePath) {
	return new Promise((resolve, reject) => {
		wx.getFileSystemManager().readFile({
			filePath: filePath,
			encoding: 'base64',
			success: function (res) {
				// console.log('base64转换成功');
				resolve(res.data);
			},
			fail: function (error) {
				// console.log('base64转换失败');
				reject(error);
			}
		});
	});
}

function uploadImage(base64Data) {
	return new Promise((resolve, reject) => {
		request.requestWithToken('/uploadImage', 'POST', {
			base64Image: base64Data
		},
			function (res) {
				// console.log(res);
				if (res.data.code === 200) {
					resolve(res.data.data);
				} else if (res.data.code === 400) {
					wx.showToast({
						title: '文件过大，无法上传',
						icon: 'none'
					})
				} else {
					reject(new Error('上传图片失败'));
				}
			},
			function (error) {
				reject(error);
			});
	});
}

const formatTime = date => {
	const year = date.getFullYear()
	const month = date.getMonth() + 1
	const day = date.getDate()
	const hour = date.getHours()
	const minute = date.getMinutes()
	const second = date.getSeconds()

	return `${[year, month, day].map(formatNumber).join('/')} ${[hour, minute, second].map(formatNumber).join(':')}`
}

const formatNumber = n => {
	n = n.toString()
	return n[1] ? n : `0${n}`
}

function formatDate(dateString) {  
	// 将日期字符串解析为Date对象  
	const date = new Date(dateString);  
	// 获取当前年份  
	const currentYear = new Date().getFullYear();  
	// 判断年份是否为当前年份  
	let yearOutput;  
	if (date.getFullYear() === currentYear) {  
	  // 如果是今年，只显示月日时分  
	  yearOutput = '';  
	} else {  
	  // 如果不是今年，显示年份  
	  yearOutput = date.getFullYear();  
	}  
	// 获取月份（从0开始，所以加1得到实际的月份）  
	const month = date.getMonth() + 1;  
	// 获取日期  
	const day = date.getDate();  
	// 获取小时  
	const hours = date.getHours();  
	// 获取分钟  
	const minutes = date.getMinutes();  
	// 格式化月、日、时和分，并返回结果  
	return `${yearOutput}${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day} ${hours}:${minutes < 10 ? '0' + minutes : minutes}`;  
  }

function currentTime() {
	let currentDate = new Date();  // 获取当前时间

	let year = currentDate.getFullYear();  // 获取年份
	let month = String(currentDate.getMonth() + 1).padStart(2, '0');  // 获取月份，并补零
	let date = String(currentDate.getDate()).padStart(2, '0');  // 获取日期，并补零
	let hours = String(currentDate.getHours()).padStart(2, '0');  // 获取小时，并补零
	let minutes = String(currentDate.getMinutes()).padStart(2, '0');  // 获取分钟，并补零
	// let seconds = String(currentDate.getSeconds()).padStart(2, '0');  // 获取秒数，并补零 这里不要

	return `${year}-${month}-${date} ${hours}:${minutes}`;  // 格式化时间字符串

}

module.exports = {
	formatTime,
	formatDate:formatDate, //格式化日期，自己的
	uploadAndRetrieveImageUrl: uploadAndRetrieveImageUrl,
	URLDeleteImage: URLDeleteImage,
	currentTime:currentTime
}