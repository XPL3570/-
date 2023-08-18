function requestWithToken(url, method, data, successCallback, failCallback) {
	// 发送请求
	wx.request({
	  url: apiUrl + url,
	  method: method,
	  header: {
		'my-token': token,
		'content-type': 'application/json'
	  },
	  data: successCallback,
	  fail: failCallback
	});
  }

  module.exports = {
	requestWithToken: requestWithToken,
  };