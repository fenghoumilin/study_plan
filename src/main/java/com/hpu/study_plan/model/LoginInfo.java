package com.hpu.study_plan.model;

public class LoginInfo {
    private String phoneNumber;
    private String code;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
