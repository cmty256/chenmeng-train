package com.chenmeng.train.business.config;

import com.google.code.kaptcha.GimpyEngine;
import com.google.code.kaptcha.NoiseProducer;
import com.google.code.kaptcha.util.Configurable;
import com.jhlabs.image.RippleFilter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class KaptchaWaterRipple extends Configurable implements GimpyEngine {

	public KaptchaWaterRipple(){}

	/**
	 * 根据传入的原始图像生成扭曲的图像
	 * 此方法首先对原始图像应用水波纹滤镜以创建扭曲效果，然后在扭曲的图像上添加噪声
	 *
	 * @param baseImage 原始图像对象，用于生成扭曲图像
	 * @return 返回应用了扭曲和噪声效果的图像对象
	 */
	@Override
	public BufferedImage getDistortedImage(BufferedImage baseImage) {
	    // 获取噪声生成器实例
	    NoiseProducer noiseProducer = this.getConfig().getNoiseImpl();

	    // 创建一个新的图像，大小与原始图像相同，类型为32位ARGB
	    BufferedImage distortedImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

	    // 获取新图像的图形上下文
	    Graphics2D graph = (Graphics2D)distortedImage.getGraphics();

	    // 创建随机数生成器
	    Random rand = new Random();

	    // 创建并配置水波纹滤镜
	    RippleFilter rippleFilter = new RippleFilter();
	    rippleFilter.setXAmplitude(7.6F); // 设置X轴振幅
	    rippleFilter.setYAmplitude(rand.nextFloat() + 1.0F); // 随机生成Y轴振幅
	    rippleFilter.setEdgeAction(1); // 设置边缘处理方式

	    // 应用水波纹滤镜到原始图像
	    BufferedImage effectImage = rippleFilter.filter(baseImage, null);

	    // 将处理后的图像绘制到扭曲图像上
	    graph.drawImage(effectImage, 0, 0, null, null);

	    // 释放图形上下文资源
	    graph.dispose();

	    // 在扭曲的图像上生成噪声
	    noiseProducer.makeNoise(distortedImage, 0.1F, 0.1F, 0.25F, 0.25F);
	    noiseProducer.makeNoise(distortedImage, 0.1F, 0.25F, 0.5F, 0.9F);

	    // 返回最终地扭曲图像
	    return distortedImage;
	}
}
