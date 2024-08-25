import {createRouter, createWebHistory} from 'vue-router'
import store from "@/store";
import {notification} from "ant-design-vue";

const routes = [{
    path: '/login',
    component: () => import('../views/login.vue')
}, {
    // 一级路由
    path: '/',
    component: () => import('../views/main.vue'),
    // 设置登录开关
    meta: {
        loginRequire: true
    },
    // 二级路由, 子路由, path 会和父路由的 path 拼接
    children: [{
        path: 'welcome',
        component: () => import('../views/main/welcome.vue'),
    }, {
        path: 'passenger',
        component: () => import('../views/main/passenger.vue'),
    }, {
        path: 'ticket',
        component: () => import('../views/main/ticket.vue'),
    }, {
        path: 'order',
        component: () => import('../views/main/order.vue'),
    }, {
        path: 'my-ticket',
        component: () => import('../views/main/my-ticket.vue')
    }, {
        path: 'seat',
        component: () => import('../views/main/seat.vue')
    }]
}, {
    // 根路由, 访问根域名的时候会重定向到欢迎页
    path: '',
    redirect: '/welcome'
}];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// 路由登录拦截
router.beforeEach((to, from, next) => {
    // 要不要对meta.loginRequire属性做监控拦截
    if (to.matched.some(function (item) {
        console.log(item, "是否需要登录校验：", item.meta.loginRequire || false);
        return item.meta.loginRequire
    })) {
        const _member = store.state.member;
        console.log("页面登录校验开始：", _member);
        if (!_member.token) {
            console.log("用户未登录或登录超时！");
            notification.error({description: "未登录或登录超时"});
            next('/login');
        } else {
            next();
        }
    } else {
        next();
    }
});

export default router
