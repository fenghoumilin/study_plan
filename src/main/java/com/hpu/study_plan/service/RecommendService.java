package com.hpu.study_plan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hpu.study_plan.dao.ArticleDao;
import com.hpu.study_plan.dao.GroupDao;
import com.hpu.study_plan.dao.RecommendDao;
import com.hpu.study_plan.dao.UserDao;
import com.hpu.study_plan.model.ArticleResponse;
import com.hpu.study_plan.model.GroupInfo;
import com.hpu.study_plan.model.GroupScore;
import com.hpu.study_plan.utils.GlobalPropertyUtils;
import com.hpu.study_plan.utils.JsonUtils;
import com.hpu.study_plan.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RecommendService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendService.class);
    private static final String RECOMMEND_PREFIX = GlobalPropertyUtils.get("redis.recommend.key.prefix");
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final int TAG_COUNT = GlobalPropertyUtils.getIntValue("tag_count");

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
                parameters.put("limit", limit - res.size());
                res.addAll(recommendDao.getHotGroups(parameters));
            }
            return res;
        } catch (Exception e) {
            logger.error("getHotGroupByTagList error userTag = " + userTag, e);
        }
        return new ArrayList<>();
    }



    public List<ArticleResponse> getHotArticles(int uid, int limit) {

        List<ArticleResponse> res = new ArrayList<>();
        try {
            if (uid > 0) {
                List<Integer> userTag = userDao.getUserTagByUid(uid);
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
                parameters.put("limit", limit - res.size());
                res.addAll(recommendDao.getHotArticles(parameters));
            }
            return res;
        } catch (Exception e) {
            logger.error("getHotArticleByTagList error userTag = " + userTag, e);
        }
        return new ArrayList<>();
    }

    public List<ArticleResponse> getHotArticles(int limit) {
        Set<Integer> tagSet = new HashSet<>();
        Random random = new Random();
        while (tagSet.size() < limit) {
            tagSet.add(random.nextInt(TAG_COUNT) + 1);
        }
        List<Integer> tagList = new ArrayList<>(tagSet);
        return getHotArticleByTagList(tagList, limit);
    }



    public void insertHotData2Redis(){

        List<Map<String, Object>> hotArticleDatas = recommendDao.getHotArticleData();
        Map<Integer, Integer> groupScoreMap = new HashMap<>();
        Map<Integer, List<Integer>> tag2Gid = new HashMap<>();
        Map<Integer, List<Integer>> tag2Aid = new HashMap<>();
        List<Integer> aidList = new ArrayList<>();
        List<Integer> gidList = new ArrayList<>();
        for (Map<String, Object> hotArticleData : hotArticleDatas) {
            int tagId = (int) hotArticleData.get("tag_id");
            int gid = ((Long) hotArticleData.get("gid")).intValue();
            int aid = ((Long) hotArticleData.get("aid")).intValue();
            int commentCount = (int) hotArticleData.get("comment_count");
            int likeCount = (int) hotArticleData.get("like_count");
            addGroupScoreMap(groupScoreMap, gid, commentCount * 5 + likeCount);
            addTag2ListMap(tag2Gid, tagId, gid);
            addTag2ListMap(tag2Aid, tagId, aid);
            gidList.add(gid);
            aidList.add(aid);
        }

        //根据gid查询出所有的groupInfo
        List<GroupInfo> groupInfoList = groupDao.getGroupInfoListByIdList(gidList);
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
            redisUtils.set(key, arrayNode.toString());
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
            redisUtils.set(key, arrayNode.toString());
        }
    }



    private String getKey(String type, int tagId) {
        return RECOMMEND_PREFIX + ":" + type + ":" + tagId;
    }

    private void addGroupScoreMap(Map<Integer, Integer> groupScoreMap, int gid, int count) {
        if (groupScoreMap.containsKey(gid)) {
            groupScoreMap.put(gid, groupScoreMap.get(gid) + count);
        } else {
            groupScoreMap.put(gid, count);
        }
    }

    private void addTag2ListMap(Map<Integer, List<Integer>> tag2Gid, int tagId, int gid) {
        if (tag2Gid.containsKey(gid)) {
            tag2Gid.get(tagId).add(gid);
        } else {
            tag2Gid.put(tagId, new ArrayList<Integer>() {{add(gid);}});
        }
    }

}
