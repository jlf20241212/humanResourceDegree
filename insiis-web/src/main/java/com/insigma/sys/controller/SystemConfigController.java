package com.insigma.sys.controller;

import com.insigma.framework.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SystemConfigController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @GetMapping("/getSystemPeiz")
    public ResponseMessage getSystemPeiz() {
        String sql="select aaa001,aaa005 from aa01 where aaa001 in('logo_url','sidebar_backgroundImage','home_image','sidebar_title','sidebar_color'\n" +
                ",'home_title','login_title')";
        Map<String, Object> map = new HashMap<>();
         jdbcTemplate.query(sql, (rs, i) -> {
            map.put(rs.getString("aaa001"), rs.getString("aaa005"));
            return map;
        });
        return ResponseMessage.ok(map);
    }
}
