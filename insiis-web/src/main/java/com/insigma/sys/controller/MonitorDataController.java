package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.sys.dto.MonitorCentersDTO;
import com.insigma.sys.service.MonitorCentersService;
import com.insigma.sys.util.WordUtil;
import com.insigma.web.support.fileaccess.excel.ExcelFactory;
import com.insigma.web.support.fileaccess.excel.ExcelWriter;
import com.insigma.web.support.fileaccess.excel.config.ExcelColConfig;
import com.insigma.web.support.fileaccess.excel.config.ExcelConfig;
import com.insigma.web.support.fileaccess.excel.enums.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GH
 * @version 2022/3/8  16:46
 * @ClassName: MonitorDataController
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/sys/monitoringcenter/index")
public class MonitorDataController {

    @Autowired
    MonitorCentersService monitorCentersService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Value("${spring.application.name}")
    private String appName;

    /**
     * 获取报告数据
     *
     * @return
     */
    @PostMapping("/getReportData")
    public JSONObject getReportData(@RequestBody MonitorCentersDTO monitorCentersDTO) {
        return monitorCentersService.getReportData(monitorCentersDTO);
    }


    //导出监控大屏数据
    @GetMapping("/dataExport")
    public void dataExport(@RequestParam("excelName") String excelName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(excelName, "utf-8"));

        ExcelTypeEnum excelType = excelName.endsWith(".xls") ? ExcelTypeEnum.XLS : ExcelTypeEnum.XLSX;
        ExcelWriter writer = ExcelFactory.getWriter(response.getOutputStream(), new ExcelConfig(excelType, true,
                ExcelColConfig.of("CREATE_TIME", "创建时间"),
                ExcelColConfig.of("START_TIME", "统计开始时间"),
                ExcelColConfig.of("END_TIME", "统计结束时间"),
                ExcelColConfig.of("SQL_EXECUTE_COUNT", "SQL执行总数"),
                ExcelColConfig.of("SQL_TOTAL_TIME", "SQL耗时总数"),
                ExcelColConfig.of("SQL_AVG_TIME", "SQL执行平均耗时"),
                ExcelColConfig.of("SQL_SLOWEST_TIME", "SQL执行最慢耗时"),
                ExcelColConfig.of("SQL_AVG_READ_ROWS", "SQL单次平均读取行数"),
                ExcelColConfig.of("URL_EXECUTE_COUNT", "URL执行总数"),
                ExcelColConfig.of("URL_TOTAL_TIME", "URL耗时总数"),
                ExcelColConfig.of("URL_AVG_TIME", "URL执行平均耗时"),
                ExcelColConfig.of("URL_SLOWEST_TIME", "URL执行最慢耗时"),
                ExcelColConfig.of("URL_AVG_JDBC_COUNT", "URL单次平均JDBC执行数"),
                ExcelColConfig.of("SLOW_SQL_ONE", "慢SQL最慢第一段个数"),
                ExcelColConfig.of("SLOW_SQL_TWO", "慢SQL最慢第二段个数"),
                ExcelColConfig.of("SLOW_SQL_THREE", "慢SQL最慢第三段个数"),
                ExcelColConfig.of("SLOW_SQL_AVG_ONE", "慢SQL平均第一段个数"),
                ExcelColConfig.of("SLOW_SQL_AVG_TWO", "慢SQL平均第二段个数"),
                ExcelColConfig.of("SLOW_SQL_AVG_THREE", "慢SQL平均第三段个数"),
                ExcelColConfig.of("SQL_ERROR_COUNT", "SQL执行错误数"),
                ExcelColConfig.of("URL_JDBC_ERROR_COUNT", "URLJDBC执行错误数"),
                ExcelColConfig.of("SCORE", "得分"),
                ExcelColConfig.of("SCORE_SLOW_SQL", "慢SQL影响分"),
                ExcelColConfig.of("SCORE_ERROR_SQL", "错误SQL影响分"),
                ExcelColConfig.of("SCORE_SLOW_URL", "慢URL影响分"),
                ExcelColConfig.of("SCORE_JDBC_ERROR_URL", "JDBC错误URL影响分"),
                ExcelColConfig.of("INSTANCE_COUNT", "集群实例数"),
                ExcelColConfig.of("DS_MAX_ACTIVE", "数据库最大连接数")
        ));
        final int[] index = {0};
        List<Map> mapList = monitorCentersService.getDataExport(appName);
        for (Map map : mapList) {
            writer.writeRow(map, ++index[0]);
        }
        writer.finish();
    }


    //获取word文档数据
    @PostMapping("/getWrodData")
    public Map<String, Object> getWrodData(@RequestBody MonitorCentersDTO monitorCentersDTO) {
        return monitorCentersService.getReportDetailedData(monitorCentersDTO);
    }

    //导出word文档
    @GetMapping("/word")
    public void generateWord(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MonitorCentersDTO dto = new MonitorCentersDTO();
        dto.setAppName(appName);
        // 要填入模本的数据文件
        Map<String, Object> dataMap = monitorCentersService.getReportDetailedData(dto);

        //提示：在调用工具类生成Word文档之前应当检查所有字段是否完整
        //否则Freemarker的模板殷勤在处理时可能会因为找不到值而报错，这里暂时忽略这个步骤
        File file = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            //调用工具类WordGenerator的createDoc方法生成Word文档
            file = WordUtil.createDoc(dataMap, "monReportTemplet");
            in = new FileInputStream(file);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/msword");
            out = response.getOutputStream();
            //缓冲区
            byte[] bytes = new byte[2048];
            int len;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while ((len = in.read(bytes)) > 0) {
                out.write(bytes, 0, len);
            }
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("报告导出异常", ex);
        } finally {
            //释放资源
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("IO close error!", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("IO close error!", e);
                }
            }
            if (file != null) {
                // 删除临时文件
                file.delete();
            }
        }

    }

}