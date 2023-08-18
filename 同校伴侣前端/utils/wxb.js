//授权登陆登陆
function login() {
  wx.login({
    success: function (res) {
      if (res.code) {
        console.log(res.code);
        Post('/api/login/index',{
          code:res.code
        },function(data2){
            if(!data2.face || !data2.nick_name){
                wx.getUserInfo({
                  success: function (data) {
                     var weixinInfo =  JSON.parse(data.rawData);
                     console.log(weixinInfo);
                     Post('/api/login/bind', { //绑定用户的头像等
                       openid:data2.open_id,
                       face: weixinInfo.avatarUrl,
                       nick_name: weixinInfo.nickName,
                       sex:weixinInfo.gender
                     },function(info){
                       module.exports.that.setData({
                         userInfo:info
                       });
                        var userInfo = JSON.stringify(info);
                        wx.setStorageSync('userInfo', userInfo);
                     });
                  },fail:function(){
                    toLogin()
                    // wx.showToast({
                    //   image: '/img/kulian.png',
                    //   title: '拒绝了授权',
                    // })
                  }
                });
            }else{
              module.exports.that.setData({
                userInfo: data2
              });
              var userInfo = JSON.stringify(data2);
              wx.setStorageSync('userInfo', userInfo);
            }
        });
      } else {
        wx.showToast({
          image: '/img/kulian.png',
          title: '拒绝了授权',
        })
      }
    }
  });
}

function toLogin(){
  wx.navigateTo({
    url: '/pages/public/login',
  })
}

//检查是否授权登陆了  不使用微信的 checksession来判断 type 等于true 的时候代表需要 SETDATA
function checkAuthLogin(type) {
  var info = wx.getStorageSync('userInfo');
  var userInfo = info ? JSON.parse(info) : {};
  if (!userInfo.open_id) return false;
  var time = Date.parse(new Date());
  //console.log(time / 1000 - userInfo.last_time );
  if(time/1000 - userInfo.last_time > 86400){
    return false;//大于一天了
  }
  if(type==true){
      module.exports.that.setData({
        userInfo: userInfo
      });
  }
  return true;
}
//检查是否手机号登陆
function checkLogin() {
  var info = wx.getStorageSync('userInfo');
  var userInfo = JSON.parse(info);
  if (!userInfo.mobile) return false;
  module.exports.that.setData({
    userInfo: userInfo
  });
  return true;
}

//获取OPENID
function getOpenId(){
  var info = wx.getStorageSync('userInfo');
  var userInfo = info ?  JSON.parse(info): {};
  if (!userInfo.open_id) return 0;
  return userInfo.open_id;
}

//定位当前的城市 会把当前的城市返回给callback
function dingWei(cityList,callback){
  var city = wx.getStorageSync('city');
  var cityinfo = city ?  JSON.parse(city) : {};
  if (!cityinfo.city_id){ //如果缓存里面解析不到城市ID
      for(var a in cityList){
          if(cityList[a].default == 1){
             cityinfo = {
               city_id: cityList[a].city_id,
               city_name:cityList[a].city_name
             };
             break;
          }
      }
      if (!cityinfo.city_id){ //标识没有定位到默认城市
        cityinfo = {
          city_id: cityList[0].city_id,
          city_name: cityList[0].city_name
        };
      }
      module.exports.setCity(cityinfo.city_id,cityinfo.city_name); 
      if (callback != undefined) {
        callback(cityinfo);
      }
  }else{
    var is_city =0;
    for (var a in cityList) {
      if (cityList[a].city_id == cityinfo.city_id) {
        cityinfo = {
          city_id: cityList[a].city_id,
          city_name: cityList[a].city_name
        };
        is_city = 1;
        break;
      }
    }
    if(is_city == 0){
      cityinfo = {
        city_id: cityList[0].city_id,
        city_name: cityList[0].city_name
      };
    }  
    module.exports.setCity(cityinfo.city_id, cityinfo.city_name); 
    if (callback != undefined) {
      callback(cityinfo);
    }
  }
}




//获取日期
function getDateStr(AddDayCount,type) {
  var dd = new Date();
  dd.setDate(dd.getDate() + AddDayCount);//获取AddDayCount天后的日期 
  var y = dd.getFullYear();
  var m = dd.getMonth() + 1;//获取当前月份的日期 
  var d = dd.getDate();

  if (m<10){
    m = "0" +m;
  }
  if(d<10){
    d = "0"+d;
  }
  if(type==1){
    return m + "月" + d+"日";
  }
  return y + "-" + m + "-" + d;
} 


// 全部用 POST
function Post(api, params, callback) {
  // 如果全局变量 globalData.apiurl 不存在
  if (!module.exports.globalData.apiurl) {
    // 通过 wx.getExtConfig 方法获取配置信息
    wx.getExtConfig({
      success: function (res) {
        // 将配置信息赋值给全局变量 globalData
        module.exports.globalData = res.extConfig;
        // 调用 PostMain 函数进行 POST 请求
        PostMain(api, params, callback);
      }
    })
  } else {
    // 如果全局变量 globalData.apiurl 存在，则直接调用 PostMain 函数进行 POST 请求
    PostMain(api, params, callback);
  }
}

function PostMain(api, params, callback) {
  var apiurl = module.exports.globalData.apiurl + api + '?appid=' + module.exports.globalData.appid
    + '&appkey=' + module.exports.globalData.appkey;
  wx.request({
    url: apiurl,
    data: params,
    method: 'POST',
    dataType: 'json',
    success: function (data) {
      console.log(data);
      //wx.hideLoading();
      switch (data.data.code) {
        case 100:
          break;
        case 101:
          break;
        case 200:
          callback(data.data.data);
          break
        default:
          console.log(data.data.msg);
          wx.showToast({
            image: '/img/kulian.png',
            title: data.data.msg,
          })
          break;
      }
    },
    fail: function (data) {
      wx.hideLoading();
      // wx.showToast({
      //   title: '请求接口超时',
      // })
    }
  })
}

//上传图片 
// param img 图片；
function fileupload(mdl = '', callback) {
  var datas = [];
  wx.chooseImage({
    count: 1, // 默认9
    sizeType: ['compressed'], // 可以指定是原图还是压缩图，默认二者都有
    sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
    success: function (res) {
      var tempFilePaths = res.tempFilePaths;
      wx.showLoading({
        title: '图片上传中..',
      })
      wx.uploadFile({
        url: module.exports.globalData.apiurl + '/api/upload/upload', //仅为示例，非真实的接口地址
        filePath: tempFilePaths[0],
        name: 'file',
        formData: {
          'mdl': mdl
        },
        
        success: function (res) {
          wx.hideLoading();
          wx.showToast({
            title: '上传成功',
          });
          var data = JSON.parse(res.data);//你大爷的强制返回字符串；强制转json
          callback(data.data);
        },
        fail: function (res) {
          wx.showToast({
            title: '图片上传中',
          });
        }
      });
    }
  });

}



function setStoreCode(code){
    var store = {
      code:code,
      last_time:Date.parse(new Date())/1000
    };
    wx.setStorageSync('storeinfo', JSON.stringify(store));
    return true;
}

module.exports = {
  login: login,
  checkAuthLogin: checkAuthLogin,
  checkLogin: checkLogin, 
  getOpenId: getOpenId,
  dingWei: dingWei,
  setStoreCode: setStoreCode,
  fileupload: fileupload,
  Post: Post,
  globalData: [],
  that: null,
  lock: 0,//用于其他的锁定
}
