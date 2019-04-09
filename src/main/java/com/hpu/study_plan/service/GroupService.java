package com.hpu.study_plan.service;

import com.hpu.study_plan.dao.GroupDao;
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
public class GroupService {

    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    GroupDao groupDao;


    public List<Map<String, Object>> getTagList() {

        try {
            return groupDao.getTagList();
        } catch (Exception e) {
            logger.info("getTagList error", e);
        }

        return new ArrayList<>();
    }

    public boolean insertGroup(int uid, String title, String content, String picUrl, int tagId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("uid", uid);
        parameters.put("title", title);
        parameters.put("content", content);
        parameters.put("pic_url", picUrl);
        parameters.put("tag_id", tagId);
        try {
            groupDao.insertGroup(parameters);
            return true;
        } catch (Exception e) {
            logger.error("insertGroup error | " + parameters, e);
        }

        return false;
    }

    public List<GroupInfo> getGroupInfoList(int uid) {
        try {
            return groupDao.getGroupInfoList(uid);
        } catch (Exception e) {
            logger.error("insertGroup error | uid = " + uid, e);
        }
        return new ArrayList<>();
    }




}
