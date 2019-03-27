package com.hpu.study_plan.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class GlobalPropertyUtils {

    private static final Logger logger = LoggerFactory.getLogger(GlobalPropertyUtils.class);
    static Map<String, String> parameters;

    public static int getIntValue(String key) {
        try {
            return Integer.parseInt(parameters.get(key));
        } catch (Exception e) {
            logger.error("want a int value, fail to convert", e);
        }
        return -1;
    }

    public static String get(String key) {
        String value = parameters.get(key);
        if (value == null) {
            return "未知错误";
        }
        return value;
    }

    static {
        Properties props = new Properties();
        InputStream in = null;
        try {
            //String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
            //logger.info("payh = " + path);
            ClassPathResource cpr = new ClassPathResource("global.properties");
            in = cpr.getInputStream();
            // 如果将in改为下面的方法，必须要将.Properties文件和此class类文件放在同一个包中
            //in = propertiesTools.class.getResourceAsStream(fileNamePath);
            props.load(in);
            parameters = new HashMap<String, String>();
            Set<Object> keySet = props.keySet();
            for (Object key : keySet) {
                parameters.put((String) key, props.getProperty((String) key));
            }
            // 有乱码时要进行重新编码
            // new String(props.getProperty("name").getBytes("ISO-8859-1"), "GBK");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("GlobalPropertyUtils 文件关闭失败");
                }
            }
        }
    }


}
