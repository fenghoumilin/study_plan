package com.hpu.study_plan.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hpu.study_plan.service.UploadService;
import com.hpu.study_plan.service.UserService;
import com.hpu.study_plan.utils.CheckFileTypeUtils;
import com.hpu.study_plan.utils.FileUtils;
import com.hpu.study_plan.utils.RequestParser;
import com.hpu.study_plan.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    private static final String LOCAL_PATH = System.getProperty("user.home") + "/graduation_project/img/";
    @Autowired
    UploadService uploadService;
    @Autowired
    UserService userService;

    @RequestMapping(value="/upload/pic", method= RequestMethod.POST, consumes="multipart/form-data")
    public String uploadPic(HttpServletRequest request,
                            @RequestParam("type") String type,
                            @RequestParam("file") MultipartFile file,
                            HttpServletResponse response) {
        ObjectNode jo;
        ObjectMapper mapper = new ObjectMapper();

        try {
            long size = file.getSize();
            if (size > 1024 * 1024 * 5) { //文件太大了
                logger.warn("pic file too large: " + size);
                jo = ResponseUtils.putErrorJson(1002);
                return jo.toString();
            } else {
                byte[] byteArray = file.getBytes();
                byte[] fileTypeByte = new byte[4];
                System.arraycopy(byteArray, 0, fileTypeByte, 0, fileTypeByte.length);
                String fileType = CheckFileTypeUtils.getFileHeaderByByteArray(fileTypeByte);
                if (!"jpg".equals(fileType) && !"png".equals(fileType)) {
                    String url = FileUtils.upload(file, LOCAL_PATH + type, fileType);
                    if (!StringUtils.isEmpty(url)) {
                        ObjectNode data = mapper.createObjectNode();
                        data.put("url", url);
                        return ResponseUtils.putSuccessJson(data).toString();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("fail to handle pic upload", e);
        }
        jo = ResponseUtils.putErrorJson(1012);
        response.setStatus(500);
        return jo.toString();
    }
}
