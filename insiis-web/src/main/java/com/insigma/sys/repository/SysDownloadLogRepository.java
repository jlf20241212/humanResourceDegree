package com.insigma.sys.repository;

import com.insigma.sys.entity.SysDownloadLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author yinjh
 * @version 2021/12/9
 */
@Repository
public interface SysDownloadLogRepository extends JpaRepository<SysDownloadLog, String> {
}
