package com.hpu.study_plan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hpu.study_plan.model.UserInfo;
import com.hpu.study_plan.service.UploadService;
import com.hpu.study_plan.service.UserService;
import com.hpu.study_plan.utils.CheckFileTypeUtils;
import com.hpu.study_plan.utils.FileUtils;
import com.hpu.study_plan.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
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
                            @RequestParam("pic") MultipartFile file,
                            Model model,
                            HttpServletResponse response) {
        ObjectNode jo;
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String phoneNumber = (String) session.getAttribute(sessionId);

        logger.info("phoneNumber = " + phoneNumber);
        UserInfo userInfo = userService.getUserInfoByPhone(phoneNumber);
        logger.info("进入上传页面");
        try {
            logger.info("type = " + type);
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
                if ("jpg".equals(fileType) || "png".equals(fileType)) {
                    String url = FileUtils.upload(file, LOCAL_PATH + type, fileType, type);
                    if (!StringUtils.isEmpty(url)) {
                        ObjectNode data = mapper.createObjectNode();
                        logger.info("url = " + url);
                        data.put("url", url);
                        ModelAndView modelAndView = new ModelAndView();
                        userInfo.setAvatarPicUrl(url);
                        model.addAttribute("resUrl", url);
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
