package com.insigma.sys.repository;

import com.insigma.sys.entity.SysOrg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: caic
 * @version: 12:05 2019/1/12
 * @Description:
 */
@Repository
public interface SysOrgRepository extends JpaRepository<SysOrg,Long>{

    @Query(value = "select s from SysOrg s order by s.orgorder")
    List<SysOrg> findAllOrg();
    List<SysOrg> findByparentid(Long parentid);

    List<SysOrg> findByRegioncode(String regionCode);

    @Query(value = "select t.* from sysorg t where t.orgid like ?1%",nativeQuery = true)
    List<SysOrg> findByOrgidStartingWith(Long orgId);

    List<SysOrg> findByIdpathStartingWith(String orgId);
    @Query(value = "select s from SysOrg s where s.orgid=?1")
    SysOrg findByOrgid(Long orgId);
    @Query(value = "select s from SysOrg s where s.orgname=?1")
    SysOrg findByName(String name);
    @Query(value = "select s from SysOrg s where s.orgname=?1 or s.orgentercode=?2")
    List<SysOrg> findByNameOrCode(String name, String code);
    @Modifying
    @Query(value = "select u.* from SysOrg u start with u.orgid=?1 connect by prior orgid=parentid",nativeQuery =true)
    List<SysOrg> findNodeByOrgid(Long id);
    @Query(value = "select s from SysOrg s where s.regioncode=?1 and ?2 like concat(s.idpath, '%')")
    List<SysOrg> findByRegioncodeAndIdPath(String regioncode, String idpath);
}
