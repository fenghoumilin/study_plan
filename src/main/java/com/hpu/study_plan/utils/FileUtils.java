package com.hpu.study_plan.utils;

import com.hpu.study_plan.controller.UploadController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    public static String upload(MultipartFile file, String path, String fileType){

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
            return url;
        } catch (Exception e) {
            logger.error("upload error", e);
            return "";
        }
    }

    private static String getFileName(String fileType){
        return UUIDUtils.getUUID() + "." + fileType;
    }
}
