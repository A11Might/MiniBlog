package com.xidian.miniblog.util;

/**
 * @author qhhu
 * @date 2020/3/12 - 19:54
 */
public interface BlogConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态的登录凭证的超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住状态的登录凭证超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 实体类型: 微博
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型: 评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型: 用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 实体状态：删除
     */
    int ENTITY_STATUS_DELETE = 1;

    /**
     * 事件类型：点赞
     */
    int EVENT_TYPE_LIKE = 1;

    /**
     * 事件类型：评论
     */
    int EVENT_TYPE_COMMENT = 2;

    /**
     * 事件类型：关注
     */
    int EVENT_TYPE_FOLLOW = 3;

    /**
     * 事件类型：发布新鲜事
     */
    int EVENT_TYPE_PUBLISH = 4;

    /**
     * 用户：系统
     */
    int SYSTEM_USER = 1;
}
