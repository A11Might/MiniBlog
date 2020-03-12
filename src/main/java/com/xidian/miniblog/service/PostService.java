package com.xidian.miniblog.service;

import com.xidian.miniblog.dao.PostMapper;
import com.xidian.miniblog.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qhhu
 * @date 2020/3/11 - 16:40
 */
@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    public List<Post> getPosts(int userId, int offset, int limit) {
        return postMapper.selectPosts(userId, offset, limit);
    }

    public Post getPostById(int postId) {
        return postMapper.selectPostById(postId);
    }

    public int getPostRows(int userId) {
        return postMapper.selectPostRows(userId);
    }

}
