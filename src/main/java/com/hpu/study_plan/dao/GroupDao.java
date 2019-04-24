package com.hpu.study_plan.dao;

import com.hpu.study_plan.model.GroupInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Mapper
public interface GroupDao {

    List<Map<String, Object>> getTagList();
    int insertGroup(Map<String, Object> parameters);
    List<GroupInfo> getGroupInfoListByUid(Map<String, Object> parameters);
    List<GroupInfo> getGroupInfoListById(int id);
    List<GroupInfo> getGroupInfoListByIdSet(Set<Integer> gidList);
    List<Map<String, Object>> getSimpleGroupList(int uid);
    List<Map<String, Object>> getSimpleGroupListByGroupFun(int uid);
    int getGroupTag(int gid);
    int insertGroupFun(Map<String, Object> parameters);
    int isGroupFun(Map<String, Object> parameters);
    int isGroupOwner(Map<String, Object> parameters);
    int copyOldGroupFun(Map<String, Object> parameters);
    int delGroupFun(Map<String, Object> parameters);
}
