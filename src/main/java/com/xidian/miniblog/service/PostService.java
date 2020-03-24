package com.xidian.miniblog.service;

import com.xidian.miniblog.dao.PostMapper;
import com.xidian.miniblog.entity.Post;
import com.xidian.miniblog.util.BlogConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author qhhu
 * @date 2020/3/11 - 16:40
 */
@Service
public class PostService implements BlogConstant {

    @Autowired
    private PostMapper postMapper;

    public Post getPostById(int postId) {
        return postMapper.selectPostById(postId);
    }

    public List<Post> getPosts(int userId, int offset, int limit) {
        return postMapper.selectPosts(userId, offset, limit);
    }

    public int getPostRows(int userId) {
        return postMapper.selectPostRows(userId);
    }

    public int addPost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 转义 HTML 标签
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        return postMapper.insertPost(post);
    }

    public int deletePost(int postId) {
        return postMapper.updateStatus(postId, ENTITY_STATUS_DELETE);
    }

    public int updatePostCommentCount(int postId, int commentCount) {
        return postMapper.updateCommentCount(postId, commentCount);
    }

    public int updatePostScore(int postId, int score) {
        return postMapper.updateScore(postId, score);
    }

}
