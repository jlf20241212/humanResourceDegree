package com.insigma.sys.service;

import com.insigma.sys.entity.Aa26;
import com.insigma.sys.entity.SysOrg;

import java.util.List;
import java.util.Map;

/**
 * @Author: caic
 * @version: 14:10 2019/1/22
 * @Description:
 */
public interface SysOrgService {
    /**
     * 保存机构（同步用）
     */
    void save(SysOrg sysOrg);
    /**
     * 删除所有机构（同步用）
     */
    void deleteAllOrgs();

    /**
     * 删除机构根据id(同步用)
     * @param id
     */
    void deleteByOrgId(Long id);
    SysOrg findByOrgid(Long orgId);
    List<SysOrg> findByRegioncode(String areaId);
    //查询tree
    List<SysOrg> queryAllOrg();
    //initcodetype
    List<Aa26> getAA26();
    //保存
    Aa26 queryAa26By301(String aab301);

    void saveOrg(SysOrg sysOrg);

    void delOrg(List<SysOrg> list, boolean o);
    //判断是否重复
    Boolean isMany(SysOrg sysOrg);
    //将pagedata转化为SysOrgBean
    SysOrg getSysOrgBean(Map<String, Object> map);
//    //查找险种
//    List<SysOrgInsType> queryInsTypeById(long id);
    //根据行政区划查找用户机构链接
    boolean findByArea(List<SysOrg> list);
    //找到子节点
    List findNodes(long node);
}

