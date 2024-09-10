package com.insigma.sys.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.commons.syslog.Syslog;
import com.insigma.framework.db.JdbcPageHelper;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.exception.AppException;
import com.insigma.framework.web.securities.service.SysCacheService;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.common.SysManageMode;
import com.insigma.sys.dto.SysRoleDTO;
import com.insigma.sys.dto.UserDTO;
import com.insigma.sys.entity.*;
import com.insigma.sys.repository.RoleRepository;
import com.insigma.sys.repository.SysCodeRepository;
import com.insigma.sys.repository.SysRoleFunctionRepository;
import com.insigma.sys.repository.SysUserRoleRepository;
import com.insigma.sys.service.RoleService;
import com.insigma.web.support.repository.CodeTypeRepository;
import com.insigma.web.support.entity.CodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by zxy on 2019/1/7.
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CodeTypeRepository codeTypeRepository;

    @Autowired
    private SysRoleFunctionRepository sysRoleFunctionRepository;

    @Autowired
    private SysUserRoleRepository sysUserRoleRepository;

    @Autowired
    private SysCodeRepository sysCodeRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SysCacheService sysCacheService;


    @Syslog("保存角色：${sysRole.rolename}")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysRole sysRole) {
        //SysRole sysRole = JavaBeanUtils.pageElementToBean(pageData, "form1", SysRole.class);
        roleRepository.save(sysRole);
    }

    @Override
    public SysRoleDTO query(String rolename, String roledesc, String roletype, Integer page, Integer size,
                            Long areaid, Long orgid, String usertype) {
        SysRoleDTO sysRoleDTO = new SysRoleDTO();
        Specification queryParams = (Specification<SysRole>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (null != rolename && !"".equals(rolename)) {
                predicates.add((Predicate) criteriaBuilder.like(root.get("rolename"), "%" + rolename + "%"));
            }
            if (null != roledesc && !"".equals(roledesc)) {
                predicates.add((Predicate) criteriaBuilder.like(root.get("roledesc"), "%" + roledesc + "%"));
            }
            if (null != roletype && !"".equals(roletype)) {
                predicates.add((Predicate) criteriaBuilder.equal(root.get("roletype"), roletype));
            }
            if (null != usertype && !"".equals(usertype)) {
                if ("1".equals(usertype)) {//超级管理员

                } else if ("2".equals(usertype)) {//行政区管理员
                    predicates.add((Predicate) criteriaBuilder.equal(root.get("areaid"), areaid));
                } else if ("3".equals(usertype)) {//机构管理员
                    predicates.add((Predicate) criteriaBuilder.equal(root.get("orgid"), orgid));
                }
            }

            Predicate[] p = new Predicate[predicates.size()];
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(p)));

            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("rolename")),
                    criteriaBuilder.asc(root.get("roletype")), criteriaBuilder.asc(root.get("roledesc")));
            return criteriaQuery.getRestriction();
        };
        Pageable pageable = PageRequest.of(page, size);
        Page<SysRole> codePage = roleRepository.findAll(queryParams, pageable);
        sysRoleDTO.setTotal(codePage.getTotalElements());
        sysRoleDTO.setData(codePage.getContent());

        return sysRoleDTO;

    }

    @Syslog("删除角色：${sysRole.rolename}")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysRole(SysRole sysRole) {
        roleRepository.delete(sysRole);
    }

    @Override
    public JSONObject getCodeTypes(JSONObject jsonObject) {
        // 获取CodeTypes start
        List<String> list = new ArrayList<>();
        // 将Set中的数据拷贝到List中
        list.addAll(jsonObject.keySet());
        List<CodeType> codeTypeList = codeTypeRepository.findByCodetypeInOrderByCodetypeAscKeyAsc(list);
        //System.out.println(codeTypeList);
        JSONObject codeTypes = new JSONObject();
        codeTypeList.stream()
                .filter(ct -> jsonObject.getJSONArray(ct.getCodetype()).size() == 0)
                .forEach(ct -> {
                    JSONObject ctObj = new JSONObject();
                    ctObj.put("key", ct.getKey());
                    ctObj.put("value", ct.getValue());
                    if (codeTypes.getJSONArray(ct.getCodetype()) == null) {
                        JSONArray jsonArray = new JSONArray();
                        codeTypes.put(ct.getCodetype(), jsonArray);
                    }
                    codeTypes.getJSONArray(ct.getCodetype()).add(ctObj);
                });
        return jsonObject.fluentPutAll(codeTypes);
    }

    /*@Override
    public void saveRoleFunction(SysRoleFunction sysRoleFunction) {

        sysRoleFunctionRepository.save(sysRoleFunction);
    }*/

    @Override
    public boolean checkRoleByRolename(String rolename) {
        List<SysRole> list = roleRepository.findByRolename(rolename);
        if (list.size() == 0) {//查询结果为空，则表示没有登录名称重复
            return true;
        }
        return false;
    }

    @Override
    public boolean checkRoleByRolenameAndRoleid(String rolename, String roleid) {
        List<SysRole> list = null;
        list = roleRepository.checkRoleIfValidByRoleid(roleid);
        String oldRolename = list.get(0).getRolename();
        if (rolename.equals(oldRolename)) {
            //说明没有改变角色名称
            return true;
        } else {
            list = roleRepository.findByRolename(rolename);
            if (list.size() > 0) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public List<SysRole> queryRoleByUserId(String userid) {
        List<SysRole> list = roleRepository.queryRoleByUserId(userid);
        return list;
    }

    @Syslog("删除角色：${sysRole.rolename}")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleRef(SysRole sysRole) {
        roleRepository.delete(sysRole);
        sysRoleFunctionRepository.deleteRoleFunction(sysRole.getRoleid());
        sysUserRoleRepository.deleteUserRole(sysRole.getRoleid());

        // 清空所有用户菜单缓存
        sysCacheService.clearUserCache(null);
        sysCacheService.clearFunctionCache(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> queryFuncitonidByRoleid(String roleid) {
        List<Long> list = sysRoleFunctionRepository.queryFuncitonidByRoleid(roleid);
        List<Long> alls = sysRoleFunctionRepository.queryFuncitonidAllByRoleid(roleid);
        List<Long> buttons = alls.stream().filter(item -> item < 0).collect(Collectors.toList());
        List<Long> originList = buttons.stream().map(item -> -item).collect(Collectors.toList());
        list.removeAll(originList);
        list.addAll(buttons);
        return list;
    }

    @Override
    public boolean checkRoleIfValidByRoleid(String roleid) {
        List<SysRole> list = roleRepository.checkRoleIfValidByRoleid(roleid);
        boolean flag = false;
        if (list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public SysRole getRoleById(String roleid) {
        List<SysRole> list = roleRepository.checkRoleIfValidByRoleid(roleid);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public boolean roleCanOperate(SysUser sysUser, SysRole sysRole) {
        boolean flag = true;
        if ("2".equals(sysUser.getUserType())) {
            if (sysRole.getAreaid() == null || !sysRole.getAreaid().equals(sysUser.getAreaId())) {
                flag = false;
            }
        } else if ("3".equals(sysUser.getUserType())) {
            if (sysRole.getOrgid() == null || !sysRole.getOrgid().equals(sysUser.getOrgId())) {
                flag = false;
            }
        } else if ("4".equals(sysUser.getUserType())) {
            flag = false;
        }
        return flag;
    }

    public void deleteRoleFunctionRefAndAddNewRef(String roleid, List<Long> list) {
        /**
         * 先删除老的角色资源关系，后增加新的角色资源关系
         */
        sysRoleFunctionRepository.deleteRoleFunction(roleid);
        SysRoleFunction sysRoleFunction = null;
        Long functionid = null;
        for (int i = 0; i < list.size(); i++) {
            functionid = list.get(i);
            sysRoleFunction = new SysRoleFunction();
            sysRoleFunction.setRoleid(roleid);
            sysRoleFunction.setFunctionid(functionid);
            sysRoleFunctionRepository.save(sysRoleFunction);
        }
    }

    @Syslog("对角色：${role.rolename}授权菜单")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGrant(SysUser sysUser, SysRole role, JSONObject jsonObject) {
        JSONArray treeInfo = jsonObject.getJSONArray("treeInfo");
        JSONObject jsonTemp = null;
        List<Long> fids = new ArrayList<>();
        for (int i = 0; i < treeInfo.size(); i++) {
            jsonTemp = treeInfo.getJSONObject(i);
            Long functionid = jsonTemp.getLong("functionid");
            fids.add(functionid);
        }
        this.deleteRoleFunctionRefAndAddNewRef(role.getRoleid(), fids);

        // 清空所有菜单缓存
        sysCacheService.clearFunctionCache(null);
    }

    @Override
    public List<Map<String, Object>> findRoleTypesCode() {
        List<Aa10> list = sysCodeRepository.findByAaa100("ROLETYPE");
        if (SysManageMode.isTripleMode()) {//三员制
            if ("1".equals(currentUserService.getCurrentUser().getUserType()) || "5".equals(currentUserService.getCurrentUser().getUserType())) {//如果不是超级管理员或者安全管理员

            } else {
                List listRemove = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    if ("1".equals(list.get(i).getAaa105())) {
                        listRemove.add(list.get(i).getAaz093());
                    }
                }
                if (listRemove.size() > 0) {
                    for (int i = 0; i < listRemove.size(); i++) {
                        for (int j = 0; j < list.size(); j++) {
                            if (listRemove.get(i).toString().equals(list.get(j).getAaz093().toString())) {
                                list.remove(j);
                                break;
                            }
                        }

                    }
                }
            }
        } else {
            List listRemove = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                if ("1".equals(list.get(i).getAaa105())) {
                    listRemove.add(list.get(i).getAaz093());
                }
            }
            if (listRemove.size() > 0) {
                for (int i = 0; i < listRemove.size(); i++) {
                    for (int j = 0; j < list.size(); j++) {
                        if (listRemove.get(i).toString().equals(list.get(j).getAaz093().toString())) {
                            list.remove(j);
                            break;
                        }
                    }

                }
            }
        }
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
    public PageInfo<UserDTO> queryToGrantUser(String roleId, String roletype, String logonname, String displayname,
                                              Integer page, Integer size) throws SQLException {
        SysUser currentUser = currentUserService.getCurrentUser();
        String usertype = currentUser.getUserType();
        String sql = null;
        if ("1".equals(roletype)) {//系统管理类角色
            if ("1".equals(usertype)) {//当前登录用户为超级管理员，则可以授权所有管理类用户(超管、行政管、机构管)
                sql = "select * from sysuser where (usertype ='1' or USERTYPE = '2' or USERTYPE = '3')";
            } else if ("2".equals(usertype)) {
                //当前登录用户为行政区划管理员,则可以授权这个行政区划下所有管理类用户
                Long areaid=currentUser.getAreaId();
                sql=
                        "select * from sysuser where (usertype ='1' or USERTYPE = '2' or USERTYPE = '3') and " +
                                "areaid='"+areaid+"'";
            } else if ("3".equals(usertype)) {
                //当前登录用户为机构管理员,则可以授权这个机构下所有管理类用户
                Long orgid=currentUser.getOrgId();
                sql=
                        "select * from sysuser where (usertype ='1' or USERTYPE = '2' or USERTYPE = '3') and orgid='"+orgid+"'";

            } else if ("5".equals(usertype)) {//当前登录用户为安全管理员，则权限与超级管理员一致
                sql = "select * from sysuser where (usertype ='1' or USERTYPE = '2' or USERTYPE = '3')";
            } else if ("4".equals(usertype) || "6".equals(usertype)) {//当前登录用户为业务操作员,审计管理员,不可以授权任何用户
                PageInfo<UserDTO> pageInfo = new PageInfo<UserDTO>();
                pageInfo.setTotal(Long.parseLong("0"));
                pageInfo.setData(null);
                return pageInfo;
            }
        } else if ("2".equals(roletype)) {//业务操作类角色
            if ("1".equals(usertype)) {//当前登录用户为超级管理员，则可以授权所有业务操作员类用户
                sql = "select * from sysuser where usertype ='4'";
            } else if ("2".equals(usertype)) {
                //当前登录用户为行政区划管理员，则可以授权这个行政区划下所有业务操作员类用户
                Long areaid=currentUser.getAreaId();
                sql="select * from sysuser where usertype ='4' and areaid='"+areaid+"'";
            } else if ("3".equals(usertype)) {
                //当前登录用户为机构管理员，则可以授权这个机构下所有业务操作员类用户
                Long orgid=currentUser.getOrgId();
                sql="select * from sysuser where usertype ='4' and orgid='"+orgid+"'";

            } else if ("5".equals(usertype)) {//当前登录用户为安全管理员，则权限与超级管理员一致
                sql = "select * from sysuser where usertype ='4'";
            } else if ("4".equals(usertype) || "6".equals(usertype)) {//当前登录用户为业务操作员,不可以授权任何用户
                PageInfo<UserDTO> pageInfo = new PageInfo<UserDTO>();
                pageInfo.setTotal(Long.parseLong("0"));
                pageInfo.setData(null);
                return pageInfo;
            }
        } else if ("3".equals(roletype)) {//安全管理员角色
            if ("1".equals(usertype)) {//当前登录用户为超级管理员，则可以授权所有业务操作员类用户
                sql = "select * from sysuser where usertype ='5'";
            } else if ("2".equals(usertype)) {
                //当前登录用户为行政区划管理员，则可以授权这个行政区划下所有业务操作员类用户
                Long areaid=currentUser.getAreaId();
                sql="select * from sysuser where usertype ='5' and areaid='"+areaid+"'";
            } else if ("3".equals(usertype)) {
                //当前登录用户为机构管理员，则可以授权这个机构下所有业务操作员类用户
                Long orgid=currentUser.getOrgId();
                sql="select * from sysuser where usertype ='5' and orgid='"+orgid+"'";

            } else if ("5".equals(usertype)) {//当前登录用户为安全管理员，则权限与超级管理员一致
                sql = "select * from sysuser where usertype ='5'";
            } else if ("4".equals(usertype) || "6".equals(usertype)) {//当前登录用户为业务操作员,不可以授权任何用户
                PageInfo<UserDTO> pageInfo = new PageInfo<UserDTO>();
                pageInfo.setTotal(Long.parseLong("0"));
                pageInfo.setData(null);
                return pageInfo;
            }
        }else if ("4".equals(roletype)) {//审计管理员角色
            if ("1".equals(usertype)) {//当前登录用户为超级管理员，则可以授权所有业务操作员类用户
                sql = "select * from sysuser where usertype ='6'";
            } else if ("2".equals(usertype)) {
                //当前登录用户为行政区划管理员，则可以授权这个行政区划下所有业务操作员类用户
                Long areaid=currentUser.getAreaId();
                sql="select * from sysuser where usertype ='6' and areaid='"+areaid+"'";
            } else if ("3".equals(usertype)) {
                //当前登录用户为机构管理员，则可以授权这个机构下所有业务操作员类用户
                Long orgid=currentUser.getOrgId();
                sql="select * from sysuser where usertype ='6' and orgid='"+orgid+"'";

            } else if ("5".equals(usertype)) {//当前登录用户为安全管理员，则权限与超级管理员一致
                sql = "select * from sysuser where usertype ='6'";
            } else if ("4".equals(usertype) || "6".equals(usertype)) {//当前登录用户为业务操作员,不可以授权任何用户
                PageInfo<UserDTO> pageInfo = new PageInfo<UserDTO>();
                pageInfo.setTotal(Long.parseLong("0"));
                pageInfo.setData(null);
                return pageInfo;
            }
        } else {
            throw new AppException("用户类型异常！");
        }

        Map<String, Object> param = new HashMap<>();
        if (logonname != null && !"".equals(logonname)) {
            sql += " and logonname like :logonname";
            param.put("logonname", "%" + logonname + "%");
        }
        if (displayname != null && !"".equals(displayname)) {
            sql += " and displayname like :displayname";
            param.put("logonname", "%" + displayname + "%");
        }

        JdbcPageHelper pageHelper = new JdbcPageHelper(jdbcTemplate, page, size);
        PageInfo<UserDTO> pageInfo = pageHelper.queryPagination(sql, param, rs -> {
            UserDTO dto = new UserDTO();
            dto.setLogonName(rs.getString("logonname"));
            dto.setUserId(rs.getString("userid"));
            dto.setDisplayName(rs.getString("displayname"));
            dto.setOrgId(Long.parseLong(rs.getString("orgid")));
            return dto;
        });
        return pageInfo;
    }

    @Override
    public List<UserDTO> queryGrantedUser(String roleId) {
        List<String> params = new ArrayList<>();
        String sql = null;
        SysUser currentUser = currentUserService.getCurrentUser();
        String usertype = currentUser.getUserType();
        Long orgid=currentUser.getOrgId();
        Long areaid=currentUser.getAreaId();
        if ("1".equals(usertype)) {//当前登录用户为超级管理员，则可以查询改角色授权的所有用户
            sql = "select su.* from sysuserrole sur,sysuser su where sur.userid=su.userid and sur.roleid=?";
        } else if ("5".equals(usertype)) {//若是安全操作员，与超管权限一致
            sql = "select su.* from sysuserrole sur,sysuser su where sur.userid=su.userid and sur.roleid=?";
        } else if ("3".equals(usertype)) {//若是机构管理员
            sql = "select su.* from sysuserrole sur,sysuser su where sur.userid=su.userid and su.orgid='" + orgid +
                    "'" + "and sur.roleid=?";
        } else if ("2".equals(usertype)) {//若是行政区操作员
            sql = "select su.* from sysuserrole sur,sysuser su where sur.userid=su.userid and su.areaid='" + areaid +
                    "'" + "and sur.roleid=?";
        } else if ("4".equals(usertype)) {//若是业务操作员，则没有权限
            throw new AppException("业务操作人员没权限查看角色列表");
        } else if ("6".equals(usertype)) {//若是审计管理员，则没有权限
            throw new AppException("审计管理人员没权限查看角色列表");
        } else {
            throw new AppException("用户类型异常！");
        }
        params.add(roleId);
        List<UserDTO> list = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, i) -> {
            UserDTO dto = new UserDTO();
            dto.setUserId(rs.getString("userid"));
            dto.setLogonName(rs.getString("logonname"));
            dto.setDisplayName(rs.getString("displayname"));
            dto.setOrgId(Long.parseLong(rs.getString("orgid")));
            return dto;
        });
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeGrantUserRole(String roleid, String userid) {
        sysUserRoleRepository.removeGrantUserRole(roleid, userid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGrantUser(JSONObject jsonObject) {
        String roleid = (String) jsonObject.get("roleId");
        JSONArray jsonArray = jsonObject.getJSONArray("tableDataGranted");
        List<String> params = new ArrayList<>();
        String sql = "select userid from sysuserrole where roleid=?";
        params.add(roleid);
        List<String> list = jdbcTemplate.query(sql, params.toArray(), (rs, i) -> {
            String userid = rs.getString("userid");
            return userid;
        });

        JSONObject jsonObject1 = null;
        String userid = null;
        List<String> listUser = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonObject1 = jsonArray.getJSONObject(i);
            userid = jsonObject1.getString("userId");
            listUser.add(userid);
            if (list.contains(userid)) {
                listUser.remove(userid);
            }
        }
        SysUserRole sysUserRole = null;
        for (int i = 0; i < listUser.size(); i++) {
            sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(roleid);
            sysUserRole.setUserId(listUser.get(i));
            sysUserRoleRepository.save(sysUserRole);
        }

        // 清空所有用户缓存
        sysCacheService.clearUserCache(null);

    }


}
