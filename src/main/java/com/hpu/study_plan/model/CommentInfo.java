package com.hpu.study_plan.model;

public class CommentInfo {
    int uid;
    int aid;
    int linkUid;
    String content;
    String authorAvatar;
    String authorName;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getLinkUid() {
        return linkUid;
    }

    public void setLinkUid(int linkUid) {
        this.linkUid = linkUid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CommentInfo{" +
                "uid=" + uid +
                ", aid=" + aid +
                ", linkUid=" + linkUid +
                ", content='" + content + '\'' +
                ", authorAvatar='" + authorAvatar + '\'' +
                ", authorName='" + authorName + '\'' +
                '}';
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public CommentInfo() {
    }

    public CommentInfo(int aid, int uid, int linkUid) {
        this.uid = uid;
        this.aid = aid;
        this.linkUid = linkUid;
    }
}
