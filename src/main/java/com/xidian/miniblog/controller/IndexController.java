package com.xidian.miniblog.controller;

import com.xidian.miniblog.annotation.LoginRequired;
import com.xidian.miniblog.entity.*;
import com.xidian.miniblog.service.*;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

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

    @Autowired
    private FollowService followService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {
        page.setRows(postService.getPostRowsByUserId(0));
        page.setPath("/index?orderMode=" + orderMode);

        List<Post> postList = postService.getPostsByUserId(0, page.getOffset(), page.getLimit(), orderMode);
        model.addAttribute("postVOList", getPostVOListByPostList(postList));

        model.addAttribute("totalNoticeUnreadCount", getTotalNoticeUnreadCount());

        // 标记当前页面是最新或者热门
        model.addAttribute("orderMode", orderMode);

        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }

    @LoginRequired
    @GetMapping(path = "/timelinebypush")
    public String getTimelineByPush(Model model, Page page) {
        User loginUser = hostHolder.getUser();

        String timelineKey = RedisKeyUtil.getTimeLineKey(loginUser.getId());
        Long size = redisTemplate.opsForList().size(timelineKey);
        page.setPath("/timelinebypush");
        page.setRows(size.intValue());

        List<Integer> postIds = redisTemplate.opsForList().range(timelineKey, page.getOffset(), page.getOffset() + page.getLimit() - 1);
        List<Post> postList = new ArrayList<>();
        if (postIds == null || postIds.size() != 0) {
            for (int postId : postIds) {
                Post post = postService.getPostById(postId);
                // 用户删除帖子后会异步删除用户所有粉丝 timeline 中该帖子的 id。
                // 所以可能帖子已经删除了但 timeline 中的帖子 id还在，需要判断。
                if (post != null) {
                    postList.add(post);
                }
            }
        }
        model.addAttribute("postVOList", getPostVOListByPostList(postList));

        model.addAttribute("totalNoticeUnreadCount", getTotalNoticeUnreadCount());

        return "/index";
    }

    @LoginRequired
    @GetMapping(path = "/timelinebypull")
    public String getTimelineByPull(Model model, Page page) {
        User loginUser = hostHolder.getUser();
        List<Integer> followeeIds = setToList(followService.getUserFolloweeIds(loginUser.getId(), ENTITY_TYPE_USER, 0, -1));
        // 若没有关注其他用户，则直接返回。
        if (followeeIds == null || followeeIds.size() == 0) {
            return "/index";
        }

        int rows = postService.getPostsRowsByUserIds(followeeIds);
        page.setPath("/timelinebypull");
        page.setRows(rows);

        List<Post> postList = postService.getPostsByUserIds(followeeIds, page.getOffset(), page.getLimit());
        model.addAttribute("postVOList", getPostVOListByPostList(postList));

        model.addAttribute("totalNoticeUnreadCount", getTotalNoticeUnreadCount());

        // 标记当前是关注页面
        model.addAttribute("orderMode", 2);

        return "/index";
    }

    private List<Integer> setToList(Set<Integer> set) {
        List<Integer> followeeUserIds = new ArrayList<>();

        for (Integer followeeUserId : set) {
            followeeUserIds.add(followeeUserId);
        }

        return followeeUserIds;
    }

    private List<Map<String, Object>> getPostVOListByPostList(List<Post> postList) {
        User loginUser = hostHolder.getUser();

        List<Map<String, Object>> postVOList = new ArrayList<>();
        // 每个帖子都有一个评论框，使用 level 标记每个帖子中的评论框（id = commentContent + level）。
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
            int commentCount = postService.getPostCommentCount(post.getId());

            long postLikeCount = likeService.getEntityLikeCount(ENTITY_TYPE_POST, post.getId());
            // 这里可能未登录，需要判断。
            boolean postLikeStatus = loginUser == null ? false : likeService.getLikeStatus(loginUser.getId(), ENTITY_TYPE_POST, post.getId());

            postVO.put("author", author);
            post.setContent(post.getContent().replaceAll("\n", "<br>"));
            if (post.getContent().length() > 150) {
                post.setContent(post.getContent().substring(0, 150));
            }
            postVO.put("post", post);
            postVO.put("level", level++);
            postVO.put("commentVOList", commentVOList);
            postVO.put("commentCount", commentCount);
            postVO.put("likeCount", postLikeCount);
            postVO.put("likeStatus", postLikeStatus);
            postVOList.add(postVO);
        }

        return postVOList;
    }

    private int getTotalNoticeUnreadCount() {
        User loginUser = hostHolder.getUser();

        if (loginUser != null) {
            return messageService.getNoticeUnreadCount(loginUser.getId(), null);
        }
        return 0;
    }

}
