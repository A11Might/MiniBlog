package com.xidian.miniblog.controller;

import com.xidian.miniblog.entity.User;
import com.xidian.miniblog.service.UserService;
import com.xidian.miniblog.util.BlogConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author qhhu
 * @date 2020/3/11 - 15:24
 */
@Controller
public class LoginController implements BlogConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            return "redirect:index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/register";
        }
    }

    @GetMapping("/activation/{userId}/{activationCode}")
    public String activationAccount(Model model,
                                    @PathVariable("userId") int userId,
                                    @PathVariable("activationCode") String activationCode) {
        int activateResult = userService.activation(userId, activationCode);
        if (activateResult == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "账号激活成功，您可以正常使用");
            model.addAttribute("target", "/login");
        } else if (activateResult == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "该账号已激活，请勿重复操作");
            model.addAttribute("target", "/login");
        } else {
            model.addAttribute("msg", "账号激活失败，该激活码无效");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }


    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    @PostMapping("/login")
    public String login(Model model, User user, boolean remember, HttpServletResponse response) {
        Map<String, Object> map = userService.login(user, remember);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setMaxAge((remember ? REMEMBER_EXPIRED_DAYS : DEFAULT_EXPIRED_DAYS) * 3600 * 24);
            cookie.setPath(contextPath);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "/site/login";
    }

}
