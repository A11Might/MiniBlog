package com.xidian.miniblog.dao;

import com.xidian.miniblog.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qhhu
 * @date 2020/3/11 - 16:18
 */
@Mapper
public interface PostMapper {

    Post selectPostById(int id);

    /**
     * 当 userId == 0 时，查询所有帖子；当 userId ！= 0 时，查询 user_id = userId 的帖子。
     */
    List<Post> selectPostsByUserId(int userId, int offset, int limit, int orderMode);

    /**
     * 当 userId == 0 时，查询所有帖子数量；当 userId ！= 0 时，查询 user_id = userId 的帖子数量。
     * @Param 注解用于给参数取别名，若方法中只有一个参数并且需要使用 <if> 或者 <foreach>，则必须加别名。
     */
    int selectPostRowsByUserId(@Param("userId") int userId);

    List<Post> selectPostsByUserIds(List<Integer> userIds, int offset, int limit);

    int selectPostRowsByUserIds(@Param("userIds") List<Integer> userIds);

    int insertPost(Post post);

    int selectCommentCount(int id);

    int updateCommentCount(int id, int commentCount);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);

}
