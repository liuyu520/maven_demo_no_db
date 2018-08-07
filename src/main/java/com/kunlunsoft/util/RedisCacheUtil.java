package com.kunlunsoft.util;

import com.common.util.RedisHelper;

public class RedisCacheUtil {
    public static String REDISKEY_SUITETICKET = "wx_suiteTicket";

    public static void saveSuiteTicket(String suiteTicket) {
        RedisHelper.getInstance().clearCache(REDISKEY_SUITETICKET);
        RedisHelper.getInstance().saveExpxKeyCache(REDISKEY_SUITETICKET, suiteTicket, 3600);
    }

    public static String getSuiteTicket() {
        return RedisHelper.getInstance().getCache(REDISKEY_SUITETICKET);
    }
}
