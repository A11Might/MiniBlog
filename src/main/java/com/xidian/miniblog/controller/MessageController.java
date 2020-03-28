package com.xidian.miniblog.controller;

import com.alibaba.fastjson.JSONObject;
import com.xidian.miniblog.annotation.LoginRequired;
import com.xidian.miniblog.entity.HostHolder;
import com.xidian.miniblog.entity.Message;
import com.xidian.miniblog.entity.Page;
import com.xidian.miniblog.entity.User;
import com.xidian.miniblog.service.MessageService;
import com.xidian.miniblog.service.UserService;
import com.xidian.miniblog.util.BlogConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qhhu
 * @date 2020/3/23 - 16:33
 */
@Controller
public class MessageController implements BlogConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @LoginRequired
    @GetMapping("/notice/list")
    public String getNoticeListPage(Model model) {
        User loginUser = hostHolder.getUser();

        // 查询点赞通知
        Message notice = messageService.getLatestNotice(loginUser.getId(), String.valueOf(EVENT_TYPE_LIKE));
        if (notice != null) {
            Map<String, Object> noticeVO = getLatestNoticeVO(notice);
            model.addAttribute("likeNoticeVO", noticeVO);
        }

        // 查询评论通知
        notice = messageService.getLatestNotice(loginUser.getId(), String.valueOf(EVENT_TYPE_COMMENT));
        if (notice != null) {
            Map<String, Object> noticeVO = getLatestNoticeVO(notice);
            model.addAttribute("commentNoticeVO", noticeVO);
        }

        // 查询关注通知
        notice = messageService.getLatestNotice(loginUser.getId(), String.valueOf(EVENT_TYPE_FOLLOW));
        if (notice != null) {
            Map<String, Object> noticeVO = getLatestNoticeVO(notice);
            model.addAttribute("followNoticeVO", noticeVO);
        }

        int totalNoticeUnreadCount = messageService.getNoticeUnreadCount(loginUser.getId(), null);
        model.addAttribute("totalNoticeUnreadCount", totalNoticeUnreadCount);

        return "/site/notice";
    }

    @LoginRequired
    @GetMapping("/notice/detail/{eventType}")
    public String getNoticeDetailPage(Page page, Model model, @PathVariable("eventType") int eventType) {
        User loginUser = hostHolder.getUser();

        page.setLimit(5);
        page.setRows(messageService.getNoticeCount(loginUser.getId(), String.valueOf(eventType)));
        page.setPath("/notice/detail/" + eventType);

        List<Message> noticeList = messageService.getNotices(loginUser.getId(), String.valueOf(eventType), page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVOList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> noticeVO = new HashMap<>();

                noticeVO.put("notice", notice);

                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                noticeVO.put("actorUser", userService.getUserById((Integer) data.get("actorId")));
                noticeVO.put("entityType", data.get("entityType"));
                noticeVO.put("entityId", data.get("entityId"));
                noticeVOList.add(noticeVO);
            }
        }
        model.addAttribute("noticeVOList", noticeVOList);
        model.addAttribute("eventType", eventType);

        // 设置通知已读
        List<Integer> ids = getNoticeIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessageOrNotice(ids);
        }

        return "/site/notice-detail";
    }

    private Map<String, Object> getLatestNoticeVO(Message notice) {
        User loginUser = hostHolder.getUser();

        Map<String, Object> noticeVO = new HashMap<>();
        noticeVO.put("notice", notice);

        String content = HtmlUtils.htmlUnescape(notice.getContent());
        Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
        noticeVO.put("actorUser", userService.getUserById((Integer) data.get("actorId")));
        noticeVO.put("entityType", data.get("entityType"));
        noticeVO.put("entityId", data.get("entityId"));

        // 通知的 conversation_id 存的是 eventType。
        int count = messageService.getNoticeCount(loginUser.getId(), notice.getConversationId());
        noticeVO.put("count", count);

        int unreadCount = messageService.getNoticeUnreadCount(loginUser.getId(), notice.getConversationId());
        noticeVO.put("unreadCount", unreadCount);

        return noticeVO;
    }

    private List<Integer> getNoticeIds(List<Message> noticeList) {
        List<Integer> ids = new ArrayList<>();
        for (Message notice : noticeList) {
            ids.add(notice.getId());
        }
        return ids;
    }

}
