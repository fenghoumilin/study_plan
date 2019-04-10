package com.hpu.study_plan.dao;

import com.hpu.study_plan.model.ArticleResponse;
import com.hpu.study_plan.model.GroupInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface ArticleDao {
    int insertArticle(Map<String, Object> parameters);
    ArticleResponse getArticleResponse(int id);
    List<ArticleResponse> getArticlesByGid(Map<String, Object> parameters);
}
