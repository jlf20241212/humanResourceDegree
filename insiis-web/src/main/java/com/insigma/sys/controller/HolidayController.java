package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.sys.entity.SysHoliday;
import com.insigma.sys.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yinjh
 * @version 2021/6/16
 */
@RestController
@RequestMapping("/sys/holiday")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @GetMapping("/query")
    public ResponseMessage getHolidayList(int year){
        List<SysHoliday> holidayList = holidayService.queryHolidayList(year);
        if (holidayList == null || holidayList.isEmpty()) {
            holidayList = holidayService.getWeekdays(year);
            holidayService.updateHolidays(year, holidayList);
        }
        List<String> dates = holidayToDate(holidayList);
        return ResponseMessage.ok(dates);
    }

    @PostMapping("/{year}/update")
    public ResponseMessage updateHolidays(@PathVariable int year, @RequestBody JSONObject jsonObject){
        List<String> holidays = jsonObject.getJSONArray("holidays").toJavaList(String.class);
        holidayService.updateHolidays(year, dateToHoliday(holidays));
        return ResponseMessage.ok("保存成功！");
    }

    private List<String> holidayToDate(List<SysHoliday> holidays) {
        return holidays.stream().map(s -> s.getYear() + "-" + (s.getMonth() < 10 ? "0" : "") + s.getMonth() + "-" + (s.getDay() < 10 ? "0" : "") + s.getDay()).collect(Collectors.toList());
    }

    private List<SysHoliday> dateToHoliday(List<String> dates) {
        return dates.stream().map(s -> {
            String[] date = s.split("-");
            SysHoliday sysHoliday = new SysHoliday();
            sysHoliday.setYear(Integer.parseInt(date[0]));
            sysHoliday.setMonth(Integer.parseInt(date[1]));
            sysHoliday.setDay(Integer.parseInt(date[2]));
            return sysHoliday;
        }).collect(Collectors.toList());
    }
}
