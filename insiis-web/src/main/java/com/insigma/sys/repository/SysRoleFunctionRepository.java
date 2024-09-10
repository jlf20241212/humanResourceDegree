package com.insigma.sys.repository;

import com.insigma.sys.entity.SysRoleFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2019/1/15.
 */
@Repository
public interface SysRoleFunctionRepository extends JpaRepository<SysRoleFunction,String>,JpaSpecificationExecutor<SysRoleFunction> {
    @Modifying
    @Query(value="delete from sysrolefunction where roleid=?1",nativeQuery = true)
    int deleteRoleFunction(String roleid);

    @Query(value="select srf.functionid from sysrolefunction srf,sysfunction sf where srf.functionid=sf.functionid and sf.active='1' and sf.nodetype<>'1' and srf.roleid=?1",nativeQuery = true)
    List<Long> queryFuncitonidByRoleid(String roleid);

    @Query(value="select functionid from sysrolefunction where roleid=?1",nativeQuery = true)
    List<Long> queryFuncitonidAllByRoleid(String roleid);

    @Query(value = "select s from SysRoleFunction s where s.functionid=?1")
    List<SysRoleFunction> queryRoleBy(Long functionid);
}
