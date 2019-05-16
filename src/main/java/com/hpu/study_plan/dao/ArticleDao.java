package com.hpu.study_plan.dao;

import com.hpu.study_plan.model.ArticleAction;
import com.hpu.study_plan.model.ArticleResponse;
import com.hpu.study_plan.model.CommentInfo;
import com.hpu.study_plan.model.GroupInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface ArticleDao {
    int insertArticle(Map<String, Object> parameters);
    int updateArticle(Map<String, Object> parameters);
    ArticleResponse getArticleResponse(int id);
    List<ArticleResponse> getArticlesByGid(Map<String, Object> parameters);
    int insertArticleLike(Map<String, Object> parameters);
    int getLikeCount(int aid);
    ArticleAction getArticleAction(int aid);
    int insertArticleComment(Map<String, Object> parameters);
    List<CommentInfo> getCommentInfoList(int aid);
    List<ArticleResponse> getArticlesByUid(Map<String, Object> parameters);
    List<ArticleResponse> getArticleResponseListByIdList(List aidList);
    int getArticleCountByGid(int gid);
    List<Map<String, Object>> getSimpleArticleComment();
    List<Map<String, Object>> getSimpleArticleCreate();
    List<Integer> getSimpleArticlesByUid(int uid);
    List<Map<String, Object>> getSimpleArticleLike();
    List<ArticleResponse> getArticleResponseListByIdListOrder(List aidList);

}
