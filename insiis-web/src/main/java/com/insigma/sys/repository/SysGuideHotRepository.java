package com.insigma.sys.repository;

import com.insigma.sys.entity.SysGuideHot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SysGuideHotRepository extends JpaSpecificationExecutor<SysGuideHot>, JpaRepository<SysGuideHot, String> {
}
