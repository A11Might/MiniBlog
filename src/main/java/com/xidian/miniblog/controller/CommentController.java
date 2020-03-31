package com.xidian.miniblog.controller;

import com.xidian.miniblog.async.EventProducer;
import com.xidian.miniblog.entity.*;
import com.xidian.miniblog.service.CommentService;
import com.xidian.miniblog.service.PostService;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.BlogUtil;
import com.xidian.miniblog.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author qhhu
 * @date 2020/3/14 - 15:52
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements BlogConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/add")
    @ResponseBody
    public String addComment(String content, int postId) {
        User loginUser = hostHolder.getUser();

        if (loginUser == null) {
            // 403 代表没有权限
            return BlogUtil.getJSONString(403, "当前未登录");
        }

        if (StringUtils.isBlank(content)) {
            return BlogUtil.getJSONString(1, "评论不能为空哦");
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUserId(hostHolder.getUser().getId());
        comment.setEntityType(ENTITY_TYPE_POST);
        comment.setEntityId(postId);
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 通知帖子作者
        Post post = postService.getPostById(postId);
        if (post.getUserId() != loginUser.getId()) {
            Event event = new Event()
                    .setType(EVENT_TYPE_COMMENT)
                    .setActorId(loginUser.getId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(postId)
                    .setEntityOwnerId(postService.getPostById(postId).getUserId());
            eventProducer.fireEvent(event);
        }

        // 将该更新分数的微博存入 redis 中，采用定时任务的方式来更新分数
        String postScoreKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(postScoreKey, postId);

        return BlogUtil.getJSONString(0, "评论 +1");
    }

    @PostMapping("/delete")
    @ResponseBody
    public String deleteComment(int commentId) {
        commentService.deleteComment(commentId);

        return BlogUtil.getJSONString(0, "评论删除成功");
    }

}
