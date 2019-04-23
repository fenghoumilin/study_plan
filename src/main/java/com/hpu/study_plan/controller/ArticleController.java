package com.hpu.study_plan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    @Autowired
    RecommendService recommendService;

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
        userService.insertUserTag(userInfo.getId(), groupService.getGroupTag(articleRequest.getGid()));
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
        ModelAndView modelAndView = new ModelAndView();
        try {
            int articleId = Integer.parseInt(request.getParameter("articleId"));


            logger.info("userInfo = " + userInfo);
            modelAndView.addObject("userInfo", userInfo);
            ArticleResponse articleResponse = articleService.getArticleResponse(articleId);
            logger.info("articleResponse = " + articleResponse);

            List<GroupInfo> groupInfoListById = groupService.getGroupInfoListById(articleResponse.getGid());
            List<GroupInfo> hotGroups = recommendService.getHotGroups(4);
            List<ArticleResponse> hotArticles = recommendService.getHotArticles(4);
            ArticleAction articleAction = articleService.getArticleAction(articleId);
            CommentInfo commentInfo = new CommentInfo(articleId, userInfo.getId(), articleResponse.getUid());
            logger.info("articleAction = " + articleAction);
            List<CommentInfo> commentInfoList = articleService.getCommentInfoList(articleId);
            logger.info("commentInfoList = " + commentInfoList);
            modelAndView.addObject("hotGroups", hotGroups);
            modelAndView.addObject("hotArticles", hotArticles);
            modelAndView.addObject("groupInfo", groupInfoListById.get(0));
            modelAndView.addObject("articleResponse", articleResponse);
            modelAndView.addObject("articleAction", articleAction);
            modelAndView.addObject("commentInfo", commentInfo);
            modelAndView.addObject("commentInfoList", commentInfoList);
            modelAndView.setViewName("article/view");
            return modelAndView;
        } catch (NumberFormatException e) {
            logger.error("articleView error ", e);
        }
        modelAndView.addObject("userInfo", userInfo);
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @RequestMapping(value="/like", method= RequestMethod.POST)
    public String insertArticleLike(HttpServletRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            int articleId = Integer.parseInt(request.getParameter("articleId"));
            int uid = Integer.parseInt(request.getParameter("uid"));
            logger.info("articleId = " + articleId);
            articleService.insertArticleLike(uid, articleId);
            int likeCount = articleService.getLikeCount(articleId);

            ObjectNode data = mapper.createObjectNode();
            data.put("count", likeCount);
            ObjectNode res = ResponseUtils.putSuccessJson(data);
            return res.toString();
        } catch (Exception e) {
            logger.error("getVerificationCode error phoneNumber", e);
        }

        ObjectNode res = ResponseUtils.putErrorJson(1012);
        return res.toString();
    }

    @RequestMapping(value="/comment", method= RequestMethod.POST)
    public ModelAndView insertArticleComment(HttpServletRequest request, @ModelAttribute CommentInfo commentInfo) {

        int articleId = commentInfo.getAid();
        ModelAndView modelAndView = new ModelAndView();
        logger.info("commentInfo = " + commentInfo);

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
        if (userInfo.getId() == commentInfo.getUid()) {
            articleService.insertArticleComment(commentInfo.getAid(), commentInfo.getUid(), commentInfo.getLinkUid(), commentInfo.getContent());
        }
        modelAndView.setViewName("redirect:/article/view?articleId="+articleId);

        return modelAndView;
    }

}
