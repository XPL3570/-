const { LoginorRegister } = require('./utils/request.js');

App({
	onLaunch: function () {
		this.LoginorRegister();
	},
	LoginorRegister:LoginorRegister,
  globalData: {
	apiUrl: 'https://www.txbbq.xyz',
	// apiUrl: 'http://localhost:2204',
	// apiUrl: 'http://172.18.124.117:2204',
	token:null,
	userInfo:{}
  }
})
