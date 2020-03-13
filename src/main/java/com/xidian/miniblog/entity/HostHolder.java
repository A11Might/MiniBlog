package com.xidian.miniblog.entity;

import org.springframework.stereotype.Component;

/**
 * @author qhhu
 * @date 2020/3/12 - 19:29
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
