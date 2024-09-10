package com.insigma.sys.service;

import com.insigma.framework.db.PageInfo;
import com.insigma.framework.web.securities.entity.SysOperateLog;
import com.insigma.sys.dto.SysOperateLogDTO;

import java.sql.SQLException;

/**
 *fukq 2020/6/1
 */
public interface SysOperateLogService {
    /**
     * 分页查询操作日志
     * @param queryDTO
     * @param page
     * @param size
     * @return
     * @throws SQLException
     */
    PageInfo<SysOperateLogDTO> querySysOperateLogList(SysOperateLogDTO queryDTO, Integer page, Integer size) throws SQLException;
}
