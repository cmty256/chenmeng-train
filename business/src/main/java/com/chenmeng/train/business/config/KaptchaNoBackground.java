package com.chenmeng.train.business.config;

import com.google.code.kaptcha.BackgroundProducer;
import com.google.code.kaptcha.util.Configurable;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class KaptchaNoBackground extends Configurable implements BackgroundProducer {

    public KaptchaNoBackground(){
    }

    /**
     * 为给定的图像添加背景
     *
     * @param baseImage 原始图像，需要添加背景
     * @return 带有背景的图像
     */
    @Override
    public BufferedImage addBackground(BufferedImage baseImage) {
        // 获取原始图像的宽度和高度
        int width = baseImage.getWidth();
        int height = baseImage.getHeight();

        // 创建一个新的BufferedImage，类型为1（BufferedImage.TYPE_INT_RGB），用于添加背景后的图像
        BufferedImage imageWithBackground = new BufferedImage(width, height, 1);

        // 获取新图像的Graphics2D对象，用于绘制
        Graphics2D graph = (Graphics2D)imageWithBackground.getGraphics();

        // 填充整个图像区域为白色，作为背景
        graph.fill(new Rectangle2D.Double(0.0D, 0.0D, width, height));

        // 将原始图像绘制到新图像上，位置为(0,0)，即左上角
        graph.drawImage(baseImage, 0, 0, null);

        // 返回添加背景后的图像
        return imageWithBackground;
    }

}
