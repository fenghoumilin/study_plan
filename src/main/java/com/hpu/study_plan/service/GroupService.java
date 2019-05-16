package com.hpu.study_plan.service;

import com.hpu.study_plan.dao.GroupDao;
import com.hpu.study_plan.model.GroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
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

    public int insertGroup(int uid, String title, String content, String picUrl, int tagId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("uid", uid);
        parameters.put("title", title);
        parameters.put("content", content);
        parameters.put("pic_url", picUrl);
        parameters.put("tag_id", tagId);
        try {
            groupDao.insertGroup(parameters);
            logger.info("id = " + parameters.get("id"));
            return ((BigInteger) parameters.get("id")).intValue();
        } catch (Exception e) {
            logger.error("insertGroup error | " + parameters, e);
        }

        return 0;
    }

    public int updateGroupInfo(int gid, String title, String content, String picUrl, int tagId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("gid", gid);
        parameters.put("title", title);
        parameters.put("content", content);
        parameters.put("pic_url", picUrl);
        parameters.put("tag_id", tagId);
        try {
            groupDao.updateGroupInfo(parameters);
            return gid;
        } catch (Exception e) {
            logger.error("insertGroup error | " + parameters, e);
        }

        return 0;
    }



    public List<GroupInfo> getGroupInfoListByUid(int uid, int limit) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("uid", uid);
        parameters.put("limit", limit);
        try {
            return groupDao.getGroupInfoListByUid(parameters);
        } catch (Exception e) {
            logger.error("getGroupInfoListByUid error | uid = " + uid, e);
        }
        return new ArrayList<>();
    }

    public List<GroupInfo> getGroupInfoListById(int id) {
        try {
            return groupDao.getGroupInfoListById(id);
        } catch (Exception e) {
            logger.error("getGroupInfoListById error | uid = " + id, e);
        }
        return new ArrayList<>();
    }

    public boolean isGroupOwner(int uid, int gid) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("uid", uid);
        parameters.put("gid", gid);
        try {
            return groupDao.isGroupOwner(parameters) == 1;
        } catch (Exception e) {
            logger.error("isGroupOwner error ", e);
        }
        return false;
    }

    public List<Map<String, Object>> getSimpleGroupList(int uid) {

        try {
            List<Map<String, Object>> simpleGroupList = groupDao.getSimpleGroupList(uid);
            simpleGroupList.addAll(groupDao.getSimpleGroupListByGroupFun(uid));
            return simpleGroupList;
        } catch (Exception e) {
            logger.error("getSimpleGroupList error | uid = " + uid, e);
        }

        return new ArrayList<>();
    }

    public int getGroupTag(int gid) {
        try {
            return groupDao.getGroupTag(gid);
        } catch (Exception e) {
            logger.error("getGroupTag error | gid = " + gid, e);
        }
        return 0;
    }

    public boolean insertGroupFun(int uid, int gid) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("uid", uid);
        parameters.put("gid", gid);
        try {
            groupDao.insertGroupFun(parameters);
            return true;
        } catch (Exception e) {
            logger.error("insertGroupFun error | parameters = " + parameters, e);
        }
        return false;
    }

    //如果是社区创建者，就不需要关注返回2
    public int isGroupFun(int uid, int gid) {

        if (uid <= 0 || gid <= 0) {
            return 0;
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("uid", uid);
        parameters.put("gid", gid);
        try {
            int owner = groupDao.isGroupOwner(parameters);
            if (owner == 1) {
                return 2;
            }
            return groupDao.isGroupFun(parameters);
        } catch (Exception e) {
            logger.error("isGroupFun error | parameters = " + parameters, e);
        }

        return 0;
    }

    public boolean copyOldGroupFun(Map<String, Object> parameters) {

        try {
            groupDao.copyOldGroupFun(parameters);
            return true;
        } catch (Exception e) {
            logger.error("copyOldGroupFun error | parameters = " + parameters, e);
        }

        return false;
    }
    public boolean delGroupFun(int uid, int gid) {
        if (uid <= 0 || gid <= 0) {
            return false;
        }
        if (isGroupFun(uid, gid) != 1) {
            return false;
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("uid", uid);
        parameters.put("gid", gid);
        if (copyOldGroupFun(parameters)) {
            try {
                groupDao.delGroupFun(parameters);
                return true;
            } catch (Exception e) {
                logger.error("delGroupFun error | parameters = " + parameters, e);
            }
        }

        return false;
    }


}
