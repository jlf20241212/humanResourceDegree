package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.dfs.FSObject;
import com.insigma.framework.dfs.FSService;
import com.insigma.framework.util.TreeUtil;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.MenuDTO;
import com.insigma.sys.dto.SysFunctionDocDTO;
import com.insigma.sys.entity.SysMenu;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.MenuService;
import com.insigma.sys.service.SysFunctionDocService;
import com.insigma.web.support.annotation.OdinRequest;
import com.insigma.web.support.controller.BaseController;
import com.insigma.web.support.repository.MdParamRepository;
import com.insigma.web.support.entity.MdParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/sys/sysFunctionDoc")
public class SysFunctionDocController extends BaseController {

    @Autowired
    private SysFunctionDocService  sysFunctionDocService;

    @Autowired
    private MdParamRepository mdParamRepository;

    @Autowired(required = false)
    private FSService fsService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private MenuService menuService;

    @GetMapping("/queryTree")
    public ResponseMessage queryTable(){
        List<MenuDTO> menuDTOS = menuService.queryAllMenu();
        JSONArray jsonArray = TreeUtil.listToTree(JSONArray.parseArray(JSONArray.toJSONString(menuDTOS)), "functionid", "parentid", "children");
        return ResponseMessage.ok("查询成功", jsonArray);
    }

    @PostMapping("/nodeClick")
    public ResponseMessage nodeClick(@RequestBody JSONObject jsonObject) {
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

    @PostMapping("/doQuery")
    public ResponseMessage doQuery(@RequestBody JSONObject jsonObject) {
        String tmp = jsonObject.getString("functionid");
        Long functionid;
        if (ObjectUtils.isEmpty(tmp)) {
            String location = jsonObject.getString("location");
            if (ObjectUtils.isEmpty(location)) {
                return ResponseMessage.error("传入菜单ID为空");
            }
            MdParam mdParam = mdParamRepository.findByLocation(location);
            if (mdParam == null) {
                return ResponseMessage.error("模块不存在");
            }
            functionid = mdParam.getFunctionid();
        } else {
            functionid = Long.parseLong(tmp);
        }
        SysFunctionDocDTO aysFunctionDocDTO = sysFunctionDocService.queryByFunctionID(functionid);
        if (aysFunctionDocDTO == null) {
            return ResponseMessage.error("未配置该模块的功能介绍");
        }
        return ResponseMessage.ok(aysFunctionDocDTO);
    }

    @PostMapping("/doSave")
    public ResponseMessage doSave(@RequestBody SysFunctionDocDTO sysFunctionDocDTO ) {
        try {
            sysFunctionDocService.saveDoc(sysFunctionDocDTO);
            return ResponseMessage.ok();
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }


    @OdinRequest
    @PostMapping("/deleteMenu")
    public ResponseMessage delete(@RequestBody JSONObject jsonObject) {
        try {
            String tmp = jsonObject.get("functionid").toString();
            Long  functionid = Long.parseLong(tmp);
            if(functionid==0){
                return ResponseMessage.error("传入菜单ID为空");
            }
            sysFunctionDocService.deleteByFunctionid(functionid);
            return ResponseMessage.ok();
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @PostMapping("/images")
    public ResponseMessage upload(MultipartFile file, String messageId) {
        try {
            if (fsService == null) {
                return ResponseMessage.error("未启用FS");
            }
            if (ObjectUtils.isEmpty(file.getContentType())) {
                return ResponseMessage.error("content-type不能为空");
            }
            if (!file.getContentType().toLowerCase().contains("image")) {
                return ResponseMessage.error("图片格式不正确");
            }
            FSObject fsObject = new FSObject();
            fsObject.setName(file.getOriginalFilename());
            fsObject.setUploadDate(new Date());
            fsObject.setInputStream(file.getInputStream());
            fsObject.setContentType(file.getContentType());
            fsObject.setSize(file.getSize());

            Map<String, String> result = new HashMap<>();
            result.put("location", "/sys/sysFunctionDoc/static/images/" + fsService.pubObject(fsObject) + file.getContentType().substring(file.getContentType().lastIndexOf("/")).toLowerCase().replace("/", ".") + "?messageId=" + messageId);
            return ResponseMessage.ok(result);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
    @GetMapping("/static/images/{prefix}.{suffix}")
    public ResponseEntity view(@PathVariable String prefix, @PathVariable String suffix, String messageId) {
        try {
            SysUser sysUser = currentUserService.getCurrentUser();
            SysFunctionDocDTO sysFunctionDocDTO = sysFunctionDocService.queryByFunctionID(Long.parseLong(messageId));
            if (sysFunctionDocDTO != null ) {
                if (sysFunctionDocDTO == null) {
                    return ResponseEntity.status(403).build();
                }
            }
            if (fsService == null) {
                return ResponseEntity.notFound().build();
            }
            FSObject fsObject;
            try {
                fsObject = fsService.getObject(prefix);
            } catch (Exception e) {
                fsObject = null;
            }
            if (fsObject == null || !fsObject.getContentType().toLowerCase().contains(suffix.toLowerCase())) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .header("Pragma", "no-cache")
                    .header("Cache-Control", "no-cache")
                    .header("Expires", "0")
                    .header("Content-Type", fsObject.getContentType())
                    .header("Content-Length", String.valueOf(fsObject.getSize()))
                    .body(new InputStreamResource(fsObject.getInputStream()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
