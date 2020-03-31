package com.xidian.miniblog.util;

/**
 * @author qhhu
 * @date 2020/3/12 - 18:55
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String LOGIN_TICKET ="loginticket";
    private static final String ENTITY_LIKE = "like:entity";
    private static final String USER_LIKE = "like:user";
    private static final String FOLLOWER = "follower";
    private static final String FOLLOWEE = "followee";
    private static final String EVENT_QUEUE = "event";
    private static final String TIMELINE = "timeline";
    private static final String POST = "post";
    private static final String POST_LIST = "postlist";
    private static final String USER = "user";
    private static final String SCORE = "score";

    private RedisKeyUtil() {}

    /**
     * 使用 String 类型存储 LoginTicket 对象，key 为该对象的 ticket 属性。
     *
     * @param ticket
     * @return
     */
    public static String getLoginTicketKey(String ticket) {
        return LOGIN_TICKET + SPLIT + ticket;
    }

    /**
     * 使用 set 类型存储点赞该实体的用户的 id，key 为该实体的类型和 id。
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 使用 String 类型存储用户获得的赞数（包括其发博和评论获得的总赞数），key 为该用户的 id。
     *
     * @param userId
     * @return
     */
    public static String getUserLikeKey(int userId) {
        return USER_LIKE + SPLIT + userId;
    }

    /**
     * 使用 zset 类型存储关注该实体的用户的 id 和关注时间（用于排序），key 为该实体的类型和 id。
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getEntityFollowerKey(int entityType, int entityId) {
        return FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 使用 zset 类型存储该用户关注的实体 id 和关注时间（用于排序），key 为该用户的 id 和实体类型。
     *
     * @param userId
     * @param entityType
     * @return
     */
    public static String getUserFolloweeKey(int userId, int entityType) {
        return FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    public static String getEventQueueKey() {
        return EVENT_QUEUE;
    }

    /**
     * 使用 list 类型存储该用户所有关注的用户的发布帖子的 id，key 为该用户的 id。
     *
     * @param userId
     * @return
     */
    public static String getTimeLineKey(int userId) {
        return TIMELINE + SPLIT + userId;
    }

    /**
     * 使用 String 类型缓存用户信息，key 为用户的 id。
     */
    public static String getUserKey(int userId) {
        return USER + SPLIT + userId;
    }

    public static String getPostListKey(int offset, int limit) {
        return POST_LIST + SPLIT + offset + SPLIT + limit;
    }

    /**
     * 使用 set 类型存储需要更新分数的微博 id。
     */
    public static String getPostScoreKey() {
        return POST + SPLIT + SCORE;
    }
}
