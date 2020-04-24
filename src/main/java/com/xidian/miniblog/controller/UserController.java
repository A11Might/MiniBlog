package com.xidian.miniblog.controller;

import com.xidian.miniblog.annotation.LoginRequired;
import com.xidian.miniblog.entity.HostHolder;
import com.xidian.miniblog.entity.Page;
import com.xidian.miniblog.entity.Post;
import com.xidian.miniblog.entity.User;
import com.xidian.miniblog.service.FollowService;
import com.xidian.miniblog.service.PostService;
import com.xidian.miniblog.service.UserService;
import com.xidian.miniblog.util.BlogConstant;
import com.xidian.miniblog.util.BlogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author qhhu
 * @date 2020/3/13 - 14:14
 */
@Controller
@RequestMapping("/user")
public class UserController implements BlogConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private FollowService followService;

    @GetMapping("/profile/{userId}")
    public String getUserProfilePage(Model model, Page page, @PathVariable("userId") int userId) {
        User loginUser = hostHolder.getUser();

        page.setRows(postService.getPostRowsByUserId(userId));
        page.setPath("/user/profile/" + userId);

        User user = userService.getUserById(userId);
        model.addAttribute("user", user);

        List<Post> postList = postService.getPostsByUserId(user.getId(), page.getOffset(), page.getLimit(), ORDER_BY_CREATE_TIME);
        model.addAttribute("postList", postList);

        int followerCount = (int) followService.getEntityFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        int followeeCount = (int) followService.getUserFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);

        boolean isFollow = false;
        if (loginUser != null) {
            isFollow = followService.getFollowStatus(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("isFollow", isFollow);

        return "/site/profile";
    }

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/modifypassword")
    public String modifyPassword(Model model, String oldPassword, String newPassword) {
        User loginUser = hostHolder.getUser();

        model.addAttribute("oldPassword", oldPassword);
        model.addAttribute("newPassword", newPassword);

        Map<String, Object> map = userService.modifyPassword(loginUser.getId(), oldPassword, newPassword);
        if (map.containsKey("oldPasswordMsg")) {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            return "/site/setting";
        }
        if (map.containsKey("newPasswordMsg")) {
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/setting";
        }

        model.addAttribute("msg", "密码修改成功，请重新登录");
        model.addAttribute("target", "/login");
        return "/site/operate-result";
    }

    @PostMapping("/modifyheaderimage")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }

        if (headerImage.getSize() > 2000000) {
            model.addAttribute("error", "图片大小不能大于2M");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (!suffix.equals(".jpg") && !suffix.equals(".png")) {
            model.addAttribute("error", "图片格式不正确");
            return "/site/setting";
        }

        fileName = BlogUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("图片上传失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常");
        }

        // 图片地址 domain/user/headerimage/xxx.png
        User loginUser = hostHolder.getUser();
        String newHeaderUrl = domain + "/user/headerimage/" + fileName;
        userService.modifyHeaderUrl(loginUser.getId(), newHeaderUrl);
        model.addAttribute("msg", "头像修改成功");
        model.addAttribute("target", "profile/" + loginUser.getId());
        return "/site/operate-result";
    }

    @GetMapping("/headerimage/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        fileName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        // 向浏览器响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            // 建立缓冲区, 一批一批输出, 提高效率
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }
}
