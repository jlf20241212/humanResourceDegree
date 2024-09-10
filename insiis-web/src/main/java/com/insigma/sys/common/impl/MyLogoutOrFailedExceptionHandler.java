package com.insigma.sys.common.impl;

import com.insigma.odin.framework.est.EstException;
import com.insigma.odin.framework.est.auth.LogoutOrFailedExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yinjh
 * @version 2021/9/1
 */
@Slf4j
public class MyLogoutOrFailedExceptionHandler extends LogoutOrFailedExceptionHandler {

    @Override
    public void handle(EstException ex, HttpServletRequest request, HttpServletResponse response) {
        try {
            log.debug("异常处理：", ex);
            response.sendError(401, "登录已失效，请重新登录！");
        } catch (IOException e) {
            log.error("返回异常码时失败！", e);
        }
    }

}
