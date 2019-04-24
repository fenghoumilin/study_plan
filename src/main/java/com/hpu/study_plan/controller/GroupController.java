package com.hpu.study_plan.controller;

import com.hpu.study_plan.dao.GroupDao;
import com.hpu.study_plan.dao.UserDao;
import com.hpu.study_plan.model.*;
import com.hpu.study_plan.service.ArticleService;
import com.hpu.study_plan.service.GroupService;
import com.hpu.study_plan.service.RecommendService;
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
    @Autowired
    ArticleService articleService;
    @Autowired
    RecommendService recommendService;

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
            int gid = groupService.insertGroup(userInfo.getId(), groupInfo.getTitle(), groupInfo.getContent(), groupInfo.getPicUrl(), groupInfo.getTagId());
            /*logger.info("userInfo = " + userInfo.toString());
            logger.info("groupInfo = " + groupInfo.toString());
            logger.info("返回社区列表");
            modelAndView.addObject("userInfo", userInfo);
            List<GroupInfo> groupInfoList = groupService.getGroupInfoList(userInfo.getId());
            logger.info("groupInfoList = " + groupInfoList);
            modelAndView.addObject("groupInfoList", groupInfoList);*/
            if (gid > 0) {
                userService.insertUserTag(userInfo.getId(), groupService.getGroupTag(gid));
                modelAndView.setViewName("redirect:/group/list?uid=" + userInfo.getId());
                return modelAndView;
            }
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
            List<GroupInfo> groupInfoList = groupService.getGroupInfoListByUid(uid, Integer.MAX_VALUE-1);
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


    @RequestMapping(value="/view", method= RequestMethod.GET)
    public ModelAndView getGroupView(HttpServletRequest request) {

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        try {
            int gid = Integer.parseInt(request.getParameter("gid"));
            int page = 0;
            try {
                page = Integer.parseInt(request.getParameter("page"));
                logger.info("page = " + page);
                if (page < 0) {
                    page = 0;
                }
            } catch (Exception e) {
                logger.error("page = 0");
            }

            ModelAndView modelAndView = new ModelAndView();
            List<ArticleResponse> articleInfoList = articleService.getArticlesByGid(gid, page);
            logger.info("articleInfoList = " + articleInfoList);

            logger.info("phoneNumber = " + phoneNumber);
            UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
            if (userInfo == null) {
                userInfo = new UserInfo();
            }
            int groupFun = groupService.isGroupFun(userInfo.getId(), gid);
            logger.info("groupFun = " + groupFun);
            List<GroupInfo> hotGroups = recommendService.getHotGroups(userInfo.getId(), 4);
            List<ArticleResponse> hotArticles = recommendService.getHotArticles(userInfo.getId(), 4);
            logger.info("hotArticles = " + hotArticles);
            logger.info("hotGroups = " + hotGroups);
            List<GroupInfo> groupInfoList = groupService.getGroupInfoListById(gid);
            logger.info("groupInfoList = " + groupInfoList);
            modelAndView.addObject("userInfo", userInfo);
            modelAndView.addObject("groupInfo", groupInfoList.get(0));
            modelAndView.addObject("hotGroups", hotGroups);
            modelAndView.addObject("hotArticles", hotArticles);
            modelAndView.addObject("articleInfoList", articleInfoList);
            modelAndView.addObject("groupFun", groupFun);
            modelAndView.addObject("page", page);
            modelAndView.setViewName("group/view");
            return modelAndView;
        } catch (Exception e) {
            logger.error("groupCreate error", e);
        }
        return getGroupCreateMAV(phoneNumber, 1012);
    }


    @RequestMapping(value="/fun", method= RequestMethod.GET)
    public ModelAndView userLikeGroup(HttpServletRequest request) {

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        try {
            UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
            int uid = userInfo.getId();
            int gid = Integer.parseInt(request.getParameter("gid"));
            userService.insertUserTag(uid, groupService.getGroupTag(gid));
            groupService.insertGroupFun(uid, gid);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("redirect:/group/view?gid=" + gid + "&page=0");
            return modelAndView;
        } catch (Exception e) {
            logger.error("groupCreate error", e);
        }
        return getGroupCreateMAV(phoneNumber, 1012);
    }

    @RequestMapping(value="/fun/del", method= RequestMethod.GET)
    public ModelAndView userUnlikeGroup(HttpServletRequest request) {

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        try {
            UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
            int uid = userInfo.getId();
            int gid = Integer.parseInt(request.getParameter("gid"));
            groupService.delGroupFun(uid, gid);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("redirect:/group/view?gid=" + gid + "&page=0");
            return modelAndView;
        } catch (Exception e) {
            logger.error("userUnlikeGroup error", e);
        }
        return getGroupCreateMAV(phoneNumber, 1012);
    }

}
