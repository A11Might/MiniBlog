package com.xidian.miniblog.service;

import com.xidian.miniblog.dao.UserMapper;
import com.xidian.miniblog.entity.User;
import com.xidian.miniblog.util.BlogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author qhhu
 * @date 2020/3/11 - 15:24
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getUserById(int id) {
        return userMapper.selectUserById(id);
    }

    public User getUserByUsername(String username) {
        return userMapper.selectUserByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        // 判断注册信息是否有效
        User u = userMapper.selectUserByUsername(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该用户名已存在");
            return map;
        }
        u = userMapper.selectUserByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        // 注册新用户
        user.setSalt(BlogUtil.generateUUID());
        user.setPassword(BlogUtil.md5(user.getPassword() + user.getSalt()));
        user.setStatus(0);
        user.setActivationCode(BlogUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送激活邮件


        return map;
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        // 判断账号是否存在
        User u = userMapper.selectUserByUsername(username);
        if (u == null) {
            map.put("usernameMsg", "该用户名不存在");
            return map;
        }

        // 验证密码


        // 生成登录凭证


        return map;
    }


}
