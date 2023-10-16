const { LoginorRegister } = require('./utils/request.js');

App({
	onLaunch: function () {
		this.LoginorRegister();
		//这里是测试，启动的时候加载一个oos秘钥等信息
	},
	LoginorRegister:LoginorRegister,
  globalData: {
	apiUrl: 'http://localhost:2204',
	token:null,
	userInfo:{}
  }
})
