package com.xidian.miniblog.controller;

import com.xidian.miniblog.entity.*;
import com.xidian.miniblog.service.*;
import com.xidian.miniblog.util.BlogConstant;
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
public class IndexController implements BlogConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private MessageService messageService;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String root() {
        return "forward:/index";
    }

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        User loginUser = hostHolder.getUser();

        page.setRows(postService.getPostRows(0));
        page.setPath("/index");

        List<Post> postList = postService.getPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> postVOList = new ArrayList<>();
        int level = 1;
        for (Post post : postList) {
            Map<String, Object> postVO = new HashMap<>();
            User author = userService.getUserById(post.getUserId());
            List<Comment> commentList =  commentService.getCommentsByEntity(ENTITY_TYPE_POST, post.getId(), 0, 3);
            List<Map<String, Object>> commentVOList = null;
            if (commentList != null) {
                commentVOList = new ArrayList<>();
                for (Comment comment : commentList) {
                    Map<String, Object> commentVO = new HashMap<>();
                    User commentUser = userService.getUserById(comment.getUserId());
                    long commentLikeCount = likeService.getEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                    boolean commentLikeStatus = loginUser == null ? false : likeService.getLikeStatus(loginUser.getId(), ENTITY_TYPE_COMMENT, comment.getId());
                    commentVO.put("commentUser", commentUser);
                    commentVO.put("comment", comment);
                    commentVO.put("likeCount", commentLikeCount);
                    commentVO.put("likeStatus", commentLikeStatus);
                    commentVOList.add(commentVO);
                }
            }
            long postLikeCount = likeService.getEntityLikeCount(ENTITY_TYPE_POST, post.getId());
            // 这里可能未登录，需要判断。
            boolean postLikeStatus = loginUser == null ? false : likeService.getLikeStatus(loginUser.getId(), ENTITY_TYPE_POST, post.getId());
            postVO.put("author", author);
            postVO.put("post", post);
            postVO.put("level", level++);
            postVO.put("commentVOList", commentVOList);
            postVO.put("likeCount", postLikeCount);
            postVO.put("likeStatus", postLikeStatus);
            postVOList.add(postVO);
        }
        model.addAttribute("postVOList", postVOList);

        if (loginUser != null) {
            int totalNoticeUnreadCount = messageService.getNoticeUnreadCount(loginUser.getId(), null);
            model.addAttribute("totalNoticeUnreadCount", totalNoticeUnreadCount);
        }

        return "/index";
    }

}
