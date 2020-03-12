package com.xidian.miniblog.dao;

import com.xidian.miniblog.MiniBlogApplication;
import com.xidian.miniblog.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author qhhu
 * @date 2020/3/11 - 15:14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MiniBlogApplication.class)
public class UserMapperTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void insertTest() {
        User user = new User();
        user.setUsername("qihhhu");
        user.setPassword("1234qazx");
        user.setEmail("abc");
        userMapper.insertUser(user);
    }

    @Test
    public void selectTest() {
        User user = userMapper.selectUserById(155);
        User user2 = userMapper.selectUserByUsername("qihhhu");
        User user3 = userMapper.selectUserByEmail("abc");
        System.out.println(user);
        System.out.println(user2);
        System.out.println(user3);
    }

}
