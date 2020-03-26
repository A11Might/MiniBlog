package com.xidian.miniblog.service;

import com.xidian.miniblog.entity.HostHolder;
import com.xidian.miniblog.entity.User;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author qhhu
 * @date 2020/3/21 - 18:09
 */
@Service
public class FollowService implements BlogConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private HostHolder hostHolder;

    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getUserFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getEntityFollowerKey(entityType, entityId);

                // 开启事务
                redisOperations.multi();

                redisOperations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                // 执行事务
                return redisOperations.exec();
            }
        });
    }

    public void unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getUserFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getEntityFollowerKey(entityType, entityId);

                // 开启事务
                redisOperations.multi();

                redisOperations.opsForZSet().remove(followeeKey, entityId, System.currentTimeMillis());
                redisOperations.opsForZSet().remove(followerKey, userId, System.currentTimeMillis());

                // 执行事务
                return redisOperations.exec();
            }
        });
    }

    /**
     * 获取用户关注某类实体的数量。
     *
     * @param userId
     * @param entityType
     * @return
     */
    public long getUserFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getUserFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * 获取实体粉丝的数量。
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public long getEntityFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getEntityFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }


    public boolean getFollowStatus(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getEntityFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().score(followerKey, userId) != null;
    }

    public Set<Integer> getUserFolloweeIds(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getUserFolloweeKey(userId, entityType);
        Set<Integer> followeeIds = redisTemplate.opsForZSet().reverseRange(followeeKey, 0, -1);
        return followeeIds;
    }

    public Set<Integer> getEntityFollowerIds(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getEntityFollowerKey(entityType, entityId);
        Set<Integer> followerIds = redisTemplate.opsForZSet().reverseRange(followerKey, 0, -1);
        return followerIds;
    }

    public List<Map<String, Object>> getUserFolloweeList(int userId, int entityType, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getUserFolloweeKey(userId, entityType);
        Set<Integer> followeeIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        if (followeeIds == null) {
            return null;
        }

        List<Map<String, Object>> followeeVOList = new ArrayList<>();
        for (Integer followeeId : followeeIds) {
            Map<String, Object> followeeVO = new HashMap<>();
            Object followee = null;
            if (entityType == ENTITY_TYPE_USER) {
                followee = userService.getUserById(followeeId);
            } else if (entityType == ENTITY_TYPE_POST) {
                followee = postService.getPostById(followeeId);
            }
            Double score = redisTemplate.opsForZSet().score(followeeKey, followeeId);
            boolean isFollow = false;
            if (hostHolder.getUser() != null) {
                isFollow = getFollowStatus(hostHolder.getUser().getId(), entityType, followeeId);
            }
            followeeVO.put("followee", followee);
            followeeVO.put("followTime", new Date(score.longValue()));
            followeeVO.put("isFollow", isFollow);
            followeeVOList.add(followeeVO);
        }

        return followeeVOList;
    }

    public List<Map<String, Object>> getEntityFollowerList(int entityType, int entityId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getEntityFollowerKey(entityType, entityId);
        Set<Integer> followerIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        if (followerIds == null) {
            return null;
        }

        List<Map<String, Object>> followerVOList = new ArrayList<>();
        for (Integer followerId : followerIds) {
            Map<String, Object> followerVO = new HashMap<>();
            User follower = userService.getUserById(followerId);
            Double score = redisTemplate.opsForZSet().score(followerKey, followerId);
            boolean isFollow = false;
            if (hostHolder.getUser() != null) {
                isFollow = getFollowStatus(hostHolder.getUser().getId(), entityType, entityId);
            }
            followerVO.put("follower", follower);
            followerVO.put("followTime", new Date(score.longValue()));
            followerVO.put("isFollow", isFollow);
            followerVOList.add(followerVO);
        }

        return followerVOList;
    }
}
