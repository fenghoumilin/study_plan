package com.hpu.study_plan.utils;

import java.util.*;

public class MapUtils {

    public static void addGroupScoreMap(Map<Integer, Integer> scoreMap, int id, int count) {
        if (scoreMap.containsKey(id)) {
            scoreMap.put(id, scoreMap.get(id) + count);
        } else {
            scoreMap.put(id, count);
        }
    }

    public static void addTag2ListMap(Map<Integer, List<Integer>> tag2Gid, int tagId, int gid) {
        if (tag2Gid.containsKey(gid)) {
            tag2Gid.get(tagId).add(gid);
        } else {
            tag2Gid.put(tagId, new ArrayList<Integer>() {{add(gid);}});
        }
    }

    public static void addArticleUidMap(Map<Integer, Set<Integer>> tag2Gid, int aid, int uid) {
        if (tag2Gid.containsKey(aid)) {
            tag2Gid.get(aid).add(uid);
        } else {
            tag2Gid.put(aid, new HashSet<Integer>() {{add(uid);}});
        }
    }

    public static void putAidSimilarMap(Map<Integer, Map<Integer, Double>> aidSimilarMap, int key, Integer mapKey, Double mapValue) {
        if (aidSimilarMap.containsKey(key)) {
            aidSimilarMap.get(key).put(mapKey, mapValue);
        } else {
            aidSimilarMap.put(key, new HashMap<Integer, Double>(){{put(mapKey, mapValue);}});
        }
    }
}
