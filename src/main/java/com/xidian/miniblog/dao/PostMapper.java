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

    Post selectPostById(int postId);

    List<Post> selectPosts(int userId, int offset, int limit);

    // @Param注解用于给参数取别名
    // 如果只有一个参数并且在<if>里使用，则必须加别名
    int selectPostRows(@Param("userId") int userId);

    int insertPost(Post post);

    int updateCommentCount(int postId, int commentCount);

    int updateStatus(int postId, int status);

    int updateScore(int postId, double score);

}
