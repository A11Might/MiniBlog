package com.xidian.miniblog.dao;

import com.xidian.miniblog.MiniBlogApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author qhhu
 * @date 2020/3/22 - 17:22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MiniBlogApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void listTest() {
        String key = "test:queue";
        redisTemplate.opsForList().rightPush(key, "1");
        redisTemplate.opsForList().rightPush(key, "2");
        redisTemplate.opsForList().rightPush(key, "3");
        redisTemplate.opsForList().rightPush(key, "4");
        while (true) {
            System.out.println(redisTemplate.opsForList().leftPop(key, 0, TimeUnit.SECONDS));
        }
    }

}
