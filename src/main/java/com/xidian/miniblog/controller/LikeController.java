package com.xidian.miniblog.controller;

import com.xidian.miniblog.async.EventProducer;
import com.xidian.miniblog.entity.Event;
import com.xidian.miniblog.entity.HostHolder;
import com.xidian.miniblog.entity.User;
import com.xidian.miniblog.service.LikeService;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.BlogUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
    private EventProducer eventProducer;

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

        return BlogUtil.getJSONString(0, map);
    }

}
