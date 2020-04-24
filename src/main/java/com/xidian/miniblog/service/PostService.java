package com.xidian.miniblog.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.xidian.miniblog.dao.PostMapper;
import com.xidian.miniblog.entity.Post;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.RedisKeyUtil;
import com.xidian.miniblog.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author qhhu
 * @date 2020/3/11 - 16:40
 */
@Service
public class PostService implements BlogConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expiredSecond;

    private LoadingCache<String, List<Post>> postListCache;

    /**
     * 一级缓存：初始化热门微博列表缓存
     */
    @PostConstruct
    public void init() {
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expiredSecond, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<Post>>() {
                    @Nullable
                    @Override
                    public List<Post> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误");
                        }

                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误");
                        }

                        int offset = Integer.parseInt(params[0]);
                        int limit = Integer.parseInt(params[1]);

                        // 二级缓存
                        List<Post> postList = getCache(offset, limit);
                        if (postList != null) {
                            logger.debug("load post list from 二级缓存");
                            return postList;
                        }

                        // 从数据库中查询
                        logger.debug("load post list from DB");
                        return initCache(offset, limit);
                    }
                });
    }

    public Post getPostById(int id) {
        return postMapper.selectPostById(id);
    }

    public List<Post> getPostsByUserId(int userId, int offset, int limit, int orderMode) {
        // 只缓存了首页的热门微博
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }

        logger.debug("load post list from DB");
        return postMapper.selectPostsByUserId(userId, offset, limit, orderMode);
    }

    public int getPostRowsByUserId(int userId) {
        return postMapper.selectPostRowsByUserId(userId);
    }

    public List<Post> getPostsByUserIds(List<Integer> userIds, int offset, int limit) {
        return postMapper.selectPostsByUserIds(userIds, offset, limit);
    }

    public int getPostsRowsByUserIds(List<Integer> userIds) {
        return postMapper.selectPostRowsByUserIds(userIds);
    }

    public int addPost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 转义 HTML 标签
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return postMapper.insertPost(post);
    }

    public int deletePost(int id) {
        return postMapper.updateStatus(id, ENTITY_STATUS_DELETE);
    }

    public int getPostCommentCount(int id) {
        return postMapper.selectCommentCount(id);
    }

    public int updatePostCommentCount(int id, int commentCount) {
        return postMapper.updateCommentCount(id, commentCount);
    }

    public int updatePostScore(int id, double score) {
        return postMapper.updateScore(id, score);
    }

    /**
     * 二级缓存：使用 redis 缓存热门微博列表
     */
    public List<Post> getCache(int offset, int limit) {
        String postListKey = RedisKeyUtil.getPostListKey(offset, limit);
        return (List<Post>) redisTemplate.opsForValue().get(postListKey);
    }

    public List<Post> initCache(int offset, int limit) {
        List<Post> postList = postMapper.selectPostsByUserId(0, offset, limit, 1);

        String postListKey = RedisKeyUtil.getPostListKey(offset, limit);
        redisTemplate.opsForValue().set(postListKey, postList, 60 * 5, TimeUnit.SECONDS);

        return postList;
    }

    public void clearCache(int offset, int limit) {
        String postListKey = RedisKeyUtil.getPostListKey(offset, limit);
        redisTemplate.delete(postListKey);
    }

}
