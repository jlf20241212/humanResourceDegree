package com.insigma.sys.repository;

import com.insigma.sys.entity.SysHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yinjh
 * @version 2021/6/16
 */
@Repository
public interface SysHolidayRepository extends JpaRepository<SysHoliday, String> {

    List<SysHoliday> findSysHolidaysByYear(int year);

    void deleteByYear(int year);

}
