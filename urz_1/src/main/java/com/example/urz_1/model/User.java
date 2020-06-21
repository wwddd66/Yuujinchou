package com.example.urz_1.model;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * All rights Reserved, Designed By DongWang
 *
 * @author 王栋
 * @version v1.0
 */
public class User extends LitePalSupport {
    private int id;
    private String username;//用户名，unique
    private String nickname;//昵称，默认为用户名
    private String password;//密码，NOT NULL
    private String email;//邮箱，NOT NULL


    private List<Post> postList = new ArrayList<>();//一对多
    private List<Comment> commentList = new ArrayList<>();//一对多

    /**
     * 曾经尝试过单表关联自己，加入了一个：
     * /*private List<User> userList = new ArrayList<>();//自身多对多
     * 这样的属性，但是会报Cursor window allocation of 2048 kb failed这样的错误；
     * 于是新加了一张关系表，里面存放了两个User对象，结果无法查询，添加好友操作时
     * 出现新的问题；
     * 既然这两种方法都解决不了问题，那就只用一个整型数组来保存好友的ID试试，或许行得通；
     *
     * @serialData
     * @author
     */


    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
}
