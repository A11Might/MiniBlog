package com.xidian.miniblog.entity;

import java.util.Date;

/**
 * @author qhhu
 * @date 2020/3/25 - 13:29
 */
public class Feed {

    private int id;
    private int type;
    private int userId;
    private String data;
    private int status;
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "id=" + id +
                ", type=" + type +
                ", userId=" + userId +
                ", data='" + data + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
