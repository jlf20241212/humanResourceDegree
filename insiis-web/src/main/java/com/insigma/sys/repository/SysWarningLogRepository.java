package com.insigma.sys.repository;

import com.insigma.sys.entity.SysWarningLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author yinjh
 * @version 2021/10/9
 */
@Repository
public interface SysWarningLogRepository extends JpaRepository<SysWarningLog, String> {
}
