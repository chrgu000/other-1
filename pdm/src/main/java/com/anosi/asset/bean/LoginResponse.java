package com.anosi.asset.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
* 登录后的返回结果
* @since  2018/7/13 16:34
* @author 倪文骅
* @param
* @return
*/
@Data
public class LoginResponse {

    // 范围，默认值 default
    private String scope;

    // 鉴权 token 类型，默认值 bearer
    @JsonProperty("token_type")
    private String tokenType;

    // 平台生成并返回 accessToken 的有效时间，单位秒
    @JsonProperty("expires_in")
    private Integer expiresIn;

    // Oauth 2.0 鉴权参数
    @JsonProperty("access_token")
    private String accessToken;

    // Oauth 2.0 鉴权参数，用来刷新 accessToken。（1 个月的有效期）
    @JsonProperty("refresh_token")
    private String refreshToken;

    public String getScope() {
        return scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
