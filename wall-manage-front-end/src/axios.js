import axios from 'axios'

//用法         const res = await http.get(api.right, params)
axios.interceptors.request.use(config => {
    // loading
    const token = localStorage.getItem('token'); // 从本地变量中获取token
    if (token) {
        config.headers['authentication'] = token; // 添加authorization字段到请求头
    }
    return config
}, error => {
    return Promise.reject(error)
})

axios.interceptors.response.use(response => {
    return response
}, error => {
    return Promise.resolve(error.response)
})


function checkCode(res) {
    if (res.data && (!res.data.ok)) {
        // alert(res.data.message)
        console.error(res.data.message);
    }
    return res
}

export default {
    post(url, data) {
        return axios({
            method: 'post',
            baseURL: 'http://localhost:2204',
            url,
            data: JSON.stringify(data), // 将数据转为JSON格式
            timeout: 10000,
            headers: {
                'Content-Type': 'application/json' // 设置Content-Type为application/json
            }
        }).then(
            (res) => {
                return checkCode(res)
            }
        )
    },
    get(url, params) {
        return axios({
            method: 'get',
            baseURL: 'http://localhost:2204',
            url,
            params, // get 请求时带的参数
            timeout: 10000,
            headers: {}
        }).then(
            (res) => {
                return checkCode(res)
            }
        )
    }
}
