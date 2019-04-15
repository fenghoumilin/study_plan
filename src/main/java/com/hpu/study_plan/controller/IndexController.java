package com.hpu.study_plan.controller;

import com.hpu.study_plan.model.ArticleResponse;
import com.hpu.study_plan.model.GroupInfo;
import com.hpu.study_plan.model.UserInfo;
import com.hpu.study_plan.service.RecommendService;
import com.hpu.study_plan.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;
    @Autowired
    RecommendService recommendService;

    @RequestMapping(value="/", method= RequestMethod.GET)
    public ModelAndView indexUI(HttpServletRequest request) {

        logger.info("进入首页");
        ModelAndView modelAndView = new ModelAndView();
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        UserInfo userInfo;
        if (!StringUtils.isEmpty(sessionId)) {
            String phoneNumber = (String) session.getAttribute(sessionId);
            userInfo = userService.getUserInfoByPhone(phoneNumber);
            if (userInfo == null) {
                userInfo = new UserInfo();
            }
        } else {
            userInfo = new UserInfo();
        }

        List<GroupInfo> hotGroups = recommendService.getHotGroups(3);
        List<ArticleResponse> hotArticles = recommendService.getHotArticles(5);

        modelAndView.addObject("hotGroups", hotGroups);
        modelAndView.addObject("hotArticles", hotArticles);
        modelAndView.addObject(userInfo);
        modelAndView.setViewName("index");

        return modelAndView;
    }
}
