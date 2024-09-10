package com.insigma.sys.service;

import com.insigma.sys.entity.SysIdMapping;

public interface SysIdMappingService {
    /**
     * 保存用户组织两者id
     */
    void saveGroupidToOrgid(SysIdMapping sysIdMapping);
    SysIdMapping queryByTid(String tid);
    void deleteAll();
}
