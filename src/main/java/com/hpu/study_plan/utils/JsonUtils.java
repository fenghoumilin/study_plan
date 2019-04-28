package com.hpu.study_plan.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hpu.study_plan.model.ArticleResponse;
import com.hpu.study_plan.model.GroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static ObjectMapper mapper = new ObjectMapper();

    public static ObjectNode groupInfo2Node(GroupInfo groupInfo) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", groupInfo.getId());
        objectNode.put("title", groupInfo.getTitle());
        objectNode.put("content", groupInfo.getContent());
        objectNode.put("picUrl", groupInfo.getPicUrl());
        objectNode.put("tagId", groupInfo.getTagId());
        objectNode.put("createdTime", timeFormat.format(groupInfo.getCreatedTime()));
        return objectNode;
    }

    public static GroupInfo node2GroupInfo(JsonNode jsonNode) {
        try {
            return new GroupInfo(
                    jsonNode.get("id").intValue(),
                    jsonNode.get("title").asText(),
                    jsonNode.get("content").asText(),
                    jsonNode.get("picUrl").asText(),
                    jsonNode.get("tagId").intValue(),
                    timeFormat.parse(jsonNode.get("createdTime").asText())
            );
        } catch (Exception e) {
            logger.error("timeFormat.parse error jsonNode = " + jsonNode, e);
        }
        return new GroupInfo();
    }

    public static ObjectNode articleResponse2Node(ArticleResponse articleResponse) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", articleResponse.getId());
        objectNode.put("uid", articleResponse.getUid());
        objectNode.put("gid", articleResponse.getUid());
        objectNode.put("authorName", articleResponse.getAuthorName());
        objectNode.put("title", articleResponse.getTitle());
        objectNode.put("content", articleResponse.getContent());
        objectNode.put("picUrl", articleResponse.getPicUrl());
        objectNode.put("authorAvatar", articleResponse.getAuthorAvatar());
        objectNode.put("createdTime", timeFormat.format(articleResponse.getCreatedTime()));
        return objectNode;
    }

    public static ArticleResponse node2ArticleResponse(JsonNode jsonNode) {
        try {
            return new ArticleResponse(
                    jsonNode.get("id").intValue(),
                    jsonNode.get("uid").intValue(),
                    jsonNode.get("gid").intValue(),
                    jsonNode.get("authorName").asText(),
                    jsonNode.get("title").asText(),
                    jsonNode.get("content").asText(),
                    jsonNode.get("picUrl").asText(),
                    jsonNode.get("authorAvatar").asText(),
                    timeFormat.parse(jsonNode.get("createdTime").asText())
            );
        } catch (Exception e) {
            logger.error("timeFormat.parse error jsonNode = " + jsonNode, e);
        }
        return new ArticleResponse();
    }



}
