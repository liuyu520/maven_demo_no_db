package com.kunlunsoft.wxcp.conf.weixin;

import com.kunlunsoft.wxcp.mp.aes.AesException;
import com.kunlunsoft.wxcp.mp.aes.WXBizMsgCrypt;
import com.kunlunsoft.wxcp.service.WxCpDecryptService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.config.WxCpInMemoryConfigStorage;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Configuration
public class WxCpConfiguration {
    private static final Logger log = LoggerFactory.getLogger(WxCpConfiguration.class);
    @Autowired
    private WxCpProperties properties;

    @Bean
    public WxCpConfigStorage configStorage() {
        WxCpInMemoryConfigStorage configStorage = new WxCpInMemoryConfigStorage();
        configStorage.setCorpId(this.properties.getCorpId().trim());
        configStorage.setAgentId(this.properties.getAgentId());
        configStorage.setCorpSecret(this.properties.getSecret());
        configStorage.setToken(this.properties.getToken().trim());
        configStorage.setAesKey(this.properties.getAesKey().trim());

        return configStorage;
    }

    @Bean
    public WxCpService WxCpService(WxCpConfigStorage configStorage) {
//        WxCpService WxCpService = new me.chanjar.weixin.cp.api.impl.okhttp.WxCpServiceImpl();
//        WxCpService WxCpService = new me.chanjar.weixin.cp.api.impl.jodd.WxCpServiceImpl();
//        WxCpService WxCpService = new me.chanjar.weixin.cp.api.impl.apache.WxCpServiceImpl();
        WxCpService service = new me.chanjar.weixin.cp.api.impl.WxCpServiceImpl();
        service.setWxCpConfigStorage(configStorage);
        return service;
    }

    @Bean
    public WxCpMessageRouter router(WxCpService wxCpService) {
        final WxCpMessageRouter newRouter = new WxCpMessageRouter(wxCpService);


        return newRouter;
    }

    @Bean
    public WxCpCryptUtil getWxCryptUtil(WxCpConfigStorage configStorage) {
        WxCpCryptUtil wxCpCryptUtil = new WxCpCryptUtil(configStorage);
        return wxCpCryptUtil;
    }

    @Bean
    public WXBizMsgCrypt getWXBizMsgCrypt(WxCpConfigStorage configStorage) {
        WXBizMsgCrypt wxcpt = null;
        try {
            wxcpt = new WXBizMsgCrypt(configStorage.getToken(), configStorage.getAesKey(), configStorage.getCorpId(), this.properties.getThirdAppSuiteID());
        } catch (AesException e) {
            e.printStackTrace();
        }
        return wxcpt;
    }

    @Bean
    public WxCpDecryptService getWxCpDecryptService(WxCpConfigStorage configStorage) {
        WxCpDecryptService wxCpDecryptService = new WxCpDecryptService(configStorage.getToken(), configStorage.getAesKey(), configStorage.getCorpId(), this.properties.getThirdAppSuiteID());
        return wxCpDecryptService;
    }
}
