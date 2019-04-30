package com.anosi.asset.component;

import com.anosi.asset.bean.ResponseEntity;
import com.anosi.asset.exception.DataCenterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务请求工具类
 *
 * @ProjectName: goaland
 * @Package: com.anosi.asset.component
 * @Description:
 * @Author: tianyufei
 * @CreateDate: 2018/6/14 10:41
 * @UpdateUser: tianyufei
 * @UpdateDate: 2018/6/15 9:20
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Component
@Slf4j
public class ServerCallComponent {

    @Autowired
    private TokenComponent tokenComponent;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${data-center.username}")
    private String username;

    /***
     * 将parameterMap转换成entity，其中parameterMap的value是数组
     * @param parameterMap 参数map
     * @return 返回entity
     * @throws UnsupportedEncodingException
     */
    public HttpEntity convertEntityArray(Map<String, String[]> parameterMap) throws UnsupportedEncodingException {
        final List<NameValuePair> formParams = new ArrayList<>();
        parameterMap.forEach((k, v) -> {
            for (String s : v) {
                formParams.add(new BasicNameValuePair(k, s));
            }
        });
        return new UrlEncodedFormEntity(formParams);
    }

    /****
     * 将parameterMap转换成entity
     * @param parameterMap 参数map
     * @return 返回entity
     * @throws UnsupportedEncodingException
     */
    public HttpEntity convertEntity(Map<String, String> parameterMap) throws UnsupportedEncodingException {
        final List<NameValuePair> formParams = new ArrayList<>();
        parameterMap.forEach((k, v) -> {
            formParams.add(new BasicNameValuePair(k, v));
        });
        return new UrlEncodedFormEntity(formParams);
    }

    public <T> T executeGet(String url, Class<T> tClass) throws IOException {
        HttpGet request;
        request = new HttpGet(url);
        return executeCore(request, tClass);
    }

    public String executeGet(String url) throws IOException {
        HttpGet request;
        request = new HttpGet(url);
        return executeCore(request);
    }

    public <T> T executePost(String url, Class<T> tClass) throws IOException {
        return executePost(url, null, tClass);
    }

    public <T> T executePost(String url, Object entity, Class<T> tClass) throws IOException {
        HttpPost request;
        request = new HttpPost(url);
        if (entity != null) {
            ObjectMapper mapper = new ObjectMapper();
            request.setEntity(new StringEntity(mapper.writeValueAsString(entity), ContentType.APPLICATION_JSON));
        }
        return executeCore(request, tClass);
    }

    public String executePost(String url) throws IOException {
        return executeCore(new HttpPost(url));
    }

    public String executePost(String url, Object entity) throws IOException {
        HttpPost request;
        request = new HttpPost(url);
        if (entity != null) {
            ObjectMapper mapper = new ObjectMapper();
            request.setEntity(new StringEntity(mapper.writeValueAsString(entity), ContentType.APPLICATION_JSON));
        }
        return executeCore(request);
    }

    public String executePost(String url, HttpEntity body) throws IOException {
        HttpPost request;
        request = new HttpPost(url);
        if (body != null)
            request.setEntity(body);
        return executeCore(request);
    }

    public <T> T executePost(String url, HttpEntity body, Class<T> tClass) throws IOException {
        HttpPost request;
        request = new HttpPost(url);
        if (body != null)
            request.setEntity(body);
        return executeCore(request, tClass);
    }

    public <T> T executePut(String url, HttpEntity body, Class<T> tClass) throws IOException {
        HttpPut request;
        request = new HttpPut(url);
        request.setEntity(body);
        return executeCore(request, tClass);
    }

    public String executePut(String url, HttpEntity body) throws IOException {
        HttpPut request;
        request = new HttpPut(url);
        request.setEntity(body);
        return executeCore(request);
    }

    public <T> T executeDelete(String url, Class<T> tClass) throws IOException {
        HttpDelete request;
        request = new HttpDelete(url);
        return executeCore(request, tClass);
    }

    public String executeDelete(String url) throws IOException {
        HttpDelete request;
        request = new HttpDelete(url);
        return executeCore(request);
    }

    private <T> T executeCore(HttpUriRequest request, Class<T> tClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(executeCore(request), tClass);
    }

    private String executeCore(HttpUriRequest request) throws IOException {
        String content = null;
        request.setHeader("Authorization", "Bearer " + stringRedisTemplate.opsForValue().get(username + "_access_token"));
        try (CloseableHttpResponse response = HttpClients.custom()
                .build().execute(request)) {
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 401) {
                // 登录过期，首先刷新token
                tokenComponent.refresh();
                // 然后重新发送请求
                return executeCore(request);
            }
            if (entity != null) {
                // 如果返回的entity不为空
                ContentType contentType = ContentType.getOrDefault(entity);
                Charset charset = contentType.getCharset();
                if (charset == null) {
                    charset = Charset.defaultCharset();
                }
                try (Reader reader = new InputStreamReader(entity.getContent(), charset)) {
                    // 解析response返回的content，序列化为responseEntity
                    content = IOUtils.toString(reader);
                    ObjectMapper mapper = new ObjectMapper();
                    ResponseEntity responseEntity = mapper.readValue(content, ResponseEntity.class);
                    Object data = responseEntity.getData();
                    if (responseEntity.getStatus() != 0) {
                        throw new DataCenterException(responseEntity.getMsg());
                    } else if (data != null) {
                        return mapper.writeValueAsString(data);
                    }
                }
            }
        }
        return null;
    }

}