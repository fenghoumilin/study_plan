package com.hpu.study_plan.interceptor;

import com.hpu.study_plan.controller.GroupController;
import com.hpu.study_plan.model.LoginInfo;
import com.hpu.study_plan.model.UserInfo;
import com.hpu.study_plan.service.UserService;
import com.hpu.study_plan.utils.PhoneNumberUtils;
import com.hpu.study_plan.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        logger.info("拦截中");
        try {
            HttpSession session = request.getSession();
            String sessionId = session.getId();
            logger.info("sessionId = " + sessionId);
            String phoneNumber = (String) session.getAttribute(sessionId);
            logger.info("phoneNumber = " + phoneNumber);

            if (!PhoneNumberUtils.validatePhoneNumber(phoneNumber) || !userService.haveUser(phoneNumber)) {
                logger.info("未登录用户，或登录超时，请重新登录");
                response.sendRedirect("/user/loginUI");
                return false;
            }
        } catch (IOException e) {
            logger.error("未知异常");
            return false;
        }


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
