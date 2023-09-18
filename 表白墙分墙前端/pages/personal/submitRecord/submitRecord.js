var request = require('../../../utils/request');

Page({
  data: {
    activeNames: ['1'],
    list: [],
    canLoadMore: true, // 是否可以继续加载更多数据
    page: 1, // 当前页数
    limit: 10, // 每页数据条数
  },
  onChange(event) {
    this.setData({
      activeNames: event.detail,
    });
  },
  onLoad() {
    this.loadData();
  },

  loadData() {
    if (!this.data.canLoadMore) {
		wx.showToast({
		  title: '没有更多数据了哦！',
		  icon:'none'
		});
      return; // 如果不能继续加载更多数据，则直接返回
    }
    request.requestWithToken('/api/confessionPost/published', 'GET', { page: this.data.page, limit: this.data.limit }, (res) => {

      const newData = res.data.data;
      const newList = this.data.list.concat(newData);
      this.setData({
        list: newList,
        canLoadMore: newData.length >= this.data.limit, // 判断是否可以继续加载更多数据
        page: this.data.page + 1, // 更新页数
      });
    }, () => {
      console.log(res)
    })
  },
  onReachBottom() { //注意这个函数的触发时机，后面要触底，上拉才会触发，如果数据不够是不会调用的
    this.loadData();
  },
  handleImageTap(event) {
    const index = event.currentTarget.dataset.index;
	const image = event.currentTarget.dataset.image;
	const images=this.data.list[index].imageURL;
    wx.previewImage({
      current: image, // 当前显示图片的链接
      urls:images // 需要预览的图片链接列表
	})
}
})