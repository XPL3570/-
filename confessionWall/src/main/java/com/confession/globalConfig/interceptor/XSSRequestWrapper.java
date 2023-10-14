package com.confession.globalConfig.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**    这里没有使用
 * 在 Java Servlet 中，HttpServletRequest 对象是只读的，无法直接修改请求参数。
 * 如果你想要过滤请求参数并将过滤后的值传递给后续处理程序，你可以创建一个
 * 包装类来包装原始的 HttpServletRequest 对象，并在包装类中重写 getParameter()
 * 和 getParameterValues() 方法来返回过滤后的值
 */
public class XSSRequestWrapper extends HttpServletRequestWrapper {

    private HttpServletRequest orgRequest = null;

    public XSSRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        System.out.println("过滤器执行了");
        // 过滤请求参数中的HTML标签和特殊字符
        Map<String, String[]> parameterMap = this.getParameterMap();

//        Document doc = Jsoup.parse( this.getInputStream(), null, "");

        // 获取请求体的输入流（适用于POST请求）
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] values = entry.getValue();
            for (int i = 0; i < values.length; i++) {
                System.out.println("请求中的值" + values[i]);
                values[i] = Jsoup.clean(values[i], Whitelist.basic());
            }
        }
    }


    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value != null) {
            value = Jsoup.clean(value, Whitelist.basic());
        }
//        System.out.println(value);
        return value;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value != null) {
            value = Jsoup.clean(value, Whitelist.none());
        }
        return value;
    }


    @Override
    public BufferedReader getReader() throws IOException {
        BufferedReader reader = super.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();
        String cleanedRequestBody = cleanJson(requestBody);
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(cleanedRequestBody.getBytes())));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                values[i] = Jsoup.clean(values[i], Whitelist.basic());
                System.out.println("参数解析：" + values[i]);
            }

        }
        return values;
    }

    private String cleanJson(String json) {
        Object jsonObject = JSON.parse(json);
        cleanJsonObject(jsonObject);
        return JSON.toJSONString(jsonObject);
    }

    private void cleanJsonObject(Object jsonObject) {
        if (jsonObject instanceof JSONObject) {
            JSONObject jsonObj = (JSONObject) jsonObject;
            for (String key : jsonObj.keySet()) {
                Object value = jsonObj.get(key);
                if (value instanceof String) {
                    String cleanedValue = Jsoup.clean((String) value, Whitelist.none());
                    jsonObj.put(key, cleanedValue);
                } else {
                    cleanJsonObject(value);
                }
            }
        } else if (jsonObject instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) jsonObject;
            for (int i = 0; i < jsonArray.size(); i++) {
                Object value = jsonArray.get(i);
                if (value instanceof String) {
                    String cleanedValue = Jsoup.clean((String) value, Whitelist.none());
                    jsonArray.set(i, cleanedValue);
                } else {
                    cleanJsonObject(value);
                }
            }
        }
    }
}
