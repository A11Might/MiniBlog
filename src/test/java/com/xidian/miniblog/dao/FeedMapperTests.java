package com.xidian.miniblog.dao;

import com.xidian.miniblog.MiniBlogApplication;
import com.xidian.miniblog.entity.Feed;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author qhhu
 * @date 2020/3/25 - 13:46
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MiniBlogApplication.class)
public class FeedMapperTests {

    @Autowired
    private FeedMapper feedMapper;

    @Test
    public void insertTest() {
        Feed feed = new Feed();
        feed.setType(4);
        feed.setUserId(155);
        feed.setData("新鲜事");
        feed.setStatus(0);
        feed.setCreateTime(new Date());
        for (int i = 0; i < 10; i++) {
            feedMapper.insertFeed(feed);
        }
    }

    @Test
    public void selectTest() {
        System.out.println(feedMapper.selectFeedById(391));
        List<Integer> ids = new ArrayList<>();
        ids.add(155);
        ids.add(144);
        for (Feed feed : feedMapper.selectFeedsByUserIds(ids, 0, 5)) {
            System.out.println(feed);
        }
    }

    @Test
    public void selectCountTest() {
        List<Integer> userIds = new ArrayList<>();
        System.out.println(feedMapper.selectFeedsCount(userIds));
    }

}
