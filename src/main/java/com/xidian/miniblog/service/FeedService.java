package com.xidian.miniblog.service;

import com.xidian.miniblog.dao.FeedMapper;
import com.xidian.miniblog.entity.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qhhu
 * @date 2020/3/25 - 14:01
 */
@Service
public class FeedService {

    @Autowired
    private FeedMapper feedMapper;

    public int addFeed(Feed feed) {
        return feedMapper.insertFeed(feed);
    }

    public Feed getFeedById(int id) {
        return feedMapper.selectFeedById(id);
    }

    public List<Feed> getFeedsByUserIds(List<Integer> userIds, int offset, int limit) {
        return feedMapper.selectFeedsByUserIds(userIds, offset, limit);
    }

    public int getFeedsCount(List<Integer> userIds) {
        return feedMapper.selectFeedsCount(userIds);
    }

}
