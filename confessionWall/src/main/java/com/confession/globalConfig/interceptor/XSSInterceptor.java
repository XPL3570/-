package com.confession.globalConfig.interceptor;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//不用了,直接在敏感字里面过滤  这里也没有生效

public class XSSInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 创建包装类，将原始的 HttpServletRequest 对象传入
        XSSRequestWrapper requestWrapper = new XSSRequestWrapper(request);

        String contentType = request.getContentType();
        System.out.println("contentType=" + contentType);

        System.out.println("拦截器执行 requestWrapper");
        ServletInputStream inputStream = requestWrapper.getInputStream();
        // 使用Jsoup解析请求体的内容
        Document doc = Jsoup.parse(inputStream, null, "");

        // 选择所有元素
        Elements allElements = doc.select("*");
        // 遍历选中的元素
        for (Element link : allElements) {
            // 获取元素的属性
            String  text= link.text();
            Jsoup.clean(text, Whitelist.basic());
            System.out.println(text);
        }
        return true;
    }

//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
//        // 在 postHandle 方法中可以对响应结果进行处理，如果需要的话
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        // 在 afterCompletion 方法中可以进行一些清理操作，如果需要的话
//    }
}
