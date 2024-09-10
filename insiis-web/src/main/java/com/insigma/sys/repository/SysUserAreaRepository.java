package com.insigma.sys.repository;

import com.insigma.sys.entity.SysUserArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: caic
 * @version: 10:41 2019/1/14
 * @Description:
 */
@Repository
public interface SysUserAreaRepository extends JpaRepository<SysUserArea,String> {

    List<SysUserArea> findByUserId(String userId);

    @Modifying
    @Query(value="delete sysuserarea where userid=?1",nativeQuery = true)
    int deleteSysUserArea(String userId);
    @Query(value="select s from SysUserArea s where s.aab301=?1")
    List<SysUserArea> findByAreaId(String orgentercode);
}
