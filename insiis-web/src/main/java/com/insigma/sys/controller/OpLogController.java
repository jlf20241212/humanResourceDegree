package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.oplog.dto.UserLogDTO;
import com.insigma.framework.oplog.entity.SbdsUserLog;
import com.insigma.framework.oplog.service.OpLogService;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.entity.SysMenu;
import com.insigma.sys.service.MenuService;
import com.insigma.web.support.service.PageInitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by yinjh on 2019/1/29.
 */
@Slf4j
@RestController
@RequestMapping("/sys/oplog")
public class OpLogController {

    @Autowired
    private OpLogService opLogService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private PageInitService pageInitService;

    @Autowired
    private MenuService menuService;

    @PostMapping("/initSysFunctions")
    public ResponseMessage initSysFuntions(@RequestBody JSONObject jsonObject) {
        jsonObject = pageInitService.loadCustomCodeType(jsonObject, "sysfunction", "functionid", "title", "nodetype='2'", "SYSFUNCTIONS");
        return ResponseMessage.ok(jsonObject.getJSONObject("codeTypes"));
    }

    @PostMapping("/query")
    public ResponseMessage query(@RequestBody JSONObject jsonObject) throws SQLException {
        Integer page = jsonObject.getInteger("page");
        Integer size = jsonObject.getInteger("size");
        UserLogDTO queryParam = jsonObject.toJavaObject(UserLogDTO.class);
        Boolean more = jsonObject.getBoolean("more");
        if (more != null && !more) {
            if (queryParam.getAae036() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String aae036 = sdf.format(queryParam.getAae036());
                queryParam.setStartTime(aae036 + " 00:00:00");
                queryParam.setEndTime(aae036 + " 23:59:59");
            }
        }
        PageInfo<UserLogDTO> pageInfo = opLogService.queryUserLogList(queryParam, page, size);
        return ResponseMessage.ok(pageInfo);
    }

    @GetMapping("/location")
    public ResponseMessage getLocation(Long opseno) {
        SbdsUserLog log = opLogService.getOpLog(opseno);
        if (log != null) {
            Long functionId = log.getFunctionid();
            SysMenu sysMenu = menuService.findMenuById(functionId);
            return ResponseMessage.ok("", sysMenu.getLocation());
        }
        return ResponseMessage.ok();
    }

    @GetMapping("/orisource")
    public ResponseMessage getOriSource(Long opseno) {
        String oriSource = opLogService.getOriSource(opseno);
        return ResponseMessage.ok("", oriSource);
    }

    @GetMapping("/oridata")
    public ResponseMessage getOriData(Long opseno) {
        String oriData = opLogService.getOriData(opseno);
        return ResponseMessage.ok("", oriData);
    }

    @GetMapping("/rollback")
    public ResponseMessage rollback(Long opseno) {
        try {
            String logonName = currentUserService.getCurrentUser().getLogonName();
            opLogService.rollBack(opseno, logonName);
        } catch (Exception e) {
            log.error("回退失败！", e);
            return ResponseMessage.error("回退失败：" + e.getMessage());
        }
        return ResponseMessage.ok("回退成功！");
    }
}
