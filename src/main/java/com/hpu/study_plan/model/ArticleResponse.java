package com.hpu.study_plan.model;

import java.util.Date;

public class ArticleResponse {

    int id;
    int uid;
    int gid;
    int commentCount;
    String authorName;
    String title;
    String content;
    String picUrl;
    String authorAvatar;
    Date createdTime;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public ArticleResponse(int id, int uid, int gid, String authorName, String title, String content, String picUrl, String authorAvatar, Date createdTime) {
        this.id = id;
        this.uid = uid;
        this.gid = gid;
        this.authorName = authorName;
        this.title = title;
        this.content = content;
        this.picUrl = picUrl;
        this.authorAvatar = authorAvatar;
        this.createdTime = createdTime;
    }

    public ArticleResponse() {
    }

    @Override
    public String toString() {
        return "ArticleResponse{" +
                "id=" + id +
                ", uid=" + uid +
                ", gid=" + gid +
                ", authorName='" + authorName + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", authorAvatar='" + authorAvatar + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
