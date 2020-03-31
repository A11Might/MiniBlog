package com.xidian.miniblog.dao;

import com.xidian.miniblog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author qhhu
 * @date 2020/3/14 - 13:53
 */
@Mapper
public interface CommentMapper {

    Comment selectCommentById(int id);

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCommentCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    int updateCommentStatus(int id, int status);

    int updateCommentScore(int id, int score);

}
