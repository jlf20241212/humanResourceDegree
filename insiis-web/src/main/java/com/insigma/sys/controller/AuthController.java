package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.encryption.util.SM3Utils;
import com.insigma.framework.util.IpUtil;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by yinjh on 2019/10/14.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired(required = false)
    private RedisTemplate redisTemplate;

    @GetMapping("/getToken")
    public ResponseMessage getToken(HttpServletRequest request) {
        SysUser sysUser = currentUserService.getCurrentUser();
        Date date = new Date();
        long timetamp = date.getTime();
        String token = SM3Utils.digest(IpUtil.getClientIp(request) + ";" + sysUser.getLogonName() + ";" + timetamp); // 生成token
        redisTemplate.opsForValue().set(token, sysUser);
        redisTemplate.expire(token, 300, TimeUnit.SECONDS);
        return ResponseMessage.ok("", token);
    }

    @PostMapping("/getUserInfo")
    public ResponseMessage getUserInfo(@RequestBody JSONObject jsonObject) {
        String token = jsonObject.getString("token");
        SysUser sysUser = (SysUser) redisTemplate.opsForValue().get(token);
        if(sysUser == null) {
            return ResponseMessage.error("获取用户信息失败！");
        }
        return ResponseMessage.ok(sysUser);
    }

}
