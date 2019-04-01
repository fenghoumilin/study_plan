package com.hpu.study_plan.model;

public class UserInfo {

    private int id;
    private String nick;
    private String phoneNumber;
    private int gender;
    private String avatarPicUrl;
    private String birthday;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
                "id=" + id +
                ", nick='" + nick + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", gender=" + gender +
                ", avatarPicUrl='" + avatarPicUrl + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }
}
