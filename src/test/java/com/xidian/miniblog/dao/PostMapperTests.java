package com.xidian.miniblog.dao;

import com.xidian.miniblog.MiniBlogApplication;
import com.xidian.miniblog.entity.Post;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @author qhhu
 * @date 2020/3/11 - 16:34
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MiniBlogApplication.class)
public class PostMapperTests {

    @Autowired
    private PostMapper postMapper;

    @Test
    public void insertTest() {
        Post post = new Post();
        post.setUserId(155);
        post.setContent("33333333333333333333！今天天气真好！今天天气真好！今天天气真好！今天天气真好！今天天气真好！");
        post.setStatus(0);
        post.setCommentCount(0);
        post.setCreateTime(new Date());
        for (int i = 0; i < 5; i++) {
            postMapper.insertPost(post);
        }
    }

    @Test
    public void selectTest() {
        Post post = postMapper.selectPostById(282);
        List<Post> list = postMapper.selectPostsByUserId(0, 0, 10);
        System.out.println(post);
        for (Post p : list) {
            System.out.println(p);
        }
    }

    @Test
    public void selectRows() {
        System.out.println(postMapper.selectPostRowsByUserId(0));
        System.out.println(postMapper.selectPostRowsByUserId(155));
    }

    @Test
    public void updateTest() {
        postMapper.updateStatus(282, 0);
        postMapper.updateCommentCount(282, 100);
        postMapper.updateScore(282, 10.0);
        System.out.println(postMapper.selectPostById(282));
    }

    @Test
    public void selectTests() {
        List<Integer> list = new ArrayList<>();
        list.add(155);
        list.add(144);
        for (Post post : postMapper.selectPostsByUserIds(list, 0, 100)) {
            System.out.println(post);
        }
        System.out.println(postMapper.selectPostRowsByUserIds(list));
    }
}
