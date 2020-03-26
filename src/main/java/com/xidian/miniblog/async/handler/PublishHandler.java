package com.xidian.miniblog.async.handler;

import com.xidian.miniblog.async.EventHandler;
import com.xidian.miniblog.entity.Event;
import com.xidian.miniblog.service.FollowService;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author qhhu
 * @date 2020/3/25 - 14:09
 */
@Component
public class PublishHandler implements EventHandler, BlogConstant {

    private static final Logger logger = LoggerFactory.getLogger(PublishHandler.class);

    @Autowired
    private FollowService followService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void doHandler(Event event) {
        Set<Integer> followerUserIds = followService.getEntityFollowerIds(BlogConstant.ENTITY_TYPE_USER, event.getActorId());
        for (int followerUserId : followerUserIds) {
            String timelineKey = RedisKeyUtil.getTimeLineKey(followerUserId);
            redisTemplate.opsForList().leftPush(timelineKey, event.getEntityId());
        }
    }

    @Override
    public List<Integer> getSupportEventTypes() {
        return Arrays.asList(EVENT_TYPE_PUBLISH);
    }
}
