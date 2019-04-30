package com.anosi.asset.component;

import com.anosi.asset.bean.LoginResponse;
import com.anosi.asset.exception.DataCenterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 该类用于登录数据中心平台
 * 1.获取access_Token
 * 2.若token过期刷新
 *
 * 该工具类所需包缺了11个，主要为org.apache.http包，
 * 本人从官网下载的httpCore 4.4.9, 以及httpClient 4.5.5.
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
public class TokenComponent {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${data-center.url}")
    private String url;
    @Value("${data-center.username}")
    private String username;
    @Value("${data-center.password}")
    private String password;
    @Value("${data-center.client}")
    private String client;
    @Value("${data-center.secret}")
    private String secret;

    /***
     * 登录平台
     * @throws IOException
     */
    public void login() throws IOException {
        HttpPost request = new HttpPost(url + "/auth/oauth/token");
        String basic = new String(Base64.encodeBase64(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(client + ":" + secret)));
//        logger.debug("basic:{}", basic);
        request.setHeader("Authorization", "Basic " + basic);
        List<NameValuePair> formParams = new ArrayList<>();

        formParams.add(new BasicNameValuePair("username", username));
        formParams.add(new BasicNameValuePair("password", password));
        formParams.add(new BasicNameValuePair("grant_type", "password"));
        request.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));

//        logger.debug("登录数据中心，账户:{},密码:{}", username, password);
        try (CloseableHttpResponse response = HttpClients.custom()
                .build().execute(request)) {
            ObjectMapper objectMapper = new ObjectMapper();
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            int statusCode = statusLine.getStatusCode();
            if (entity == null) {
                throw new ClientProtocolException("Response contains no content");
            }
            ContentType contentType = ContentType.getOrDefault(entity);
            Charset charset = contentType.getCharset();
            try (Reader reader = new InputStreamReader(entity.getContent(), charset)) {
                String content = IOUtils.toString(reader);
                if (statusCode == 401) {
                    throw new DataCenterException(content);
                }
                LoginResponse loginResponse = objectMapper.readValue(content, LoginResponse.class);
                // 获取到access_token和refresh_token,存储到redis
                stringRedisTemplate.opsForValue().set(username + "_access_token", loginResponse.getAccessToken(), loginResponse.getExpiresIn(), TimeUnit.SECONDS);
                stringRedisTemplate.opsForValue().set(username + "_refresh_token", loginResponse.getRefreshToken(), 60 * 60 * 24 * 30, TimeUnit.SECONDS);// 一个月过期
            }
        }
    }

    /***
     * 刷新token
     * @throws IOException
     */
    public void refresh() throws IOException {
        String refreshToken = stringRedisTemplate.opsForValue().get(username + "_refresh_token");// 获取refreshToken
        if (StringUtils.isBlank(refreshToken)) {
            // refreshToken为空则直接登录
            login();
            return;
        }

        HttpPost request = new HttpPost(url + "/auth/oauth/token");
        request.setHeader("Authorization",
                "Basic " + new String(
                        Base64.encodeBase64(
                                org.apache.commons.codec.binary.StringUtils.getBytesUtf8(client + ":" + secret)
                        )));
        List<NameValuePair> formParams = new ArrayList<>();

        formParams.add(new BasicNameValuePair("refresh_token", refreshToken));
        formParams.add(new BasicNameValuePair("grant_type", "refresh_token"));
        request.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));

        try (CloseableHttpResponse response = HttpClients.custom()
                .build().execute(request)) {
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            int statusCode = statusLine.getStatusCode();
            if (entity == null) {
                throw new ClientProtocolException("Response contains no content");
            }
            if (statusCode == 401 || statusCode == 400) {
                // username或password错误或refreshToken无效,需要重新登录
                login();
                return;
            }
            ContentType contentType = ContentType.getOrDefault(entity);
            Charset charset = contentType.getCharset();
            try (Reader reader = new InputStreamReader(entity.getContent(), charset)) {
                String content = IOUtils.toString(reader);
                if (statusCode == 400) {
                    throw new DataCenterException(content);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                LoginResponse loginResponse = objectMapper.readValue(content, LoginResponse.class);
                // 获取到access_token和refresh_token,存储到redis
                stringRedisTemplate.opsForValue().set(username + "_access_token", loginResponse.getAccessToken(), loginResponse.getExpiresIn(), TimeUnit.SECONDS);
                stringRedisTemplate.opsForValue().set(username + "_refresh_token", loginResponse.getRefreshToken(), 60 * 60 * 24 * 30, TimeUnit.SECONDS);// 一个月过期
            }
        }
    }

}
