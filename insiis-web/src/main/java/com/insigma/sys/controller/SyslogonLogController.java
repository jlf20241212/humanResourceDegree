package com.insigma.sys.controller;

import com.insigma.framework.ResponseMessage;
import com.insigma.framework.db.PageInfo;
import com.insigma.sys.dto.SysLogonLogDTO;
import com.insigma.sys.service.SyslogonLogService;
import com.insigma.web.support.annotation.OdinRequest;
import com.insigma.web.support.annotation.OdinRequestParam;
import com.insigma.web.support.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * fukq 2020/6/2
 */
@RestController
@RequestMapping("/sys/syslogonlog")
public class SyslogonLogController extends BaseController {


    @Autowired
    SyslogonLogService syslogonLogService;

    @OdinRequest(init = true)
    @PostMapping("/doInit")
    public ResponseMessage doInit(SysLogonLogDTO f_form, Integer page, Integer size) {
        tableDataQuery(f_form, page, size);
        return this.ok();
    }

    @OdinRequest
    @PostMapping("/doGridQuery/{name}")
    public ResponseMessage doGridQuery(@OdinRequestParam("f_form") SysLogonLogDTO queryDTO,
                                       @OdinRequestParam("t_tableData_page") Integer page,
                                       @OdinRequestParam("t_tableData_size") Integer size,
                                       @PathVariable String name) {
        tableDataQuery(queryDTO, page, size);
        this.set("page", page);
        return this.ok();
    }

    @OdinRequest
    @PostMapping("/query")
    public ResponseMessage query(@OdinRequestParam("f_form") SysLogonLogDTO queryDTO,
                                 @OdinRequestParam("t_tableData") PageInfo<SysLogonLogDTO> pageInfo,
                                 Integer size) {
        tableDataQuery(queryDTO, 1, size);
        this.set("page", 1);
        return this.ok();
    }


    public void tableDataQuery(SysLogonLogDTO queryDTO, Integer page, Integer size) {
            this.pageQuery("t_tableData", () -> syslogonLogService.querySysLogonList(queryDTO, page, size));

    }
}
