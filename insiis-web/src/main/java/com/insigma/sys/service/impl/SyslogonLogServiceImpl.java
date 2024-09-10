package com.insigma.sys.service.impl;

import com.insigma.framework.db.JdbcPageHelper;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.web.securities.entity.SysLogonLog;
import com.insigma.framework.web.securities.repository.SysLogonLogRepository;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.common.SysManageMode;
import com.insigma.sys.dto.SysLogonLogDTO;
import com.insigma.sys.entity.SysRole;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.RoleService;
import com.insigma.sys.service.SyslogonLogService;
import com.insigma.web.support.util.SysFunctionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * fukq 2020/6/2
 */
@Slf4j
@Service
public class SyslogonLogServiceImpl implements SyslogonLogService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RoleService roleService;
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private SysManageMode sysManageMode;
    @Autowired
    private SysLogonLogRepository sysLogonLogRepository;
    @Value("${insiis.log.show.all:true}")
    private Boolean logShowAll;

    @Override
    public PageInfo<SysLogonLogDTO> querySysLogonList(SysLogonLogDTO queryDTO, Integer page, Integer size) throws SQLException {
        Map<String, Object> params = new HashMap<>();
        // 当前登录用户所拥有的角色
        SysUser sysUser = currentUserService.getCurrentUser();
        String userId = sysUser.getUserId();
        List<SysRole> roleList = roleService.queryRoleByUserId(userId);
        // List<SysUserRole> roleList = currentUserService.getCurrentUser().getSysUserRoleList();
        StringBuffer sql = new StringBuffer("select dp.*,up.usertype,up.logonname,up.displayname from syslogonlog dp left join " +
                "sysuser up on dp.userid = up.userid where 1=1 ");
        if (sysManageMode.isTripleMode() == true && "1".equals(currentUserService.getCurrentUser().getUserType())){
            PageInfo<SysLogonLogDTO> pageInfo =new PageInfo<SysLogonLogDTO>();
            pageInfo.setTotal(Long.parseLong("0"));
            pageInfo.setData(new ArrayList<>());

            return pageInfo;
        }
        if (sysManageMode.isTripleMode() == true) {//启用三员制
            // 当登录用户的角色为安全管理员
            if (roleList.stream().anyMatch(role -> role.getRoletype().equals("3"))) {
                sql.append("and (up.usertype = '6' or up.usertype = '4' )");
            }
            // 当登录用户的角色为审计管理员
            if (roleList.stream().anyMatch(role -> role.getRoletype().equals("4"))) {
                sql.append("and (up.usertype = '1' or up.usertype = '5' )");
            }
            // sql.append("and up.usertype = :usertype");
            // params.put("usertype", currentUserService.getCurrentUser().getUserType());
        } else {
            if (!logShowAll) {
                if ("2".equals(sysUser.getUserType())) {
                    // 行政区管理员
                    sql.append(" and exists (select 1 from sysorg so where so.orgid=up.orgid and so.regioncode='" + sysUser.getAreaId() + "')");
                } else if ("3".equals(sysUser.getUserType())) {
                    // 机构管理员
                    sql.append(" and up.orgid='" + sysUser.getOrgId() + "' ");
                } else if ("4".equals(sysUser.getUserType())) {
                    // 业务操作员，无可查看数据
                    PageInfo<SysLogonLogDTO> pageInfo = new PageInfo<SysLogonLogDTO>();
                    pageInfo.setTotal(Long.parseLong("0"));
                    pageInfo.setData(new ArrayList<>());
                    return pageInfo;
                }
            }
        }
        if (!ObjectUtils.isEmpty(queryDTO.getLogonname())) {
                sql.append(" and up.logonname like :logonname");
                params.put("logonname", "%" + queryDTO.getLogonname() + "%");
          }
        if (!ObjectUtils.isEmpty(queryDTO.getDisplayname())) {
            sql.append(" and up.displayname like :displayname");
            params.put("displayname", "%" + queryDTO.getDisplayname() + "%");
        }
        if (queryDTO.getOpDate()!=null&&!"".equals(queryDTO.getOpDate())){
            String [] opDate = queryDTO.getOpDate().split(",");
            SimpleDateFormat st =new SimpleDateFormat("yyyy-MM-dd");
            sql.append(" and to_date(to_char(dp.logontime,'YYYY-MM-DD'),'YYYY-MM-DD') between to_date(:startTime,'YYYY-MM-DD') and to_date(:endTime,'YYYY-MM-DD')");
            try {
                params.put("startTime",st.format(st.parse(opDate[0])));
                params.put("endTime",st.format(st.parse(opDate[1])));
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }

        }
            sql.append(" order by dp.logontime desc");
            JdbcPageHelper pageHelper = new JdbcPageHelper(jdbcTemplate, page, size, "grid", SysFunctionManager.getFunctionId());
            PageInfo<SysLogonLogDTO> pageInfo = pageHelper.queryPagination(sql.toString(), params, rs -> {
                SysLogonLogDTO dto = new SysLogonLogDTO();
                dto.setUserid(rs.getString("userid"));
                String logonname = rs.getString("logonname");
                if (ObjectUtils.isEmpty(logonname) && dto.getUserid().startsWith("NE:")) {
                    dto.setLogonname(dto.getUserid().substring(3));
                } else {
                    dto.setLogonname(logonname);
                }
                dto.setDisplayname(rs.getString("displayname"));
                dto.setLogonip(rs.getString("logonip"));
                dto.setSessionid(rs.getString("sessionid"));
                dto.setFailreason(rs.getString("failreason"));
                dto.setLogontime(rs.getTimestamp("logontime"));
                dto.setLogofftime(rs.getTimestamp("logofftime"));
                dto.setLogoffreason(rs.getString("logoffreason"));
                dto.setSuccessflag(rs.getString("successflag"));
                dto.setBrowser(rs.getString("browser"));
                dto.setOs(rs.getString("os"));
                return dto;
            });
            return pageInfo;
        }

    @Override
    public List<SysLogonLog> getLastLoginTime(String userId) {
        return sysLogonLogRepository.findListByPage(userId, PageRequest.of(0, 2));
//        StringBuffer sql = new StringBuffer("select * from ( SELECT s.* FROM SYSLOGONLOG s where s.USERID=? and s.SUCCESSFLAG = '1' ORDER BY s.LOGONTIME desc) where rownum<=2");
//        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql.toString(), new Object[]{userId});
//        return maps;
    }
}


