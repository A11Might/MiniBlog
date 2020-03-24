package com.xidian.miniblog.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author qhhu
 * @date 2020/3/23 - 15:16
 */
@Component
public class EventQueue {

    private static final Logger logger = LoggerFactory.getLogger(EventQueue.class);

    private BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);

    public void add(String json) {
        try {
            queue.put(json);
        } catch (InterruptedException e) {
            logger.error("阻塞队列加入事件出错" + e.getMessage());
        }
    }

    public String poll() {
        String ret = null;
        try {
            ret = queue.take();
        } catch (InterruptedException e) {
            logger.error("阻塞队列取出事件出错" + e.getMessage());
        }

        return ret;
    }

}
