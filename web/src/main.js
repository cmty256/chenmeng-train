import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd, {notification} from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import * as Icons from '@ant-design/icons-vue';
import axios from 'axios';
import './assets/js/enums';

// main.js 是整个项目的入口文件，主要负责初始化和配置整个 Vue 应用程序

const app = createApp(App);
app.use(Antd).use(store).use(router).mount('#app');

// 全局使用图标
const icons = Icons;
for (const i in icons) {
  app.component(i, icons[i]);
}

/**
 * 配置axios全局 请求 拦截器, 方便打印日志查看
 */
axios.interceptors.request.use(function (config) {
  console.log('请求参数：', config);

  // 设置请求时带上 token
  const _token = store.state.member.token;
  if (_token) {
    config.headers.token = _token;
    console.log("请求headers增加token:", _token);
  }

  return config;
}, error => {
  return Promise.reject(error);
});

/**
 * 配置axios全局 响应 拦截器, 方便打印日志查看
 */
axios.interceptors.response.use(function (response) {
  console.log('返回结果：', response);
  return response;
}, error => {
  console.log('返回错误：', error);
  const response = error.response;
  const status = response.status;

  // 判断状态码是401 跳转到登录页
  if (status === 401) {
    console.log("未登录或登录超时，跳到登录页");
    store.commit("setMember", {});
    notification.error({ description: "未登录或登录超时" });
    router.push('/login');
  }

  return Promise.reject(error);
});

// 给axios添加基础URL, 配置之后所有axios请求都会自动带上这个URL
axios.defaults.baseURL = process.env.VUE_APP_SERVER;
// 打印当前环境配置
console.log('环境：', process.env.NODE_ENV);
console.log('服务端：', process.env.VUE_APP_SERVER);

