package com.hpu.study_plan.service;

import com.hpu.study_plan.dao.RecommendDao;
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
public class RecommendService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendService.class);

    @Autowired
    RecommendDao recommendDao;

    public List<GroupInfo> getHotGroups(int limit) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("limit", limit);
        try {
            return recommendDao.getHotGroups(parameters);
        } catch (Exception e) {
            logger.error("getHotGroups error", e);
        }
        return new ArrayList<>();
    }

    public List<ArticleResponse> getHotArticles(int limit) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("limit", limit);
        try {
            return recommendDao.getHotArticles(parameters);
        } catch (Exception e) {
            logger.error("getHotArticles error", e);
        }
        return new ArrayList<>();
    }


}
