package com.hpu.study_plan.service;

import com.hpu.study_plan.dao.ArticleDao;
import com.hpu.study_plan.dao.GroupDao;
import com.hpu.study_plan.dao.UserDao;
import com.hpu.study_plan.model.ArticleAction;
import com.hpu.study_plan.model.ArticleResponse;
import com.hpu.study_plan.model.CommentInfo;
import com.hpu.study_plan.model.GroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    @Autowired
    ArticleDao articleDao;
    @Autowired
    UserDao userDao;

    public int insertArticle(int uid, int gid, String title, String content, String pic_url) {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("uid", uid);
        parameters.put("gid", gid);
        parameters.put("title", title);
        parameters.put("content", content);
        parameters.put("pic_url", pic_url);

        logger.info("insertArticle parameters = " + parameters);
        try {
            articleDao.insertArticle(parameters);
            logger.info("id = " + parameters.get("id"));
            return ((BigInteger) parameters.get("id")).intValue();
        } catch (Exception e) {
            logger.error("insertArticle error parameters = " + parameters, e);
        }

        return 0;

    }

    public ArticleResponse getArticleResponse(int id) {
        try {
            return articleDao.getArticleResponse(id);
        } catch (Exception e) {
            logger.error("getArticleResponse error id = " + id, e);
        }
        return new ArticleResponse();
    }

    public List<ArticleResponse> getArticlesByGid(int gid, int page) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("gid", gid);
        parameters.put("start", page * 6);
        parameters.put("end", 6);
        try {
            return articleDao.getArticlesByGid(parameters);
        } catch (Exception e) {
            logger.error("getArticlesByGid error gid = " + gid);
        }
        return new ArrayList<>();
    }

    public boolean insertArticleLike(int uid, int articleId) {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("uid", uid);
        parameters.put("aid", articleId);

        try {
            articleDao.insertArticleLike(parameters);
            return true;
        } catch (DuplicateKeyException e) {
            logger.error("重复点赞 error parameters = " + parameters);
        } catch (Exception e) {
            logger.error("insertArticleLike error parameters = " + parameters, e);
        }

        return false;
    }

    public int getLikeCount(int articleId) {

        try {
            return articleDao.getLikeCount(articleId);
        } catch (Exception e) {
            logger.error("getLikeCount error articleId = " + articleId);
        }

        return 0;
    }

    public ArticleAction getArticleAction(int aid) {

        try {
            return articleDao.getArticleAction(aid);
        } catch (Exception e) {
            logger.error("getLikeCount error aid = " + aid);
        }

        return new ArticleAction();
    }

    public boolean insertArticleComment(int aid, int uid, int linkUid, String content) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("aid", aid);
        parameters.put("uid", uid);
        parameters.put("linkUid", linkUid);
        parameters.put("content", content);

        try {
            articleDao.insertArticleComment(parameters);
            return true;
        } catch (Exception e) {
            logger.error("insertArticleComment error parameters = " + parameters, e);
        }

        return false;
    }

    public List<CommentInfo> getCommentInfoList(int aid) {
        try {
            return articleDao.getCommentInfoList(aid);
        } catch (Exception e) {
            logger.error("getCommentInfoList error aid = " + aid, e);
        }
        return new ArrayList<>();
    }

}
