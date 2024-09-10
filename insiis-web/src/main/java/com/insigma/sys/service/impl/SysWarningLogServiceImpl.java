package com.insigma.sys.service.impl;

import com.insigma.framework.web.securities.service.SysLogService;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.entity.SysWarningLog;
import com.insigma.sys.repository.SysWarningLogRepository;
import com.insigma.sys.service.SysWarningLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author yinjh
 * @version 2021/10/9
 */
@Service
public class SysWarningLogServiceImpl implements SysWarningLogService {

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private SysWarningLogRepository sysWarningLogRepository;

    @Autowired
    private SysLogService logService;

    @Autowired
    private HttpServletRequest request;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLog(String content) {
        SysWarningLog log = new SysWarningLog();
        log.setUserid(currentUserService.getCurrentUser().getUserId());
        log.setSessionid(request.getSession().getId());
        log.setContent(content);
        log.setCreateTime(new Date());
        sysWarningLogRepository.saveAndFlush(log);

        logService.saveLogoffLogBySid(request.getSession().getId(), "输入异常退出");
    }
}
