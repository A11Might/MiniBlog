package com.xidian.miniblog.util;

/**
 * @author qhhu
 * @date 2020/3/12 - 18:55
 */
public class RedisKeyUtil {

    private RedisKeyUtil() {}

    private static final String SPLIT = ":";
    private static final String LOGINTICKET ="loginticket";

    public static String getLoginTicketKey(String ticket) {
        return LOGINTICKET + SPLIT + ticket;
    }

}
