package com.confession.globalConfig.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.confession.comm.JwtConfig;
import com.confession.pojo.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 解析并验证JWT
        Integer idByJwtToken = JwtConfig.getIdByJwtToken(request);

        // 将解析后的信息保存在ThreadLocal中，以便后续的Controller使用
        ThreadLocal<User> UserThreadLocal = new ThreadLocal<>();
        User user = new User();
        //这里默认只放了id，理论可以是Integer，放User是为了后续扩展
        user.setId(idByJwtToken);
        UserThreadLocal.set(user);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // 在请求处理完成后清除ThreadLocal中保存的用户信息
        ThreadLocal<User> UserThreadLocal = new ThreadLocal<>();
        UserThreadLocal.remove();
    }

}
