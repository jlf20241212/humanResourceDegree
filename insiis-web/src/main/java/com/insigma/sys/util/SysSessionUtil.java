package com.insigma.sys.util;

import com.insigma.framework.exception.UserNoLoginException;
import com.insigma.framework.util.SysUtils;
import com.insigma.sys.service.SysWarningLogService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 会话工具类
 *
 * @author yinjh
 * @version 2021/10/9
 */
public class SysSessionUtil {

    /**
     * 清除会话
     * @param warningLogMsg 告警内容
     */
    public static void clear(String warningLogMsg) {
        clear(warningLogMsg, "输入异常，请重新登录！");
    }

    /**
     * 清除会话
     * @param warningLogMsg 告警内容
     * @param throwMsg 异常信息
     */
    public static void clear(String warningLogMsg, String throwMsg) {
        // 记录告警日志
        SysWarningLogService sysWarningLogService = SysUtils.getBean(SysWarningLogService.class);
        sysWarningLogService.saveLog(warningLogMsg);
        // 清除会话
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.getSession().invalidate();
        throw new UserNoLoginException(throwMsg);
    }

}
