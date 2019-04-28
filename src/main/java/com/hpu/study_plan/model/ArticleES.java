package com.hpu.study_plan.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "study_plan",type = "article")
public class ArticleES implements Comparable<ArticleScore>{

    @Id
    private String id;

    private Integer articleId;

    private Integer score = 0;

    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public ArticleES() {
    }

    public ArticleES(String id, Integer articleId, String title) {
        this.id = id;
        this.articleId = articleId;
        this.title = title;
    }

    public ArticleES(Integer articleId, String title) {
        this.articleId = articleId;
        this.title = title;
    }

    public ArticleES(Integer articleId, String title, Integer score) {
        this.articleId = articleId;
        this.score = score;
        this.title = title;
    }

    @Override
    public int compareTo(ArticleScore o) {
        return this.score.compareTo(o.score);
    }
}
