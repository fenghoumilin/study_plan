package com.hpu.study_plan.service;

import com.hpu.study_plan.dao.ArticleDao;
import com.hpu.study_plan.dao.GroupDao;
import com.hpu.study_plan.model.ArticleResponse;
import com.hpu.study_plan.model.GroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    @Autowired
    ArticleDao articleDao;

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
            return ((Long)parameters.get("id")).intValue();
        } catch (Exception e) {
            logger.error("insertArticle error parameters = " + parameters);
        }

        return 0;

    }

    public ArticleResponse getArticleResponse(int id) {
        try {
            return articleDao.getArticleResponse(id);
        } catch (Exception e) {
            logger.error("insertArticle error id = " + id);
        }
        return new ArticleResponse();
    }

    public List<ArticleResponse> getArticlesByGid(int gid, int page) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("gid", gid);
        parameters.put("start", page * 5);
        parameters.put("end", 5);
        try {
            return articleDao.getArticlesByGid(parameters);
        } catch (Exception e) {
            logger.error("getArticlesByGid error gid = " + gid);
        }
        return new ArrayList<>();
    }

}
