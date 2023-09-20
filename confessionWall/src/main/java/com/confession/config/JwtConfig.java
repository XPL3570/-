package com.confession.config;


import com.confession.globalConfig.exception.WallException;
import com.confession.pojo.Admin;
import com.confession.pojo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.confession.comm.ResultCodeEnum.LOGIN_AURH;

public class JwtConfig {

    //常量
    public static final long EXPIRE = 1000 * 60 * 60 * 24; //token过期时间
    public static final String APP_SECRET = "zhangjie"; //秘钥，加盐


    public static String getJwtToken(User user) {

        String JwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")    //头部信息
                .setHeaderParam("alg", "HS256")    //头部信息
                //下面这部分是payload部分
                // 设置默认标签
                .setSubject("zhangjie")    //设置jwt所面向的用户
                .setIssuedAt(new Date())    //设置签证生效的时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))    //设置签证失效的时间
                //自定义的信息，这里存储id和姓名信息
                .claim("id", user.getId())  //设置token主体部分 ，存储用户信息
                .claim("UserName", user.getUsername())
                .claim("openId",user.getOpenId())
                .claim("schoolId",user.getSchoolId())
                .claim("role", "user")  // 添加角色信息
                //下面是第三部分
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();
        String prefixedToken = "PREFIX_" + JwtToken; // 添加前缀
        // 生成的字符串就是jwt信息，这个通常要返回出去
        return prefixedToken;
    }

    public static String getAdminJwtToken(Admin admin) {

        String JwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")    //头部信息
                .setHeaderParam("alg", "HS256")    //头部信息
                //下面这部分是payload部分
                // 设置默认标签
                .setSubject("zhangjie")    //设置jwt所面向的用户
                .setIssuedAt(new Date())    //设置签证生效的时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))    //设置签证失效的时间
                //自定义的信息，这里存储id和姓名信息
                .claim("id", admin.getId())  //设置token主体部分 ，存储用户信息
                .claim("phoneNumber", admin.getPhoneNumber())
                .claim("weChatId",admin.getWeChatId())
                .claim("role", "admin")  // 添加角色信息
                //下面是第三部分
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();
        String prefixedToken = "PREFIX_" + JwtToken; // 添加前缀
        // 生成的字符串就是jwt信息，这个通常要返回出去
        return prefixedToken;
    }



    public static Integer getIdByJwtToken(HttpServletRequest request) {
//        System.out.println(request.toString());
        String jwtToken = request.getHeader("authentication");
        System.out.println("jwtToken: "+jwtToken);
        if (StringUtils.isEmpty(jwtToken)) {
            throw new WallException(LOGIN_AURH); // 抛出异常
        }
        // 移除前缀
        String cleanToken = jwtToken.replaceFirst("^PREFIX_", "");

        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(cleanToken);
            Claims claims = claimsJws.getBody();
            return claims.get("id", Integer.class); // 解析为整数类型
        } catch (Exception e) {
            e.printStackTrace();
            throw new WallException("Token解析失败",222); // 抛出异常
        }
    }

    public static Map<String, Object> getIdAndRoleByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("authentication");
        System.out.println("jwtToken: "+jwtToken);
        if (StringUtils.isEmpty(jwtToken)) {
            throw new WallException(LOGIN_AURH); // 抛出异常
        }
        // 移除前缀
        String cleanToken = jwtToken.replaceFirst("^PREFIX_", "");

        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(cleanToken);
            Claims claims = claimsJws.getBody();
            Integer id = claims.get("id", Integer.class); // 解析为整数类型
            String role = claims.get("role", String.class); // 解析为字符串类型

            // 创建一个包含ID和角色的Map
            Map<String, Object> result = new HashMap<>();
            result.put("id", id);
            result.put("role", role);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new WallException("Token解析失败",222); // 抛出异常
        }
    }



    //校验token是不是超级管理员的
    public static boolean isAdminToken(String token) {
        // 去掉前缀
        if (token.startsWith("PREFIX_")) {
            token = token.substring(7);
        }

        // 解析JWT
        Claims claims = Jwts.parser()
                .setSigningKey(APP_SECRET)
                .parseClaimsJws(token)
                .getBody();

        // 检查role字段
        String role = claims.get("role", String.class);
        return "admin".equals(role);
    }




    public static boolean checkToken(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)) return false;
        try {
            String cleanToken = jwtToken.replaceFirst("^PREFIX_", ""); // 清除前缀
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(cleanToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("Authentication");//注意名字必须为token才能获取到jwt
            if (StringUtils.isEmpty(jwtToken)) return false;
            String cleanToken = jwtToken.replaceFirst("^PREFIX_", ""); // 清除前缀
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(cleanToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 解析JWT
     * @param jwt
     * @return
     */
    public static Claims parseJWT(String jwt) {
        String cleanToken = jwt.replaceFirst("^PREFIX_", ""); // 清除前缀
        Claims claims = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(cleanToken).getBody();
        return claims;
    }
}