package com.hpu.study_plan.model;

public class SimilarInfo implements Comparable<SimilarInfo> {
    int aid;
    Double score;

    public SimilarInfo() {
    }

    public SimilarInfo(int aid, Double score) {
        this.aid = aid;
        this.score = score;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "SimilarInfo{" +
                "aid=" + aid +
                ", score=" + score +
                '}';
    }

    @Override
    public int compareTo(SimilarInfo similarInfo) {
        if (similarInfo.score > this.score) {
            return 1;
        } else if (similarInfo.score < this.score){
            return -1;
        }
        return 0;
    }
}
