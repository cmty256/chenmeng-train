package com.chenmeng.train.generator.gen;

import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import com.chenmeng.train.generator.util.FreemarkerUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义代码生成器
 *
 * @author 沉梦听雨
 **/
public class ServerGenerator {

    /**
     * 服务路径，代码会在该路径下生成
     */
    static String serverPath = "[module]/src/main/java/com/chenmeng/train/[module]/";

    /**
     * pom路径
     */
    static String pomPath = "generator/pom.xml";

    /**
     * 模块名
     */
    static String module = "";

    public static void main(String[] args) throws Exception {
        // 获取mybatis-generator
        String generatorPath = getGeneratorPath();
        // 比如generator-config-member.xml，得到module = member
        module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("module: " + module);
        serverPath = serverPath.replace("[module]", module);
        new File(serverPath).mkdirs();
        System.out.println("servicePath: " + serverPath);

        // 读取table节点
        Document document = new SAXReader().read("generator/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());

        // 示例：表名 chenmeng_test
        // Domain = ChenmengTest
        String Domain = domainObjectName.getText();
        // domain = chenmengTest
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        // do_main = chenmeng-test
        String do_main = tableName.getText().replaceAll("_", "-");

        // 组装参数
        Map<String, Object> param = new HashMap<>();
        param.put("module", module);
        param.put("tableName", tableName.getText());
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);
        System.out.println("组装参数：" + param);

        // 生成业务类
        gen(Domain, param, "service", "service");
        // 生成 Controller 类
        gen(Domain, param, "controller", "controller");
    }

    private static void gen(String Domain, Map<String, Object> param, String packageName, String target) throws IOException, TemplateException {
        // 初始化配置
        FreemarkerUtil.initConfig(target + ".ftl");
        // 生成文件路径
        String toPath = serverPath + packageName + "/";
        // 在生成文件路径下新建一个文件
        new File(toPath).mkdirs();
        // 生成文件名
        String FileName = target.substring(0, 1).toUpperCase() + target.substring(1);
        // 生成文件的仓库根路径加文件后缀
        String repositoryRootPath = toPath + Domain + FileName + ".java";
        System.out.println("开始生成：" + repositoryRootPath);
        // 生成文件
        FreemarkerUtil.generator(repositoryRootPath, param);
    }

   private static String getGeneratorPath() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Map<String, String> map = new HashMap<>(1);
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document = saxReader.read(pomPath);
        // 使用XPath快速定位节点或属性, 需要引入依赖
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();
    }
}