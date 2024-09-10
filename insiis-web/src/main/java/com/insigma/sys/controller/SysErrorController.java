package com.insigma.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.db.JdbcPageHelper;
import com.insigma.framework.db.PageInfo;
import com.insigma.sys.entity.SysError;
import com.insigma.sys.service.SysErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 异常信息维护
 */
@RestController
@RequestMapping("/sys/error")
public class SysErrorController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SysErrorService sysErrorService;
    @PostMapping("/doInit")
    public ResponseMessage doInit(@RequestBody JSONObject pageData) throws SQLException {

        Integer size = pageData.getInteger("size");
        HashMap<String, Object> tableData = tableDataQuery(pageData, 1, size);
        pageData.put("t_tableData", tableData);

       /* List<SysError> errors=sysErrorService.queryAllErrors();
       // JSONArray array1=new JSONArray();
        JSONArray array=JSONArray.parseArray(JSONArray.toJSONString(errors));
        JSONObject tableData = new JSONObject();

        tableData.put("Data",array);
        tableData.put("total",errors.size());*/
        pageData.put("t_tableData", tableData);
        return ResponseMessage.ok(pageData);
    }
    @PostMapping("/doQuery/goIt")
    public ResponseMessage queryBySome(@RequestBody JSONObject pageData){
        Map<String, HashMap<String,String>> map= (Map<String, HashMap<String, String>>) pageData.get("f_form1_n");
        String serviceName=map.get("serviceName").get("value");
        String functionName=map.get("functionName").get("value");
        String errorCode= map.get("errorCode").get("value");
        List<SysError> errors=sysErrorService.findAllBySome(serviceName,functionName,errorCode);
        JSONArray array=JSONArray.parseArray(JSONArray.toJSONString(errors));
        JSONObject tableData = new JSONObject();
        tableData.put("Data",array);
        tableData.put("total",errors.size());
        pageData.put("t_tableData", tableData);
        return  ResponseMessage.ok(pageData);
    }
    @PostMapping("/doGridQuery/{id}")
    public ResponseMessage doGridQuery(@RequestBody JSONObject jsonObject, @PathVariable String id) throws SQLException {
        Integer page = jsonObject.getInteger("t_tableData_page");
        Integer size = jsonObject.getInteger("t_tableData_size");
        HashMap<String, Object> tableData = tableDataQuery(jsonObject, page, size);
        jsonObject.put("t_tableData", tableData);
        return ResponseMessage.ok(jsonObject);
    }
    @PostMapping("/delete")
    public ResponseMessage deleteError(@RequestBody JSONObject pageData){
        Map<String,String> map= (Map<String,String>) pageData.get("data");
        try {
            SysError sysError=sysErrorService.getSysErrorBean2(map);
            sysErrorService.deleteError(sysError);
            List<SysError> errors=sysErrorService.queryAllErrors();
            JSONArray array=JSONArray.parseArray(JSONArray.toJSONString(errors));
            JSONObject tableData = new JSONObject();
            tableData.put("Data",array);
            tableData.put("total",errors.size());
            return ResponseMessage.ok("删除成功",tableData);
        }catch (Exception e){
            return  ResponseMessage.error("删除失败");
        }
    }
    @PostMapping("/doSave/addError")
    public ResponseMessage addError(@RequestBody JSONObject pageData){
        try {
            Map<String,HashMap<String,String>> map= (Map<String,HashMap<String,String>>) pageData.get("f_errorForm_n");
            SysError sysError=sysErrorService.getSysErrorBean1(map);
            sysErrorService.save(sysError);
            List<SysError> errors=sysErrorService.queryAllErrors();
            JSONArray array=JSONArray.parseArray(JSONArray.toJSONString(errors));
            JSONObject tableData = new JSONObject();
            tableData.put("Data",array);
            tableData.put("total",errors.size());
            //将table装载到pageData
            pageData.put("t_tableData", tableData);
            pageData.put("dialogFormVisible",false);
            return ResponseMessage.ok("增加成功",pageData);
        }catch (Exception e){
            pageData.put("dialogFormVisible",false);
            return  ResponseMessage.error("异常编码重复");
        }
    }
    @PostMapping("/doCheck/{checkname}")
    public ResponseMessage updateError(@RequestBody JSONObject pageData){
        try {
            int index=Integer.parseInt(pageData.get("index").toString());
            HashMap<String,Object> maps= (HashMap<String,Object>) pageData.get("t_tableData_n");
           // String datas=maps.get("Data").toString();
            String datas=JSONObject.toJSONString(maps.get("Data"));
            JSONArray jsonArray = JSON.parseArray(datas);
            SysError sysError=JSON.toJavaObject(jsonArray.getJSONObject(index),SysError.class);
            sysErrorService.save(sysError);
            List<SysError> errors=sysErrorService.queryAllErrors();
            JSONArray array=JSONArray.parseArray(JSONArray.toJSONString(errors));
            JSONObject tableData = new JSONObject();
            tableData.put("Data",array);
            tableData.put("total",errors.size());
            //将table装载到pageData
            pageData.put("t_tableData_n", tableData);
            return ResponseMessage.ok("修改成功",pageData);
        }catch (Exception e){
            return  ResponseMessage.error(e.getMessage());
        }
    }

    @PostMapping("/queryIsMany")
    public ResponseMessage queryIsMany(@RequestBody JSONObject pageData){
        Map<String,HashMap<String,String>> map= (Map<String,HashMap<String,String>>) pageData.get("f_errorForm_n");
        String errCode=map.get("errorCode1").get("value");
        boolean flag=sysErrorService.queryByErrCode(errCode);
        if (flag==true){
            map.get("errorCode1").put("value","");
            pageData.put("f_errorForm",map);
            return ResponseMessage.error("异常编码重复");
        }
        return ResponseMessage.ok(pageData);
    }
    public HashMap<String, Object> tableDataQuery(JSONObject jsonObject, Integer page, Integer size) throws SQLException {
        StringBuffer querySQL = new StringBuffer("select * from syserror where 1=1 ");

        JdbcPageHelper helper = new JdbcPageHelper(jdbcTemplate, page, size);
        PageInfo<Map<String, Object>> data = helper.queryPagination(querySQL.toString(), new ArrayList<>(), rs -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", rs.getString("id"));
            map.put("serviceName", rs.getString("servicename"));
            map.put("functionName", rs.getString("functionname"));
            map.put("errorCode", rs.getString("errorcode"));
            map.put("message", rs.getString("message"));
            map.put("createTime", rs.getDate("createtime"));
            return map;
        });

        HashMap<String, Object> gridData = new HashMap<>();
        gridData.put("total", data.getTotal());
        gridData.put("Data", data.getData());
        return gridData;
    }
}
