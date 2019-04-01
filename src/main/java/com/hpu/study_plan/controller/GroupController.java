package com.hpu.study_plan.controller;

import com.hpu.study_plan.dao.GroupDao;
import com.hpu.study_plan.dao.UserDao;
import com.hpu.study_plan.model.ErrorModel;
import com.hpu.study_plan.model.GroupInfo;
import com.hpu.study_plan.model.LoginInfo;
import com.hpu.study_plan.model.UserInfo;
import com.hpu.study_plan.service.GroupService;
import com.hpu.study_plan.service.UserService;
import com.hpu.study_plan.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RequestMapping("/group")
@RestController
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    @Autowired
    UserService userService;
    @Autowired
    GroupService groupService;

    @RequestMapping(value="/createUI", method= RequestMethod.GET)
    public ModelAndView groupCreateUI(HttpServletRequest request) {

        HttpSession session = request.getSession();
        ModelAndView modelAndView = new ModelAndView();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);

        logger.info("phoneNumber = " + phoneNumber);
        UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
        GroupInfo groupInfo = new GroupInfo();

        logger.info("userInfo = " + userInfo.toString());
        logger.info("创建社区");
        List<Map<String, Object>> tagList = groupService.getTagList();
        modelAndView.addObject("userInfo", userInfo);
        modelAndView.addObject("groupInfo", groupInfo);
        modelAndView.addObject("tagList", tagList);
        modelAndView.addObject("errorModel", new ErrorModel());
        modelAndView.setViewName("group/create");

        return modelAndView;
    }

    @RequestMapping(value="/create", method= RequestMethod.POST)
    public ModelAndView groupCreate(HttpServletRequest request, @ModelAttribute GroupInfo groupInfo) {

        HttpSession session = request.getSession();
        ModelAndView modelAndView = new ModelAndView();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        logger.info("phoneNumber = " + phoneNumber);

        UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
        groupService.insertGroup(userInfo.getId(), groupInfo.getTitle(), groupInfo.getContent(), groupInfo.getPicUrl(), groupInfo.getTagId());

        logger.info("userInfo = " + userInfo.toString());
        logger.info("创建社区");
        List<Map<String, Object>> tagList = groupService.getTagList();
        modelAndView.addObject("userInfo", userInfo);
        modelAndView.addObject("groupList", groupInfo);
        modelAndView.setViewName("group/list");

        return modelAndView;
    }



}
