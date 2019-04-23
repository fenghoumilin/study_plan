package com.hpu.study_plan.model;

public class GroupScore implements Comparable<GroupScore>{

    int gid;
    int score;

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public GroupScore(int gid, int score) {
        this.gid = gid;
        this.score = score;
    }

    public GroupScore() {
    }

    @Override
    public int compareTo(GroupScore groupScore) {

        return groupScore.score - this.score;
    }
}
