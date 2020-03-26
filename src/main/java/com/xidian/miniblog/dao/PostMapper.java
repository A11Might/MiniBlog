package com.xidian.miniblog.dao;

import com.xidian.miniblog.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author qhhu
 * @date 2020/3/11 - 16:18
 */
@Mapper
public interface PostMapper {

    Post selectPostById(int postId);

    List<Post> selectPostsByUserId(int userId, int offset, int limit);

    // @Param注解用于给参数取别名，若方法中只有一个参数并且需要使用 <if> 或者 <foreach>，则必须加别名。
    int selectPostRowsByUserId(@Param("userId") int userId);

    List<Post> selectPostsByUserIds(List<Integer> userIds, int offset, int limit);

    int selectPostRowsByUserIds(@Param("userIds") List<Integer> userIds);

    int selecttest(Set<Integer> userIds);

    int insertPost(Post post);

    int updateCommentCount(int postId, int commentCount);

    int updateStatus(int postId, int status);

    int updateScore(int postId, double score);

}
