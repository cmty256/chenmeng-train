package com.chenmeng.train.business.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    /**
     * 配置并返回一个DefaultKaptcha实例
     * 该方法通过@Bean注解指示该方法返回的对象将由Spring容器管理，自动装配到应用程序上下文中
     *
     * @return 配置好的DefaultKaptcha实例，用于生成验证码
     */
    @Bean
    public DefaultKaptcha getDefaultKaptcha() {
        // 创建并初始化一个DefaultKaptcha对象
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        // 创建一个Properties对象用于存储验证码的配置属性
        Properties properties = new Properties();
        // properties.setProperty("kaptcha.border.color", "105,179,90"); // 设置验证码图片边框的颜色，但这里设置为无
        // 设置验证码图片的边框为无
        properties.setProperty("kaptcha.border", "no");
        // 设置验证码文本的颜色为蓝色
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        // 设置验证码图片的宽度
        properties.setProperty("kaptcha.image.width", "90");
        // 设置验证码图片的高度
        properties.setProperty("kaptcha.image.height", "28");
        // 设置验证码文本的字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "20");
        // 设置存储在session中的验证码的键名
        properties.setProperty("kaptcha.session.key", "code");
        // 设置验证码文本的长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 设置验证码文本的字体
        properties.setProperty("kaptcha.textproducer.font.names", "Arial");
        // 设置验证码干扰项的颜色
        properties.setProperty("kaptcha.noise.color", "255,96,0");
        // 设置验证码的干扰实现类，这里设置为无干扰
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        // 设置验证码的模糊实现类，增强安全性（为了防止黑客使用OCR技术自动识别图片验证码的文字，我们需要让验证码变得模糊），水波纹设置
        // properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
        properties.setProperty("kaptcha.obscurificator.impl", KaptchaWaterRipple.class.getName());
        // 设置验证码的背景实现类，这里设置为无背景
        properties.setProperty("kaptcha.background.impl", KaptchaNoBackground.class.getName());
        // 创建一个Config对象，并将Properties对象传递给它，用于配置DefaultKaptcha
        Config config = new Config(properties);
        // 设置DefaultKaptcha的配置
        defaultKaptcha.setConfig(config);
        // 返回配置好的DefaultKaptcha对象
        return defaultKaptcha;
    }


    @Bean
    public DefaultKaptcha getWebKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "no");
        // properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        properties.setProperty("kaptcha.image.width", "90");
        properties.setProperty("kaptcha.image.height", "45");
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        properties.setProperty("kaptcha.session.key", "code");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.font.names", "Arial");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        properties.setProperty("kaptcha.obscurificator.impl", KaptchaWaterRipple.class.getName());
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
