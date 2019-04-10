package com.hpu.study_plan.model;

public class ArticleRequest {

    int uid;
    int gid;
    String title;
    String content;

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

    @Override
    public String toString() {
        return "ArticleRequest{" +
                "uid=" + uid +
                ", gid=" + gid +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
