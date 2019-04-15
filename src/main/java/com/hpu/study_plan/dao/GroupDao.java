package com.hpu.study_plan.dao;

import com.hpu.study_plan.model.GroupInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface GroupDao {

    List<Map<String, Object>> getTagList();
    int insertGroup(Map<String, Object> paramaters);
    List<GroupInfo> getGroupInfoListByUid(Map<String, Object> paramaters);
    List<GroupInfo> getGroupInfoListById(int id);
    List<Map<String, Object>> getSimpleGroupList(int uid);
}
