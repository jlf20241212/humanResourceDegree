package com.insigma.sys.service.impl;

import com.insigma.sys.entity.SysIdMapping;
import com.insigma.sys.repository.SysIdMappingRespository;
import com.insigma.sys.service.SysIdMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysIdMappingImpl implements SysIdMappingService {
    @Autowired
    private SysIdMappingRespository sysIdMappingRespository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void saveGroupidToOrgid(SysIdMapping sysIdMapping) {
        if(sysIdMapping.getSELFID() == null || sysIdMapping.getSELFID().intValue() == 0){
            Long selfid = jdbcTemplate.queryForObject("select SYS_IDMAPPING.nextval from dual",Long.class);
            sysIdMapping.setSELFID(selfid.intValue());
        }
        sysIdMappingRespository.save(sysIdMapping);
    }
    @Override
    public SysIdMapping queryByTid(String tid) {
        return sysIdMappingRespository.findByTID(tid);
    }

    @Override
    public void deleteAll() {
        sysIdMappingRespository.deleteAll();
    }
}
