package com.confession.globalConfig.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value != null) {
            value = cleanXss(value);
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                values[i] = cleanXss(values[i]);
            }
        }
        return values;
    }

    private String cleanXss(String value) {
        // 进行XSS过滤逻辑，可以使用第三方库或自定义方法进行过滤
        // 请根据具体需求实现XSS过滤逻辑
        // 这里仅作为示例，使用简单的替换方式进行过滤
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        return value;
    }
}
