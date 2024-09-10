package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.encryption.util.SM3Utils;
import com.insigma.framework.exception.AppException;
import com.insigma.framework.web.securities.commons.SM3PasswordEncoder;
import com.insigma.framework.web.securities.entity.SysLogonLog;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.SysUserService;
import com.insigma.sys.service.SyslogonLogService;
import com.insigma.web.support.config.SysConfigProperties;
import com.insigma.web.support.repository.Aa01Repository;
import com.insigma.web.support.entity.Aa01;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 首页面公共Controller，密码修改等在首页面的请求处理可统一放到这里来
 * @author jinw
 * @version 2019/4/2
 * <p>epsoft - insiis7</p>
 */
@RestController
@Slf4j
public class HomeController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private SysConfigProperties sysConfigProperties;

    @Autowired
    private Aa01Repository aa01Repository;

    @Autowired
    private SyslogonLogService syslogonLogService;

    @PostMapping("/sys/modifypd")
    public ResponseMessage modifyPasswd(@RequestBody JSONObject data) {
        SysUser currentUser = currentUserService.getCurrentUser();
        try {
            String oldPass=data.getString("opasswd");
            String newPass=data.getString("npasswd");
            String confirmPass = data.getString("rpasswd");
            if (ObjectUtils.isEmpty(newPass) || !newPass.equals(confirmPass)){
                throw new AppException("新密码为空或两次输入不等！");
            }
            String sm3LogonName = SM3Utils.digest(currentUser.getLogonName());
            String sm3ReverseLogonName = SM3Utils.digest(new StringBuffer(currentUser.getLogonName()).reverse().toString());
            if (sm3LogonName.equals(newPass) || sm3ReverseLogonName.equals(newPass)) {
                throw new AppException("新密码不能与登录名（包含逆序）相同！");
            }
            sysUserService.updataPassWD(oldPass,newPass);
            return ResponseMessage.ok("修改密码成功");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseMessage.error("修改密码失败，失败原因：" + e.getMessage());
        }
    }

    @PostMapping("/sys/checkDefaultPasswd")
    public ResponseMessage checkDefaultPasswd() {
        SysUser sysUser = currentUserService.getCurrentUser();
        sysUser = sysUserService.findUserByLogonName(sysUser.getLogonName());
        Date date = sysUser.getPwEditDate();
        if(date==null){
            return ResponseMessage.ok(true);
        }
        String passwd = sysUser.getPassWD();
        String defaultPasswd = sysConfigProperties.getDefaultPassword();
        if(ObjectUtils.isEmpty(defaultPasswd)) {
            return ResponseMessage.ok(false);
        }
        return ResponseMessage.ok(new SM3PasswordEncoder().matches(SM3Utils.digest(defaultPasswd), passwd));
    }

    @PostMapping("/sys/checkPasswordDisabled")
    public ResponseMessage checkPasswordDisabled() {
        SysUser sysUser = currentUserService.getCurrentUser();
        sysUser = sysUserService.findUserByLogonName(sysUser.getLogonName());
        if ("1".equals(sysUser.getPwExpireType())) {
            // 密码周期过期
            Aa01 aa01 = aa01Repository.findByAaa001("PASSWORD_EXPIRED_PERIOD");
            if (aa01 != null) {
                Long period = Long.valueOf(aa01.getAaa005());
                if (sysUser.getPwEditDate() != null) {
                    // 密码修改过，以修改之后的周期计算
                    if (sysUser.getPwEditDate().getTime() + period * 24 * 60 * 60 * 1000 <= System.currentTimeMillis()) {
                        return ResponseMessage.ok(true);
                    }
                } else {
                    // 密码没有修改过，以新建用户之后的周期计算
                    if (sysUser.getCreateTime().getTime() + period * 24 * 60 * 60 * 1000 <= System.currentTimeMillis()) {
                        return ResponseMessage.ok(true);
                    }
                }
            }
        } else if ("3".equals(sysUser.getPwExpireType())) {
            // 指定日期过期
            if (sysUser.getPwExpireDate() != null && sysUser.getPwExpireDate().getTime() <= System.currentTimeMillis()) {
                return ResponseMessage.ok(true);
            }
        }
        return ResponseMessage.ok(false);
    }

    @GetMapping("/sys/getLastLoginTime")
    public ResponseMessage getLastLoginTime() {
        SysUser sysUser = currentUserService.getCurrentUser();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<SysLogonLog> list = syslogonLogService.getLastLoginTime(sysUser.getUserId());
        String lastLoginTime = "首次登录";
        if(list != null&& list.size() > 1){
            lastLoginTime = sdf.format(list.get(1).getLogontime());
        }
        return ResponseMessage.ok("", lastLoginTime);
    }

    @GetMapping("/sys/getUserInfo")
    public ResponseMessage getUserInfo(){
        SysUser sysUser = currentUserService.getCurrentUser();
//        SysUser sysUser = sysUserService.queryOneUser(currentUserService.getCurrentUser().getUserId());
        return ResponseMessage.ok(sysUser);
    }

    @PostMapping("/sys/updateUserInfo")
    public ResponseMessage updateUserInfo(@RequestBody SysUser sysUser){
        sysUserService.updateUser(sysUser);
        return ResponseMessage.ok("修改成功");

    }
}
