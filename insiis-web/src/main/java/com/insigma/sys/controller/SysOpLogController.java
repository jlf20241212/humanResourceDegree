package com.insigma.sys.controller;

import com.insigma.framework.ResponseMessage;
import com.insigma.framework.db.PageInfo;
import com.insigma.sys.dto.SysOperateLogDTO;
import com.insigma.sys.service.SysOperateLogService;
import com.insigma.web.support.annotation.OdinRequest;
import com.insigma.web.support.annotation.OdinRequestParam;
import com.insigma.web.support.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * fukq 2020/6/1
 */
@RestController
@RequestMapping("/sys/sysoplog")
public class SysOpLogController extends BaseController {

    @Autowired
    SysOperateLogService sysOperateLogService;

    @OdinRequest(init = true)
    @PostMapping("/doInit")
    public ResponseMessage doInit(SysOperateLogDTO f_form, Integer page, Integer size) {
        tableDataQuery(f_form, page, size);
        return this.ok();
    }

    @OdinRequest
    @PostMapping("/doGridQuery/{name}")
    public ResponseMessage doGridQuery(@OdinRequestParam("f_form") SysOperateLogDTO queryDTO,
                                       @OdinRequestParam("t_tableData_page") Integer page,
                                       @OdinRequestParam("t_tableData_size") Integer size,
                                       @PathVariable String name) {
        tableDataQuery(queryDTO, page, size);
        this.set("page", page);
        return this.ok();
    }

    @OdinRequest
    @PostMapping("/query")
    public ResponseMessage query(@OdinRequestParam("f_form") SysOperateLogDTO queryDTO,
                                 @OdinRequestParam("t_tableData") PageInfo<SysOperateLogDTO> pageInfo,
                                 Integer size) {
        tableDataQuery(queryDTO, 1, size);
        this.set("page", 1);
        return this.ok();
    }


    public void tableDataQuery(SysOperateLogDTO queryDTO, Integer page, Integer size) {
            this.pageQuery("t_tableData", () -> sysOperateLogService.querySysOperateLogList(queryDTO, page, size));

    }

}
