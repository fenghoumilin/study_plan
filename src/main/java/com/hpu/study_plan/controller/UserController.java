package com.hpu.study_plan.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hpu.study_plan.model.ErrorModel;
import com.hpu.study_plan.model.LoginInfo;
import com.hpu.study_plan.model.UserInfo;
import com.hpu.study_plan.service.UserService;
import com.hpu.study_plan.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequestMapping("/user")
@Controller
public class UserController {

    private static final String PHONE_CODE_PREFIX = GlobalPropertyUtils.get("redis.phone_code.key.prefix");
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private ObjectMapper mapper = new ObjectMapper();


    @Autowired
    UserService userService;

    @Autowired
    RedisUtils redisUtils;

    @RequestMapping(value="/loginUI", method= RequestMethod.GET)
    public ModelAndView userRegisterUI() {

        logger.info("进入注册页面");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/login");
        modelAndView.addObject(new LoginInfo());
        modelAndView.addObject(new ErrorModel());

        return modelAndView;
    }

    @RequestMapping(value="/update", method= RequestMethod.GET)
    public ModelAndView userUpdate() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");

        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value="/get/verificationCode", method= RequestMethod.POST)
    public String getVerificationCode(HttpServletRequest request, HttpServletResponse response) {

        try {
            String phoneNumber = request.getParameter("phoneNumber");
            logger.info("phoneNumber = " + phoneNumber);
            if (PhoneNumberUtils.validatePhoneNumber(phoneNumber)) {

                logger.info("aaaaaa");
                String key = PHONE_CODE_PREFIX + ":" + phoneNumber;
                String code;
                if (redisUtils.exists(key)) {
                    code = redisUtils.getPhoneCode(key);
                } else {
                    code = redisUtils.insertPhoneCode(key);
                }
                logger.info("code = " + code);
                if (!"".equals(code)) {
                    ObjectNode data = mapper.createObjectNode();
                    data.put("code", code);
                    ObjectNode res = ResponseUtils.putSuccessJson(data);
                    return res.toString();
                }
            }
        } catch (Exception e) {
            logger.error("getVerificationCode error phoneNumber", e);
        }

        ObjectNode res = ResponseUtils.putErrorJson(1012);
        return res.toString();
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public ModelAndView userLogin(HttpServletRequest request, @ModelAttribute LoginInfo loginInfo) {


        logger.info("login start");
        logger.info("loginInfo = " + loginInfo.toString());

        String phoneNumber = loginInfo.getPhoneNumber();
        String code = loginInfo.getCode();
        logger.info("phone = " + phoneNumber + "|code = " + code);

        ModelAndView modelAndView = new ModelAndView();
        if (!PhoneNumberUtils.validatePhoneNumber(phoneNumber)) {
            modelAndView.addObject(ResponseUtils.putErrorModel(1002));
            modelAndView.setViewName("login");
            return modelAndView;
        }
        HttpSession session = request.getSession();
        String key = PHONE_CODE_PREFIX + ":" + phoneNumber;
        if (redisUtils.getPhoneCode(key).equals(code)) {
            if (!userService.haveUser(phoneNumber)) {
                if (!userService.insertUser("hello world", phoneNumber, 0, "", "2050-01-01")) {
                    logger.info("数据库插入错误");
                    modelAndView.addObject(ResponseUtils.putErrorModel(1012));
                    modelAndView.setViewName("login");
                    return modelAndView;
                }
            }
            session.setAttribute(session.getId(), phoneNumber);
            modelAndView.addObject("userInfo", userService.getUserInfoByPhone(phoneNumber));
            modelAndView.setViewName("index");
            return modelAndView;
        }

        logger.info("验证码错误");
        modelAndView.addObject(ResponseUtils.putErrorModel(1021));
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value="/loginOut", method= RequestMethod.POST)
    public String userLoginOut(HttpServletRequest request, HttpServletResponse response) {

        JsonNode requestJson = RequestParser.getPostParameter(request);


        return "";
    }

    @RequestMapping(value="/settingUI", method= RequestMethod.GET)
    public ModelAndView userSettingUI(HttpServletRequest request) {

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
        logger.info("进入用户设置页面");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/setting");
        modelAndView.addObject(userInfo);
        modelAndView.addObject(new ErrorModel());

        return modelAndView;
    }

    @RequestMapping(value="/setting", method= RequestMethod.POST, consumes="multipart/form-data")
    public ModelAndView userSetting(HttpServletRequest request,
                                    @ModelAttribute UserInfo userInfo,
                                    @RequestParam("pic") MultipartFile file) {

        logger.info("file = " + file);

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        ModelAndView modelAndView = new ModelAndView();
        String phoneNumber = (String) session.getAttribute(sessionId);
        if (phoneNumber.equals(userInfo.getPhoneNumber())) {
            userInfo.setAvatarPicUrl(FileUtils.upload(file, "user/avatar"));
            if (!userService.updateUser(userInfo.getNick(), userInfo.getGender(), userInfo.getAvatarPicUrl(), userInfo.getBirthday())) {
                modelAndView.setViewName("/index");
                modelAndView.addObject(userInfo);
                modelAndView.addObject(ResponseUtils.putErrorModel(1012));
                return modelAndView;
            }
            modelAndView.setViewName("/index");
            modelAndView.addObject(userInfo);
            modelAndView.addObject(new ErrorModel());
            return modelAndView;
        }

        modelAndView.setViewName("user/login");
        modelAndView.addObject(new LoginInfo());
        modelAndView.addObject(ResponseUtils.putErrorModel(1031));
        return modelAndView;
    }

}
