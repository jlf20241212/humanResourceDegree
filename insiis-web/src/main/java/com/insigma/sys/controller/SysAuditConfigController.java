package com.insigma.sys.controller;

import com.insigma.framework.ResponseMessage;
import com.insigma.framework.audit.dto.AuditConfig;
import com.insigma.framework.audit.service.SysAuditConfigService;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

/**
 * @author yinjh
 * @version 2022/11/15
 * @since 2.7.0
 */
@RestController
@RequestMapping("/sys/audit/config")
public class SysAuditConfigController {

    @Autowired
    private SysAuditConfigService sysAuditConfigService;

    @GetMapping("/query")
    public ResponseMessage query(String functionName, Integer page, Integer size) throws SQLException {
        PageInfo<AuditConfig> pageInfo = sysAuditConfigService.query(functionName, page, size);
        return ResponseMessage.ok(pageInfo);
    }

    @PostMapping("/save")
    public ResponseMessage save(@RequestBody AuditConfig auditConfig) {
        if (auditConfig.getFunctionId() == null) {
            return ResponseMessage.error("请选择功能模块");
        }
        if (auditConfig.getAuditTotalLevel() == null) {
            return ResponseMessage.error("请输入审核总级别");
        }
        if (ObjectUtils.isEmpty(auditConfig.getServiceName())) {
            return ResponseMessage.error("请输入服务名称");
        }
        sysAuditConfigService.save(auditConfig);
        return ResponseMessage.ok("保存成功");
    }

    @PostMapping("/delete")
    public ResponseMessage delete(@RequestBody AuditConfig auditConfig) throws AppException {
        if (ObjectUtils.isEmpty(auditConfig.getId())) {
            return ResponseMessage.error("请选择需要删除的配置");
        }
        sysAuditConfigService.delete(auditConfig.getId());
        return ResponseMessage.ok("删除成功");
    }
}
