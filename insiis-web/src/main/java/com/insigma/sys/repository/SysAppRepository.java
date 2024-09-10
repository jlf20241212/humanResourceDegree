package com.insigma.sys.repository;

import com.insigma.sys.entity.SysApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author yinjh
 * @version 2022/3/28
 * @since 2.6.5
 */
@Repository
public interface SysAppRepository extends JpaRepository<SysApp, String> {

    @Query(value="select max(ORDER_NO) from SYSAPP",nativeQuery =true)
    Long selectMaxOrder();
    @Query(value="select * from SYSAPP where APP_CODE = ?1",nativeQuery =true)
    SysApp selectAppCode(String appCode);
}
