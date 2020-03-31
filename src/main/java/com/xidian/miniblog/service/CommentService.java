package com.xidian.miniblog.service;

import com.xidian.miniblog.dao.CommentMapper;
import com.xidian.miniblog.entity.Comment;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author qhhu
 * @date 2020/3/14 - 14:11
 */
@Service
public class CommentService implements BlogConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostService postService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public Comment getCommentById(int commentId) {
        return commentMapper.selectCommentById(commentId);
    }

    public List<Comment> getCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int getCommentCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCommentCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数为空");
        }

        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        // 更新帖子评论数（不包括回复的数量）
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = getCommentCountByEntity(comment.getEntityType(), comment.getEntityId());
            postService.updatePostCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int deleteComment(int commentId) {
        Comment comment = commentMapper.selectCommentById(commentId);
        int rows = commentMapper.updateCommentStatus(commentId, ENTITY_STATUS_DELETE);

        int count = getCommentCountByEntity(comment.getEntityType(), comment.getEntityId());
        postService.updatePostCommentCount(comment.getEntityId(), count);

        return rows;
    }

    public int updateCommentScore(int id, int score) {
        return commentMapper.updateCommentScore(id, score);
    }

}
