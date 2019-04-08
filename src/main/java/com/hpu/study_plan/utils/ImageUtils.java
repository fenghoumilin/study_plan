package com.hpu.study_plan.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ImageUtils{


    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    public static void writeImage(BufferedImage imageNew, String formatName, String path) {
        try {
            File outFile = new File(path);
            if (outFile.isFile() && outFile.exists()) {
                outFile.delete();
            }
            ImageIO.write(imageNew, formatName, outFile);
        } catch (Exception e) {
            logger.error("写入失败", e);
        }
    }

    public static BufferedImage transformCircular(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        output = g2.getDeviceConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        g2.dispose();
        g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawImage(image, 0, 0, w, h, null);
        g2.dispose();
        return output;
    }


    public static BufferedImage narrowImage(BufferedImage image, int width, int height) {

        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = buffImg.createGraphics();
        graphics.setBackground(new Color(255,255,255));
        graphics.setColor(new Color(255,255,255));
        graphics.fillRect(0, 0, width, height);
        graphics.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        graphics.dispose();
        return buffImg;
    }

}
