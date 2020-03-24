package com.xidian.miniblog.service;

import com.xidian.miniblog.dao.MessageMapper;
import com.xidian.miniblog.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qhhu
 * @date 2020/3/23 - 16:27
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    public int addMessageOrNotice(Message message) {
        return messageMapper.insertMessageOrNotice(message);
    }

    public int readMessageOrNotice(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    public Message getLatestNotice(int userId, String eventType) {
        return messageMapper.selectLatestNotice(userId, eventType);
    }

    public List<Message> getNotices(int userId, String eventType, int offset, int limit) {
        return messageMapper.selectNotices(userId, eventType, offset, limit);
    }

    public int getNoticeCount(int userId, String eventType) {
        return messageMapper.selectNoticeCount(userId, eventType);
    }

    public int getNoticeUnreadCount(int userId, String eventType) {
        return messageMapper.selectNoticeUnreadCount(userId, eventType);
    }
}
