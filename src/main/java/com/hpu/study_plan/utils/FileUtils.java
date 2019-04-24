package com.hpu.study_plan.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final String USER_AVATAR = GlobalPropertyUtils.get("img_type.user_avatar");
    private static final String LOCAL_PATH = System.getProperty("user.home") + "/graduation_project/study_plan/src/main/resources/static/img/";

    public static String upload(MultipartFile file, String path, String fileType, String type){

        String url = FileUtils.getFileName(fileType);
        // 生成新的文件名
        String realPath = path + "/" + url;

        try {
            File dest = new File(realPath);
            //判断文件父目录是否存在
            if(!dest.getParentFile().exists()){
                dest.getParentFile().mkdirs();
                dest.createNewFile();
            }
            //保存文件
            logger.info("dest = " + dest);
            file.transferTo(dest);

            if (USER_AVATAR.equals(type)) {
                BufferedImage bufferedImage = ImageIO.read(dest);
                BufferedImage headImage = ImageUtils.narrowImage(bufferedImage, 80, 80);
                //转化成圆
                headImage = ImageUtils.transformCircular(headImage, 720);
                ImageUtils.writeImage(headImage, fileType, realPath);
            } else {
                BufferedImage bufferedImage = ImageIO.read(dest);
                BufferedImage headImage = ImageUtils.narrowImage(bufferedImage, 1750, 1000);
                //
                ImageUtils.writeImage(headImage, fileType, realPath);
            }
            return url;
        } catch (Exception e) {
            logger.error("upload error", e);
            return "";
        }
    }

    public static String upload(MultipartFile file, String type) {
        if (file.isEmpty()) {
            return "";
        }
        try {
            logger.info("type = " + type);
            long size = file.getSize();
            if (size > 1024 * 1024 * 5) { //文件太大了
                logger.warn("pic file too large: " + size);
            } else {
                byte[] byteArray = file.getBytes();
                byte[] fileTypeByte = new byte[4];
                System.arraycopy(byteArray, 0, fileTypeByte, 0, fileTypeByte.length);
                String fileType = CheckFileTypeUtils.getFileHeaderByByteArray(fileTypeByte);
                if ("jpg".equals(fileType) || "png".equals(fileType)) {
                    String url = upload(file, LOCAL_PATH + type, fileType, type);
                    logger.info("url = " + url);
                    return url;
                }
            }
        } catch (Exception e) {
            logger.error("fail to handle pic upload", e);
        }

        return "";
    }

    private static String getFileName(String fileType){
        return UUIDUtils.getUUID() + "." + fileType;
    }
}
