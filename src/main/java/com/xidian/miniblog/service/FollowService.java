package com.xidian.miniblog.service;

import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author qhhu
 * @date 2020/3/21 - 18:09
 */
@Service
public class FollowService implements BlogConstant {

    @Autowired
    private RedisTemplate redisTemplate;

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

    /**
     * 返回用户关注的该类型的实体 id。
     */
    public Set<Integer> getUserFolloweeIds(int userId, int entityType, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getUserFolloweeKey(userId, entityType);
        Set<Integer> followeeIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, limit);
        return followeeIds;
    }

    /**
     * 返回该类型实体的粉丝 id。
     */
    public Set<Integer> getEntityFollowerIds(int entityType, int entityId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getEntityFollowerKey(entityType, entityId);
        Set<Integer> followerIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, limit);
        return followerIds;
    }

}
