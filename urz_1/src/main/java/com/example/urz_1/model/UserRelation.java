package com.example.urz_1.model;

import org.litepal.crud.LitePalSupport;

public class UserRelation extends LitePalSupport {
    /**
     * 一对一关联的实现方式是用外键，
     * 多对一关联的实现方式也是用外键，
     * 多对多关联的实现方式是用中间表
     */

    private int id;
    private int userId;//当前登录用户的user.getId()
    private int friendId;//点击添加好友后的点击事件中的user.getId()

    public UserRelation() {
    }

    public UserRelation(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
