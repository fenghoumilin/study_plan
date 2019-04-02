package com.hpu.study_plan.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class FileUtils {

    public static String upload(MultipartFile file, String path, String fileType){

        String url = FileUtils.getFileName(fileType);
        // 生成新的文件名
        String realPath = path + "/" + url;

        File dest = new File(realPath);

        try {
            //判断文件父目录是否存在
            if(!dest.getParentFile().exists()){
                dest.getParentFile().mkdir();
            }
            //保存文件
            file.transferTo(dest);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getFileName(String fileType){
        return UUIDUtils.getUUID() + "." + fileType;
    }
}
