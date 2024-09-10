package com.insigma.sys.repository;

import com.insigma.sys.entity.SysUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: caic
 * @version: 16:56 2019/1/12
 * @Description:
 */
@Repository
public interface SysUserRoleRepository extends JpaRepository<SysUserRole,String> {
    List<SysUserRole> findByUserId(String userId);

    @Modifying
    @Query(value="delete from sysuserrole where userid=?1",nativeQuery = true)
    int deleteSysUserRole(String userId);


    @Modifying
    @Query(value="delete from sysuserrole where roleid=?1",nativeQuery = true)
    int deleteUserRole(String roleid);

    @Modifying
    @Query(value="delete from sysuserrole where roleid=?1 and userid=?2",nativeQuery = true)
    void removeGrantUserRole(String roleid, String userid);
}
