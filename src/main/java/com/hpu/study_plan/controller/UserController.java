package com.hpu.study_plan.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hpu.study_plan.model.*;
import com.hpu.study_plan.service.ArticleService;
import com.hpu.study_plan.service.GroupService;
import com.hpu.study_plan.service.RecommendService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("/user")
@Controller
public class UserController {

    private static final String PHONE_CODE_PREFIX = GlobalPropertyUtils.get("redis.phone_code.key.prefix");
    private static final String USER_AVATAR = GlobalPropertyUtils.get("img_type.user_avatar");
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String RECOMMEND_PREFIX = GlobalPropertyUtils.get("redis.recommend.key.prefix");
    private ObjectMapper mapper = new ObjectMapper();


    @Autowired
    UserService userService;
    @Autowired
    GroupService groupService;
    @Autowired
    ArticleService articleService;
    @Autowired
    RecommendService recommendService;

    @Autowired
    RedisUtils redisUtils;

    @RequestMapping(value="/loginUI", method= RequestMethod.GET)
    public ModelAndView userLoginUI(HttpServletRequest request) {

        logger.info("进入登录页面");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/login");
        modelAndView.addObject(new LoginInfo());
        modelAndView.addObject(new ErrorModel());
        try {
            HttpSession session = request.getSession();
            String sessionId = session.getId();
            String phoneNumber = (String) session.getAttribute(sessionId);
            UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
            modelAndView.addObject(userInfo);
        } catch (Exception e) {
            logger.info("userLoginUI error ", e);
            modelAndView.addObject(new UserInfo());
        }

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
            modelAndView.addObject(new LoginInfo());
            modelAndView.addObject(new UserInfo());
            modelAndView.setViewName("user/login");
            return modelAndView;
        }
        HttpSession session = request.getSession();
        String key = PHONE_CODE_PREFIX + ":" + phoneNumber;
        if (redisUtils.getPhoneCode(key).equals(code)) {
            if (!userService.haveUser(phoneNumber)) {
                if (!userService.insertUser("hello world", phoneNumber, 0, "", "2050-01-01")) {
                    logger.info("数据库插入错误");
                    modelAndView.addObject(ResponseUtils.putErrorModel(1012));
                    modelAndView.addObject(new LoginInfo());
                    modelAndView.addObject(new UserInfo());
                    modelAndView.setViewName("user/login");
                    return modelAndView;
                }
            }
            session.setAttribute(session.getId(), phoneNumber);
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        logger.info("验证码错误");
        modelAndView.addObject(ResponseUtils.putErrorModel(1021));
        modelAndView.addObject(new LoginInfo());
        modelAndView.addObject(new UserInfo());
        modelAndView.setViewName("user/login");
        return modelAndView;
    }

    @RequestMapping(value="/loginOut", method= RequestMethod.GET)
    public ModelAndView userLoginOut(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();

        String sessionId = session.getId();
        session.removeAttribute(sessionId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/user/loginUI");
        logger.info("退出登录");

        return modelAndView;
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

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        ModelAndView modelAndView = new ModelAndView();
        String phoneNumber = (String) session.getAttribute(sessionId);
        if (phoneNumber.equals(userInfo.getPhoneNumber())) {
            String uploadUrl = FileUtils.upload(file, USER_AVATAR);
            if (StringUtils.isEmpty(uploadUrl)) {
                modelAndView.addObject(userInfo);
                modelAndView.addObject(ResponseUtils.putErrorModel(1032));
            }
            userInfo.setAvatarPicUrl(uploadUrl);
            if (!userService.updateUser(userInfo.getId(), userInfo.getNick(), userInfo.getGender(), userInfo.getAvatarPicUrl(), userInfo.getBirthday())) {
                modelAndView.setViewName("redirect:/");
                return modelAndView;
            }
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        modelAndView.setViewName("user/login");
        modelAndView.addObject(new UserInfo());
        modelAndView.addObject(new LoginInfo());
        modelAndView.addObject(ResponseUtils.putErrorModel(1031));
        return modelAndView;
    }


    @RequestMapping(value="/view", method= RequestMethod.GET)
    public ModelAndView userView(HttpServletRequest request) {

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
        int showUid = Integer.parseInt(request.getParameter("uid"));
        UserInfo showUserInfo = userService.getUserInfoById(showUid);
        List<GroupInfo> groupInfoList = groupService.getGroupInfoListByUid(showUid, 3);
        List<ArticleResponse> articleList = articleService.getArticlesByUid(showUid, 3);
        logger.info("进入用户页面");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/view");
        modelAndView.addObject("userInfo", userInfo);
        modelAndView.addObject("showUserInfo", showUserInfo);
        modelAndView.addObject("groupInfoList", groupInfoList);
        modelAndView.addObject("articleList", articleList);

        modelAndView.addObject(new ErrorModel());

        return modelAndView;
    }

    @RequestMapping(value="/search", method= RequestMethod.POST)
    public ModelAndView userSearchPost(HttpServletRequest request, @RequestParam("content") String content) {

        logger.info("content = " + content);
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
        if (userInfo == null) {
            userInfo = new UserInfo();
        }

        int limit = 6;
        int page = 0, totalPage = 1;
        List<ArticleResponse> articleInfoList = articleService.searchByContent(content);
        int articleInfoListSize = articleInfoList.size();
        if (limit - articleInfoListSize > 0) {
            articleInfoList = recommendService.addHotArticle(articleInfoList, recommendService.getHotArticles(userInfo.getId(), limit), limit);
        } else {
            if (articleInfoListSize % limit == 0) {
                totalPage = articleInfoListSize / limit;
            } else {
                totalPage = articleInfoListSize / limit + 1;
            }
            List<String> nodesString = new ArrayList<>();
            logger.info("groupTasks = " + articleInfoList.toString());
            for (ArticleResponse articleResponse : articleInfoList) {
                ObjectNode objectNode = JsonUtils.articleResponse2Node(articleResponse);
                nodesString.add(objectNode.toString());
            }
            String key = getArticleSearchKey(sessionId);
            redisUtils.pushListToRedis(key, nodesString);
            articleInfoList = articleInfoList.subList(0, limit);
        }
        logger.info("articleInfoList = " + articleInfoList);
        ModelAndView modelAndView = new ModelAndView();
        List<GroupInfo> hotGroups = recommendService.getHotGroups(userInfo.getId(), 4);
        List<ArticleResponse> hotArticles = recommendService.getHotArticles(userInfo.getId(), 4);

        modelAndView.addObject("userInfo", userInfo);
        modelAndView.addObject("hotGroups", hotGroups);
        modelAndView.addObject("hotArticles", hotArticles);
        modelAndView.addObject("articleInfoList", articleInfoList);
        modelAndView.addObject("page", page);
        modelAndView.addObject("totalPage", totalPage);
        modelAndView.setViewName("search_res");

        return modelAndView;
    }

    @RequestMapping(value="/search", method= RequestMethod.GET)
    public ModelAndView userSearchGet(HttpServletRequest request, @RequestParam("page") int page, @RequestParam("totalPage") int totalPage) {

        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);
        UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
        if (userInfo == null) {
            userInfo = new UserInfo();
        }

        int limit = 6;
        String key = getArticleSearchKey(sessionId);
        ArrayNode nodesFromRedis = redisUtils.getNodesFromRedis(key, page, limit);
        List<ArticleResponse> articleInfoList = new ArrayList<>();
        for (JsonNode jsonNode : nodesFromRedis) {
            articleInfoList.add(JsonUtils.node2ArticleResponse(jsonNode));
        }

        logger.info("articleInfoList = " + articleInfoList);
        ModelAndView modelAndView = new ModelAndView();
        List<GroupInfo> hotGroups = recommendService.getHotGroups(userInfo.getId(), 4);
        List<ArticleResponse> hotArticles = recommendService.getHotArticles(userInfo.getId(), 4);

        modelAndView.addObject("userInfo", userInfo);
        modelAndView.addObject("hotGroups", hotGroups);
        modelAndView.addObject("hotArticles", hotArticles);
        modelAndView.addObject("articleInfoList", articleInfoList);
        modelAndView.addObject("page", page);
        modelAndView.addObject("totalPage", totalPage);

        modelAndView.setViewName("search_res");
        return modelAndView;
    }


    private String getArticleSearchKey(String sessionId) {
        return RECOMMEND_PREFIX + ":article:search:" + sessionId;
    }

}
