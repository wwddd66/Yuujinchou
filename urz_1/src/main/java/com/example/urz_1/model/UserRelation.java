package com.example.urz_1.model;

import org.litepal.crud.LitePalSupport;

public class UserRelation extends LitePalSupport {
    /**
     * 一对一关联的实现方式是用外键，
     * 多对一关联的实现方式也是用外键，
     * 多对多关联的实现方式是用中间表
     */


    private int id;
    private User user;
    private User friend;

    public void setUser(User user) {
        this.user = user;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public User getUser() {
        return user;
    }

    public User getFriend() {
        return friend;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
