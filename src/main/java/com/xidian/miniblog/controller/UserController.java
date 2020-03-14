package com.xidian.miniblog.controller;

import com.xidian.miniblog.entity.HostHolder;
import com.xidian.miniblog.entity.Page;
import com.xidian.miniblog.entity.Post;
import com.xidian.miniblog.entity.User;
import com.xidian.miniblog.service.PostService;
import com.xidian.miniblog.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * @author qhhu
 * @date 2020/3/13 - 14:14
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getUserProfilePage(Model model,
                                     @PathVariable("userId") int userId,
                                     Page page) {
        page.setRows(postService.getPostRows(userId));
        page.setPath("/user/profile/" + userId);

        User user = userService.getUserById(userId);
        model.addAttribute("user", user);

        List<Post> postList = postService.getPosts(user.getId(), page.getOffset(), page.getLimit());
        model.addAttribute("postList", postList);

        return "/site/profile";
    }

    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {

        return "/site/setting";

    }

    @RequestMapping(path = "/modifypassword", method = RequestMethod.POST)
    public String modifyPassword(Model model, String oldPassword, String newPassword) {
        model.addAttribute("oldPassword", oldPassword);
        model.addAttribute("newPassword", newPassword);

        User user = hostHolder.getUser();

        Map<String, Object> map = userService.modifyPassword(user.getId(), oldPassword, newPassword);
        if (map.containsKey("oldPasswordMsg")) {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            return "/site/setting";
        }
        if (map.containsKey("newPasswordMsg")) {
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/setting";
        }

        return "redirect:/index";

    }

}
