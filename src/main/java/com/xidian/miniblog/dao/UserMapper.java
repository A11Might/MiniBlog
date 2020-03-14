package com.xidian.miniblog.dao;

import com.xidian.miniblog.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qhhu
 * @date 2020/3/11 - 14:39
 */
@Mapper
public interface UserMapper {

    User selectUserById(int id);

    User selectUserByUsername(String username);

    User selectUserByEmail(String email);

    int insertUser(User user);

    int updateUserStatus(int userId, int status);

    int updateUserPassword(int userId, String password);
}
