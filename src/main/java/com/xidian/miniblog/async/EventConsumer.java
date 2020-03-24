package com.xidian.miniblog.async;

import com.alibaba.fastjson.JSONObject;
import com.xidian.miniblog.entity.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qhhu
 * @date 2020/3/22 - 17:04
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<Integer, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Autowired
    private EventQueue eventQueue;


    @Override
    public void afterPropertiesSet() throws Exception {
        // 通过当前上下文获取所有实现 EventHandler 接口的类，后将所有实现的 handler 通过其所注册的事件类型与事件关联起来。
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<Integer> eventTypes = entry.getValue().getSupportEventTypes();

                for (int type : eventTypes) {
                    if (config.get(type) == null) {
                        config.put(type, new ArrayList<>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }

        // 处理事件
        new Thread(() -> {
            while (true) {
                String json = eventQueue.poll();
                Event event = JSONObject.parseObject(json, Event.class);

                if (!config.containsKey(event.getType())) {
                    logger.error("不能识别的事件");
                    continue;
                }

                for (EventHandler handler : config.get(event.getType())) {
                    handler.doHandler(event);
                }
            }
        }).start();
    }

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
