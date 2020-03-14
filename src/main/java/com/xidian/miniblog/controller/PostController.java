package com.xidian.miniblog.controller;

import com.xidian.miniblog.entity.HostHolder;
import com.xidian.miniblog.entity.Post;
import com.xidian.miniblog.entity.User;
import com.xidian.miniblog.service.PostService;
import com.xidian.miniblog.service.UserService;
import com.xidian.miniblog.util.BlogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qhhu
 * @date 2020/3/14 - 9:40
 */
@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addPost(String content) {
        User user = hostHolder.getUser();

        if (user == null) {
            // 403 代表没有权限
            return BlogUtil.getJSONString(403, "当前未登录");
        }

        if (StringUtils.isBlank(content)) {
            return BlogUtil.getJSONString(1, "新鲜事不能为空哦");
        }

        Post post = new Post();
        post.setUserId(user.getId());
        post.setContent(content);
        post.setCreateTime(new Date());
        postService.addPost(post);

        return BlogUtil.getJSONString(0, "新鲜事 +1");
    }

    @RequestMapping(path = "/detail/{postId}", method = RequestMethod.GET)
    public String getPostDetailPage(Model model,
                                    @PathVariable("postId") int postId) {
        Post post = postService.getPostById(postId);
        User user = userService.getUserById(post.getUserId());
        Map<String, Object> postVO = new HashMap<>();
        postVO.put("post", post);
        postVO.put("user", user);

        model.addAttribute("postVO", postVO);

        return "/site/post-detail";
    }

}
