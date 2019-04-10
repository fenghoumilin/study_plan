package com.hpu.study_plan.controller;

import com.hpu.study_plan.model.*;
import com.hpu.study_plan.service.ArticleService;
import com.hpu.study_plan.service.GroupService;
import com.hpu.study_plan.service.UserService;
import com.hpu.study_plan.utils.FileUtils;
import com.hpu.study_plan.utils.GlobalPropertyUtils;
import com.hpu.study_plan.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RequestMapping("/article")
@RestController
public class ArticleController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    private static final String ARTICLE_PIC = GlobalPropertyUtils.get("img_type.article_pic");

    @Autowired
    UserService userService;
    @Autowired
    GroupService groupService;
    @Autowired
    ArticleService articleService;

    @RequestMapping(value="/createUI", method= RequestMethod.GET)
    public ModelAndView articleCreateUI(HttpServletRequest request) {

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        logger.info("phoneNumber = " + phoneNumber);

        return getArticleCreateMAV(phoneNumber, 0);
    }

    private ModelAndView getArticleCreateMAV(String phoneNumber, int code) {
        UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
        ArticleRequest articleRequest = new ArticleRequest();
        logger.info("userInfo = " + userInfo.toString());
        logger.info("创建文章");
        List<Map<String, Object>> groupList = groupService.getSimpleGroupList(userInfo.getId());
        ModelAndView modelAndView = new ModelAndView();
        if (groupList.size() == 0) {
            modelAndView.addObject("userInfo", userInfo);
            modelAndView.addObject("groupInfo", new GroupInfo());
            modelAndView.addObject("tagList", groupService.getTagList());
            modelAndView.addObject("errorModel", ResponseUtils.putErrorModel(1041));
            modelAndView.setViewName("group/create");
            return modelAndView;
        }
        modelAndView.addObject("userInfo", userInfo);
        modelAndView.addObject("articleRequest", articleRequest);
        modelAndView.addObject("groupList", groupList);
        if (code == 0) {
            modelAndView.addObject("errorModel", new ErrorModel());
        } else {
            modelAndView.addObject("errorModel", ResponseUtils.putErrorModel(code));
        }

        modelAndView.setViewName("article/create");
        return modelAndView;
    }

    @RequestMapping(value="/create", method= RequestMethod.POST)
    public ModelAndView articleCreate(HttpServletRequest request, @ModelAttribute ArticleRequest articleRequest,
                                      @RequestParam("pic") MultipartFile file) {

        HttpSession session = request.getSession();

        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        logger.info("phoneNumber = " + phoneNumber);
        String uploadUrl = FileUtils.upload(file, ARTICLE_PIC);
        if (StringUtils.isEmpty(uploadUrl)) {
            return getArticleCreateMAV(phoneNumber, 1032);
        }
        UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
        int articleId = articleService.insertArticle(userInfo.getId(), articleRequest.getGid(),
                articleRequest.getTitle(), articleRequest.getContent(), uploadUrl);
        if (articleId == 0) {
            return getArticleCreateMAV(phoneNumber, 1012);
        }
        logger.info("articleRequest = " + articleRequest);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/article/view?articleId="+articleId);

        return modelAndView;
    }

    @RequestMapping(value="/view", method= RequestMethod.GET)
    public ModelAndView articleView(HttpServletRequest request) {

        HttpSession session = request.getSession();

        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
        int articleId = Integer.parseInt(request.getParameter("articleId"));

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("userInfo", userInfo);
        ArticleResponse articleResponse = articleService.getArticleResponse(articleId);

        List<GroupInfo> groupInfoListById = groupService.getGroupInfoListById(articleResponse.getGid());
        modelAndView.addObject("groupInfo", groupInfoListById.get(0));
        modelAndView.addObject("articleResponse", articleResponse);
        modelAndView.setViewName("article/view");
        return modelAndView;
    }

}
