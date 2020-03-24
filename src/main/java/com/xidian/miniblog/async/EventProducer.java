package com.xidian.miniblog.async;

import com.alibaba.fastjson.JSONObject;
import com.xidian.miniblog.entity.Event;
import com.xidian.miniblog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author qhhu
 * @date 2020/3/22 - 16:55
 */
@Service
public class EventProducer {

    @Autowired
    private EventQueue eventQueue;

    /**
     * 发布事件，将 Event 对象转化为 String 存储在阻塞队列中。
     *
     * @param event
     */
    public void fireEvent(Event event) {
        String eventQueueKey = RedisKeyUtil.getEventQueueKey();
        String json = JSONObject.toJSONString(event);
        eventQueue.add(json);
    }

}
