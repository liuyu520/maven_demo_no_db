package com.kunlunsoft.wxcp.conf.weixin;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.context.annotation.Configuration;

/**
 * * @author Binary Wang(https://github.com/binarywang)
 */
@Configuration
public class WxCpProperties {
    /**
     * 设置微信企业号的corpId
     */
//    @Value("${wechat.cp.corpId}")
    private String corpId;
    /**
     * 第三方应用的SuiteID <br />
     * *  see https://open.work.weixin.qq.com/wwopen/developer#/sass/apps/detail/ww7ef07db8e157fc07
     */
//    @Value("${wechat.cp.thirdAppSuiteID}")
    private String thirdAppSuiteID;

    /**
     * 设置微信企业应用的AgentId
     */
//    @Value("${wechat.cp.agentId}")
    private Integer agentId;

    /**
     * 设置微信企业应用的Secret
     */
//    @Value("${wechat.cp.secret}")
    private String secret;

    /**
     * 设置微信企业号的token
     */
//    @Value("${wechat.cp.token}")
    private String token;

    /**
     * 设置微信企业号的EncodingAESKey
     */
//    @Value("${wechat.cp.aesKey}")
    private String aesKey;

    public String getCorpId() {
        return this.corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAesKey() {
        return this.aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public String getThirdAppSuiteID() {
        return thirdAppSuiteID;
    }

    public void setThirdAppSuiteID(String thirdAppSuiteID) {
        this.thirdAppSuiteID = thirdAppSuiteID;
    }
}
