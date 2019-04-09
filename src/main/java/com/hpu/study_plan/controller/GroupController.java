package com.hpu.study_plan.controller;

import com.hpu.study_plan.dao.GroupDao;
import com.hpu.study_plan.dao.UserDao;
import com.hpu.study_plan.model.ErrorModel;
import com.hpu.study_plan.model.GroupInfo;
import com.hpu.study_plan.model.LoginInfo;
import com.hpu.study_plan.model.UserInfo;
import com.hpu.study_plan.service.GroupService;
import com.hpu.study_plan.service.UserService;
import com.hpu.study_plan.utils.FileUtils;
import com.hpu.study_plan.utils.GlobalPropertyUtils;
import com.hpu.study_plan.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RequestMapping("/group")
@RestController
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    private static final String GROUP_PIC = GlobalPropertyUtils.get("img_type.group_pic");

    @Autowired
    UserService userService;
    @Autowired
    GroupService groupService;

    @RequestMapping(value="/createUI", method= RequestMethod.GET)
    public ModelAndView groupCreateUI(HttpServletRequest request) {

        HttpSession session = request.getSession();

        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);

        logger.info("phoneNumber = " + phoneNumber);


        return getGroupCreateMAV(phoneNumber, 0);
    }

    private ModelAndView getGroupCreateMAV(String phoneNumber, int code) {
        UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
        GroupInfo groupInfo = new GroupInfo();
        logger.info("userInfo = " + userInfo.toString());
        logger.info("创建社区");
        List<Map<String, Object>> tagList = groupService.getTagList();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("userInfo", userInfo);
        modelAndView.addObject("groupInfo", groupInfo);
        modelAndView.addObject("tagList", tagList);

        if (code == 0) {
            modelAndView.addObject("errorModel", new ErrorModel());
        } else {
            modelAndView.addObject("errorModel", ResponseUtils.putErrorModel(code));
        }


        modelAndView.setViewName("group/create");
        return modelAndView;
    }

    @RequestMapping(value="/create", method= RequestMethod.POST)
    public ModelAndView groupCreate(HttpServletRequest request, @ModelAttribute GroupInfo groupInfo,
                                    @RequestParam("pic") MultipartFile file) {

        HttpSession session = request.getSession();
        ModelAndView modelAndView = new ModelAndView();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        logger.info("phoneNumber = " + phoneNumber);
        String uploadUrl = FileUtils.upload(file, GROUP_PIC);
        if (StringUtils.isEmpty(uploadUrl)) {
            return getGroupCreateMAV(phoneNumber, 1032);
        }
        try {
            groupInfo.setPicUrl(uploadUrl);
            UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
            groupService.insertGroup(userInfo.getId(), groupInfo.getTitle(), groupInfo.getContent(), groupInfo.getPicUrl(), groupInfo.getTagId());
            logger.info("userInfo = " + userInfo.toString());
            logger.info("userInfo = " + groupInfo.toString());
            logger.info("返回社区列表");
            modelAndView.addObject("userInfo", userInfo);
            List<GroupInfo> groupInfoList = groupService.getGroupInfoList(userInfo.getId());
            logger.info("groupInfoList = " + groupInfoList);
            modelAndView.addObject("groupInfoList", groupInfoList);
            modelAndView.setViewName("group/list");
            return modelAndView;
        } catch (Exception e) {
            logger.error("groupCreate error", e);
        }
        return getGroupCreateMAV(phoneNumber, 1012);
    }



    @RequestMapping(value="/list", method= RequestMethod.GET)
    public ModelAndView getGrouplist(HttpServletRequest request) {

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        try {
            int uid = Integer.parseInt(request.getParameter("uid"));

            ModelAndView modelAndView = new ModelAndView();

            logger.info("phoneNumber = " + phoneNumber);
            UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
            if (userInfo == null) {
                userInfo = new UserInfo();
            }
            logger.info("userInfo = " + userInfo.toString());
            logger.info("返回社区列表");
            modelAndView.addObject("userInfo", userInfo);
            List<GroupInfo> groupInfoList = groupService.getGroupInfoList(uid);
            logger.info("groupInfoList = " + groupInfoList);
            logger.info("count = " + groupInfoList.size());
            modelAndView.addObject("groupInfoList", groupInfoList);
            modelAndView.setViewName("group/list");
            return modelAndView;
        } catch (Exception e) {
            logger.error("groupCreate error", e);
        }
        return getGroupCreateMAV(phoneNumber, 1012);
    }

}
