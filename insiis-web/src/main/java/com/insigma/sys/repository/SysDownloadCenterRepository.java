package com.insigma.sys.repository;

import com.insigma.sys.entity.SysDownloadCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author GH
 * @ClassName: SysDownloadCenterRepository
 * @Description:
 * @version 2021/8/4  10:21
 */
@Repository
public interface SysDownloadCenterRepository extends JpaRepository<SysDownloadCenter, String>, JpaSpecificationExecutor<SysDownloadCenter> {
}
