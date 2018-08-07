package com.kunlunsoft.wxcp.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kunlunsoft.util.HttpUtils;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.json.GsonHelper;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpOAuth2ServiceImpl;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RightWxCpOAuth2ServiceImpl extends WxCpOAuth2ServiceImpl {
    @Autowired
    private WxCpService mainService;

    @Autowired
    public RightWxCpOAuth2ServiceImpl(WxCpService mainService) {
        super(mainService);
    }

    public String[] getUserIdInfo(String accessToken, String code) throws WxErrorException {
        String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=%s&code=%s",
                accessToken, code);
        String responseText = mainService.get(url, null);
        JsonElement je = new JsonParser().parse(responseText);
        JsonObject jo = je.getAsJsonObject();
        return new String[]{GsonHelper.getString(jo, "UserId"),
                GsonHelper.getString(jo, "DeviceId"),
                GsonHelper.getString(jo, "OpenId")};
    }

    public String getUserId(String accessToken, String code) {
        try {
            URIBuilder uriBuilder = new URIBuilder("https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo");
            uriBuilder.setParameter("access_token", accessToken);
            uriBuilder.setParameter("code", code);

            String response = HttpUtils.get(uriBuilder.toString());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }
}
