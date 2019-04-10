package com.hpu.study_plan.service;

import com.hpu.study_plan.dao.RecommendDao;
import com.hpu.study_plan.model.ArticleResponse;
import com.hpu.study_plan.model.GroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendService.class);

    @Autowired
    RecommendDao recommendDao;

    public List<GroupInfo> getHotGroups() {
        try {
            return recommendDao.getHotGroups();
        } catch (Exception e) {
            logger.error("getHotGroups error", e);
        }
        return new ArrayList<>();
    }

    public List<ArticleResponse> getHotArticles() {

        try {
            return recommendDao.getHotArticles();
        } catch (Exception e) {
            logger.error("getHotArticles error", e);
        }
        return new ArrayList<>();
    }


}
