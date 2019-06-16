package com.hpu.study_plan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hpu.study_plan.dao.*;
import com.hpu.study_plan.model.*;
import com.hpu.study_plan.utils.GlobalPropertyUtils;
import com.hpu.study_plan.utils.JsonUtils;
import com.hpu.study_plan.utils.MapUtils;
import com.hpu.study_plan.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RecommendService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendService.class);
    private static final String RECOMMEND_PREFIX = GlobalPropertyUtils.get("redis.recommend.key.prefix");
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final int TAG_COUNT = GlobalPropertyUtils.getIntValue("tag_count");
    private static final int GET_TAG_COUNT = GlobalPropertyUtils.getIntValue("get_tag_count");

    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    RecommendDao recommendDao;
    @Autowired
    ArticleDao articleDao;
    @Autowired
    GroupDao groupDao;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    UserDao userDao;
    @Autowired
    ElasticSearchDao elasticSearchDao;

    public List<GroupInfo> getHotGroups(int limit) {
        Set<Integer> tagSet = new HashSet<>();
        Random random = new Random();
        while (tagSet.size() < limit) {
            tagSet.add(random.nextInt(TAG_COUNT) + 1);
        }
        List<Integer> tagList = new ArrayList<>(tagSet);
        return getHotGroupByTagList(tagList, limit);
    }

    public List<GroupInfo> getHotGroups(int uid, int limit) {

        List<GroupInfo> res = new ArrayList<>();
        try {
            if (uid > 0) {
                List<Integer> userTag = userDao.getUserTagByUid(uid);
                res = getHotGroupByTagList(userTag, limit);
            } else {
                res = getHotGroups(limit);
            }
            return res;
        } catch (Exception e) {
            logger.error("getHotGroups error", e);
        }
        return new ArrayList<>();
    }

    private List<GroupInfo> getHotGroupByTagList(List<Integer> userTag, int limit){
        List<GroupInfo> res = new ArrayList<>();
        try {
            List<List<GroupInfo>> allGroupInfo = new ArrayList<>();
            //根据用户tag在每一个groupInfoList取出前limit个
            for (int tag : userTag) {
                String key = getKey("group", tag);
                String value = redisUtils.get(key);
                if (!"".equals(value)) {
                    ArrayNode readNode = (ArrayNode) mapper.readTree(value);
                    List<GroupInfo> groupInfoList = new ArrayList<>();
                    for (JsonNode jsonNode : readNode) {
                        GroupInfo groupInfo = JsonUtils.node2GroupInfo(jsonNode);
                        groupInfoList.add(groupInfo);
                        if (groupInfoList.size() == limit) {
                            break;
                        }
                    }
                    allGroupInfo.add(groupInfoList);
                }
            }
            int cycCount = 0;
            //每一个tag尽量取一个
            while (cycCount < limit && res.size() < limit) {
                for (List<GroupInfo> groupInfoList : allGroupInfo) {
                    if (groupInfoList.size() > cycCount) {
                        res.add(groupInfoList.get(cycCount));
                    }
                }
                cycCount++;
            }
            //不够就随机取，一般不会出现这种情况
            if (res.size() < limit) {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("limit", limit);
                res = addHotGroup(res, recommendDao.getHotGroups(parameters), limit);
            }
            logger.info("推荐 res = " + res);
            return res;
        } catch (Exception e) {
            logger.error("getHotGroupByTagList error userTag = " + userTag, e);
        }
        return new ArrayList<>();
    }

    private List<GroupInfo> addHotGroup(List<GroupInfo> origin, List<GroupInfo> addGroup, int limit) {
        if (origin == null || origin.size() == 0) {
            return addGroup;
        }
        Set<Integer> set = new HashSet<>();

        for (GroupInfo groupInfo : origin) {
            set.add(groupInfo.getId());
        }
        for (GroupInfo groupInfo : addGroup) {
            if (!set.contains(groupInfo.getId())) {
                origin.add(groupInfo);
                if (origin.size() == limit) {
                    return origin;
                }
            }
        }

        return origin;
    }

    public List<ArticleResponse> getHotArticles(int limit) {
        Set<Integer> tagSet = new HashSet<>();
        Random random = new Random();
        while (tagSet.size() < GET_TAG_COUNT) {
            tagSet.add(random.nextInt(TAG_COUNT) + 1);
        }
        List<Integer> tagList = new ArrayList<>(tagSet);
        logger.info("tagList" + tagList);
        return getHotArticleByTagList(tagList, limit);
    }

    public List<ArticleResponse> getHotArticles(int uid, int limit) {

        List<ArticleResponse> res = new ArrayList<>();
        logger.info("uid = " + uid);
        try {
            if (uid > 0) {
                List<Integer> userTag = userDao.getUserTagByUid(uid);
                logger.info("userTag = " + userTag);
                res = getHotArticleByTagList(userTag, limit);
            } else {
                res = getHotArticles(limit);
            }
            return res;
        } catch (Exception e) {
            logger.error("getHotArticles error", e);
        }
        return new ArrayList<>();
    }

    private List<ArticleResponse> getHotArticleByTagList(List<Integer> userTag, int limit){
        List<ArticleResponse> res = new ArrayList<>();
        try {
            List<List<ArticleResponse>> allArticleResponse = new ArrayList<>();
            for (int tag : userTag) {
                String key = getKey("article", tag);
                String value = redisUtils.get(key);
                if (!"".equals(value)) {
                    ArrayNode readNode = (ArrayNode) mapper.readTree(value);
                    List<ArticleResponse> articleInfoList = new ArrayList<>();
                    for (JsonNode jsonNode : readNode) {
                        ArticleResponse articleInfo = JsonUtils.node2ArticleResponse(jsonNode);
                        articleInfoList.add(articleInfo);
                        if (articleInfoList.size() == limit) {
                            break;
                        }
                    }
                    allArticleResponse.add(articleInfoList);
                }
            }
            logger.info("allArticleResponse = " + allArticleResponse);
            int cycCount = 0;
            while (cycCount < limit && res.size() < limit) {
                for (List<ArticleResponse> articleInfoList : allArticleResponse) {
                    if (articleInfoList.size() > cycCount) {
                        res.add(articleInfoList.get(cycCount));
                    }
                }
                cycCount++;
            }
            if (res.size() < limit) {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("limit", limit);
                res = addHotArticle(res, recommendDao.getHotArticles(parameters), limit);
            }
            logger.info("推荐 res = " + res);
            return res;
        } catch (Exception e) {
            logger.error("getHotArticleByTagList error userTag = " + userTag, e);
        }
        return new ArrayList<>();
    }

    public List<ArticleResponse> addHotArticle(List<ArticleResponse> origin, List<ArticleResponse> addArticle, int limit) {
        if (origin == null || origin.size() == 0) {
            return addArticle;
        }
        Set<Integer> set = new HashSet<>();
        logger.info("origin = " + origin);

        for (ArticleResponse articleResponse : origin) {
            set.add(articleResponse.getId());
        }
        for (ArticleResponse articleResponse : addArticle) {
            if (!set.contains(articleResponse.getId())) {
                origin.add(articleResponse);
                if (origin.size() == limit) {
                    return origin;
                }
            }
        }

        return origin;
    }

    public void insertHotData2Redis(){

        List<Map<String, Object>> hotArticleDatas = recommendDao.getHotArticleData();
        logger.info("hotArticleDatas = " + hotArticleDatas.size());
        Map<Integer, Integer> groupScoreMap = new HashMap<>();
        Map<Integer, List<Integer>> tag2Gid = new HashMap<>();
        Map<Integer, List<Integer>> tag2Aid = new HashMap<>();
        List<Integer> aidList = new ArrayList<>();
        Set<Integer> gidList = new HashSet<>();
        List<ArticleES> diaryScoreList = new ArrayList<>();
        for (Map<String, Object> hotArticleData : hotArticleDatas) {
            int tagId = ((Long) hotArticleData.get("tag_id")).intValue();
            int gid = ((Long) hotArticleData.get("gid")).intValue();
            int aid = ((Long) hotArticleData.get("aid")).intValue();
            String title = (String) hotArticleData.get("title");
            int commentCount = ((Long) hotArticleData.get("comment_count")).intValue();
            int likeCount = ((Long) hotArticleData.get("like_count")).intValue();
            diaryScoreList.add(new ArticleES(aid, title, commentCount * 5 + likeCount));
            MapUtils.addGroupScoreMap(groupScoreMap, gid, commentCount * 5 + likeCount);
            MapUtils.addTag2ListMap(tag2Gid, tagId, gid);
            MapUtils.addTag2ListMap(tag2Aid, tagId, aid);
            gidList.add(gid);
            aidList.add(aid);
        }

        //存放入elasticSearch
        try {
            elasticSearchDao.deleteAll();
            elasticSearchDao.saveAll(diaryScoreList);
        } catch (Exception e) {
            logger.info("elasticSearchDao error", e);
        }

        //根据gid查询出所有的groupInfo
        List<GroupInfo> groupInfoList = groupDao.getGroupInfoListByIdSet(gidList);
        logger.info("groupInfoList = " + groupInfoList);
        Map<Integer, ObjectNode> gid2GroupInfo = new HashMap<>();
        for (GroupInfo groupInfo : groupInfoList) {
            gid2GroupInfo.put(groupInfo.getId(), JsonUtils.groupInfo2Node(groupInfo));
        }
        //根据aid查出所有的articleResponse
        List<ArticleResponse> articleResponseList = articleDao.getArticleResponseListByIdList(aidList);
        Map<Integer, ObjectNode> aid2ArticleResponse = new HashMap<>();
        for (ArticleResponse articleResponse : articleResponseList) {
            aid2ArticleResponse.put(articleResponse.getId(), JsonUtils.articleResponse2Node(articleResponse));
        }

        //查询出所有的热门art放入redis
        for (Map.Entry<Integer, List<Integer>> entry : tag2Aid.entrySet()) {
            int tagId = entry.getKey();
            List<Integer> aidSortedList = entry.getValue();
            ArrayNode arrayNode = mapper.createArrayNode();
            for (int aid : aidSortedList) {
                arrayNode.add(aid2ArticleResponse.get(aid));
            }
            String key = getKey("article", tagId);
            redisUtils.set(key, arrayNode.toString(), 864000L);
        }


        //查询所有热门group放入redis
        for (Map.Entry<Integer, List<Integer>> entry : tag2Gid.entrySet()) {
            int tagId = entry.getKey();
            List<GroupScore> groupScores = new ArrayList<>();
            ArrayNode arrayNode = mapper.createArrayNode();
            //遍历该tagId下面的group
            List<Integer> gidValueList = entry.getValue();
            for (int gid : gidValueList) {
                GroupScore groupScore = new GroupScore(gid, groupScoreMap.get(gid));
                groupScores.add(groupScore);
            }
            //排序
            Collections.sort(groupScores);
            for (GroupScore groupScore : groupScores) {
                arrayNode.add(gid2GroupInfo.get(groupScore.getGid()));
            }
            String key = getKey("group", tagId);
            redisUtils.set(key, arrayNode.toString(), 864000L);
        }
    }

    private String getKey(String type, int tagId) {
        return RECOMMEND_PREFIX + ":" + type + ":" + tagId;
    }


    public List<ArticleResponse> getUserItemCFArticles(int uid, int limit) {

        List<ArticleResponse> res = new ArrayList<>();
        try {
            if (uid > 0) {

                String value = redisUtils.get(getUserItemCFKey(uid));
                if ("".equals(value)) {
                    return getHotArticles(limit);
                } else {
                    List<Integer> aidList = new ArrayList<>();
                    logger.info("value = " + value);
                    String[] aidArray = value.split("_");
                    for (String aid : aidArray) {
                        aidList.add(Integer.parseInt(aid));
                    }
                    logger.info("aidList = " + aidList);
                    //自己创建的日志不进行推荐
                    Set<Integer> userCreatedArticles = getUserCreatedArticles(uid);
                    logger.info("userCreatedArticles = " + userCreatedArticles);
                    List<ArticleResponse> articleResponseList = articleDao.getArticleResponseListByIdListOrder(aidList);

                    logger.info("articleResponseList  = " + articleResponseList.size());
                    for (ArticleResponse articleResponse : articleResponseList) {
                        if (!userCreatedArticles.contains(articleResponse.getId())) {
                            res.add(articleResponse);
                        }
                    }
                    if (res.size() >= limit) {
                        return res.subList(0, limit);
                    } else {
                        return addHotArticle(res, getHotArticles(limit), limit);
                    }
                }
            } else {
                res = getHotArticles(limit);
            }
            return res;
        } catch (Exception e) {
            logger.error("getHotArticles error", e);
        }
        return new ArrayList<>();
    }

    private Set<Integer> getUserCreatedArticles(int uid) {
        try {
            List<Integer> simpleArticles = articleDao.getSimpleArticlesByUid(uid);
            logger.info("simpleArticles = " + simpleArticles);
            return new HashSet<>(simpleArticles);

        } catch (Exception e) {
            logger.error("getUserCreatedArticles error uid = " + uid, e);
        }
        return new HashSet<>();
    }



    public void insertUserItemCF2Redis() {

        //将用户创建，点赞过和用户评论过的aid作为用户感兴趣aid
        List<Map<String, Object>> articleCreateList = articleDao.getSimpleArticleCreate();
        logger.info("articleCreateList = " + articleCreateList);
        List<Map<String, Object>> articleCommentList = articleDao.getSimpleArticleComment();
        logger.info("articleCommentList = " + articleCommentList);
        List<Map<String, Object>> articleLikeList = articleDao.getSimpleArticleLike();
        logger.info("articleLikeList = " + articleLikeList);
        //找出对每一个aid感兴趣对所有uid
        Map<Integer, Set<Integer>> aid2UidMap = new HashMap<>();
        //获取所有aid
        Set<Integer> allAidSet = new HashSet<>();
        //获取所有对uid
        Set<Integer> allUidSet = new HashSet<>();
        //获取所有uid感兴趣对aid
        Map<Integer, Set<Integer>> uid2AidMap = new HashMap<>();
        getInterestArticle(articleCreateList, aid2UidMap, allAidSet, uid2AidMap, allUidSet);
        getInterestArticle(articleCommentList, aid2UidMap, allAidSet, uid2AidMap, allUidSet);
        getInterestArticle(articleLikeList, aid2UidMap, allAidSet, uid2AidMap, allUidSet);
        logger.info("aid2UidMap = " + aid2UidMap);

        logger.info("uid2AidMap = " + uid2AidMap);
        logger.info("allUidSet = " + allUidSet);
        //计算出两两aid之间的相似度
        Map<Integer, Map<Integer, Double>> aidSimilarMap = new HashMap<>();
        logger.info("allAidSet = " + allAidSet);
        List<Integer> allAidList = new ArrayList<>(allAidSet);
        logger.info("allAidList = " + allAidList);
        for (int i=0; i<allAidList.size(); i++) {
            for (int j=i+1; j<allAidList.size(); j++) {
                int iAid = allAidList.get(i);
                int jAid = allAidList.get(j);
                Set<Integer> iSet = aid2UidMap.get(iAid);
                Set<Integer> jSet = aid2UidMap.get(jAid);

                double unCount = 0;
                for (int sameUid : iSet) {
                    if (jSet.contains(sameUid)) {
                        unCount++;
                    }
                }
                //相似度为0不存放
                if (unCount == 0D) {
                    continue;
                }
                double iCount = (double) iSet.size();
                double jCount = (double) jSet.size();
                double under = Math.sqrt(iCount * jCount);
                double similar = unCount / under;
                MapUtils.putAidSimilarMap(aidSimilarMap, iAid, jAid, similar);
                MapUtils.putAidSimilarMap(aidSimilarMap, jAid, iAid, similar);
            }
        }
        logger.info("aidSimilarMap = " + aidSimilarMap);
        Map<Integer, List<SimilarInfo>> sortedAidSimilarMap = sortAidSimilarMap(aidSimilarMap);
        logger.info("sortedAidSimilarMap = " + sortedAidSimilarMap);

        for (int uid : allUidSet) {
            Set<Integer> userInterestSet = uid2AidMap.get(uid);
            if (userInterestSet == null || userInterestSet.size() <= 0) {
                continue;
            }
            List<SimilarInfo> similarInfos = new ArrayList<>();
            
            for (int aid : allAidList) {
                if (userInterestSet.contains(aid)) {
                    continue;
                }
                double aidScore = getAidScoreByAidSimilarMap(sortedAidSimilarMap, userInterestSet, aid);
                if (aidScore > 0) {
                    similarInfos.add(new SimilarInfo(aid, aidScore));
                }
            }
            if (similarInfos.size() > 0) {
                logger.info("similarInfos = " + similarInfos);
                Collections.sort(similarInfos);
                StringBuilder sb = new StringBuilder();
                for (SimilarInfo similarInfo : similarInfos) {
                    sb.append(similarInfo.getAid()).append("_");
                }
                String key = getUserItemCFKey(uid);
                redisUtils.set(key, sb.toString(), 864000L);
            }
        }

    }

    private String getUserItemCFKey(int uid) {
        return RECOMMEND_PREFIX + ":user_itemCF:" + uid;
    }

    //根据本地排序获取
    private double getAidScoreByAidSimilarMap(Map<Integer, List<SimilarInfo>> sortedAidSimilarMap, Set<Integer> userInterestSet, int aid) {
        double aidScore = 0;
        List<SimilarInfo> aidSimilarTopSet = getTopNBYAidSimilarMap(sortedAidSimilarMap, aid, 20);
        if (aidSimilarTopSet.isEmpty()) {
            return 0;
        }

        for (SimilarInfo aidSimilarInfo : aidSimilarTopSet) {
            int similarAid = aidSimilarInfo.getAid();
            double similarScore = aidSimilarInfo.getScore();
            if (userInterestSet.contains(similarAid)) {
                aidScore += similarScore;
            }
        }
        return aidScore;
    }
    
    private List<SimilarInfo> getTopNBYAidSimilarMap(Map<Integer, List<SimilarInfo>> sortedAidSimilarMap, int aid, int n) {
        if (!sortedAidSimilarMap.containsKey(aid)) {
            return new ArrayList<>();
        }
        List<SimilarInfo> similarInfos = sortedAidSimilarMap.get(aid);
        if (n > similarInfos.size()) {
            return similarInfos.subList(0, similarInfos.size());
        }
        return similarInfos.subList(0, n);
    }

    private Map<Integer, List<SimilarInfo>> sortAidSimilarMap(Map<Integer, Map<Integer, Double>> aidSimilarMap) {

        try {
            Map<Integer, List<SimilarInfo>> res = new HashMap<>();
            for (Map.Entry<Integer, Map<Integer, Double>> similarMap : aidSimilarMap.entrySet()) {
                int key = similarMap.getKey();
                List<SimilarInfo> similarInfos = new ArrayList<>();
                for (Map.Entry<Integer, Double> entry : similarMap.getValue().entrySet()) {
                    similarInfos.add(new SimilarInfo(entry.getKey(), entry.getValue()));
                }
                Collections.sort(similarInfos);
                res.put(key, similarInfos);
            }
            return res;
        } catch (Exception e) {
            logger.error("sortAidSimilarMap error", e);
        }

        return new HashMap<>();
    }

    private void getInterestArticle(List<Map<String, Object>> articleInfoList, Map<Integer, Set<Integer>> aid2UidMap,
                                    Set<Integer> allAidSet, Map<Integer, Set<Integer>> uid2AidMap, Set<Integer> allUidSet) {
        for (Map<String, Object> articleInfo : articleInfoList) {
            int uid = ((Long) articleInfo.get("uid")).intValue();
            int aid = ((Long) articleInfo.get("aid")).intValue();
            MapUtils.addArticleUidMap(aid2UidMap, aid, uid);
            MapUtils.addArticleUidMap(uid2AidMap, uid, aid);
            allAidSet.add(aid);
            allUidSet.add(uid);
        }
    }


}
