package com.insigma.sys.repository;

import com.insigma.sys.entity.SysRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zxy on 2019/1/7.
 */
@Repository
public interface RoleRepository extends JpaRepository<SysRole, String>, JpaSpecificationExecutor<SysRole> {
    List<SysRole> findAllByRoletype(String roletype);

    Page<SysRole> findAll(Specification queryParams, Pageable pageable);

    List<SysRole> findByRoletype(String roleType);

    List<SysRole> findByRoletypeAndOrgid(String roletype, Long orgid);

    List<SysRole> queryRoleByRoletypeAndAreaid(String roleType, Long areaId);

    /**
     * 根据用户id，查询用户所属区域的管理角色
     *
     * @param areaId
     * @return
     */
    @Query(value = "select t from SysRole t where (t.roletype='1' or roletype='3') and t.areaid=?1")
    List<SysRole> queryRoleByArea(Long areaId);

    @Query(value = "select t from SysRole t where (t.roletype='1' or roletype='3') and t.orgid=?1")
    List<SysRole> queryByOrgId(Long orgId);

    @Query(value = "select t from SysRole t where (t.roletype='1' or roletype='3')")
    List<SysRole> queryByAdmin();

    /**
     * 根据角色名称查询角色
     * @param rolename
     * @return
     */
    List<SysRole> findByRolename(String rolename);

    /**
     * 根据角色名称查询角色
     * @param rolename
     * @return
     */
    List<SysRole> findByRolenameAndRoleid(String rolename, String roleid);

    /**
     * 根据用户id查询用户所拥有的角色信息
     * @param userid
     * @return
     */
    @Query(value = "select * from sysrole r where creatorid=?1 or exists (select 1 from sysuserrole sur where sur.roleid=r.roleid and sur.userid=?1)", nativeQuery = true)
    List<SysRole> queryRoleByUserId(String userid);

    /**
     * 根据角色id查询角色是否存在
     * @param roleid
     * @return
     */
    @Query(value = "select t from SysRole t where t.roleid=?1")
    List<SysRole> checkRoleIfValidByRoleid(String roleid);

    @Query(value = "select r.* from sysrole r ,sysuserrole sur where r.roleid=sur.roleid and sur.userid=?1 and sur.roletype=?2", nativeQuery = true)
    List<SysRole> queryRoleByUserIdAndRoleType(String userId, String roleType);
}
