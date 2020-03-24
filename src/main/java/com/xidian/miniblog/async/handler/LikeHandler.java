package com.xidian.miniblog.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.xidian.miniblog.async.EventHandler;
import com.xidian.miniblog.entity.Event;
import com.xidian.miniblog.entity.Message;
import com.xidian.miniblog.service.MessageService;
import com.xidian.miniblog.util.BlogConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author qhhu
 * @date 2020/3/23 - 14:44
 */
@Component
public class LikeHandler implements EventHandler, BlogConstant {

    @Autowired
    private MessageService messageService;

    @Override
    public void doHandler(Event event) {
        Message notice = new Message();
        notice.setFromId(SYSTEM_USER);
        notice.setToId(event.getEntityOwnerId());
        notice.setConversationId(String.valueOf(event.getType()));
        Map<String, Object> map = new HashMap<>();
        map.put("actorId", event.getActorId());
        map.put("entityType", event.getEntityType());
        map.put("entityId", event.getEntityId());
        String content = HtmlUtils.htmlEscape(JSONObject.toJSONString(map));
        notice.setContent(content);
        notice.setStatus(0);
        notice.setCreateTime(new Date());
        messageService.addMessageOrNotice(notice);
    }

    @Override
    public List<Integer> getSupportEventTypes() {
        return Arrays.asList(EVENT_TYPE_LIKE);
    }
}
