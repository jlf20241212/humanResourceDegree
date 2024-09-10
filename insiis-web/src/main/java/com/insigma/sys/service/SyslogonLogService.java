package com.insigma.sys.service;

import com.insigma.framework.db.PageInfo;
import com.insigma.framework.web.securities.entity.SysLogonLog;
import com.insigma.sys.dto.SysLogonLogDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * fukq 2020/6/2
 */
public interface SyslogonLogService {
    /**
     * 分页查询登录日志
     * @param queryDTO
     * @param page
     * @param size
     * @return
     * @throws SQLException
     */
    PageInfo<SysLogonLogDTO> querySysLogonList(SysLogonLogDTO queryDTO, Integer page, Integer size) throws SQLException;
    /**
     * 查询上次登录时间
     * @param userId 用户id
     * @return 登录日志集合
     */
    List<SysLogonLog> getLastLoginTime(String userId);
}
