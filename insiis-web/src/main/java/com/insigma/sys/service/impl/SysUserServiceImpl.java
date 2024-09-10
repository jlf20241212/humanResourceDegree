package com.insigma.sys.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.commons.syslog.Syslog;
import com.insigma.framework.encryption.util.SM3Utils;
import com.insigma.framework.exception.AppException;
import com.insigma.framework.util.DtoEntityUtil;
import com.insigma.framework.web.securities.commons.SM3PasswordEncoder;
import com.insigma.framework.web.securities.entity.SysLogonLog;
import com.insigma.framework.web.securities.repository.SysLogonLogRepository;
import com.insigma.framework.web.securities.service.SysCacheService;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.common.SysManageMode;
import com.insigma.sys.dto.UserDTO;
import com.insigma.sys.entity.*;
import com.insigma.sys.repository.*;
import com.insigma.sys.service.SysUserService;
import com.insigma.web.support.config.SysConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: caic
 * @version: 15:39 2019/1/7
 * @Description:
 */
@Service("SysUserService")
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private SysOrgRepository sysOrgRepository;
    @Autowired
    private Aa26Repository aa26Repository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private SysUserRoleRepository sysUserRoleRepository;
    @Autowired
    private SysUserAreaRepository sysUserAreaRepository;
    @Autowired
    private SysConfigProperties sysConfigProperties;
    @Autowired
    private SysCodeRepository sysCodeRepository;
    @Autowired
    private SysCacheService sysCacheService;
    @Autowired
    private SysLogonLogRepository sysLogonLogRepository;

    /**
     * 同步用户删除操作
     */
    @Syslog("删除所有用户")
    @Override
    @Transactional(rollbackFor=Exception.class)
    public void deleteAll() {
        sysUserRepository.deleteAll();
    }

    @Syslog("删除用户：${id}")
    @Override
    @Transactional(rollbackFor=Exception.class)
    public void deleteByUserId(String id) {

        sysUserRepository.deleteById(id);
    }

    @Syslog("保存用户：${sysUser.logonName}")
    @Override
    @Transactional(rollbackFor=Exception.class)
    public void saveAll(SysUser sysUser) {
        sysUserRepository.save(sysUser);
    }

    /**
     * 保存用户
     *
     * @param sysUser
     */
    @Syslog("保存用户：${sysUser.logonName}")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(SysUser sysUser, Map<String, List<String>> map) throws AppException {
        SysUser currentUser = currentUserService.getCurrentUser();
        if ("3".equals(currentUser.getUserType())) {//当前登录用户为机构管理员，添加的用户机构随当前登录用户机构
            sysUser.setOrgId(currentUser.getOrgId());
            sysUser.setAreaId(currentUser.getAreaId());
        }

        //如果是更新操作，将原密码查询到新的更新对象中
        if (sysUser.getUserId() != null && sysUser.getUserId().length() > 0) {
            SysUser temUser=queryOneUser(sysUser.getUserId());
            if (temUser == null) {
                throw new AppException("当前用户不存在");
            }
            if((temUser.getPassWD()!=null) && (!temUser.getPassWD().equals(sysUser.getPassWD())) //原码是否和原来一样
                    && (!SM3Utils.digest(temUser.getPassWD()).equals(sysUser.getPassWD()))){
                //密码有更新，需要重新加密
                sysUser.setPassWD(new SM3PasswordEncoder().encode(sysUser.getPassWD()));
            } else {
                sysUser.setPassWD(temUser.getPassWD());
            }
        }else{
            if(checkLogonName(sysUser.getLogonName(), null)) {
                throw new AppException("该用户已存在！");
            }
            // 新增用户使用默认配置好的密码
            if (!SysManageMode.isTripleMode()) {
                sysUser.setPassWD(new SM3PasswordEncoder().encode(SM3Utils.digest(sysConfigProperties.getDefaultPassword())));
            } else {
                sysUser.setPassWD(new SM3PasswordEncoder().encode(SM3Utils.digest(sysUser.getPassWD())));
            }
        }
        String userId = sysUserRepository.save(sysUser).getUserId();

        // 保存用户角色
        sysUserRoleRepository.deleteSysUserRole(userId);
        if (sysUser.getSysUserRoleList() != null) {
            List<SysUserRole> list = sysUser.getSysUserRoleList();
            for (SysUserRole sysUserRole : list) {
                sysUserRole.setUserId(userId);
            }
            sysUserRoleRepository.saveAll(list);
        }


        //删除去掉打钩的区域
        List<SysUserArea> areaDelList = new ArrayList<>();
        if (map.get("removeAreaIds").size() > 0) {
            for (String areaId : map.get("removeAreaIds")) {
                SysUserArea sysUserArea = new SysUserArea();
                sysUserArea.setUserId(userId);
                sysUserArea.setAab301(areaId);
                areaDelList.add(sysUserArea);
            }
        }
        sysUserAreaRepository.deleteAll(areaDelList);
        //保存添加打钩的区域
        List<SysUserArea> areaAddList = new ArrayList<>();
        if (map.get("addAreaIds").size() > 0) {
            for (String areaId : map.get("addAreaIds")) {
                SysUserArea sysUserArea = new SysUserArea();
                sysUserArea.setUserId(userId);
                sysUserArea.setAab301(areaId);
                areaAddList.add(sysUserArea);
            }
        }
        sysUserAreaRepository.saveAll(areaAddList);

        // 清除用户缓存
        sysCacheService.clearUserCache(sysUser.getLogonName());
    }

    /***
     * 查询全部用户
     * @return
     */
    @Override
    public List<SysUser> queryAllUser() {
        return sysUserRepository.findAll();
    }

    /***
     * 注销用户
     * @param userId
     */
    @Syslog("注销用户：${returnVal.logonName}")
    @Override
    @Transactional
    public SysUser logoutUser(String userId) throws AppException {
        SysUser sysUser = queryOneUser(userId);
        if (sysUser == null) {
            throw new AppException("所选用户不存在");
        }
        sysUser.setUserState("3");
        sysUserRepository.saveAndFlush(sysUser);
        // 清除用户缓存
        sysCacheService.clearUserCache(sysUser.getLogonName());
        //暂时不删除用户绑定的用户及管理的区域
        //sysUserRepository.deleteUserRole(userId);
        return sysUser;
    }

    /***
     * 解锁用户
     * @param userId
     */
    @Syslog("解锁用户：${returnVal.logonName}")
    @Override
    @Transactional
    public SysUser unlockUser(String userId) throws AppException {
        SysUser sysUser = queryOneUser(userId);
        if (sysUser == null) {
            throw new AppException("所选用户不存在");
        }
        if (!"2".equals(sysUser.getUserState())) {
            throw new AppException("所选用户未被锁定");
        }
        sysUser.setFailNO(0L);
        sysUser.setUserState("1");
        sysUser.setUnlockTime(new Date());
        sysUserRepository.saveAndFlush(sysUser);
        // 清除用户缓存
        sysCacheService.clearUserCache(sysUser.getLogonName());
        return sysUser;
    }

    /***
     * 锁定用户
     * @param userId
     */
    @Syslog("锁定用户：${returnVal.logonName}")
    @Override
    @Transactional
    public SysUser lockUser(String userId) throws AppException {
        SysUser sysUser = queryOneUser(userId);
        if (sysUser == null) {
            throw new AppException("所选用户不存在");
        }
        if (!"1".equals(sysUser.getUserState())) {
            throw new AppException("所选用户不是正常用户");
        }
        sysUser.setFailNO(0L);
        sysUser.setUserState("2");
        sysUser.setLockTime(new Date());
        sysUserRepository.saveAndFlush(sysUser);
        // 清除用户缓存
        sysCacheService.clearUserCache(sysUser.getLogonName());
        return sysUser;
    }

    /**
     * 重置密码
     */
    @Syslog("重置密码：${returnVal.logonName}")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser resetPassWD(String userId) throws AppException {
        SysUser sysUser = queryOneUser(userId);
        if (sysUser == null) {
            throw new AppException("所选用户不存在");
        }
        if ("3".equals(sysUser.getUserState())) {
            throw new AppException("所选用户已注销");
        }
        sysUser.setPassWD(new SM3PasswordEncoder().encode(SM3Utils.digest(sysConfigProperties.getDefaultPassword())));
        sysUser.setPwEditDate(new Date());
        sysUserRepository.saveAndFlush(sysUser);
        // 清除用户缓存
        sysCacheService.clearUserCache(sysUser.getLogonName());
        return sysUser;
    }

    /**
     * 修改密码
     * @param oldPass
     * @param newPass
     * @throws AppException
     */
    @Syslog("修改密码：${returnVal.logonName}")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser updataPassWD(String oldPass, String newPass) throws AppException {
        SysUser currentUser = currentUserService.getCurrentUser();
        boolean bool = new SM3PasswordEncoder().matches(oldPass,currentUser.getPassWD());
        if (bool) {
            SysUser sysUser = sysUserRepository.getOne(currentUser.getUserId());
            sysUser.setPassWD(new SM3PasswordEncoder().encode(newPass));
            sysUser.setPwEditDate(new Date());
            sysUserRepository.saveAndFlush(sysUser);
            // 清除用户缓存
            sysCacheService.clearUserCache(currentUser.getLogonName());
            return sysUser;
        } else {
            throw new AppException("旧密码输入错误，请重新输入");
        }
    }

    /**
     * 查询区域下的机构树节点
     *
     * @param areaId
     * @return
     */
    @Override
    public List<SysOrg> queryOrgNodes(String areaId) {
        List<SysOrg> orgList = sysOrgRepository.findByRegioncode(areaId);
        List<SysOrg> orgTree = new ArrayList<>();
        for (SysOrg sysOrg : orgList) {
            orgTree.add(sysOrg);//添加自身节点
            orgTree.addAll(sysOrgRepository.findByIdpathStartingWith(sysOrg.getIdpath() + "/"));//后面加"/"为了防止匹配例如1/2/3，匹配成1/2/323
        }
        List<SysOrg> list = orgTree.stream().distinct().collect(Collectors.toList());
        return list;
    }

    /**
     * 查找区域树结构数据
     *
     * @return
     */
    @Override
    public List<Aa26> queryAa26Nodes() {
        SysUser currentUser = currentUserService.getCurrentUser();
        List<Aa26> list = new ArrayList<>();
        if ("1".equals(currentUser.getUserType())) {//超级管理员
            list = aa26Repository.findAll();
        } else if ("2".equals(currentUser.getUserType())) {//区域管理员
            Aa26 aa26 = aa26Repository.findByAab301(currentUser.getAreaId() + "");
            list = aa26Repository.findByidpathStartingWith(aa26.getIdpath());
        } else if ("3".equals(currentUser.getUserType())) {//机构管理员不查询所属行政区，默认跟随自身机构
            // sysOrgRepository.getOne(currentUser.getOrgId());
            Aa26 aa26 = aa26Repository.getOne(currentUser.getAreaId() + "");
            list.add(aa26);
        }
        return list;
    }

    /**
     * 查询用户类型列表
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> findUsetTypeCode() {
        List<Aa10> list = sysCodeRepository.findByAaa100("USERTYPE");
        if(SysManageMode.isTripleMode()){//三员制
            if(!"1".equals(currentUserService.getCurrentUser().getUserType())){//如果当前登录用户不是超级管理员，则不能添加超级管理员。
               if(!"5".equals(currentUserService.getCurrentUser().getUserType())){//如果不是安全管理员则不能显示安全管理员
                   for(int i=0;i<list.size();i++){
                       if("1".equals(list.get(i).getAaa102())){
                           list.remove(i);
                           break;
                       }
                   }
                   if("3".equals(currentUserService.getCurrentUser().getUserType())){//如果当前用户是机构管理员，则不能添加区域管理员
                       for(int i=0;i<list.size();i++){
                           if("2".equals(list.get(i).getAaa102())){
                               list.remove(i);
                               break;
                           }
                       }
                   }
                   List listRemove=new ArrayList();
                   for(int i=0;i<list.size();i++){
                       if("1".equals(list.get(i).getAaa105())){
                           listRemove.add(list.get(i).getAaz093());
                       }
                   }
                   if(listRemove.size()>0){
                       for(int i=0;i<listRemove.size();i++){
                           for(int j=0;j<list.size();j++){
                               if(listRemove.get(i).toString().equals(list.get(j).getAaz093().toString())){
                                   list.remove(j);
                                   break;
                               }
                           }

                       }
                   }
               }
            }
        }else{
            if(!"1".equals(currentUserService.getCurrentUser().getUserType()) ){//如果当前登录用户不是超级管理员，则不能添加超级管理员。
                for(int i=0;i<list.size();i++){
                    if("1".equals(list.get(i).getAaa102())){
                        list.remove(i);
                        break;
                    }
                }
                if("3".equals(currentUserService.getCurrentUser().getUserType())){//如果当前用户是机构管理员，则不能添加区域管理员
                    for(int i=0;i<list.size();i++){
                        if("2".equals(list.get(i).getAaa102())){
                            list.remove(i);
                            break;
                        }
                    }
                }
            }
            List listRemove=new ArrayList();
            for(int i=0;i<list.size();i++){
                if("1".equals(list.get(i).getAaa105())){
                    listRemove.add(list.get(i).getAaz093());
                }
            }
            if(listRemove.size()>0){
                for(int i=0;i<listRemove.size();i++){
                    for(int j=0;j<list.size();j++){
                        if(listRemove.get(i).toString().equals(list.get(j).getAaz093().toString())){
                            list.remove(j);
                            break;
                        }
                    }

                }
            }
        }

       return aa10ToCodeList(list);
    }


    @Override
    public List<Map<String, Object>> findSlevel() {
        List<Aa10> list = sysCodeRepository.findByAaa100("SLEVEL");
        return aa10ToCodeList(list);
    }

    /**
     * 分页查询用户
     *
     * @param logonName
     * @param displayName
     * @param orgId
     * @param userState
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<UserDTO> findAll(String logonName, String displayName, String orgId, String userState, String userType, String aa26, String cardId, Integer page, Integer size,String type) {
        SysUser currentUser = currentUserService.getCurrentUser();
        Specification queryParams = (Specification<SysUser>) (root, criteriaQuery, criteriaBuilder) -> {
            try {
                List<Predicate> predicates = new ArrayList<>();
               // if("1".equals(type) || "2".equals(type)){//常规用户管理模块或者三员制用户管理模块
                    if (!"1".equals(currentUser.getUserType())) {//超级管理员
                        String[] states = new String[]{"1", "2"};//过滤的用户状态，只查询状态为1：正常，2：锁定的用户
                        CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("userState"));
                        for (String state : states) {
                            in.value(state);
                        }
                        predicates.add(in);
                        if ("2".equals(currentUser.getUserType())) {//行政区管理员
                            predicates.add((Predicate) criteriaBuilder.equal(root.get("areaId"), currentUser.getAreaId()));
                            predicates.add((Predicate) criteriaBuilder.notEqual(root.get("userType"), "1"));
                            //                    List<SysOrg> orgList = sysOrgRepository.findByparentid(currentUser.getOrgId());
                            //                    CriteriaBuilder.In<Long> areaIn = criteriaBuilder.in(root.get("orgId"));
                            //                    for (SysOrg sysOrg : orgList) {
                            //                        areaIn.value(sysOrg.getOrgid());
                            //                    }
                            //                    areaIn.value(currentUser.getOrgId());
                            //                    predicates.add(in);
                        }
                        if ("3".equals(currentUser.getUserType())) {//机构管理员
                            predicates.add((Predicate) criteriaBuilder.equal(root.get("orgId"), currentUser.getOrgId()));
                            predicates.add((Predicate) criteriaBuilder.notEqual(root.get("userType"), "1"));
                            predicates.add((Predicate) criteriaBuilder.notEqual(root.get("userType"), "2"));
                            //                    List<SysOrg> orgList = sysOrgRepository.findByparentid(currentUser.getOrgId());
                            //                    CriteriaBuilder.In<Long> areaIn = criteriaBuilder.in(root.get("orgId"));
                            //                    for (SysOrg sysOrg : orgList) {
                            //                        areaIn.value(sysOrg.getOrgid());
                            //                    }
                            //                    predicates.add(in);
                        }
                        if ("4".equals(currentUser.getUserType())) {//业务操作员
                            throw new RuntimeException("业务操作人员没权限查看用户列表");
                        }
                        /*if ("5".equals(currentUser.getUserType())) {//安全管理员
                            throw new RuntimeException("安全管理员没权限查看用户列表");
                        }
                        if ("6".equals(currentUser.getUserType())) {//审计管理员
                            throw new RuntimeException("审计管理员没权限查看用户列表");
                        }*/
                    }
                /*}else if("3".equals(type)) {//三员制下用户授权模块
                    if (!"5".equals(currentUser.getUserType())) {
                        throw new RuntimeException("三员制下，除了安全管理员有用户授权权限，其他人员没权限查看用户列表");
                    }
                }*/
                if (null != logonName && !"".equals(logonName)) {
                    predicates.add((Predicate) criteriaBuilder.like(root.get("logonName"), "%" + logonName + "%"));
                }
                if (null != displayName && !"".equals(displayName)) {
                    predicates.add((Predicate) criteriaBuilder.like(root.get("displayName"), "%" + displayName + "%"));
                }
                if (null != orgId && !"".equals(orgId)) {
                    predicates.add((Predicate) criteriaBuilder.equal(root.get("orgId"), orgId));
                }
                if (null != userState && !"".equals(userState)) {
                    predicates.add((Predicate) criteriaBuilder.equal(root.get("userState"), userState));
                }
                if (null != userType && !"".equals(userType)) {
                    predicates.add((Predicate) criteriaBuilder.equal(root.get("userType"), userType));
                }
                if (null != aa26 && !"".equals(aa26)) {
                    predicates.add((Predicate) criteriaBuilder.equal(root.get("areaId"), aa26));
                }
                if (null != cardId && !"".equals(cardId)) {
                    predicates.add((Predicate) criteriaBuilder.equal(root.get("cardId"), cardId));
                }

                Predicate[] p = new Predicate[predicates.size()];
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(p)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return criteriaQuery.getRestriction();
        };
        page = page < 0 ? 0 : page;
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> codePage = sysUserRepository.findAll(queryParams, pageable);
        List<UserDTO> dtoList = DtoEntityUtil.trans(codePage.getContent(), UserDTO.class);
        dtoList.stream().forEach(userDTO -> {
            userDTO.setVisible("1");
            if (!ObjectUtils.isEmpty(userDTO.getTel())){
                String tel = userDTO.getTel();
                String substring = tel.substring(3, 7);
                String replace = tel.replace(substring, "****");
                userDTO.setTel(replace);
            }
            if (!ObjectUtils.isEmpty(userDTO.getCardId()) && userDTO.getCardId().length() > 3) {
                StringBuilder sb = new StringBuilder();
                int len = userDTO.getCardId().length() - 3;
                for (int i = 0; i < len; i++) {
                    sb.append("*");
                }
                String replace = userDTO.getCardId().replaceAll("^(.{1})(?:\\d+).(.{2})$", "$1" + sb.toString() + "$2");
                userDTO.setCardId(replace);
            }
        });
        return new PageImpl(dtoList,pageable,codePage.getTotalElements());
    }

    /**
     * 查询用户状态列表
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getUserStateCode() {
        List<Aa10> list = sysCodeRepository.findByAaa100("USERSTATE");
        return aa10ToCodeList(list);
    }

    /**
     * 将aa10数据组装成List<Map>格式
     *
     * @param list
     * @return
     */
    private List<Map<String, Object>> aa10ToCodeList(List<Aa10> list) {
        List<Map<String, Object>> codeList = new ArrayList<>();
        for (Aa10 aa10 : list) {
            Map tem = new ConcurrentHashMap();
            tem.put("key", aa10.getAaa102());
            tem.put("value", aa10.getAaa102());
            tem.put("label", aa10.getAaa103());
            codeList.add(tem);
        }
        return codeList;
    }

    @Override
    public List<SysRole> findByRoleType(String userType) {
        String currentUserType = currentUserService.getCurrentUser().getUserType();
        if("1".equals(userType)){//新增的用户类型为超级管理员
            return roleRepository.queryByAdmin();
        }
        if ("2".equals(userType)) {//新增的用户类型为行政区管理员
            return roleRepository.queryRoleByArea(currentUserService.getCurrentUser().getAreaId());
        }
        if ("3".equals(userType)) {//新增的用户类型为机构管理员
            return roleRepository.queryByOrgId(currentUserService.getCurrentUser().getOrgId());
        }
        if ("4".equals(userType)) {//新增的用户类型为业务操作员（普通用户）
            if("1".equals(currentUserType)){//当前登录用户为超级管理员
                return roleRepository.findAllByRoletype("2");
            }
            if ("2".equals(currentUserType)) {//当前登录用户为行政区管理员
                return roleRepository.queryRoleByRoletypeAndAreaid("2", currentUserService.getCurrentUser().getAreaId());
            }
            if ("3".equals(currentUserType)) {//当前登录用户为机构管理员
                return roleRepository.findByRoletypeAndOrgid("2", currentUserService.getCurrentUser().getOrgId());
            }
            if ("5".equals(currentUserType)) {//当前登录用户为安全管理员
                return roleRepository.findAllByRoletype("2");
            }
        }
        if(SysManageMode. isTripleMode()){//三员制
            if("5".equals(userType)){
                return roleRepository.findAllByRoletype("3");
            }else if("6".equals(userType)){
                return roleRepository.findAllByRoletype("4");
            }
        }
        return roleRepository.findAll();
    }

    /**
     * 校验登录名是否重复
     *
     * @param logonName
     * @return
     */
    @Override
    public boolean checkLogonName(String logonName, String userId) {
        List<SysUser> list;
        if (userId == null || userId.length() == 0) {
            list = sysUserRepository.findByLogonName(logonName);
        } else {
            list = sysUserRepository.findByLogonNameAndUserIdNot(logonName, userId);
        }
        if (list.size() == 0) {//查询结果为空，则表示没有登录名称重复
            return false;
        }
        return true;
    }

    @Override
    public SysUser queryOneUser(String userId) {
        SysUser sysUser = sysUserRepository.findById(userId).orElse(null);
        return sysUser;
    }

    /**
     * 更新单个用户信息
     * @param sysUser
     */
    @Override
    public void updateUser(SysUser sysUser) {
        sysUserRepository.saveAndFlush(sysUser);
    }

    @Override
    public List<SysUserRole> queryUserRole(String userId) throws AppException {
        if (!checkUser(userId)) {
            throw new AppException("该用户无效");
        }
        return sysUserRoleRepository.findByUserId(userId);
    }

    @Override
    public List<SysUserArea> queryUserArea(String userId) throws AppException {
        if (!checkUser(userId)) {
            throw new AppException("该用户无效");
        }
        return sysUserAreaRepository.findByUserId(userId);
    }

    /**
     * 查询所有机构
     *
     * @return
     */
    @Override
    public List<SysOrg> findAllOrg() {
        return sysOrgRepository.findAll();
    }

    /**
     * 校验用户是否有效
     *
     * @param userId
     */
    private boolean checkUser(String userId) {
        SysUser sysUser = queryOneUser(userId);
        if (sysUser == null) {
            return false;
        }
        if ("3".equals(sysUser.getUserState())) {
            return false;
        }
        return true;
    }

    /**
     * 通用查询aa10编码
     * @param codeType
     * @return
     */
    @Override
    public List<Map<String, Object>> getAa10Code(String codeType) {
        List<Aa10> list = sysCodeRepository.findByAaa100(codeType);
        return aa10ToCodeList(list);
    }

    @Override
    public void deleteAllUserExitsAdmin() {
        sysUserRepository.deleteAllUserExitsAdmin();
    }

    /**
     * 保存用户
     *
     * @param sysUser
     */
    @Override
    @Transactional
    public void saveUserRole(SysUser sysUser)  throws AppException {
        String userId=sysUser.getUserId();
        if(SysManageMode.isTripleMode()){//三员制
            sysUserRoleRepository.deleteSysUserRole(userId);
            List<SysUserRole> list = sysUser.getSysUserRoleList();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setUserId(userId);
            }
            sysUserRoleRepository.saveAll(list);
        }
    }

    @Override
    public String getUserSlevel(){
        String logonName=currentUserService.getCurrentUser().getLogonName();
        List<SysUser> sysUser=sysUserRepository.findByLogonName(logonName);
        return sysUser.get(0).getSlevel();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveUsers(List<SysUser> users) {
        users.stream().forEach(sysUser -> {
            sysUser.setPassWD(new SM3PasswordEncoder().encode(SM3Utils.digest(sysConfigProperties.getDefaultPassword())));
            sysUser.setUserState("1");
        });
        sysUserRepository.saveAll(users);
    }

    @Override
    public SysUser findUserByLogonName(String logonName) {
        return sysUserRepository.findUsefulUserByLogonName(logonName);
    }

    @Override
    public void copyUser(JSONObject data) throws AppException {
        String cuType = currentUserService.getCurrentUser().getUserType();
        String userId = data.getString("userId");
        String logonName = data.getString("logonName");
        String displayName = data.getString("displayName");
        String cardType = data.getString("cardType");
        String cardId = data.getString("cardId");
        SysUser sysUser = sysUserRepository.findById(userId).orElse(null);
        SysUser oneUser = new SysUser();
        if(checkLogonName(logonName, null)) {
            throw new AppException("该用户名已存在！");
        }
        if(sysUser != null){
            if(Integer.parseInt(sysUser.getUserType()) < Integer.parseInt(cuType)){
                throw new AppException("无复制权限");
            }
            oneUser.setLogonName(logonName);
            oneUser.setDisplayName(displayName);
            oneUser.setOrgId(sysUser.getOrgId());
            oneUser.setAreaId(sysUser.getAreaId());
            oneUser.setUserType(sysUser.getUserType());
            oneUser.setUserState(sysUser.getUserState());
            oneUser.setCardType(cardType);
            oneUser.setCardId(cardId);
            oneUser.setPassWD(new SM3PasswordEncoder().encode(SM3Utils.digest(sysConfigProperties.getDefaultPassword())));
            sysUserRepository.save(oneUser);
        }
    }

    @Override
    public void clearSessions(String userId) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date todayStart = calendar.getTime();
        List<SysLogonLog> list = sysLogonLogRepository.findNotLogoffListByUserIdToday(userId, todayStart);
        List<String> sessionIds = list.stream().map(SysLogonLog::getSessionid).collect(Collectors.toList());
        if (sessionIds.size() > 0) {
            sysCacheService.clearSessions(sessionIds);
        }
    }


}

