package com.insigma.sys.service;

import com.insigma.sys.entity.SysHoliday;

import java.util.List;

/**
 * @author yinjh
 * @version 2021/6/16
 */
public interface HolidayService {
    List<SysHoliday> queryHolidayList(int year);

    void updateHolidays(int year, List<SysHoliday> holidays);

    List<SysHoliday> getWeekdays(int year);
}
