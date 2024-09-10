package com.insigma.sys.service.impl;

import com.insigma.framework.db.JdbcPageHelper;
import com.insigma.framework.db.PageInfo;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.common.SysManageMode;
import com.insigma.sys.dto.SysOperateLogDTO;
import com.insigma.sys.entity.SysRole;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.RoleService;
import com.insigma.sys.service.SysOperateLogService;
import com.insigma.web.support.util.SysFunctionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SysOperateLogServiceImpl implements SysOperateLogService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    RoleService roleService;
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private SysManageMode sysManageMode;
    @Value("${insiis.log.show.all:true}")
    private Boolean logShowAll;
    @Override
    public PageInfo<SysOperateLogDTO> querySysOperateLogList(SysOperateLogDTO queryDTO, Integer page, Integer size) throws SQLException {
        Map<String, Object> params = new HashMap<>();
        SysUser sysUser = currentUserService.getCurrentUser();
        String userId = sysUser.getUserId();
        List<SysRole> roleList = roleService.queryRoleByUserId(userId);
        StringBuffer sql = new StringBuffer("select dm.* from (select db.*, sy.title\n" +
                "  from (select dp.*, up.usertype, up.logonname, up.displayname, up.orgid\n" +
                "          from sysoperatelog dp left join sysuser up \n" +
                "          on dp.userid = up.userid) db\n" +
                "  left join sysfunction sy\n" +
                "    on db.functionid = sy.functionid) dm where 1=1 ");
        if (sysManageMode.isTripleMode() == true && "1".equals(currentUserService.getCurrentUser().getUserType())){
            PageInfo<SysOperateLogDTO> pageInfo =new PageInfo<SysOperateLogDTO>();
            pageInfo.setTotal(Long.parseLong("0"));
            pageInfo.setData(null);
            return pageInfo;//系统管理员启用三员制 无法查看数据
        }
        if (sysManageMode.isTripleMode() == true){//启用三员制
            // 当登录用户的角色为安全管理员
            if (roleList.stream().anyMatch(role -> role.getRoletype().equals("3"))) {
                sql.append(" and (dm.usertype = '6' or dm.usertype = '4' )");
            }
            // 当登录用户的角色为审计管理员
            if (roleList.stream().anyMatch(role -> role.getRoletype().equals("4"))) {
                sql.append(" and (dm.usertype = '1' or dm.usertype = '5' )");
            }

        } else {
            if (!logShowAll) {
                if ("2".equals(sysUser.getUserType())) {
                    // 行政区管理员
                    sql.append(" and exists (select 1 from sysorg so where so.orgid=dm.orgid and so.regioncode='" + sysUser.getAreaId() + "')");
                } else if ("3".equals(sysUser.getUserType())) {
                    // 机构管理员
                    sql.append(" and dm.orgid='" + sysUser.getOrgId() + "' ");
                } else if ("4".equals(sysUser.getUserType())) {
                    // 业务操作员，无可查看数据
                    PageInfo<SysOperateLogDTO> pageInfo = new PageInfo<>();
                    pageInfo.setTotal(Long.parseLong("0"));
                    pageInfo.setData(new ArrayList<>());
                    return pageInfo;
                }
            }
        }
        if (!ObjectUtils.isEmpty(queryDTO.getLogonname())) {
            sql.append(" and dm.logonname like :logonname");
            params.put("logonname",  "%" + queryDTO.getLogonname() + "%");
        }
        if (!ObjectUtils.isEmpty(queryDTO.getDisplayname())) {
            sql.append(" and dm.displayname like :displayname");
            params.put("displayname",  "%" + queryDTO.getDisplayname() + "%");
        }
        if (queryDTO.getOpDate()!=null&&!"".equals(queryDTO.getOpDate())){
            String [] opDate = queryDTO.getOpDate().split(",");
            sql.append(" and dm.begintime between :startTime and :endTime");
            params.put("startTime", Timestamp.valueOf(opDate[0]));
            params.put("endTime", Timestamp.valueOf(opDate[1]));

        }
        sql.append(" and dm.begintime is not null order by dm.begintime desc");
        JdbcPageHelper pageHelper = new JdbcPageHelper(jdbcTemplate, page, size,"grid", SysFunctionManager.getFunctionId());
        PageInfo<SysOperateLogDTO> pageInfo = pageHelper.queryPagination(sql.toString(), params,SysOperateLogDTO.class);
        return pageInfo;
    }
}
