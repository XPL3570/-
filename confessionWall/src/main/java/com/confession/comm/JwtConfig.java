package com.confession.comm;


import com.confession.pojo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author dailyblue
 * @since 2022/6/23
 */
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
                .claim("UserName", user.getUserName())
                .claim("openId",user.getOpenId())
                //下面是第三部分
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();
        // 生成的字符串就是jwt信息，这个通常要返回出去
        return JwtToken;
    }

    /**
     * 判断token是否存在与有效
     * 直接判断字符串形式的jwt字符串
     *
     * @param jwtToken
     * @return
     */
    public static boolean checkToken(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断token是否存在与有效
     * 因为通常jwt都是在请求头中携带，此方法传入的参数是请求
     *
     * @param request
     * @return
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");//注意名字必须为token才能获取到jwt
            if (StringUtils.isEmpty(jwtToken)) return false;
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据token字符串获取会员id
     * 这个方法也直接从http的请求中获取id的
     *
     * @param request
     * @return
     */
    public static String getMemberIdByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("token");
        if (StringUtils.isEmpty(jwtToken)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return claims.get("id").toString();
    }

    /**
     * 解析JWT
     * @param jwt
     * @return
     */
    public static Claims parseJWT(String jwt) {
        Claims claims = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwt).getBody();
        return claims;
    }
}