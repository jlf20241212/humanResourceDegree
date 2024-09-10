package com.insigma.sys.service.impl;

import com.insigma.sys.entity.SysHoliday;
import com.insigma.sys.repository.SysHolidayRepository;
import com.insigma.sys.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author yinjh
 * @version 2021/6/16
 */
@Service
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    private SysHolidayRepository sysHolidayRepository;

    @Override
    public List<SysHoliday> queryHolidayList(int year) {
        return sysHolidayRepository.findSysHolidaysByYear(year);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHolidays(int year, List<SysHoliday> holidays) {
        sysHolidayRepository.deleteByYear(year);
        sysHolidayRepository.flush();
        sysHolidayRepository.saveAll(holidays);
    }

    @Override
    public List<SysHoliday> getWeekdays(int year) {
        List<SysHoliday> sysHolidays = new ArrayList<>();
        //如果当年没有设置节假日，则默认所有周六周日为节假日
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        //第一天
        Date startDate = calendar.getTime();
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        //最后一天
        Date endDate = calendar.getTime();
        // 相隔天数  364
        long step = (endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
        //周末集合
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < step + 1; i++) {
            //判断是否为周末
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                SysHoliday sysHoliday = new SysHoliday();
                sysHoliday.setYear(calendar.get(Calendar.YEAR));
                sysHoliday.setMonth(calendar.get(Calendar.MONTH) + 1);
                sysHoliday.setDay(calendar.get(Calendar.DAY_OF_MONTH));
                sysHolidays.add(sysHoliday);
            }
            //加一天
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return sysHolidays;
    }
}
