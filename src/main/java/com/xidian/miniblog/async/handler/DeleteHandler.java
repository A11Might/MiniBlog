package com.xidian.miniblog.async.handler;

import com.xidian.miniblog.async.EventHandler;
import com.xidian.miniblog.entity.Event;
import com.xidian.miniblog.service.FollowService;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author qhhu
 * @date 2020/3/28 - 11:30
 */
@Component
public class DeleteHandler implements EventHandler, BlogConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void doHandler(Event event) {
        Set<Integer> followerIds = followService.getEntityFollowerIds(BlogConstant.ENTITY_TYPE_USER, event.getActorId(), 0, -1);
        for (int followerUserId : followerIds) {
            String timelineKey = RedisKeyUtil.getTimeLineKey(followerUserId);
            redisTemplate.opsForList().remove(timelineKey, 0, event.getEntityId());
        }
    }

    @Override
    public List<Integer> getSupportEventTypes() {
        return Arrays.asList(EVENT_TYPE_DELETE);
    }
}
