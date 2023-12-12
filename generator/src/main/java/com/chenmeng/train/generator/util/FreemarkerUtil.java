package com.chenmeng.train.generator.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * 模板引擎 freemarker 工具类
 *
 * @author 沉梦听雨
 **/
public class FreemarkerUtil {

    /**
     * 模板文件路径
     */
    static String ftlPath = "generator/src/main/java/com/chenmeng/train/generator/ftl/";

    /**
     * 模板对象
     */
    static Template temp;

    /**
     * 读模板
     */
    public static void initConfig(String ftlName) throws IOException {
        // 版本与freemarker模板引擎依赖一致
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File(ftlPath));
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_31));
        temp = cfg.getTemplate(ftlName);
    }

    /**
     * 根据模板，生成文件
     */
    public static void generator(String fileName, Map<String, Object> map) throws IOException, TemplateException {
        FileWriter fw = new FileWriter(fileName);
        BufferedWriter bw = new BufferedWriter(fw);
        temp.process(map, bw);
        bw.flush();
        fw.close();
    }
}