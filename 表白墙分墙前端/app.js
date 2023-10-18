const { LoginorRegister } = require('./utils/request.js');

App({
	onLaunch: function () {
		this.LoginorRegister();
		//这里是测试，启动的时候加载一个oos秘钥等信息
	},
	LoginorRegister:LoginorRegister,
  globalData: {
	// apiUrl: 'https://www.txbbq.xyz:2204',
	apiUrl: 'http://localhost:2204',
	// apiUrl: 'http://172.18.124.117:2204',
	token:null,
	userInfo:{}
  }
})
