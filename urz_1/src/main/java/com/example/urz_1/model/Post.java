package com.example.urz_1.model;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;

public class Post extends LitePalSupport {
    private int id;//主键
    private String content;//动态内容
    private String date;//动态发布日期
    private int likes;//点赞数
    private int comments;//评论数

    private User user;//多对一
    private ArrayList<Comment> commentList = new ArrayList<Comment>();//一对多


    public Post() {
    }

    public Post(String content, String date, int likes, int comments, User user) {
        this.content = content;
        this.date = date;
        this.likes = likes;
        this.comments = comments;
        this.user = user;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCommentList(ArrayList<Comment> commentList) {
        this.commentList = commentList;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public int getLikes() {
        return likes;
    }

    public int getComments() {
        return comments;
    }

    public User getUser() {
        return user;
    }

    public ArrayList<Comment> getCommentList() {
        return commentList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
