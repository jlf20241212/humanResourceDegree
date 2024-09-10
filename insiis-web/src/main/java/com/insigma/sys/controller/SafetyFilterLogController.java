package com.insigma.sys.controller;

import com.insigma.framework.ResponseMessage;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.system.safety.log.dto.SafetyFilterLogDTO;
import com.insigma.framework.system.safety.log.service.SafetyFilterLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yinjh
 * @version 2022/9/15
 * @since 2.7.0
 */
@RestController
@RequestMapping("/safety/filter/log")
public class SafetyFilterLogController {

    @Autowired
    private SafetyFilterLogService safetyFilterLogService;

    @PostMapping("/query")
    public ResponseMessage query(@RequestBody SafetyFilterLogDTO safetyFilterLogDTO) {
        PageInfo<SafetyFilterLogDTO> pageInfo = safetyFilterLogService.query(safetyFilterLogDTO);
        return ResponseMessage.ok(pageInfo);
    }

}
