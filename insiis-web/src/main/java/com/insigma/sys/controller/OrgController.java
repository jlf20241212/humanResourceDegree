package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.util.TreeUtil;
import com.insigma.sys.entity.Aa26;
import com.insigma.sys.entity.SysOrg;
import com.insigma.sys.service.SysCodeService;
import com.insigma.sys.service.SysOrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 菜单管理
 */
@Slf4j
@RestController
@RequestMapping("/sys/org")
public class OrgController {
    @Autowired
    private SysOrgService sysOrgService;
    @Autowired
    private SysCodeService sysCodeService;
    /**
     * 初始化页面数据
     * @return
     */
    @PostMapping("/doInit")
    public ResponseMessage doInit(@RequestBody JSONObject jsonObject) {
        List<SysOrg> orgs=sysOrgService.queryAllOrg();
        JSONArray jsonArray = TreeUtil.listToTree(JSONArray.parseArray(JSONArray.toJSONString(orgs)), "orgid", "parentid", "children");
        return ResponseMessage.ok("查询成功", jsonArray);
    }
    /**
     * 根据id查找org对象
     * @return
     */
    @PostMapping("/findOrgById")
    public ResponseMessage findOrgById(@RequestBody JSONObject jsonObject) {
        ResponseMessage rm;
        String a=jsonObject.get("orgid").toString();
        SysOrg sysOrg;
        if (a!=null){
            int orgid= Integer.parseInt(a) ;
            sysOrg=sysOrgService.findByOrgid((long) orgid);
            rm= ResponseMessage.ok(sysOrg);
        }else {
            rm= ResponseMessage.error();
        }
        return rm;
    }
    /**
     * 根据id查找险种
     * @return
     */
//    @PostMapping("/findInsTypeById")
//    public ResponseMessage findInTypeById(@RequestBody JSONObject jsonObject) {
//        ResponseMessage rm;
//        String a=jsonObject.get("regioncode").toString();
//        List list=new ArrayList();
//        if (a!=null){
//            long regioncode=Long.valueOf(a);
//            List<SysOrgInsType> ins=sysOrgService.queryInsTypeById(regioncode);
//            for (SysOrgInsType s:ins){
//                list.add(s.getInstype());
//            }
//            rm=ResponseMessage.ok(list);
//        }else {
//            rm=ResponseMessage.error();
//        }
//        return rm;
//    }
    /**
     * 保存机构
     * @return
     */
    @PostMapping("/saveOrg")
    public ResponseMessage saveOrg(@RequestBody JSONObject jsonObject) {
        ResponseMessage rm=null;
        //List checkedCities= (List) jsonObject.get("checkedCities");
        Map<String,Object> map= (Map<String, Object>) jsonObject.get("form");
        SysOrg sysOrg=sysOrgService.getSysOrgBean(map);
        Boolean flag=sysOrgService.isMany(sysOrg);
        try {
        if (flag==false){
            //没有重复
            if (sysOrg!=null){
                sysOrgService.saveOrg(sysOrg);
                rm= ResponseMessage.ok("保存成功");
            }
        }else {
            //重复
            rm= ResponseMessage.error("重复");
        }
        }catch (Exception e){
            log.error(e.getMessage(), e);
            rm = ResponseMessage.error(e.getMessage());
        }
        return rm;
    }
    /**
     * 初始化页面aa26
     * @return
     */
    @PostMapping("/initAA26")
    public ResponseMessage initAA26() {
        ResponseMessage rm;
        try {
            List<Aa26> aa26s = sysOrgService.getAA26();
            JSONArray orgNodes = TreeUtil.listToTree(JSONArray.parseArray(JSONArray.toJSONString(aa26s)), "aab301", "aaa148", "children");
            rm= ResponseMessage.ok(orgNodes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            rm = ResponseMessage.error(e.getMessage());
        }
        return rm;
    }
    /**
     根据id查找aa26
     */
    @PostMapping("/queryAa26By301/{id}")
    public ResponseMessage queryAa26By301(@PathVariable("id") String aab301) {
        ResponseMessage rm;
        try {
            Aa26 aa26s = sysOrgService.queryAa26By301(aab301);
            rm= ResponseMessage.ok(aa26s);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            rm = ResponseMessage.error(e.getMessage());
        }
        return rm;
    }
    /**
     * 初始化页面数据
     * @return
     */
    @PostMapping("/initCodeTypes")
    public ResponseMessage initCodeTypes(@RequestBody JSONObject jsonObject) {
        ResponseMessage rm;
        try {
            JSONObject codeTypes = sysCodeService.getCodeTypes(jsonObject);
            rm = ResponseMessage.ok(codeTypes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            rm = ResponseMessage.error(e.getMessage());
        }
        return rm;
    }
    /**
     *
     * @return
     */
    @PostMapping("/queryUserOrg")
    public ResponseMessage queryUserOrg(@RequestBody JSONObject jsonObject) {
        ResponseMessage rm;
        String regioncode="";
        long node=Long.valueOf(jsonObject.get("node").toString());
        Map<String,Object> form= (Map<String, Object>) jsonObject.get("form");
       // JSONArray orgTreeData=jsonObject.getJSONArray("orgTreeData"); // 首先转成 JSONArray  对象
       // String trees=orgTreeData.toJSONString();
        List<SysOrg> list=sysOrgService.findNodes(node);
        boolean s =sysOrgService.findByArea(list);
       if (s==true){
           rm= ResponseMessage.ok("有联系");
       }else {
           rm= ResponseMessage.error("没联系");
       }
        return rm;
    }
    /**
     * 删除机构
     */
    @PostMapping("/deleteOrg")
    public ResponseMessage deleteMenu(@RequestBody JSONObject pageData){
        ResponseMessage rm;
        long node=Long.valueOf(pageData.get("node").toString());
        boolean withUser= (boolean) pageData.get("withUser");
        List<SysOrg> list=sysOrgService.findNodes(node);
        try {
            if (list.size()>0){
                //withUser为true则和用户有关
                sysOrgService.delOrg(list,withUser);
               rm= ResponseMessage.ok("删除成功");
            }else {
                rm= ResponseMessage.error("数据不存在");
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
            rm = ResponseMessage.error(e.getMessage());
        }

        return rm;
    }
}
