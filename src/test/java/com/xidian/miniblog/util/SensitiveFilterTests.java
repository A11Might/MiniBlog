package com.xidian.miniblog.util;

import com.xidian.miniblog.MiniBlogApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author qhhu
 * @date 2020/3/29 - 10:33
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MiniBlogApplication.class)
public class SensitiveFilterTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void test() {
        String text = "色*情， 色情，黄色，色，黄*色";
        System.out.println(sensitiveFilter.filter(text));
    }

}
