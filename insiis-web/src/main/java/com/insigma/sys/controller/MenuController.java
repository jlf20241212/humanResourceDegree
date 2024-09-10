package com.insigma.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.util.TreeUtil;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.MenuDTO;
import com.insigma.sys.dto.SysAppDTO;
import com.insigma.sys.entity.SysMenu;
import com.insigma.sys.service.MenuService;
import com.insigma.sys.service.SysAppService;
import com.insigma.sys.service.SysCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 菜单管理
 */
@Slf4j
@RestController
@RequestMapping("/sys/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private SysCodeService sysCodeService;
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private SysAppService sysAppService;
    /**
     * 初始化页面数据
     * @return
     */
    @PostMapping("/initCodeTypes")
    public ResponseMessage initCodeTypes(@RequestBody JSONObject jsonObject) {
        try {
            JSONObject codeTypes = sysCodeService.getCodeTypes(jsonObject);
            List<SysAppDTO> list = sysAppService.querySysAppDTO();
            JSONArray jsonArray = new JSONArray();
            for (SysAppDTO sysAppDTO : list) {
                JSONObject j = new JSONObject();
                j.put("value",sysAppDTO.getAppName());
                j.put("key",sysAppDTO.getAppId());
                jsonArray.add(j);
            }
            codeTypes.put("APPID",jsonArray);
            return ResponseMessage.ok(codeTypes);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
    @GetMapping("/queryTree")
    public ResponseMessage queryTable(){
        List<MenuDTO> menuDTOS = menuService.queryAllMenu();
        JSONArray jsonArray = TreeUtil.listToTree(JSONArray.parseArray(JSONArray.toJSONString(menuDTOS)), "functionid", "parentid", "children");
        return ResponseMessage.ok("查询成功", jsonArray);
    }

    /**
     * 拖拽成功完成时触发
     * @param jsonObject
     * @return
     */
    @PostMapping("/nodeDrop")
    public ResponseMessage nodeDrop(@RequestBody JSONObject jsonObject){
        System.out.println(jsonObject);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject oneMenu = jsonArray.getJSONObject(i);
            SysMenu sysMenu = new SysMenu();
            sysMenu.setFunctionid(oneMenu.getLong("functionid"));
            sysMenu.setFunorder(oneMenu.getInteger("funorder"));
            sysMenu.setParentid(oneMenu.getLong("parentid"));
            sysMenu.setTitle(oneMenu.getString("title"));
            menuService.update(sysMenu);
            System.out.println(sysMenu);
        }
        return ResponseMessage.ok();
    }
    /**
     * 点击左侧菜单后
     */
    @PostMapping("/nodeClick")
    public ResponseMessage nodeClick(@RequestBody() JSONObject jsonObject) {
        SysMenu sysMenu;
        String a=jsonObject.get("functionid").toString();
        if (a!=null){
            long functionid =(long)Integer.parseInt(a);
            sysMenu =menuService.findMenuById(functionid);
            List<SysMenu> menulist=menuService.findTreesByPId(sysMenu.getFunctionid());
            if (menulist.size()>0){
                //有子类
                return ResponseMessage.ok(sysMenu);
            }else {
                //没有子类
                return ResponseMessage.ok("重复",sysMenu);
            }
        }else {
            return ResponseMessage.error();
        }
    }

    /**
     * 根据id查找对象
     * @param jsonObject
     * @return
     */
    @PostMapping("/queryById")
    public ResponseMessage queryById(@RequestBody  JSONObject jsonObject) {
        try {
            String id=jsonObject.get("functionid").toString();
            String funtype=jsonObject.get("funtype").toString();
            List<MenuDTO> menuDTOS=menuService.queryMenuByFuntypeAndActive(funtype);
            JSONArray jsonArray = TreeUtil.listToTree(JSONArray.parseArray(JSONArray.toJSONString(menuDTOS)), "functionid", "parentid", "children");
            jsonObject.put("treedata",jsonArray);
            long fId=Integer.parseInt(id);
            SysMenu menu=menuService.findMenuById(fId);
            List list=new ArrayList();
            if(menu!=null){
            String [] ms=menu.getIdpath().split("/");
            if (ms.length>0){
                for (String s:ms){
                    if(s.equals("0")){
                        continue;
                    }
                    list.add(Long.valueOf(s));
                }
            }}
            jsonObject.put("pname",list);
            return ResponseMessage.ok(jsonObject);
        }catch (Exception e){
            return ResponseMessage.error(e.getMessage());
        }
    }
    /**
     * 查询是否多于三层
     * @param jsonObject
     * @return
     */
    @PostMapping("/querySumIsThree")
    public ResponseMessage querySumIsThree(@RequestBody  JSONObject jsonObject) {
        try {
            String id=jsonObject.get("functionid").toString();
            Map<String,List> list= (Map<String, List>) jsonObject.get("pname");
            long fId=Integer.parseInt(id);
            SysMenu sysMenu=menuService.findMenuById(fId);
            List<SysMenu> menu=menuService.findNodes(sysMenu.getIdpath());//查询当前子节点
            for(int i=0;i<list.get("value").size();i++){
                for (SysMenu menu1:menu){
                    if(String.valueOf(menu1.getFunctionid()).equals(list.get("value").get(i).toString())){
                        return ResponseMessage.error("上级菜单无法选择本身及下级菜单");
                    }
                }
            }
            return ResponseMessage.ok();
        }catch (Exception e){
            return ResponseMessage.error(e.getMessage());
        }
    }
    @PostMapping("/saveMenu")
    public ResponseMessage saveMenu(@RequestBody JSONObject pageData){
        Map<String,Object> map= (Map<String, Object>) pageData.get("form");
        SysMenu sysMenu = menuService.getSysMenuBean(map);
        boolean flag = menuService.isManyLocations(sysMenu);
        if (flag) {
            return ResponseMessage.error("路径重复");
        }
        try {
            menuService.save(sysMenu);
            return ResponseMessage.ok("保存成功", sysMenu);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseMessage.error("保存失败，失败原因：" +e.getMessage());
        }
    }
    @PostMapping("/deleteMenu")
    public ResponseMessage deleteMenu(@RequestBody JSONObject pageData){
        if (!"1".equals(currentUserService.getCurrentUser().getUserType())) {
            return ResponseMessage.error("无权操作！");
        }
        Long node=Long.valueOf(pageData.get("node").toString());
        boolean withRole= (boolean) pageData.get("withRole");
        List<SysMenu> m=menuService.findNodes(menuService.findMenuById(node).getIdpath());
        //当withRole为false时，和角色没有关系，如果是1就和角色有关系
        try {
            if (m!=null){
                menuService.delete(m,withRole);
                return ResponseMessage.ok("删除成功");
            }else{
                return ResponseMessage.error("删除失败");
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return ResponseMessage.error("删除失败，失败原因：" +e.getMessage());
        }
    }
    /**
     * 查找是否有角色关系
     */
    @PostMapping("/queryRoleBy")
    public ResponseMessage queryRoleBy(@RequestBody JSONObject pageData){
        Map<String,Object> map= (Map<String, Object>) pageData.get("form");
        SysMenu sysMenu=menuService.getSysMenuBean(map);
        boolean f=menuService.findFunctionRoles(sysMenu);
        if (f==true){
            return ResponseMessage.error("有权限");
        }else if (f==false){
            return ResponseMessage.ok("没有权限");
        }else {
            return ResponseMessage.error();
        }
    }
    //判断是否三级
    @PostMapping("queryMenuByPid")
    public ResponseMessage queryMenuByPid(@RequestBody JSONObject pageData) {
        ResponseMessage rm=new ResponseMessage();
        String a=pageData.get("parentid").toString();
        if (a!=null){
            int parentid =Integer.parseInt(a);
            SysMenu menu=menuService.queryByFuncID(parentid);
            if (menu!=null){
                SysMenu menu1=menuService.queryByFuncID(menu.getParentid());
                if (menu1!=null){
                    rm= ResponseMessage.error("最多添加三级");
                }
            }
        }else {
            rm= ResponseMessage.error("没取到父节点");
        }
        return rm;
    }



}
