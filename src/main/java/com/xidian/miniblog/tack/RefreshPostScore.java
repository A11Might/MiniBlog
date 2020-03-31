package com.xidian.miniblog.tack;

import com.xidian.miniblog.entity.Post;
import com.xidian.miniblog.service.LikeService;
import com.xidian.miniblog.service.PostService;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qhhu
 * @date 2020/3/31 - 10:08
 */
@Component
public class RefreshPostScore implements BlogConstant {

    private static final Logger logger = LoggerFactory.getLogger(RefreshPostScore.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    // 我的纪元（2018-10-16）
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-10-16 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化我的纪元失败：" + e);
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void execute() {
        String postScoreKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(postScoreKey);

        if (operations.size() == 0) {
            logger.info("没有需要刷新分数的微博");
            return;
        }

        logger.info("正在刷新微博分数，需要刷新的微博数为：" + operations.size());
        while (operations.size() > 0) {
            refresh((Integer) operations.pop());
        }
        logger.info("微博分数刷新完成");
    }

    private void refresh(int postId) {
        Post post = postService.getPostById(postId);

        if (post == null) {
            logger.error("该帖子不存在：id = " + postId);
            return;
        }

        long likeCount = likeService.getEntityLikeCount(ENTITY_TYPE_POST, postId);
        int commentCount = postService.getPostCommentCount(postId);

        double w = likeCount * 1 + commentCount * 5;
        double score = Math.log10(Math.max(w, 1)) + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);

        postService.updatePostScore(postId, score);
    }

}
