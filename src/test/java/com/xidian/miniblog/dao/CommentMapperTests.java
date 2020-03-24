package com.xidian.miniblog.dao;

import com.xidian.miniblog.MiniBlogApplication;
import com.xidian.miniblog.entity.Comment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author qhhu
 * @date 2020/3/14 - 14:04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MiniBlogApplication.class)
public class CommentMapperTests {

    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void insertTest() {
        Comment comment = new Comment();
        comment.setUserId(155);
        comment.setEntityType(0);
        comment.setEntityId(0);
        comment.setTargetId(155);
        comment.setContent("nihk");
        comment.setCreateTime(new Date());
        comment.setScore(1.0);
        for (int i = 0; i < 10; i++) {
            commentMapper.insertComment(comment);
        }
    }

    @Test
    public void selectTest() {
        System.out.println(commentMapper.selectCommentById(234));
        for (Comment comment : commentMapper.selectCommentsByEntity(0, 0, 0, 1)) {
            System.out.println(comment);
        }
        System.out.println(commentMapper.selectCommentCountByEntity(0, 0));
    }

    @Test
    public void updateTest() {
        commentMapper.updateCommentStatus(234, 1);
    }

}
