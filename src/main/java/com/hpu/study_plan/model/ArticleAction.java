package com.hpu.study_plan.model;

public class ArticleAction {
    int likeCount;
    int commentCount;

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public String toString() {
        return "ArticleAction{" +
                "likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                '}';
    }
}
