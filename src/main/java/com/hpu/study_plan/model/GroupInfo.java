package com.hpu.study_plan.model;

import java.util.Date;

public class GroupInfo {

    int id;
    String title;
    String content;
    String picUrl;
    int tagId;
    Date createdTime;

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

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", tagId=" + tagId +
                ", createdTime=" + createdTime +
                '}';
    }

    public GroupInfo(int id, String title, String content, String picUrl, int tagId, Date createdTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.picUrl = picUrl;
        this.tagId = tagId;
        this.createdTime = createdTime;
    }

    public GroupInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
