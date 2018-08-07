package com.kunlunsoft.wxcp.web.controller;

import com.common.bean.BaseResponseDto;
import com.common.util.BeanHWUtil;
import com.common.util.RedisHelper;
import com.common.util.SystemHWUtil;
import com.common.util.WebServletUtil;
import com.io.hw.json.HWJacksonUtils;
import com.kunlunsoft.dto.SuiteTicketDto;
import com.kunlunsoft.util.RedisCacheUtil;
import com.kunlunsoft.wxcp.entity.WxCpCommandToSuite;
import com.kunlunsoft.wxcp.mp.aes.AesException;
import com.kunlunsoft.wxcp.mp.aes.WXBizMsgCrypt;
import com.kunlunsoft.wxcp.service.WxCpDecryptService;
import com.string.widget.util.ValueWidget;
import com.time.util.CreateTimeDto;
import com.time.util.TimeHWUtil;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/***
 * 企业微信
 */
@RestController
@RequestMapping(value = {"/corp/weixin", "/wkwx"}, produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
public class CorpWeixinController {
    public static final String RESPONSE_SUCCESS = "success";
    //    @Autowired
//    private WxCpConfigStorage wxCpConfigStorage;
    public static final String weixinUserCode = "code";
    /***
     * 可信域名
     */
    public static final String auth_domain = "wx.yhskyc.com";
    private static final Logger log = LoggerFactory.getLogger(CorpWeixinController.class);
    @Autowired
    private WxCpService wxCpService;
    @Autowired
    private WxCpCryptUtil wxCpCryptUtil;
    @Autowired
    private WXBizMsgCrypt wxBizMsgCrypt;
    //    @Autowired
//    private RightWxCpOAuth2ServiceImpl rightWxCpOAuth2Service;
    @Autowired
    private WxCpDecryptService wxCpDecryptService;

    /***
     * 获取企业微信 code,code用于获取userId
     * @param model
     * @param request
     * @param response
     * @param code
     * @param state
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/getCode/json", "index", "set"}, produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String json2(Model model, HttpServletRequest request, HttpServletResponse response
            , @RequestParam(value = "code", required = false) String code, @RequestParam(value = "state", required = false) String state) {
        if (code == null) {
            try {
                response.sendRedirect("/corp/weixin/oauth2/json");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        Map map = new HashMap();
        map.put("code", code);
        try {
            String queryString = WebServletUtil.getRequestQueryStr(request, SystemHWUtil.CHARSET_UTF);
            System.out.println("queryString 22:" + queryString);
            System.out.println("请去方式 :" + request.getMethod());
            map.put("queryString", queryString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HWJacksonUtils.getJsonP(map);
//        WeixinUserCodeDto weixinUserCodeDto = new WeixinUserCodeDto();
        /*weixinUserCodeDto.setCode(code);
        weixinUserCodeDto.setState(state);
//            String queryString = WebServletUtil.getRequestQueryStr(request, SystemHWUtil.CHARSET_UTF);
//            System.out.println("queryString :" + queryString);
        if (!ValueWidget.isNullOrEmpty(code)) {
            RedisHelper.getInstance().saveKeyCacheExpire1hour("wxcp", weixinUserCode, code);
        }

        String[] userInfo = getUserIdInfo(code);
//        return BaseResponseDto.jsonValue(weixinUserCodeDto);
        return BaseResponseDto.put2("weixinUserCodeDto", weixinUserCodeDto).put("userInfo", userInfo).toJson();
    */
    }

    /***
     * 获取token
     * @param model
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getToken/json", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String jsonGetAcessToken2(Model model, HttpServletRequest request, HttpServletResponse response
    ) {
        String token = null;
        try {
//            this.wxCpService.setWxCpConfigStorage(wxCpConfigStorage);
            token = this.wxCpService.getAccessToken();
        } catch (WxErrorException e) {
            e.printStackTrace();
            return buildBaseResponseDto(e).toJson();
        }
        return BaseResponseDto.jsonValue(token);
    }

    /***
     * 需要用户点击的授权链接,目的:获取用户的code
     * @param model
     * @param request
     * @param response
     * @param redirectUri
     * @return
     */
//    @ResponseBody
    @RequestMapping(value = "/oauth2/json", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String jsonAuthForCode2(Model model, HttpServletRequest request, HttpServletResponse response
            , @RequestParam(value = "redirectUri", required = false) String redirectUri
            , @RequestParam(value = "state", required = false) String state,
                                   @RequestParam(value = "json", required = false) Boolean json) {
//        String redirectUri="aa";
        if (ValueWidget.isNullOrEmpty(redirectUri)) {
            redirectUri = request.getRequestURL().toString().replaceAll("oauth2/json.*$", "getCode/json");
        }
        redirectUri = redirectUri.replace("127.0.0.1:8080", auth_domain);
        String oauth2Url = this.wxCpService.getOauth2Service().buildAuthorizationUrl(redirectUri, state);
        System.out.println("oauth2Url :" + oauth2Url);
        if (null != json && json) {
            try {
                WebServletUtil.writeResponse(response, BaseResponseDto.jsonValue(oauth2Url));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            response.sendRedirect(oauth2Url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/queryUserIdByCode/json", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String jsonGetUserId2(Model model, HttpServletRequest request, HttpServletResponse response
            , @RequestParam(value = "code", required = false) String code) {
        if (ValueWidget.isNullOrEmpty(code)) {
            code = RedisHelper.getInstance().getKeyCache("wxcp", weixinUserCode);
        }
        String[] userInfo = getUserIdInfo(code);
        return BaseResponseDto.jsonValue(userInfo);
//        return Constant2.RESPONSE_RIGHT_RESULT;
    }

    private String[] getUserIdInfo(String code) {
        System.out.println("code :" + code);
        log.warn("code:" + code);
        String[] userInfo = null;
        try {
            userInfo = this.wxCpService.getOauth2Service().getUserInfo(1000020, code);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        /*userInfo =new String[]{this.rightWxCpOAuth2Service.getUserId(
                "se3BleuQaep5brOsAwWdFfbq5hN1X7e416ueMefSVmFnO6hi7YKfOkoYVeT01swZlRRfr9ULwQKZcl5ZD7-9JOqaVgANXzYwgjWip6WomIVuoeXPgc0xGbhLvVMlakUjvwyobsVJ9NAPgPgsBOhBkh4aPN0lsez4Mfx8pAzhq-OJzV-IIg4uDsC-VXCpDV9Ir8wNXfOqK5U-T1JxMT5rkw", code)} ;*/

        return userInfo;
    }

    private BaseResponseDto buildBaseResponseDto(WxErrorException e) {
        return new BaseResponseDto(false).setErrorCode(String.valueOf(e.getError().getErrorCode()))
                .setErrorMessage(e.getError().getErrorMsg());
    }

    /***
     * userId 换openid
     * @param model
     * @param request
     * @param response
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/userId2Openid/json", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String jsonUserId2openid2(Model model, HttpServletRequest request, HttpServletResponse response
            , @RequestParam(value = "userId", required = false) String userId) {
        try {
            Map<String, String> map = this.wxCpService.getUserService().userId2Openid(userId, null);
            return BaseResponseDto.jsonValue(map);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return buildBaseResponseDto(e).toJson();
        }
    }

    /***
     * 数据回调URL
     * @param model
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/dataCallback/json", "receive/data/58"}, produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String jsonDataCall2(Model model, HttpServletRequest request, HttpServletResponse response,
                                @RequestParam(value = "msg_signature", required = false) String msg_signature,
                                @RequestParam(value = "echostr", required = false) String echostr,
                                @RequestParam(value = "timestamp", required = false) String timestamp,
                                @RequestParam(value = "nonce", required = false) String nonce,
                                @RequestBody(required = false) String sReqData) {
        try {
            String queryString = WebServletUtil.getRequestQueryStr(request, SystemHWUtil.CHARSET_UTF);
            System.out.println("queryString 22:" + queryString);
            System.out.println("请去方式 :" + request.getMethod());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("msg_signature :" + msg_signature);
        System.out.println("nonce :" + nonce);
        System.out.println("echostr :" + echostr);
        System.out.println("timestamp :" + timestamp);

//        WxCpCryptUtil wxCpCryptUtil;
//       String plainText= wxCpCryptUtil.decrypt(/*msg_signature, timestamp, nonce, */echostr);
//        System.out.println("plainText :" + plainText);

        if (ValueWidget.isNullOrEmpty(sReqData)) {
            try {
                String sEchoStr = wxBizMsgCrypt.VerifyURL(msg_signature, timestamp, nonce, echostr);
//            String decrypted= wxBizMsgCrypt.decrypt(echostr);
                System.out.println("sEchoStr :" + sEchoStr);
                boolean success = this.wxCpService.checkSignature(msg_signature, timestamp, nonce, echostr);
                System.out.println("success22 :" + success);
                return sEchoStr;
            } catch (AesException e) {
                e.printStackTrace();
            }
        }
        String encrypted = this.wxCpDecryptService.decrypt(msg_signature, timestamp, nonce, sReqData);
//        boolean success = this.wxCpService.checkSignature(msg_signature, timestamp, nonce, encrypted);
//        System.out.println("success :" + success);
//        String encrypted=this.wxBizMsgCrypt.DecryptMsg(msg_signature, timestamp, nonce,)
        System.out.println("encrypted :" + encrypted);
        return encrypted;
    }

    /***
     * 指令回调URL <br />
     * 安装应用会调用,DecryptMsg,必须返回"success"
     * @param model
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/commandCallback/json", "receive/evtdata/58", "receive/evtdata/59"}, produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String jsonCommandCallback2(Model model, HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam(value = "msg_signature", required = false) String msg_signature
            , @RequestParam(value = "timestamp", required = false) String timestamp
            , @RequestParam(value = "nonce", required = false) String nonce
            , @RequestParam(value = "echostr", required = false) String echostr,
                                       @RequestBody(required = false) String sReqData
    ) {
        try {
            String queryString = WebServletUtil.getRequestQueryStr(request, SystemHWUtil.CHARSET_UTF);
            System.out.println("queryString :" + queryString);
            System.out.println("sReqData :" + sReqData);
            System.out.println("请去方式 43:" + request.getMethod());
            if (ValueWidget.isNullOrEmpty(sReqData)) {
                return this.wxBizMsgCrypt.VerifyURL(msg_signature, timestamp, nonce, echostr);
            }
            //安装应用会调用,DecryptMsg,必须返回"success"
            String xml = this.wxBizMsgCrypt.DecryptMsg(msg_signature, timestamp, nonce, sReqData);
            String contentType = request.getContentType();
            if (contentType.contains("xml")) {
                //获取SuiteTicket
                SuiteTicketDto suiteTicketDto = wxCpDecryptService.dealSuiteTicket(xml);
                WxCpCommandToSuite wxCpCommandToSuite = new WxCpCommandToSuite();
                BeanHWUtil.copyProperties(suiteTicketDto, wxCpCommandToSuite);
                wxCpCommandToSuite.setTimeStamp2(suiteTicketDto.getTimeStamp());
                CreateTimeDto createTimeDto = TimeHWUtil.getCreateTimeDao();
                wxCpCommandToSuite.setCreateTime(createTimeDto.getCreateTime());
                wxCpCommandToSuite.setUpdateTime(createTimeDto.getUpdateTime());
            }

            //必须返回"success"
            return RESPONSE_SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AesException e) {
            e.printStackTrace();
        }
        return RESPONSE_SUCCESS;
    }

    /***
     *  获取suiteTicket,企业微信不会调用该接口 <br />
     *  该接口 为外部提供统一服务
     * @param model
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/suiteTicket/json", produces = SystemHWUtil.RESPONSE_CONTENTTYPE_JSON_UTF)
    public String jsonGetSuiteTicket2(Model model, HttpServletRequest request, HttpServletResponse response
    ) {
        String suiteTicket = RedisCacheUtil.getSuiteTicket();
        return BaseResponseDto.jsonValue(suiteTicket);
    }


}
