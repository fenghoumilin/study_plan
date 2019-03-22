package com.hpu.study_plan.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestParser {
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);

    public static JsonNode getPostParameter(HttpServletRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode returnNode = mapper.createObjectNode();
        try {
            InputStream is = request.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer content = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
            String parameterStr = content.toString().trim();
            logger.info(parameterStr);
            returnNode = mapper.readTree(parameterStr);
        } catch (Exception e) {
            logger.error("fail to parse parameter string to json object", e);
        }

        return returnNode;
    }

    public static JsonNode getPostParameter(String parameterStr) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode returnNode = mapper.createObjectNode();
        try {
            returnNode = mapper.readTree(parameterStr);
        } catch (Exception e) {
            logger.error("fail to parse parameter string to json object|" + parameterStr, e);
        }

        return returnNode;
    }

    public static String getIP(HttpServletRequest request) {
        String realIp = request.getHeader("x-real-ip");
        if (StringUtils.isEmpty(realIp)) {
            realIp = request.getRemoteAddr();
        }
        return realIp;
    }
}
