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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

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

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User loginUser = hostHolder.getUser();
        followService.follow(loginUser.getId(), entityType, entityId);

        Event event = new Event()
                .setType(EVENT_TYPE_FOLLOW)
                .setActorId(loginUser.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityOwnerId(entityId);
        eventProducer.fireEvent(event);

        return BlogUtil.getJSONString(0, "关注成功");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unFollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unFollow(user.getId(), entityType, entityId);

        return BlogUtil.getJSONString(0, "已取消关注");
    }

    @RequestMapping(path = "/follower/{entityType}/{entityId}", method = RequestMethod.GET)
    public String getEntityFollowerPage(@PathVariable("entityType") int entityType,
                                        @PathVariable("entityId") int entityId,
                                        Page page, Model model) {
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

        List<Map<String, Object>> followerVOList = followService.getEntityFollowerList(entityType, entityId, page.getOffset(), page.getLimit());
        model.addAttribute("followerVOList", followerVOList);

        return "/site/follower";
    }

    @RequestMapping(path = "/followee/{userId}/{entityType}", method = RequestMethod.GET)
    public String getEntityFolloweePage(@PathVariable("userId") int userId,
                                        @PathVariable("entityType") int entityType,
                                        Page page, Model model) {
        page.setLimit(16);
        page.setPath("/followee/" + userId + "/" + entityType);
        page.setRows((int) followService.getUserFolloweeCount(userId, entityType));

        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);

        List<Map<String, Object>> followeeVOList = followService.getUserFolloweeList(userId, entityType, page.getOffset(), page.getLimit());
        model.addAttribute("followeeVOList", followeeVOList);

        return "/site/followee";
    }

}
