package com.insigma.sys.controller;

import com.insigma.framework.ResponseMessage;
import com.insigma.framework.audit.dto.AuditHistory;
import com.insigma.framework.audit.dto.AuditInfo;
import com.insigma.framework.audit.dto.AuditResult;
import com.insigma.framework.audit.service.SysAuditService;
import com.insigma.framework.db.PageInfo;
import com.insigma.sys.common.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yinjh
 * @version 2022/11/14
 * @since 2.7.0
 */
@RestController
@RequestMapping("/sys/audit")
public class SysAuditController {

    @Autowired
    private SysAuditService sysAuditService;

    @Autowired
    private CurrentUserService currentUserService;

    /**
     * 待审核列表
     * @param functionId 模块ID
     * @param level 审核级别
     * @param page 分页
     * @param size 大小
     * @return 审核列表
     */
    @GetMapping("/{functionId}/{level}/list")
    public ResponseMessage list(@PathVariable Long functionId, @PathVariable Integer level, Integer page, Integer size) throws SQLException {
        PageInfo<AuditInfo> pageInfo = sysAuditService.query(functionId, level, page, size);
        return ResponseMessage.ok(pageInfo);
    }

    /**
     * 审核
     *
     * @param functionId 模块ID
     * @param level      审核级别
     * @param auditResult 审核信息
     * @return 审核结果
     */
    @PostMapping("/{functionId}/{level}/post")
    public ResponseMessage audit(@PathVariable Long functionId, @PathVariable Integer level, @RequestBody AuditResult auditResult) {
        if (CollectionUtils.isEmpty(auditResult.getOpsenos())) {
            return ResponseMessage.error("请选择需要审核的业务！");
        }
        if (ObjectUtils.isEmpty(auditResult.getResult())) {
            return ResponseMessage.error("审核结果不能为空！");
        }
        if ("0".equals(auditResult.getResult()) && ObjectUtils.isEmpty(auditResult.getReason())) {
            return ResponseMessage.error("审核不通过时，审核原因不能为空！");
        }
        sysAuditService.audit(currentUserService.getCurrentUser().getLogonName(), functionId, level, auditResult);
        return ResponseMessage.ok("审核成功！");
    }

    @GetMapping("/{functionId}/{level}/history")
    public ResponseMessage history(@PathVariable Long functionId, @PathVariable Integer level, Long opseno) {
        List<AuditHistory> histories =  sysAuditService.queryHistories(opseno);
        return ResponseMessage.ok(histories);
    }

}
