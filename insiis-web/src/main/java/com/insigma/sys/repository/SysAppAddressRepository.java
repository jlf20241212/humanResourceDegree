package com.insigma.sys.repository;

import com.insigma.sys.entity.SysAppAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * @author yinjh
 * @version 2022/3/28
 * @since 2.6.5
 */
@Repository
public interface SysAppAddressRepository extends JpaRepository<SysAppAddress, String> {

    /**
     * 根据门户地址和应用Id查询
     * @param portalUrl
     * @param appId
     * @return
     */
    SysAppAddress findSysAppAddressByPortalUrlAndAppId(String portalUrl, String appId);

    @Modifying
    @Query(value = "delete SYSAPPADDRESS where APP_ID=?1",nativeQuery = true)
    int deleteSysAppAddressByAppId(String appid);
    @Query(value = "select * from SYSAPPADDRESS where APP_ID=?1",nativeQuery = true)
    List<SysAppAddress> querySysAddressDTO(String appid);
}
