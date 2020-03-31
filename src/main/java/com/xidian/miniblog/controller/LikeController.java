package com.xidian.miniblog.controller;

import com.xidian.miniblog.async.EventProducer;
import com.xidian.miniblog.entity.Event;
import com.xidian.miniblog.entity.HostHolder;
import com.xidian.miniblog.entity.User;
import com.xidian.miniblog.service.CommentService;
import com.xidian.miniblog.service.LikeService;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.BlogUtil;
import com.xidian.miniblog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qhhu
 * @date 2020/3/20 - 15:53
 */
@Controller
public class LikeController implements BlogConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityOwnerId, int postId) {
        User loginUser = hostHolder.getUser();

        if (loginUser == null) {
            return BlogUtil.getJSONString(1, "当前未登录");
        }

        likeService.like(loginUser.getId(), entityType, entityId, entityOwnerId);

        long likeCount = likeService.getEntityLikeCount(entityType, entityId);
        boolean likeStatus = likeService.getLikeStatus(loginUser.getId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 通知帖子作者
        if (likeStatus && loginUser.getId() != entityOwnerId) {
            Event event = new Event()
                    .setType(EVENT_TYPE_LIKE)
                    .setActorId(loginUser.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityOwnerId(entityOwnerId);
            eventProducer.fireEvent(event);
        }

        if (entityType == ENTITY_TYPE_POST) {
            // 将该更新分数的微博存入 redis 中，采用定时任务的方式来更新分数
            String postScoreKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(postScoreKey, entityId);
        } else if (entityType == ENTITY_TYPE_COMMENT) {
            commentService.updateCommentScore(entityId, (int) likeCount);
        }

        return BlogUtil.getJSONString(0, map);
    }

}
