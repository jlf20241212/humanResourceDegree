package com.insigma.sys.service;

import com.insigma.sys.entity.SysUserArea;

import java.util.List;

/**
 * 用户区域表
 */
public interface SysUserAreaService {
    List<SysUserArea> findUserAreaByUserid(String userid);
    void deleteAllByUserid(String userid);
}
