package com.xidian.miniblog.controller;

import com.xidian.miniblog.entity.Page;
import com.xidian.miniblog.entity.Post;
import com.xidian.miniblog.entity.User;
import com.xidian.miniblog.service.PostService;
import com.xidian.miniblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qhhu
 * @date 2020/3/11 - 16:40
 */
@Controller
public class IndexController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        page.setRows(postService.getPostRows(0));
        page.setPath("/index");
        List<Post> postList = postService.getPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> VOList = new ArrayList<>();
        for (Post post : postList) {
            Map<String, Object> VO = new HashMap<>();
            User author = userService.getUserById(post.getUserId());
            VO.put("author", author);
            VO.put("post", post);
            VOList.add(VO);
        }
        model.addAttribute("VOList", VOList);
        return "/index";
    }

}
