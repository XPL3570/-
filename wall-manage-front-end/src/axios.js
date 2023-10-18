import axios from 'axios'
import router from '@/router'; // 导入Vue Router实例
// const baseURL = process.env.VUE_APP_BASE_API

//用法         const res = await http.get(api.right, params)

const instance = axios.create({
    // baseURL: 'http://www.txbbq.xyz:2204',  todo //记得修改地址
    baseURL: 'http://localhost:2204',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
});

instance.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        // console.log(token);
        if (token) {
            config.headers['authentication'] = token;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

instance.interceptors.response.use(
    response => {
        // console.log(response);
        if (response.data.code === 214) {
            router.push({ path: '/login' });
        }
        return response;
    },
    error => {
        return Promise.resolve(error.response);
    }
);

function checkCode(res) {
    if (res){
        if (res.data && !res.data.ok) {
            console.error(res.data.message);
        }
    }

    return res;
}

export default {
    post(url, data) {
        return instance
            .post(url, JSON.stringify(data))
            .then(res => {
                return checkCode(res);
            });
    },
    get(url, params) {
        return instance
            .get(url, { params })
            .then(res => {
                return checkCode(res);
            });
    }
};
