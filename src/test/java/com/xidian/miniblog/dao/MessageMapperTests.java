package com.xidian.miniblog.dao;

import com.xidian.miniblog.MiniBlogApplication;
import com.xidian.miniblog.entity.Message;
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
 * @date 2020/3/23 - 16:07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MiniBlogApplication.class)
public class MessageMapperTests {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void insertTest() {
        Message message = new Message();
        message.setFromId(1);
        message.setToId(155);
        message.setConversationId("1_155");
        message.setContent("通知内容");
        message.setStatus(0);
        message.setCreateTime(new Date());
        for (int i = 0; i < 10; i++) {
            messageMapper.insertMessageOrNotice(message);
        }
    }

    @Test
    public void updateTest() {
        List<Integer> list = new ArrayList<>();
        for (int i = 360; i <= 365; i++) {
            list.add(i);
        }
        messageMapper.updateStatus(list, 1);
    }

    @Test
    public void selectTest() {
        System.out.println(messageMapper.selectLatestNotice(155, "1_155"));
        System.out.println(messageMapper.selectNoticeCount(155, "1_155"));
        System.out.println(messageMapper.selectNoticeUnreadCount(155, "1_155"));
        System.out.println(messageMapper.selectNoticeUnreadCount(155, null));
        List<Message> messages = messageMapper.selectNotices(155, "1_155", 0, 10);
        for (Message message : messages) {
            System.out.println(message);
        }
    }

}
