var api = require('./request');

const date = new Date();
date.setHours(date.getHours() + 1);
const policyText = {
	expiration: date.toISOString(), // 设置policy过期时间。
	conditions: [
		// 限制上传大小。
		["content-length-range", 0, 2 * 1024 * 1024],
	],
};

//使用回调函数让调用方拿到url
function uploadImagesAlibabaCloud(filePathTemp, callback) {
	const allowedExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp'];
	const fileExtension = filePathTemp.substring(filePathTemp.lastIndexOf('.')).toLowerCase();
	if (!allowedExtensions.includes(fileExtension)) {
		wx.showToast({
			title: '不支持上传该类型的图片',
			icon: 'none'
		})
		return; // 结束方法
	}
	let keyTemp = {};
	api.requestWithToken('/getStsToken', 'GET', null,
		(res) => {
			if (res.data.code === 200) {
				keyTemp = res.data.data;
				// console.log(keyTemp);
				let host = keyTemp.host;
				let signature = keyTemp.signature;
				let OSSAccessKeyId = keyTemp.accessid;
				let policy = keyTemp.policy;
				let key = keyTemp.dir;
				// const securityToken = keyTemp.securityToken;
				wx.uploadFile({
					url: host,
					filePath: filePathTemp,// 待上传文件的文件路径。
					name: 'file', // 必须填file。
					formData: {
						host,
						key,
						policy,
						OSSAccessKeyId,
						signature,
						// 'x-oss-security-token': securityToken // 使用STS签名时必传。
					},
					success: (res) => {
						callback(keyTemp.host + '/' + keyTemp.dir);
					},
					fail: err => {
						wx.showToast({
							title: '上传图片异常',
							icon: 'none'
						})
						console.log(err);
						callback(null); // 或传递错误信息
					}
				});
			} else if (res.data.code === 244) {
				wx.showToast({
					title: res.data.message,
					icon: 'none',
					expiration:3500
				})
			}
			else {
				console.error(res.data);
				callback(null); // 或传递错误信息
			}
		},
		(res) => {
			console.log(res);
		});
}

module.exports = {
	uploadImagesAlibabaCloud: uploadImagesAlibabaCloud
};



