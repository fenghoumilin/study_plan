package com.hpu.study_plan.model;

public class UserInfo {

    private String nick;
    private String phoneNumber;
    private int gender;
    private String avatarPicUrl;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAvatarPicUrl() {
        return avatarPicUrl;
    }

    public void setAvatarPicUrl(String avatarPicUrl) {
        this.avatarPicUrl = avatarPicUrl;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "nick='" + nick + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", gender=" + gender +
                ", avatarPicUrl='" + avatarPicUrl + '\'' +
                '}';
    }
}
