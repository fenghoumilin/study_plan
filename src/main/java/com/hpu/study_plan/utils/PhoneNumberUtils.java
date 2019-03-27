package com.hpu.study_plan.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberUtils {

    public static Boolean validatePhoneNumber(String phoneNumber) {

        if (StringUtils.isEmpty(phoneNumber)) {
            return false;
        }

        if (phoneNumber.length() == 11) {
            Pattern p1 = Pattern.compile("^1[34578]\\d{9}$");
            Matcher matcher = p1.matcher(phoneNumber);
            return matcher.matches();
        }
        return false;
    }

}
