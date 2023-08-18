var api = require('../../utils/api');

Page({
  data: {
    avatarUrl: '', // 当前用户的头像URL   可以在页面加载的时候获取，后面做
  },
  onLoad: function () {
	const userInfo =wx.getStorageSync('userInfo');
    if (userInfo) {
      this.setData({
        avatarUrl: userInfo.ico
      });
    }
  },


  // 上传头像按钮点击事件
  uploadAvatar() {
    var that = this;
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath;
        try {
          const base64Data = await this.imageToBase64(tempFilePath);
		  const avatarUrl = await this.uploadImage(base64Data);
		  const app = getApp();
		  app.globalData.userInfo.ico=avatarUrl;
          api.requestWithToken('/webapi/UserApi.ashx', 'POST', {
            businessType: 'api.user.set.ico',
            ico: avatarUrl
          }, function(response) {
            if (response.statusCode === 200) {
              const data = response.data;
              if (data.code === 0) {
                console.log(data.code)
                wx.showToast({
                  title: '修改头像成功',
                  icon: 'success'
				});
				var userInfo = {
					ico: avatarUrl
				  };
			wx.setStorageSync('userInfo', userInfo);
                that.setData({
                  avatarUrl: avatarUrl
				});
              } else {
                wx.showToast({
                  title: '更新头像失败',
                  icon: 'none'
                });
              }
            }
          }, function(error) {
            console.log(error);
          });
        } catch (error) {
          console.log(error)
          wx.showToast({
            title: '上传头像失败',
            icon: 'none'
          });
        }
      },
      fail: (error) => {
        wx.showToast({
          title: '选择图片失败',
          icon: 'none'
        });
      }
    });
  },

  // 将图片转换为base64格式的字符串
  imageToBase64(filePath) {
    return new Promise((resolve, reject) => {
      wx.getFileSystemManager().readFile({
        filePath: filePath,
        encoding: 'base64',
        success: function(res) {
          resolve(res.data);
        },
        fail: function(error) {
          reject(error);
        }
      });
    });
  },

  // 上传图片并获取头像地址
  uploadImage(base64Data) {
    return new Promise((resolve, reject) => {
      api.requestWithToken('/webapi/Upload.ashx', 'POST', {
        businessType: 'api.app.image.upload',
        data: base64Data
      }, function(res) {
        if (res.data.code === 0) {
          resolve(res.data.result.filePath);
        } else {
          reject(new Error('上传图片失败'));
        }
      }, function(error) {
        reject(error);
      });
    });
  }
});