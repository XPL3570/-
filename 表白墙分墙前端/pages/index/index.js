
const app = getApp()

Page({
  data: {
	value: "",
	swiperOptions:{
		indicatorDots:true,
		autoplay:true,
		interval:2000,
		duration:1000,
	
	},
	swiperData:[
		'../../image/Carousel/1.jpg',
		'../../image/Carousel/2.jpg',
		'../../image/Carousel/3.jpg'
	],
    imageUrls: [
		'../../image/Carousel/1.jpg',
		'../../image/Carousel/2.jpg'
	  ],
	  page:1,
	  goodsData:[]
  },
  onLoad(){
  }
})
