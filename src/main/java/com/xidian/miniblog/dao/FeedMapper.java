package com.xidian.miniblog.dao;

import com.xidian.miniblog.entity.Feed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qhhu
 * @date 2020/3/25 - 13:30
 */
@Mapper
public interface FeedMapper {

    int insertFeed(Feed feed);

    Feed selectFeedById(int id);

    List<Feed> selectFeedsByUserIds(List<Integer> userIds, int offset, int limit);

    int selectFeedsCount(@Param("userIds") List<Integer> userIds);

}
