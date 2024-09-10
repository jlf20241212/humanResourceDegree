package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONArray;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.util.TreeUtil;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.MenuDTO;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by yinjh on 2019/3/15.
 */
@RestController
@RequestMapping("/sys/usermenu")
public class UserMenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private CurrentUserService currentUserService;

    @GetMapping("/getMenuList")
    public ResponseMessage getMenuList() {
        SysUser sysUser = currentUserService.getCurrentUser();
        List<MenuDTO> menuDTOS = menuService.getMenuList(sysUser);
        JSONArray jsonArray = TreeUtil.listToTree(JSONArray.parseArray(JSONArray.toJSONString(menuDTOS)), "functionid", "parentid", "children", "0");
        return ResponseMessage.ok("查询成功", jsonArray);
    }

    @GetMapping("/getButtonAuthList")
    public ResponseMessage getButtonList(String location) {
        if (ObjectUtils.isEmpty(location)) {
            return ResponseMessage.error("location不能为空！");
        }
        SysUser sysUser = currentUserService.getCurrentUser();
        List<String> buttonAuthList = menuService.getButtonAuthList(sysUser, location);
        return ResponseMessage.ok(buttonAuthList);
    }

}
