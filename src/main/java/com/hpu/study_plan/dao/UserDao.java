package com.hpu.study_plan.dao;

import com.hpu.study_plan.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface UserDao {
    int insertUser(Map<String, Object> parameters);
    int getUserCount(String phoneNumber);
    UserInfo getUserInfoByPhone(String phoneNumber);
    UserInfo getUserInfoById(int id);
    int updateUser(Map<String, Object> parameters);
    List<Integer> getUserTagByUid(int uid);
    int insertUserTag(Map<String, Object> parameters);
}
