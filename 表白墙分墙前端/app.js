const { LoginorRegister } = require('./utils/request.js');
App({
	onLaunch: function () {
		this.LoginorRegister();
	},
	LoginorRegister:LoginorRegister,
  globalData: {
	apiUrl: 'http://localhost:2204',
	token:null,
	userInfo:{}
  }
})
