package com.xidian.miniblog.controller;

import com.xidian.miniblog.async.EventProducer;
import com.xidian.miniblog.entity.*;
import com.xidian.miniblog.service.CommentService;
import com.xidian.miniblog.service.LikeService;
import com.xidian.miniblog.service.PostService;
import com.xidian.miniblog.service.UserService;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.BlogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author qhhu
 * @date 2020/3/14 - 9:40
 */
@Controller
@RequestMapping("/post")
public class PostController implements BlogConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addPost(String content) {
        User loginUser = hostHolder.getUser();

        if (loginUser == null) {
            // 403 代表没有权限
            return BlogUtil.getJSONString(403, "当前未登录");
        }

        if (StringUtils.isBlank(content)) {
            return BlogUtil.getJSONString(1, "新鲜事不能为空哦");
        }

        Post post = new Post();
        post.setUserId(loginUser.getId());
        post.setContent(HtmlUtils.htmlEscape(content));
        post.setCreateTime(new Date());
        postService.addPost(post);

        Event event = new Event()
                .setType(EVENT_TYPE_PUBLISH)
                .setActorId(loginUser.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        return BlogUtil.getJSONString(0, "新鲜事 +1");
    }

    @RequestMapping(path = "/detail/{postId}", method = RequestMethod.GET)
    public String getPostDetailPage(Model model,
                                    Page page,
                                    @PathVariable("postId") int postId) {
        page.setRows(commentService.getCommentCountByEntity(ENTITY_TYPE_POST, postId));
        page.setPath("/post/detail/" + postId);

        Post post = postService.getPostById(postId);
        User user = userService.getUserById(post.getUserId());
        User loginUser = hostHolder.getUser();
        List<Comment> commentList = commentService.getCommentsByEntity(ENTITY_TYPE_POST, postId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        for (Comment comment : commentList) {
            Map<String, Object> commentVO = new HashMap<>();
            User commentUser = userService.getUserById(comment.getUserId());
            long commentLikeCount = likeService.getEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
            boolean commentLikeStatus = loginUser == null ? false : likeService.getLikeStatus(loginUser.getId(), ENTITY_TYPE_COMMENT, comment.getId());
            commentVO.put("likeCount", commentLikeCount);
            commentVO.put("likeStatus", commentLikeStatus);
            commentVO.put("commentUser", commentUser);
            commentVO.put("comment", comment);
            commentVOList.add(commentVO);
        }
        Map<String, Object> postVO = new HashMap<>();
        long postLikeCount = likeService.getEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        // 这里可能未登录，需要判断。
        boolean postLikeStatus = loginUser == null ? false : likeService.getLikeStatus(loginUser.getId(), ENTITY_TYPE_POST, post.getId());
        postVO.put("likeCount", postLikeCount);
        postVO.put("likeStatus", postLikeStatus);
        postVO.put("post", post);
        postVO.put("user", user);
        postVO.put("commentVOList", commentVOList);

        model.addAttribute("postVO", postVO);

        return "/site/post-detail";
    }

    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deletePost(int postId) {
        postService.deletePost(postId);

        return BlogUtil.getJSONString(0, "微博删除成功");
    }

}
