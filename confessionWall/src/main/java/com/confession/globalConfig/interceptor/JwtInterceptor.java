package com.confession.globalConfig.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.confession.config.JwtConfig;
import com.confession.globalConfig.exception.WallException;
import com.confession.pojo.User;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static com.confession.comm.ResultCodeEnum.PERMISSION;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 解析并验证JWT  权限校验
//        JwtConfig.getIdAndRoleByJwtToken(request);
        Map<String, Object> idAndRole = JwtConfig.getIdAndRoleByJwtToken(request);
        String role = (String) idAndRole.get("role");

        // 根据请求的URL判断角色
        String servletPath = request.getServletPath();
        if (servletPath.contains("/admin")) { //
            if (!"admin".equals(role)) {
                throw new WallException(PERMISSION);
            }
        } else {
            if (!"user".equals(role)) {
                throw new WallException(PERMISSION);
            }
        }

        // 将解析后的信息保存在ThreadLocal中，以便后续的Controller使用
        Integer id = (Integer) idAndRole.get("id");
        User user = new User();
        //这里默认只放了id，
        user.setId(id);
        USER_THREAD_LOCAL.set(user);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // 在请求处理完成后清除ThreadLocal中保存的用户信息
        ThreadLocal<User> UserThreadLocal = new ThreadLocal<>();
        UserThreadLocal.remove();
    }

    public static User getUser() {
        return USER_THREAD_LOCAL.get();
    }

}
