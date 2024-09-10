package com.insigma.sys.service;

import com.insigma.framework.db.PageInfo;
import com.insigma.sys.dto.SysDownloadCenterDTO;

import java.sql.SQLException;

/**
 * @author GH
 * @ClassName: SysDownloadCenterService
 * @Description:
 * @version 2021/8/4  10:15
 */
public interface SysDownloadCenterService {

    PageInfo<SysDownloadCenterDTO> queryDownloadCenterList(SysDownloadCenterDTO queryDTO, Integer page, Integer size)throws SQLException;

    void saveDownloadCenter(SysDownloadCenterDTO sysDownloadCenterDTO);

    void delete(String id);

    void saveDownloadLog(String id, String logonName);
}
