package com.insigma.sys.repository;

import com.insigma.sys.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MenuRepository extends JpaRepository<SysMenu, Long> {
    @Query(value = "select s from SysMenu s order by s.funorder")
    List<SysMenu> findTrees();
    @Query(value = "select s from SysMenu s where s.parentid=?1 order by s.funorder")
    List<SysMenu> findTreesByPId(long id);
    @Query(value = "select s from SysMenu s where s.location=?1")
    List<SysMenu> findLocation(String location);
    @Query(value = "select s from SysMenu s where s.functionid=?1")
    SysMenu findById(long id);
    @Query(value = "select s from SysMenu s where funtype=?1 or funtype='0' order by s.funorder")
    List<SysMenu> findTreesByFuntype(String funtype);
    @Query(value = "select s from SysMenu s where s.nodetype<>'3' and s.active='1' order by s.funorder")
    List<SysMenu> getMenuList();
    @Query(value = "select s from SysMenu s where (s.funtype=?1 or s.funtype='0') and s.active='1' and (s.auflag<>'1' or s.auflag is null) order by s.funorder")
    List<SysMenu> findTreesByFuntypeAndActive(String funtype);
    @Query(value = "select distinct s from SysMenu s, SysRoleFunction sr where s.functionid=sr.functionid and s.nodetype<>'3' and s.active='1' and sr.roleid in (?1) order by s.funorder")
    List<SysMenu> getMenuList(List<String> roleIds);
    @Modifying
    @Query(value = "select u.* from sysfunction u start with u.functionid=?1 connect by prior functionid=parentid",nativeQuery =true)
    List<SysMenu> findNodeByFuncid(Long id);
    @Query(value = " select  * from sysfunction  o start with o.functionid =?1 connect by prior o.parentid = o.functionid order by functionid",nativeQuery =true)
    List<SysMenu> getIdpath(Long id);
    @Query(value = " select  * from sysfunction  o where o.nodetype<>'3' start with o.functionid =?1 connect by prior functionid=parentid order by functionid",nativeQuery =true)
    List<SysMenu> findNodesNoAnNiu(Long id);
    @Query(value = "SELECT level FROM sysfunction START WITH functionid  = ?1 CONNECT BY prior functionid = parentid ORDER BY level",nativeQuery =true)
    List findNodeslevel(Long id);
    List<SysMenu> findByIdpathStartingWith(String id);
    @Query(value = "SELECT * FROM sysfunction  where location  = ?1",nativeQuery =true)
    SysMenu queryEntity(String url);

    @Query(value = "select distinct sf.* from sysfunction sf, sysrolefunction srf, sysuserrole sur where sf.functionid=srf.functionid and srf.roleid=sur.roleid and sur.userid=?1 order by sf.funorder", nativeQuery =true)
    List<SysMenu> findTreesByUserId(String userId);

    @Query(value = "select distinct s.funcode from SysMenu s, SysRoleFunction sr where s.functionid=sr.functionid and s.nodetype='3' and s.active='1' and sr.roleid in (?1) and s.parentid=?2 and s.funcode is not null")
    List<String> getButtonAuthList(List<String> roleIds, Long parentId);

    @Query(value = "select distinct s.funcode from SysMenu s where s.nodetype='3' and s.active='1' and s.parentid=?1 and s.funcode is not null")
    List<String> getAllButtonAuthList(Long parentId);

    @Transactional
    @Modifying
    @Query(value = "update SysMenu set funorder = ?2 where functionid = ?1")
    int updateNodeOrder(Long functionId,int funOrder);

    @Transactional
    @Modifying
    @Query(value = "update SysMenu set parentid = ?3,funorder = ?2 where functionid = ?1")
    int update(Long functionId,int funOrder,Long parentId);
}
