package com.hpu.study_plan.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class CheckFileTypeUtils {
    private static final Logger logger = LoggerFactory.getLogger(CheckFileTypeUtils.class);

    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();

    static {
        //初始化文件类型信息
        FILE_TYPE_MAP.put("ffd8ff", "jpg"); //JPEG (jpg)
        FILE_TYPE_MAP.put("ffd8ffe0", "jpg"); //JPEG (jpg)
        FILE_TYPE_MAP.put("ffd8ffe1", "jpg"); //JPEG (jpg)
        FILE_TYPE_MAP.put("89504e47", "png"); //PNG (png)
        FILE_TYPE_MAP.put("47494638", "gif"); //GIF (gif)
        FILE_TYPE_MAP.put("49492a00", "tif"); //TIFF (tif)
        FILE_TYPE_MAP.put("424d", "bmp"); //16色位图(bmp)
        FILE_TYPE_MAP.put("41433130", "dwg"); //CAD (dwg)
        FILE_TYPE_MAP.put("3c21444f", "html"); //HTML (html)
        FILE_TYPE_MAP.put("52617221", "rar"); //css
        FILE_TYPE_MAP.put("504b0304", "docx"); //docx、xlsx
        FILE_TYPE_MAP.put("7b5c727466", "rtf"); //Rich Text Format (rtf)
        FILE_TYPE_MAP.put("38425053", "psd"); //Photoshop (psd)
        FILE_TYPE_MAP.put("44656c69766572792d646174653a", "eml"); //Email [Outlook Express 6] (eml)
        FILE_TYPE_MAP.put("d0cf11e0", "doc"); //MS office 注意：word、msi、excel、visio、WPS的文件头一样
        FILE_TYPE_MAP.put("5374616e64617264204a", "mdb"); //MS Access (mdb)
        FILE_TYPE_MAP.put("252150532d41646f6265", "ps");
        FILE_TYPE_MAP.put("25504446", "pdf"); //Adobe Acrobat (pdf)
        FILE_TYPE_MAP.put("2e524d46", "rmvb"); //rmvb/rm相同
        FILE_TYPE_MAP.put("464c5601", "flv"); //flv与f4v相同
        FILE_TYPE_MAP.put("00000018", "mp4");
        FILE_TYPE_MAP.put("0000001c", "mp4");
        FILE_TYPE_MAP.put("00000020", "mp4");
        FILE_TYPE_MAP.put("49443301", "mp3");
        FILE_TYPE_MAP.put("49443302", "mp3");
        FILE_TYPE_MAP.put("49443303", "mp3");
        FILE_TYPE_MAP.put("49443304", "mp3");
        FILE_TYPE_MAP.put("fffbc040", "mp3");
        FILE_TYPE_MAP.put("fffbe064", "mp3");
        FILE_TYPE_MAP.put("fffb90c4", "mp3");
        FILE_TYPE_MAP.put("fff", "mp3");
        FILE_TYPE_MAP.put("ffe", "mp3");
        FILE_TYPE_MAP.put("000001ba", "mpg");
        FILE_TYPE_MAP.put("000001b3", "mpg");
        FILE_TYPE_MAP.put("3026b2758e66cf11", "wmv"); //wmv、asf
        FILE_TYPE_MAP.put("52494646", "wav"); //Wave (wav)
        FILE_TYPE_MAP.put("41564920", "avi");
        FILE_TYPE_MAP.put("4d546864", "mid"); //MIDI (mid)
        FILE_TYPE_MAP.put("3c3f786d6c", "xml");//xml文件
        FILE_TYPE_MAP.put("1f8b08", "gz");//gz文件
        FILE_TYPE_MAP.put("1f8b0800", "gz");//gz文件
        FILE_TYPE_MAP.put("1f8b0808", "gz");//gz文件
        FILE_TYPE_MAP.put("00000014", "mov"); //Quicktime (mov)
    }

    /**
     * 得到上传文件的文件头
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 根据制定文件的文件头判断其文件类型
     *
     * @param filePath
     * @return
     */
    public static String getFileTypeByPath(String filePath) {
        logger.info(filePath);
        return getType(getFileHeader(filePath));
    }

    public static String getFileTypeByInputStream(FileInputStream fis) {
        return getType(getFileHeaderByInputStream(fis));
    }

    private static String getFileHeader(String filePath) {
        FileInputStream fis = null;
        String value = null;
        try {
            fis = new FileInputStream(filePath);
            value = getFileHeaderByInputStream(fis);
        } catch (Exception e) {
            logger.error("fail to initialize FileInputStream for file - " + filePath, e);
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("fail to get file header, " + filePath, e);
                }
            }
        }

        return value;
    }

    public static String getFileHeaderByInputStream(FileInputStream is) {
        String value = null;
        try {
            byte[] b = new byte[4];
            /*
             * int read() 从此输入流中读取一个数据字节。int read(byte[] b) 从此输入流中将最多 b.length
             * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
             * 从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
             */
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
            logger.error("fail to get type for file", e);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        logger.info("File Header:" + value);
        return value;
    }

    public static String getFileHeaderByByteArray(byte[] ba) {
        String type = null;
        String value = null;
        try {
            if (ba != null && ba.length == 4) {
                value = bytesToHexString(ba);
            }
            type = getType(value);
        } catch (Exception e) {
            logger.error("fail to get type for byte array", e);
        }
        logger.info("File Header:" + value + "| type:" + type);
        return type;
    }

    private static String getType(String header) {
        if (header == null || header.length() < 4 || header.length() > 8) {
            return null;
        }

        String type = FILE_TYPE_MAP.get(header);
        if (type != null) {
            return type;
        }

        header = header.substring(0,6);
        type = FILE_TYPE_MAP.get(header);
        if (type != null) {
            return type;
        }

        header = header.substring(0,4);
        type = FILE_TYPE_MAP.get(header);
        if (type != null) {
            return type;
        }

        header = header.substring(0,3);
        return FILE_TYPE_MAP.get(header);
    }

    public static void main(String[] args){
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/3.jpg"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/WechatIMG62.jpeg"));
        logger.info(getFileTypeByPath("/Users/jetyang/Desktop/test.png"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/default-avatar/7.png"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/bm25.gif"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/vi-vim-cheat-sheet-sch.gif"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/data_analysis_data_format.html"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/XX儿童视频项目产品文档.html"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/Photos.rar"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/20161123141034268383.docx"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/201010081722558.doc"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/seo.pdf"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/文网文.pdf"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/行歌-陈鸿宇.wav"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/formula.wav"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/pairSentence.txt.gz"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/apache-tomcat-8.0.24.tar.gz"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/取证内容.mov"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/paris.mp3"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/靳松 - 起风的瞬间 [rock]_128kb.mp3"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/行歌-陈鸿宇-soundlinks处理后.mp3"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/看见ING榜 VOL.9.mp3"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/Silent-Elegy乐队-Valkyrie.mp3"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/走歌人-暗杠.mp3"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/mamahao.mp4"));
        logger.info(getFileTypeByPath("/Users/jetyang/Downloads/PeppaPig粉紅豬小妹第三季08【募捐長跑】中文版1080P-Y4v0WT1oLAw.mp4"));
    }
}
