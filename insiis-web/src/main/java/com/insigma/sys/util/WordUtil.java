package com.insigma.sys.util;


import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author GH
 * @ClassName: WordUtil
 * @Description:
 * @date 2022/3/22  11:12
 */
public class WordUtil {


    //读取配置文件
    private static Configuration configuration = null;
    private static Map<String, Template> allTemplate = null;
    private static final String TEMPLATE_PATH = "/templates"; // 模板加载路径


    static {
        configuration = new Configuration(Configuration.VERSION_2_3_22);
        //设置编码
        configuration.setDefaultEncoding("UTF-8");
        //按照路径加载模板
        configuration.setClassForTemplateLoading(WordUtil.class, TEMPLATE_PATH);
        //存储模板
        allTemplate = new HashMap<String, Template>();
        try {
            //按照指定编码读取文档报告模板
            allTemplate.put("monReportTemplet", configuration.getTemplate("monReportTemplate.ftl", "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建doc文档
     *
     * @param dataMap
     * @param type
     * @return
     */
    public static File createDoc(Map<?, ?> dataMap, String type) {
        //文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String name = "temp_" + sdf.format(new Date()) + ".doc";
        File file = new File(name);
        //获取模板
        Template t = allTemplate.get(type);
        //这个地方不能使用FileWriter因为需要指定编码类型否则声场的word文档会因为有无法识别的编码而无法打开
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
                Writer w = new OutputStreamWriter(fileOutputStream, "UTF-8")) {
            t.process(dataMap, w);
            w.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return file;
    }
}
