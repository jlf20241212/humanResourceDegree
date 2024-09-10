package com.insigma.sys.repository;

import com.insigma.sys.dto.UserDTO;
import com.insigma.sys.entity.Aa10;
import com.insigma.sys.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Author: caic
 * @version: 14:50 2019/1/7
 * @Description:
 */
@Repository
public interface SysUserRepository extends JpaRepository<SysUser,String> {
    /***
     * 查询用户信息
     * @return
     */
    @Query(value="select new SysUser(t.userId,t.logonName,t.displayName, t.orgId, t.userState, t.userType,t.slevel) from SysUser t")
    List<SysUser> queryAllUser();

    /**
     * 修改用户状态
     * @param userState
     * @param userId
     * @return
     */
    @Modifying
    @Query(value="update sysuser t set t.userstate=?1 where userid=?2",nativeQuery =true)
    int updateUserState(String userState, String userId);

    /**
     * 删除用户角色
     * @param userId
     * @return
     */
    @Modifying
    @Query(value="delete sysuserrole where userid=?1",nativeQuery = true)
    int deleteUserRole(String userId);

    /**
     *解锁用户
     * @return
     */
    @Modifying
    @Query(value="update sysuser t set t.userstate='1',t.failno=0,t.unlocktime=?1 where userid=?2",nativeQuery =true)
    int unlockUser(Date locaktTime, String userId);

    /**
     *锁定用户
     * @return
     */
    @Modifying
    @Query(value="update sysuser t set t.userstate='2',t.failno='0',t.locktime=?1 where userid=?2",nativeQuery =true)
    int lockUser(Date unlocaktTime, String userId);

    /**
     * 修改用户密码
     * @param userId
     * @param passWD
     * @param pwEditDate
     * @return
     */
    @Modifying
    @Query(value="update sysuser t set t.passwd=?1,t.pweditdate=?2 where userid=?3",nativeQuery =true)
    int updatePassWD(String passWD, Date pwEditDate, String userId);

    @Query(value = "select t from Aa10 t where t.aaa100=?1")
    List<Aa10> findByAaa100(String aaa100);

    /***
     * 分页查询用户列表
     * @param spec
     * @param pageable
     * @return
     */
    Page<UserDTO> findAll(Specification<SysUser> spec, Pageable pageable);

    /**
     * 根据用户名查询用户
     * @param logonName
     * @return
     */
    List<SysUser> findByLogonName(String logonName);

    /**
     * 根据用户名和用户Id查询用户
     * @param logonName
     * @param userId
     * @return
     */
    List<SysUser> findByLogonNameAndUserIdNot(String logonName, String userId);

    /**
     * 删除所有用户除去amdin
     * @param
     * @return
     */
    @Modifying
    @Query(value = "delete from SysUser t where t.logonName <> 'admin'")
    int deleteAllUserExitsAdmin();

    @Query(value = "select su from SysUser su where su.logonName=?1 and su.userState <> '3'")
    SysUser findUsefulUserByLogonName(String logonName);
}
