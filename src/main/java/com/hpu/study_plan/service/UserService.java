package com.hpu.study_plan.service;

import com.hpu.study_plan.dao.UserDao;
import com.hpu.study_plan.model.UserInfo;
import com.hpu.study_plan.utils.PhoneNumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public boolean insertUser(String nick, String phoneNumber, int gender, String avatarPicUrl, String birthday) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nick", nick);
        parameters.put("phoneNumber", phoneNumber);
        parameters.put("gender", gender);
        parameters.put("avatarPicUrl", avatarPicUrl);
        parameters.put("birthday", birthday);

        try {
            userDao.insertUser(parameters);
            return true;
        } catch (Exception e) {
            logger.error("insertUser error | paramaters = " + parameters, e);
        }

        return false;
    }

    public boolean haveUser(String phoneNumber) {

        try {
            int res = userDao.getUserCount(phoneNumber);
            if (res > 0) {
                return true;
            }
        } catch (Exception e) {
            logger.error("insertUser error | phoneNumber = " + phoneNumber, e);
        }

        return false;
    }

    public UserInfo getUserInfoByPhone(String phoneNumber) {
        if (!PhoneNumberUtils.validatePhoneNumber(phoneNumber)) {
            return null;
        }

        try {
            return userDao.getUserInfoByPhone(phoneNumber);
        } catch (Exception e) {
            logger.error("getUserInfoByPhone error | phoneNumber = " + phoneNumber, e);
        }

        return null;
    }


}
