package com.insigma.sys.service.impl;

import com.insigma.sys.entity.SysUserArea;
import com.insigma.sys.repository.SysUserAreaRepository;
import com.insigma.sys.service.SysUserAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysUserAreaImpl implements SysUserAreaService {
    @Autowired
    private SysUserAreaRepository sysUserAreaRepository;
    @Override
    public List<SysUserArea> findUserAreaByUserid(String userid) {
        return sysUserAreaRepository.findByUserId(userid);
    }
    @Transactional(rollbackFor=Exception.class)
    @Override
    public void deleteAllByUserid(String userid) {
        sysUserAreaRepository.deleteSysUserArea(userid);
    }
}
