package com.insigma.sys.service;

import com.insigma.framework.db.PageInfo;
import com.insigma.sys.dto.SysAppAddressDTO;
import com.insigma.sys.dto.SysAppDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * @author GH
 * @version 2022/3/28
 * @since 2.6.5
 */
public interface SysAppService {
    PageInfo<SysAppDTO> pageQuery(SysAppDTO queryDTO, Integer page, Integer size) throws SQLException;

    String saveSysAppDTO(SysAppDTO queryDTO,String secretKey);

    void saveSysAppAddressDTO(List<SysAppAddressDTO> listsysAppAddressDTO, String appid);

    SysAppDTO querySysApp(String appid);

    List<SysAppAddressDTO> querySysAddressDTO(String appid);

    void deleteSysApp(String appid);

    void deleteSysAppAddressByAppId(String appid);

    void deleteSysAppAddress(String addressId);

    void toActiveSysApp(String appid);

    void toNotActiveSysApp(String appid);

    List<SysAppDTO> querySysAppDTO();

    boolean selectAppCode(String appCode);
}
