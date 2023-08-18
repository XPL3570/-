Page({

	/**
	 * 页面的初始数据
	 */
  data: {
    mode:"scaleToFill",
    arr:[],
    indicatorDots: true,
    autoplay: true,
    interval: 2000,
    duration: 2000,
  },

  onLoad: function (options) {
  },

  PatrolListView: function (event) {
    wx.navigateTo({
    //   url: '..'
    });
  },

  LeaveRecordView: function (event) {
    wx.navigateTo({
  
    });
  },
  PatrolRecordView: function (event) {
    wx.navigateTo({

    });
  },
  CheckOutView: function (event) {
	wx.switchTab({
	})
  },
	
})