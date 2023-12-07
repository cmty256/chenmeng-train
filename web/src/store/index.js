import { createStore } from 'vuex'

// 声明变量
const MEMBER = "MEMBER";

export default createStore({
  // 创建全局变量
  state: {
    // 从会话存储中获取值
    member: window.SessionStorage.get(MEMBER) || {}
  },
  getters: {
  },
  // 给变量赋值, _member 是外部传进来的参数
  mutations: {
    setMember (state, _member) {
      state.member = _member;
      // 存入会话存储, 设置值
      window.SessionStorage.set(MEMBER, _member);
    }
  },
  // 异步
  actions: {
  },
  // 模块化, 可将上面的函数放进里面进行模块化使用
  modules: {
  }
})
