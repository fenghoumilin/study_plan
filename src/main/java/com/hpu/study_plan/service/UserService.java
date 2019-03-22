package com.hpu.study_plan.service;

import com.hpu.study_plan.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public boolean insertUser(String nick, String phoneNumber, String account, String password, int gender, String avatarPicUrl, String birthday) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nick", nick);
        parameters.put("phoneNumber", phoneNumber);
        parameters.put("account", account);
        parameters.put("password", password);
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

    public boolean updatePassword(int uid, String password) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("uid", uid);
        parameters.put("password", password);

        try {
            int ans = userDao.updatePassword(parameters);
            logger.info("ans = " + ans);
            if (ans == 1) {
                return true;
            }
        } catch (Exception e) {
            logger.error("updatePassword error | paramaters = " + parameters, e);
        }

        return false;
    }


}
