package com.xidian.miniblog.dao;

import com.xidian.miniblog.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author qhhu
 * @date 2020/3/23 - 15:44
 */
@Mapper
public interface MessageMapper {

    int insertMessageOrNotice(Message message);

    int updateStatus(List<Integer> ids, int status);

    Message selectLatestNotice(int userId, String eventType);

    List<Message> selectNotices(int userId, String eventType, int offset, int limit);

    int selectNoticeCount(int userId, String eventType);

    int selectNoticeUnreadCount(int userId, String eventType);

}
