package com.xidian.miniblog.controller;

import com.xidian.miniblog.async.EventProducer;
import com.xidian.miniblog.entity.Event;
import com.xidian.miniblog.entity.HostHolder;
import com.xidian.miniblog.entity.Page;
import com.xidian.miniblog.entity.User;
import com.xidian.miniblog.service.FollowService;
import com.xidian.miniblog.service.PostService;
import com.xidian.miniblog.service.UserService;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.BlogUtil;
import com.xidian.miniblog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author qhhu
 * @date 2020/3/21 - 18:56
 */
@Controller
public class FollowController implements BlogConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User loginUser = hostHolder.getUser();
        followService.follow(loginUser.getId(), entityType, entityId);

        // 通知被关注的用户
        Event event = new Event()
                .setType(EVENT_TYPE_FOLLOW)
                .setActorId(loginUser.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityOwnerId(entityId);
        eventProducer.fireEvent(event);

        return BlogUtil.getJSONString(0, "关注成功");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unFollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unFollow(user.getId(), entityType, entityId);

        return BlogUtil.getJSONString(0, "已取消关注");
    }

    @GetMapping("/follower/{entityType}/{entityId}")
    public String getEntityFollowerPage(Page page, Model model,
                                        @PathVariable("entityType") int entityType,
                                        @PathVariable("entityId") int entityId) {
        page.setLimit(16);
        page.setPath("/follower/" + entityType + "/" + entityId);
        page.setRows((int) followService.getEntityFollowerCount(entityType, entityId));

        Object entity = null;
        if (entityType == ENTITY_TYPE_USER) {
            entity = userService.getUserById(entityId);
        } else if (entityType == ENTITY_TYPE_POST) {
            entity = postService.getPostById(entityId);
        }
        if (entity == null) {
            throw new RuntimeException("该实体不存在");
        }
        model.addAttribute("entity", entity);

        List<Map<String, Object>> followerVOList = getEntityFollowerList(entityType, entityId, page.getOffset(), page.getLimit());
        model.addAttribute("followerVOList", followerVOList);

        return "/site/follower";
    }

    @GetMapping("/followee/{userId}/{entityType}")
    public String getEntityFolloweePage(Page page, Model model,
                                        @PathVariable("userId") int userId,
                                        @PathVariable("entityType") int entityType) {
        page.setLimit(16);
        page.setPath("/followee/" + userId + "/" + entityType);
        page.setRows((int) followService.getUserFolloweeCount(userId, entityType));

        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);

        List<Map<String, Object>> followeeVOList = getUserFolloweeList(userId, entityType, page.getOffset(), page.getLimit());
        model.addAttribute("followeeVOList", followeeVOList);

        return "/site/followee";
    }

    private List<Map<String, Object>> getUserFolloweeList(int userId, int entityType, int offset, int limit) {
        Set<Integer> followeeIds = followService.getUserFolloweeIds(userId, entityType, offset, limit);

        if (followeeIds == null) {
            return null;
        }

        List<Map<String, Object>> followeeVOList = new ArrayList<>();
        for (Integer followeeId : followeeIds) {
            Map<String, Object> followeeVO = new HashMap<>();

            Object followee = null;
            if (entityType == ENTITY_TYPE_USER) {
                followee = userService.getUserById(followeeId);
            } else if (entityType == ENTITY_TYPE_POST) {
                followee = postService.getPostById(followeeId);
            }

            Double score = redisTemplate.opsForZSet().score(RedisKeyUtil.getUserFolloweeKey(userId, entityType), followeeId);

            boolean isFollow = false;
            if (hostHolder.getUser() != null) {
                isFollow = followService.getFollowStatus(hostHolder.getUser().getId(), entityType, followeeId);
            }

            followeeVO.put("followee", followee);
            followeeVO.put("followTime", new Date(score.longValue()));
            followeeVO.put("isFollow", isFollow);
            followeeVOList.add(followeeVO);
        }

        return followeeVOList;
    }

    public List<Map<String, Object>> getEntityFollowerList(int entityType, int entityId, int offset, int limit) {
        Set<Integer> followerIds = followService.getEntityFollowerIds(entityType, entityId, offset, limit);

        if (followerIds == null) {
            return null;
        }

        List<Map<String, Object>> followerVOList = new ArrayList<>();
        for (Integer followerId : followerIds) {
            Map<String, Object> followerVO = new HashMap<>();

            User follower = userService.getUserById(followerId);

            Double score = redisTemplate.opsForZSet().score(RedisKeyUtil.getEntityFollowerKey(entityType, entityId), followerId);

            boolean isFollow = false;
            if (hostHolder.getUser() != null) {
                isFollow = followService.getFollowStatus(hostHolder.getUser().getId(), entityType, entityId);
            }

            followerVO.put("follower", follower);
            followerVO.put("followTime", new Date(score.longValue()));
            followerVO.put("isFollow", isFollow);
            followerVOList.add(followerVO);
        }

        return followerVOList;
    }

}
