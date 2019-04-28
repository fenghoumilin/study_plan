package com.hpu.study_plan.model;

public class ArticleScore implements Comparable<ArticleScore>{
    int aid;
    String title;
    int score;

    public ArticleScore() {
    }

    public ArticleScore(int aid, String title, int score) {
        this.aid = aid;
        this.title = title;
        this.score = score;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(ArticleScore articleScore) {

        return this.score - articleScore.score;
    }

    @Override
    public String toString() {
        return "ArticleScore{" +
                "aid=" + aid +
                ", title='" + title + '\'' +
                ", score=" + score +
                '}';
    }
}
