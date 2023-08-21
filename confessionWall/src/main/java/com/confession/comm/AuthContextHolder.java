package com.confession.comm;



import javax.servlet.http.HttpServletRequest;

//获取当前用户用户信息工具类
public class AuthContextHolder {

    //获取当前用户id
//    public static Long getUserId(HttpServletRequest request){
//        //从header获取token
//        String token = request.getHeader("token");
//        //调用Jwt工具类获取他生产过的userId
//        System.out.println("从请求中拿到的token是："+token);
//        Long userId = JwtConfig.getUserId(token);
//        return userId;
//    }

    //获取当前用户名称
//    public static String getUserName(HttpServletRequest request){
//        //从header获取token
//        String token = request.getHeader("token");
//        //调用Jwt工具类获取他生产过的UserName
//        String userName = JwtConfig.(token);
//        return userName;
//    }

}
