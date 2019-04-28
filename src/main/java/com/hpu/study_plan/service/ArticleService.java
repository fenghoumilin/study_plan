package com.hpu.study_plan.service;

import com.hpu.study_plan.dao.ArticleDao;
import com.hpu.study_plan.dao.ElasticSearchDao;
import com.hpu.study_plan.dao.UserDao;
import com.hpu.study_plan.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Service
public class ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    @Autowired
    ArticleDao articleDao;
    @Autowired
    UserDao userDao;
    @Autowired
    ElasticSearchDao elasticSearchDao;

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
            int articleId = ((BigInteger) parameters.get("id")).intValue();
            logger.info("id = " + parameters.get("id"));
            elasticSearchDao.save(new ArticleES(articleId, title));
            return articleId;
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
            logger.error("getArticlesByGid error parameters = " + parameters, e);
        }
        return new ArrayList<>();
    }
    public int getArticleTotalPageByGid(int gid) {

        if (gid <= 0) {
            return 0;
        }
        try {
            int count = articleDao.getArticleCountByGid(gid);
            if(count % 6 == 0) {
                return count / 6;
            }
            return count / 6 + 1;
        } catch (Exception e) {
            logger.error("getArticleTotalPageByGid error gid = " + gid, e);
        }
        return 0;
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

    public List<ArticleResponse> getArticlesByUid(int uid, int limit) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("uid", uid);
        parameters.put("limit", limit);
        try {
            return articleDao.getArticlesByUid(parameters);
        } catch (Exception e) {
            logger.error("getArticlesByUid error parameters = " + parameters, e);
        }
        return new ArrayList<>();
    }


    public List<ArticleResponse> searchByContent(String content, int limit) {

        try {
            logger.info("ES start");
            List<ArticleES> articleESList = elasticSearchDao.findByTitleLike(content);
            logger.info("articleESList = " + articleESList);
            logger.info("ES end");
            if (articleESList == null || articleESList.size() == 0) {
                return new ArrayList<>();
            }
            List<ArticleScore> articleScoreList = new ArrayList<>();
            List<Integer> articleIdList = new ArrayList<>();
            for (ArticleES articleES : articleESList) {
                articleScoreList.add(new ArticleScore(articleES.getArticleId(), articleES.getTitle(), articleES.getScore()));
            }
            Collections.sort(articleScoreList);
            logger.info("articleScoreList = " + articleScoreList);
            for (ArticleScore articleScore : articleScoreList) {
                articleIdList.add(articleScore.getAid());
            }
            List<ArticleResponse> articleResponseList = articleDao.getArticleResponseListByIdList(articleIdList);
            if (articleResponseList == null || articleResponseList.size() == 0) {
                return new ArrayList<>();
            }
            if (articleResponseList.size() > limit) {
                return articleResponseList.subList(0, limit);
            } else {
                return articleResponseList;
            }

        } catch (Exception e) {
            logger.error("getArticlesByUid error content = " + content, e);
        }
        return new ArrayList<>();
    }


}
