package com.xidian.miniblog.controller;

import com.xidian.miniblog.annotation.LoginRequired;
import com.xidian.miniblog.entity.HostHolder;
import com.xidian.miniblog.entity.Page;
import com.xidian.miniblog.entity.Post;
import com.xidian.miniblog.entity.User;
import com.xidian.miniblog.service.FollowService;
import com.xidian.miniblog.service.PostService;
import com.xidian.miniblog.service.UserService;
import com.xidian.miniblog.util.BlogConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author qhhu
 * @date 2020/3/13 - 14:14
 */
@Controller
@RequestMapping("/user")
public class UserController implements BlogConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private FollowService followService;

    @GetMapping("/profile/{userId}")
    public String getUserProfilePage(Model model, Page page, @PathVariable("userId") int userId) {
        User loginUser = hostHolder.getUser();

        page.setRows(postService.getPostRowsByUserId(userId));
        page.setPath("/user/profile/" + userId);

        User user = userService.getUserById(userId);
        model.addAttribute("user", user);

        List<Post> postList = postService.getPostsByUserId(user.getId(), page.getOffset(), page.getLimit());
        model.addAttribute("postList", postList);

        int followerCount = (int) followService.getEntityFollowerCount(BlogConstant.ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        int followeeCount = (int) followService.getUserFolloweeCount(userId, BlogConstant.ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);

        boolean isFollow = false;
        if (loginUser != null) {
            isFollow = followService.getFollowStatus(hostHolder.getUser().getId(), BlogConstant.ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("isFollow", isFollow);

        return "/site/profile";
    }

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/modifypassword")
    public String modifyPassword(Model model, String oldPassword, String newPassword) {
        User loginUser = hostHolder.getUser();

        model.addAttribute("oldPassword", oldPassword);
        model.addAttribute("newPassword", newPassword);

        Map<String, Object> map = userService.modifyPassword(loginUser.getId(), oldPassword, newPassword);
        if (map.containsKey("oldPasswordMsg")) {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            return "/site/setting";
        }
        if (map.containsKey("newPasswordMsg")) {
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/setting";
        }

        model.addAttribute("msg", "密码修改成功，请重新登录");
        model.addAttribute("target", "/login");

        return "/site/operate-result";
    }

}
