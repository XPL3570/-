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
function uploadImagesAlibabaCloud(filePathTemp,callback) {
	let keyTemp = {};
	api.requestWithToken('/getStsToken', 'GET', null,
		(res) => {
			// console.log(res);
			if (res.data.code === 200) {
				keyTemp = res.data.data;
				//这里下面还有一次过滤，这里数据是从后端拿的，用的阿里云的sdk，先试试不用转换的
				const host = keyTemp.host;
				const signature = keyTemp.signature;
				const ossAccessKeyId = keyTemp.accessKeyId;
				const policy = keyTemp.policyBase64Str;
				const key = keyTemp.key;
				const securityToken = keyTemp.securityToken;
				const filePath = filePathTemp; // 待上传文件的文件路径。
				wx.uploadFile({
					url: host,
					filePath: filePath,
					name: 'file', // 必须填file。
					formData: {
						key,
						policy,
						OSSAccessKeyId: ossAccessKeyId,
						signature,
						'x-oss-security-token': securityToken // 使用STS签名时必传。
					},
					success: (res) => {
						// console.log(res);
						// if (res.statusCode === 204) {
						// 	console.log('上传成功');
						// }
						callback(keyTemp.host + '/' + keyTemp.key);
					},
					fail: err => {
						console.log(err);
						callback(null); // 或传递错误信息
					}
				});
			} else {
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



