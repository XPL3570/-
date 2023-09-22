import axios from 'axios';

// 创建一个Axios实例
const zj = axios.create({
    baseURL: 'https://localhost:2204', // 设置请求的基础URL
    timeout: 5000, // 设置请求超时时间
});

// 添加请求拦截器
zj.interceptors.request.use(
    function (config) {
        // 在发送请求之前做些什么

        // 获取Token
        const token = localStorage.getItem('token');

        // 如果Token存在，则将Token添加到请求头中
        if (token) {
            config.headers['authorization'] = `PREFIX_${token}`;
        }

        return config;
    },
    function (error) {
        // 对请求错误做些什么
        return Promise.reject(error);
    }
);


// 添加响应拦截器
zj.interceptors.response.use(
    function (response) {
        // 对响应数据做些什么

        // 如果返回的响应值为222，则跳转到登录页面
        if (response.data.code === 222) {
            // 跳转到登录页面
            window.location.href = '/login';

            // 抛出一个错误，以中断Promise链
            return Promise.reject(new Error('登录状态过期'));
        }

        return response;
    },
    function (error) {
        // 对响应错误做些什么
        return Promise.reject(error);
    }
);

export default zj;
