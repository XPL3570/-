const { LoginorRegister } = require('./utils/request.js');

App({
	onLaunch: function () {
		wx.setStorageSync('initializeHomepageIdentification', 0); //初始化首页标识
		this.LoginorRegister();
	},
	LoginorRegister:LoginorRegister,
  globalData: {
	// apiUrl: 'https://www.txbbq.xyz',
	apiUrl: 'http://localhost:2204',
	// apiUrl: 'http://172.18.124.117:2204',
	token:null,
	userInfo:{}
  }
})
