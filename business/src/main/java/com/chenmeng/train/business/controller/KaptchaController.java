package com.chenmeng.train.business.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/kaptcha")
public class KaptchaController {

    final DefaultKaptcha defaultKaptcha;

    @Resource
    public StringRedisTemplate stringRedisTemplate;

    /**
     * @Qualifier 可以用来指定具体要注入哪一个 Bean
     *
     * @param defaultKaptcha
     */
    public KaptchaController(@Qualifier("getDefaultKaptcha") DefaultKaptcha defaultKaptcha) {
        this.defaultKaptcha = defaultKaptcha;
    }

    /**
     * 生成验证码
     * <p>
     * 此方法用于生成图像验证码，并将其输出到HttpServletResponse中
     * 它接受一个唯一的token作为参数，该token用于标识生成的验证码，
     * 以便在后续的验证过程中进行匹配
     * </p>
     *
     * @param imageCodeToken 前端生成唯一的token，每次都不能一样，用于贯穿验证码的生成和校验流程
     * @param httpServletResponse 用于向客户端输出图像验证码的HttpServletResponse对象
     * @throws Exception 如果生成过程中出现错误，可能会抛出异常
     */
    @GetMapping("/image-code/{imageCodeToken}")
    public void imageCode(@PathVariable(value = "imageCodeToken") String imageCodeToken,
                          HttpServletResponse httpServletResponse) throws Exception {
        // 使用ByteArrayOutputStream来存储生成的验证码图片的字节数据
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            // 生成验证码字符串
            String createText = defaultKaptcha.createText();

            // 将生成的验证码放入redis缓存中，后续验证的时候用到
            stringRedisTemplate.opsForValue()
                    .set(imageCodeToken, createText, 300, TimeUnit.SECONDS);

            // 使用验证码字符串生成验证码图片
            BufferedImage challenge = defaultKaptcha.createImage(createText);
            // 将生成的验证码图片写入到OutputStream中
            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            // 如果出现IllegalArgumentException异常，发送404错误
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        byte[] captchaChallengeAsJpeg = jpegOutputStream.toByteArray();

        // 设置Http响应头，防止图像缓存，确保用户每次请求获取的都是新的验证码
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        // 设置内容类型为图像/jpeg
        httpServletResponse.setContentType("image/jpeg");

        // 获取HttpServletResponse的输出流，用于输出图像数据
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        // 将生成的验证码图片数据写入到响应输出流中
        responseOutputStream.write(captchaChallengeAsJpeg);
        // 刷新输出流
        responseOutputStream.flush();
        // 关闭输出流
        responseOutputStream.close();
    }
}
