package com.insigma.sys.service;
import com.insigma.sys.entity.SysError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SysErrorService {
    List<SysError> queryAllErrors();
    List<SysError> findAllBySome(String serviceName, String functionName, String errorCode);
    void save(SysError sysError);
    void deleteError(SysError sysError);
    SysError getSysErrorBean(Map<String, HashMap<String, String>> map);
    SysError getSysErrorBean2(Map<String, String> map);
    SysError getSysErrorBean1(Map<String, HashMap<String, String>> map);
    boolean queryByErrCode(String code);
}
