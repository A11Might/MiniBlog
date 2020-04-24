package com.xidian.miniblog.service;

import com.xidian.miniblog.dao.UserMapper;
import com.xidian.miniblog.entity.LoginTicket;
import com.xidian.miniblog.entity.User;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.BlogUtil;
import com.xidian.miniblog.util.MailClient;
import com.xidian.miniblog.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author qhhu
 * @date 2020/3/11 - 15:24
 */
@Service
public class UserService implements BlogConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    public User getUserById(int id) {
        // 优先从缓存中寻找用户信息
        User user = getCache(id);

        // 找不到时初始化缓存信息
        if (user == null) {
            user = initCache(id);
            logger.debug("从数据库中读取用户信息");
        }

        return user;
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
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
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
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://huqihh/miniblog/activation/userId/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    public int activation(int id, String activationCode) {
        User user = userMapper.selectUserById(id);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(activationCode)) {
            userMapper.updateUserStatus(id, 1);

            // 用户信息变化时清除缓存
            clearCache(id);

            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(User user, boolean remember) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        // 判断账号是否存在
        User u = userMapper.selectUserByUsername(user.getUsername());
        if (u == null) {
            map.put("usernameMsg", "该用户名不存在");
            return map;
        }

        // 验证状态
        if (u.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活，请前往注册邮箱中查看激活邮件");
            return map;
        }

        // 验证密码
        user.setPassword(BlogUtil.md5(user.getPassword() + u.getSalt()));
        if (!u.getPassword().equals(user.getPassword())) {
            map.put("passwordMsg", "密码不正确");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(u.getId());
        loginTicket.setTicket(BlogUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(DateUtils.addDays(new Date(), remember ? REMEMBER_EXPIRED_DAYS : DEFAULT_EXPIRED_DAYS));

        String loginTicketKey = RedisKeyUtil.getLoginTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(loginTicketKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket) {
        String loginTicketKey = RedisKeyUtil.getLoginTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(loginTicketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(loginTicketKey, loginTicket);
    }

    public Map<String, Object> modifyPassword(int id, String oldPassword, String newPassword) {
        Map<String , Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(oldPassword)) {
            map.put("oldPasswordMsg", "旧密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg", "新密码不能为空");
            return map;
        }

        // 验证旧密码
        User user = userMapper.selectUserById(id);
        oldPassword = BlogUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            map.put("oldPasswordMsg", "旧密码错误");
            return map;
        }

        // 修改密码
        newPassword = BlogUtil.md5(newPassword + user.getSalt());
        userMapper.updateUserPassword(id, newPassword);

        // 用户信息变化时清除缓存
        clearCache(id);

        return map;
    }

    public int modifyHeaderUrl(int userId, String headerUrl) {
        int row = userMapper.updateUserHeaderUrl(userId, headerUrl);

        // 用户信息变化时清除缓存
        clearCache(userId);

        return row;
    }

    public LoginTicket getLoginTicketByTicket(String ticket) {
        String loginTicketKey = RedisKeyUtil.getLoginTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(loginTicketKey);
    }

    /**
     * 使用 redis 缓存用户信息：二级缓存
     */
    private User getCache(int id) {
        String userKey = RedisKeyUtil.getUserKey(id);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    private User initCache(int id) {
        User user = userMapper.selectUserById(id);

        String userKey = RedisKeyUtil.getUserKey(id);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);

        return user;
    }

    private void clearCache(int id) {
        String userKey = RedisKeyUtil.getUserKey(id);
        redisTemplate.delete(userKey);
    }
}
