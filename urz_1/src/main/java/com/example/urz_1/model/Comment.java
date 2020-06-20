package com.example.urz_1.model;

import org.litepal.crud.LitePalSupport;

public class Comment extends LitePalSupport {
    private int id;
    private String comment_content;//评论内容
    private String comment_date;//评论时间

    private User user;//多对一
    private Post post;//多对一


    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getComment_content() {
        return comment_content;
    }

    public String getComment_date() {
        return comment_date;
    }

    public User getUser() {
        return user;
    }

    public Post getPost() {
        return post;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
