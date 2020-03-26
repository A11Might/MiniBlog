package com.xidian.miniblog.controller;

import com.xidian.miniblog.entity.*;
import com.xidian.miniblog.service.*;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
 * @date 2020/3/25 - 14:03
 */
@Controller
public class FeedController implements BlogConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FeedService feedService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/feedsbypush", method = RequestMethod.GET)
    public String getFeedsByPush(Model model, Page page) {
        User loginUser = hostHolder.getUser();

        String timelineKey = RedisKeyUtil.getTimeLineKey(loginUser.getId());
        Long size = redisTemplate.opsForList().size(timelineKey);
        page.setPath("/feedsbypush");
        page.setRows(size.intValue());

        List<Integer> feedIds = redisTemplate.opsForList().range(timelineKey, page.getOffset(), page.getOffset() + page.getLimit() - 1);

        List<Post> postList = new ArrayList<>();
        if (feedIds == null || feedIds.size() != 0) {
            for (int feedId : feedIds) {
                Feed feed = feedService.getFeedById(feedId);
                Post post = postService.getPostById(Integer.parseInt(feed.getData()));
                if (post != null) {
                    postList.add(post);
                }
            }
        }

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
