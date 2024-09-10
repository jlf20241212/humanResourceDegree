package com.insigma.sys.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.insigma.framework.db.util.DBUtil;
import com.insigma.framework.util.SysUtils;
import com.insigma.sys.entity.SysIdMapping;
import com.insigma.sys.entity.SysOrg;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.entity.SysUserArea;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UService {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserAreaService sysUserAreaService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SysIdMappingService sysIdMappingService;
    @Autowired
    private SysOrgService sysOrgService;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @PersistenceContext
    private EntityManager em;
    @Transactional(rollbackFor = Exception.class)
    public void syncuser(String data){
        JSONArray jsonArray = (JSONArray) JSONArray.parse(data, Feature.IgnoreNotMatch);
        List<Map<String, Object>> paramList = new ArrayList<>();//存放新增
        List<Map<String,Object>> paramList1=new ArrayList<>();//存放修改数据
        JSONObject jsonObject=null;

        if(sysUserService== null){
            sysUserService = SysUtils.getBean(SysUserService.class);
        }
        String insertSql="insert into sysuser (USERID, LOGONNAME, PASSWD, DISPLAYNAME, USERSTATE, USERTYPE, CARDID, TEL, MOBILE, EMAIL, USERADDR,CREATORID, CREATETIME, DEPARTMENT, CARDTYPE)" +
                " values (:USERID,:LOGONNAME,:PASSWD,:DISPLAYNAME,:USERSTATE,:USERTYPE,:CARDID,:TEL,:MOBILE,:EMAIL,:USERADDR,:CREATORID,:CREATETIME,:DEPARTMENT,:CARDTYPE)";
        String updateSql="update sysuser set LOGONNAME=:LOGONNAME,PASSWD=:PASSWD,DISPLAYNAME=:DISPLAYNAME,USERSTATE=:USERSTATE,USERTYPE=:USERTYPE,CARDID=:CARDID,TEL=:TEL,MOBILE=:MOBILE,EMAIL=:EMAIL,USERADDR=:USERADDR,CREATORID=:CREATORID,CREATETIME=:CREATETIME,DEPARTMENT=:DEPARTMENT,CARDTYPE=:CARDTYPE where USERID=:USERID";
        for(int i=0;i<jsonArray.size();i++){
            Map<String, Object> param=new HashMap<>();//存放新增
            Map<String, Object> param1=new HashMap<>();//存放修改
            SysUser sysUser=new SysUser();
            jsonObject=JSONObject.parseObject(jsonArray.get(i).toString());
            if("admin".equals(jsonObject.getString("LOGINNAME"))) {
                continue;
            }
            sysUser.setUserId(jsonObject.getString("USERID"));
            SysIdMapping useridMapping = sysIdMappingService.queryByTid(sysUser.getUserId());
            if(null==jsonObject.getString("LOGINNAME")|| "".equals(jsonObject.getString("LOGINNAME")) || "null".equals(jsonObject.getString("LOGINNAME"))){
                sysUser.setLogonName("");
            }else {
                sysUser.setLogonName(jsonObject.getString("LOGINNAME"));
            }
            if(null==jsonObject.getString("PASSWD")|| "".equals(jsonObject.getString("PASSWD")) || "null".equals(jsonObject.getString("PASSWD"))){
                sysUser.setPassWD("");
            }else {
                sysUser.setPassWD(jsonObject.getString("PASSWD"));
            }
            if(null==jsonObject.getString("USERNAME")|| "".equals(jsonObject.getString("USERNAME")) || "null".equals(jsonObject.getString("USERNAME"))){
                sysUser.setDisplayName("");
            }else {
                sysUser.setDisplayName(jsonObject.getString("USERNAME"));
            }
            if(null==jsonObject.getString("USEFUL")|| "".equals(jsonObject.getString("USEFUL")) || "null".equals(jsonObject.getString("USEFUL"))){
                sysUser.setUserState("");
            }else {
                sysUser.setUserState(jsonObject.getString("USEFUL"));
            }
            if(null==jsonObject.getString("ISLEADER")|| "".equals(jsonObject.getString("ISLEADER")) || "null".equals(jsonObject.getString("ISLEADER"))){
                sysUser.setUserType("");
            }else {
                String isLeader = jsonObject.getString("ISLEADER");
                if("1".equals(isLeader)) {
                    isLeader = "3";
                } else {
                    isLeader = "4";
                }
                sysUser.setUserType(isLeader);
            }
            if(null==jsonObject.getString("IDCARD")|| "".equals(jsonObject.getString("IDCARD")) || "null".equals(jsonObject.getString("IDCARD"))){
                sysUser.setCardId("");
            }else {
                sysUser.setCardId(jsonObject.getString("IDCARD"));
            }
            if(null==jsonObject.getString("OFFICEPHONE")|| "".equals(jsonObject.getString("OFFICEPHONE")) || "null".equals(jsonObject.getString("OFFICEPHONE"))){
                sysUser.setTel("");
            }else {
                sysUser.setTel(jsonObject.getString("OFFICEPHONE"));
            }
            if(null==jsonObject.getString("PHONE")|| "".equals(jsonObject.getString("PHONE")) || "null".equals(jsonObject.getString("PHONE"))){
                sysUser.setMobile("");
            }else {
                sysUser.setTel(jsonObject.getString("PHONE"));
                sysUser.setMobile(jsonObject.getString("PHONE"));
            }
            if(null==jsonObject.getString("EMAIL")|| "".equals(jsonObject.getString("EMAIL")) || "null".equals(jsonObject.getString("EMAIL"))){
                sysUser.setEMail("");
            }else {
                sysUser.setEMail(jsonObject.getString("EMAIL"));
            }
            if(null==jsonObject.getString("MACADDR")|| "".equals(jsonObject.getString("MACADDR")) || "null".equals(jsonObject.getString("MACADDR"))){
                sysUser.setUserAddr("");
            }else {
                sysUser.setUserAddr(jsonObject.getString("MACADDR"));
            }
            if(null==jsonObject.getString("OWNER")|| "".equals(jsonObject.getString("OWNER")) || "null".equals(jsonObject.getString("OWNER"))){
                sysUser.setCreatorId("");
            }else {
                sysUser.setCreatorId(jsonObject.getString("OWNER"));
            }
            if(null==jsonObject.getString("CREATEDATE")|| "".equals(jsonObject.getString("CREATEDATE")) || "null".equals(jsonObject.getString("CREATEDATE"))){
                sysUser.setCreateTime(null);
            }else {
                sysUser.setCreateTime(new java.sql.Date(Long.valueOf(jsonObject.getString("CREATEDATE"))));
            }
            if(null==jsonObject.getString("DEPT")|| "".equals(jsonObject.getString("DEPT")) || "null".equals(jsonObject.getString("DEPT"))){
                sysUser.setDepartment("");
            }else {
                sysUser.setDepartment(jsonObject.getString("DEPT"));
            }
            SysUser user=sysUserService.queryOneUser(sysUser.getUserId());
            //jdbcTemplate.execute();
            String sql="";
            if (user!=null){
                //修改
                param1.put("USERID",sysUser.getUserId());
                param1.put("LOGONNAME",sysUser.getLogonName());
                param1.put("PASSWD",sysUser.getPassWD());
                param1.put("DISPLAYNAME",sysUser.getDisplayName());
                param1.put("USERSTATE",sysUser.getUserState());
                param1.put("USERTYPE",sysUser.getUserType());
                param1.put("CARDID",sysUser.getCardId());
                param1.put("TEL",sysUser.getTel());
                param1.put("MOBILE",sysUser.getMobile());
                param1.put("EMAIL",sysUser.getEMail());
                param1.put("USERADDR",sysUser.getUserAddr());
                param1.put("CREATORID",sysUser.getCreatorId());
                param1.put("CREATETIME",sysUser.getCreateTime());
                param1.put("DEPARTMENT",sysUser.getDepartment());
                param1.put("CARDTYPE","1");
                paramList1.add(param1);
                if(i%1000==0){
                    namedParameterJdbcTemplate.batchUpdate(updateSql,SqlParameterSourceUtils.createBatch(paramList1));
                    paramList1=new ArrayList<>();
                }
            }else {
                //新增
                param.put("USERID", sysUser.getUserId());
                param.put("LOGONNAME", sysUser.getLogonName());
                param.put("PASSWD", sysUser.getPassWD());
                param.put("DISPLAYNAME", sysUser.getDisplayName());
                param.put("USERSTATE", sysUser.getUserState());
                param.put("USERTYPE", sysUser.getUserType());
                param.put("CARDID", sysUser.getCardId());
                param.put("TEL", sysUser.getTel());
                param.put("MOBILE", sysUser.getMobile());
                param.put("EMAIL", sysUser.getEMail());
                param.put("USERADDR", sysUser.getUserAddr());
                param.put("CREATORID", sysUser.getCreatorId());
                param.put("CREATETIME", sysUser.getCreateTime());
                param.put("DEPARTMENT", sysUser.getDepartment());
                param.put("CARDTYPE", "1");
                paramList.add(param);
                if (i % 1000 == 0) {
                    namedParameterJdbcTemplate.batchUpdate(insertSql, SqlParameterSourceUtils.createBatch(paramList));
                    paramList = new ArrayList<>();
                }
            }
        }
        namedParameterJdbcTemplate.batchUpdate(updateSql,SqlParameterSourceUtils.createBatch(paramList1));
        namedParameterJdbcTemplate.batchUpdate(insertSql, SqlParameterSourceUtils.createBatch(paramList));
    }
    @Transactional(rollbackFor = Exception.class)
    public void syncuserarea(String data){
        JSONArray jsonArray = (JSONArray) JSONArray.parse(data,Feature.IgnoreNotMatch);
        JSONObject jsonObject=null;
        List<Map<String, Object>> paramList = new ArrayList<>();//存放新增
        String insertSql="insert into SYSUSERAREA (USERID, AAB301) values (:USERID,:AAB301)";
        //List<SysOrg> list=new ArrayList<>();
        //HashMap<String,SysIdMapping> tidMap = new HashMap<>();
        for(int i=0;i<jsonArray.size();i++) {
            Map<String, Object> param = new HashMap<>();//存放新增
            SysUserArea sysUserArea = new SysUserArea();
            jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
            //sysOrg.setOrgid(Long.valueOf(jsonObject.getString("GROUPID")));
            if (null == jsonObject.getString("USERID") || "".equals(jsonObject.getString("USERID")) || "null".equals(jsonObject.getString("USERID"))) {
                sysUserArea.setUserId("");
            } else {
                sysUserArea.setUserId(jsonObject.getString("USERID"));
            }
            if (null == jsonObject.getString("DATAGROUPID") || "".equals(jsonObject.getString("DATAGROUPID")) || "null".equals(jsonObject.getString("DATAGROUPID"))) {
                sysUserArea.setAab301("");
            } else {
                sysUserArea.setAab301(jsonObject.getString("DATAGROUPID"));
            }
            List<SysUserArea> sysUserAreas = sysUserAreaService.findUserAreaByUserid(jsonObject.getString("USERID"));
            if (sysUserAreas.size() > 0) {
                //在表中能查到数据先删除再添加
                sysUserAreaService.deleteAllByUserid(jsonObject.getString("USERID"));
            }
            //如果两id关联表里面通过同步的groupid查SELFID
            SysIdMapping sysIdMapping = sysIdMappingService.queryByTid(jsonObject.getString("DATAGROUPID"));
            if (sysIdMapping != null) {
                sysUserArea.setAab301(sysIdMapping.getSELFID().toString());
                param.put("USERID", sysUserArea.getUserId());
                param.put("AAB301", sysUserArea.getAab301());
                paramList.add(param);
//                if (i%1000==0) {
//                    namedParameterJdbcTemplate.batchUpdate(insertSql, SqlParameterSourceUtils.createBatch(paramList));
//                    paramList = new ArrayList<>();
//                }
//                log.info("orgid={},userid={}", sysIdMapping.getSELFID().longValue(), sysUserArea.getUserId());
            }
//                            if (i%1000==0) {
//                    namedParameterJdbcTemplate.batchUpdate(insertSql, SqlParameterSourceUtils.createBatch(paramList));
//                    paramList = new ArrayList<>();
//                }
        }
        namedParameterJdbcTemplate.batchUpdate(insertSql, SqlParameterSourceUtils.createBatch(paramList));
    }
    @Transactional(rollbackFor = Exception.class)
    public void syncgroup(String data){
        JSONArray jsonArray = (JSONArray) JSONArray.parse(data,Feature.IgnoreNotMatch);
        JSONObject jsonObject=null;
        List<Map<String, Object>> paramList = new ArrayList<>();//存放新增
        String insertSql="insert into sysorg (ORGID, ORGNAME, ORGENTERCODE, REGIONCODE, PARENTID, SHORTNAME, LINKMAN, TEL, ORGADDR, ORGDESC,IDPATH)\n" +
                "values (:ORGID,:ORGNAME,:ORGENTERCODE, :REGIONCODE, :PARENTID,:SHORTNAME,:LINKMAN,:TEL,:ORGADDR,:ORGDESC,:IDPATH)";
        //List<SysOrg> list=new ArrayList<>();
        //HashMap<String,SysIdMapping> tidMap = new HashMap<>();
        for(int i=0;i<jsonArray.size();i++){
            Map<String, Object> param=new HashMap<>();//存放新增
            SysOrg sysOrg=new SysOrg();
            jsonObject=JSONObject.parseObject(jsonArray.get(i).toString());
            //sysOrg.setOrgid(Long.valueOf(jsonObject.getString("GROUPID")));
            if(null==jsonObject.getString("NAME")|| "".equals(jsonObject.getString("NAME")) || "null".equals(jsonObject.getString("NAME"))){
                sysOrg.setOrgname("");
            }else {
                sysOrg.setOrgname(jsonObject.getString("NAME"));
            }
            String orgentercode="";
            String str=jsonObject.getString("GROUPID");
            for(int j=0;j<str.length();j++) {
                if (str.charAt(j) >= 48 && str.charAt(j) <= 57) {
                    orgentercode += str.charAt(j);
                    if (orgentercode.length()==6){
                        break;
                    }
                }
            }
            if (orgentercode.length()<6) {
                for (int k = 0; orgentercode.length() < 6; k++) {
                    orgentercode += 0;
                }
            }
            String parentid = jsonObject.getString("PARENTID");
            if(null==jsonObject.getString("SHORTNAME")|| "".equals(jsonObject.getString("SHORTNAME")) || "null".equals(jsonObject.getString("SHORTNAME"))){
                sysOrg.setShortname("");
            }else {
                sysOrg.setShortname(jsonObject.getString("SHORTNAME"));
            }
            if(null==jsonObject.getString("LINKMAN")|| "".equals(jsonObject.getString("LINKMAN")) || "null".equals(jsonObject.getString("LINKMAN"))){
                sysOrg.setLinkman("");
            }else {
                sysOrg.setLinkman(jsonObject.getString("LINKMAN"));
            }
            if(null==jsonObject.getString("TEL")|| "".equals(jsonObject.getString("TEL")) || "null".equals(jsonObject.getString("TEL"))){
                sysOrg.setTel("");
            }else {
                sysOrg.setTel(jsonObject.getString("TEL"));
            }
            if(null==jsonObject.getString("ADDRESS")|| "".equals(jsonObject.getString("ADDRESS")) || "null".equals(jsonObject.getString("ADDRESS"))){
                sysOrg.setOrgaddr("");
            }else {
                sysOrg.setOrgaddr(jsonObject.getString("ADDRESS"));
            }
            if(null==jsonObject.getString("DESCRIPTION")|| "".equals(jsonObject.getString("DESCRIPTION")) || "null".equals(jsonObject.getString("DESCRIPTION"))){
                sysOrg.setOrgdesc("");
            }else {
                sysOrg.setOrgdesc(jsonObject.getString("DESCRIPTION"));
            }
            if(null==jsonObject.getString("STATUS")|| "".equals(jsonObject.getString("STATUS")) || "null".equals(jsonObject.getString("STATUS"))){
                sysOrg.setOrgstate("1");
            }else {
                sysOrg.setOrgstate(jsonObject.getString("STATUS"));
            }
            if(null==jsonObject.getString("OTHERINFO")|| "".equals(jsonObject.getString("OTHERINFO")) || "null".equals(jsonObject.getString("OTHERINFO"))){
                sysOrg.setIdpath("");
            }else {
                sysOrg.setIdpath(jsonObject.getString("OTHERINFO"));
            }
            if(null==jsonObject.getString("ORG")|| "".equals(jsonObject.getString("ORG")) || "null".equals(jsonObject.getString("ORG"))){
                sysOrg.setOrgentercode("");
            }else {
                sysOrg.setOrgentercode(jsonObject.getString("ORG"));
            }
            if(null==jsonObject.getString("DISTRICTCODE")|| "".equals(jsonObject.getString("DISTRICTCODE")) || "null".equals(jsonObject.getString("DISTRICTCODE"))){
                sysOrg.setRegioncode("");
            }else {
                sysOrg.setRegioncode(jsonObject.getString("DISTRICTCODE"));
            }
            if(null==jsonObject.getString("PRINCIPAL")|| "".equals(jsonObject.getString("PRINCIPAL")) || "null".equals(jsonObject.getString("PRINCIPAL"))){
                sysOrg.setLeader("");
            }else {
                sysOrg.setLeader(jsonObject.getString("PRINCIPAL"));
            }
            if(null==jsonObject.getString("CHARGEDEPT")|| "".equals(jsonObject.getString("CHARGEDEPT")) || "null".equals(jsonObject.getString("CHARGEDEPT"))){
                sysOrg.setSuperdept("");
            }else {
                sysOrg.setSuperdept(jsonObject.getString("CHARGEDEPT"));
            }
            if(null==jsonObject.getString("RATE")|| "".equals(jsonObject.getString("RATE")) || "null".equals(jsonObject.getString("RATE"))){
                sysOrg.setRate("");
            }else {
                sysOrg.setRate(jsonObject.getString("RATE"));
            }
            //如果两id关联表里面通过同步的groupid能查到=修改
            SysIdMapping sysIdMapping=sysIdMappingService.queryByTid(jsonObject.getString("GROUPID"));
            Long realParentid = null;
            if(null==parentid|| "".equals(parentid) || "null".equals(parentid)){
                //
            }else{
                SysIdMapping orgparentidmapping=sysIdMappingService.queryByTid(parentid);
//                SysIdMapping orgparentidmapping = tidMap.get(parentid);
//                if(orgparentidmapping == null){
//                    orgparentidmapping = sysIdMappingService.queryByTid(parentid);
//                    if(orgparentidmapping == null) {
//                        orgparentidmapping = new SysIdMapping();
//                        orgparentidmapping.setTID(parentid);
//                        sysIdMappingService.saveGroupidToOrgid(orgparentidmapping);
//                        tidMap.put(parentid,orgparentidmapping);
//                    }
//                }
                if (orgparentidmapping!=null){
                    realParentid = orgparentidmapping.getSELFID().longValue();
                }else {
                    realParentid= Long.valueOf(0);
                }
            }
            sysOrg.setParentid(realParentid);
//            if (sysIdMapping!=null){
            //修改
            sysOrg.setOrgid(sysIdMapping.getSELFID().longValue());
            //sysOrg.setOrgentercode(sysOrg.getOrgentercode()==null?"888888":sysOrg.getOrgentercode());
            transformOrgIdPath(sysOrg);
            if(sysOrgService.findByOrgid(sysOrg.getOrgid()) != null) {
                em.persist(em.merge(sysOrg));
                if (i % 1000 == 0) {
                    em.flush();
                    em.clear();
                }
            }else{
                //新增
                param.put("ORGID", sysOrg.getOrgid());
                param.put("ORGNAME",sysOrg.getOrgname());
                param.put("ORGENTERCODE",sysOrg.getOrgentercode());
                param.put("REGIONCODE", sysOrg.getRegioncode());
                param.put("PARENTID", sysOrg.getParentid());
                param.put("SHORTNAME",sysOrg.getShortname());
                param.put("LINKMAN",sysOrg.getLinkman());
                param.put("TEL", sysOrg.getTel());
                param.put("ORGADDR",sysOrg.getOrgaddr());
                param.put("ORGDESC",sysOrg.getOrgdesc());
                param.put("IDPATH", sysOrg.getIdpath());
                paramList.add(param);
                if (i % 1000 == 0) {
                    namedParameterJdbcTemplate.batchUpdate(insertSql, SqlParameterSourceUtils.createBatch(paramList));
                    paramList = new ArrayList<>();
                }
            }
            log.info("orgid={},save={}",sysIdMapping.getSELFID().longValue(),sysOrg.getOrgid());
//            }
//            else {
//                //新增
//                sysIdMapping = tidMap.get(jsonObject.getString("GROUPID"));
//                if(sysIdMapping == null) {
//                    SysIdMapping sysIdMapping1 = new SysIdMapping();
//                    sysIdMapping1.setTID(jsonObject.getString("GROUPID"));
//                    sysIdMappingService.saveGroupidToOrgid(sysIdMapping1);
//                    tidMap.put(sysIdMapping1.getTID(),sysIdMapping1);
//                    sysIdMapping = sysIdMapping1;
//                }
//                sysOrg.setOrgid(sysIdMapping.getSELFID().longValue());
//                transformOrgIdPath(sysOrg);
//                saveSysOrg(jdbcTemplate,sysOrg);
//            }
        }
        em.flush();
        em.clear();
        namedParameterJdbcTemplate.batchUpdate(insertSql, SqlParameterSourceUtils.createBatch(paramList));
    }

//    private void saveSysOrg(NamedParameterJdbcTemplate namedParameterJdbcTemplate,SysOrg sysOrg){
//
//
//
//
////        String sql="insert into sysorg (ORGID, ORGNAME, ORGENTERCODE, PARENTID, SHORTNAME, LINKMAN, TEL, ORGADDR, ORGDESC,IDPATH)\n" +
////                "values (?,?,?,?,?,?,?,?,?)"; //错的，ID path值不对
////        jdbcTemplate.execute(sql);
////        jdbcTemplate.update(sql,sysOrg.getOrgid(),sysOrg.getOrgname(),sysOrg.getOrgentercode()==null?"888888":sysOrg.getOrgentercode(),sysOrg.getParentid(),sysOrg.getShortname(),sysOrg.getLinkman(),sysOrg.getTel(),sysOrg.getOrgaddr(),sysOrg.getOrgdesc(),sysOrg.getIdpath());
//    }

    private void transformOrgIdPath(SysOrg org){
        if(org != null){
            if(!ObjectUtils.isEmpty(org.getIdpath())) {
                String[] ids = StringUtils.split(org.getIdpath(), "/");
                String newIdPath = Arrays.stream(ids).map((id) -> {
                    SysIdMapping idMapping = sysIdMappingService.queryByTid(id);
                    if (idMapping != null) {
                        return idMapping.getSELFID().toString();
                    } else {
                        log.error("找不到机构（{}）字符ID对应的数字ID", id);
                        return "0";
                    }
                }).collect(Collectors.joining("/"));
                org.setIdpath(newIdPath);
            }else{
                org.setIdpath(org.getOrgid().toString());
            }
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void syncgroupref(String data){
        JSONArray jsonArray = (JSONArray) JSONArray.parse(data,Feature.IgnoreNotMatch);
        JSONObject jsonObject=null;
        HashMap<String, Object> ret=null;
        //List<SysUser> list=new ArrayList<>();
        List<Map<String, Object>> paramList = new ArrayList<>();//存放用户新增
        List<Map<String,Object>> paramList1=new ArrayList<>();//存放组织新增
        String insertUserSql="insert into sysuser (USERID, ORGID, LOGONNAME,DISPLAYNAME,AREAID) values (:USERID,:ORGID,:LOGONNAME,:DISPLAYNAME,:AREAID)";
        String insertOrgSql = "insert into sysorg(ORGID,ORGNAME,ORGENTERCODE) values(:ORGID,:ORGNAME,:ORGENTERCODE)";
        for(int i=0;i<jsonArray.size();i++){
            Map<String, Object> param=new HashMap<>();//存放新增
            Map<String, Object> param1=new HashMap<>();//存放新增
            SysUser user=new SysUser();
            jsonObject=JSONObject.parseObject(jsonArray.get(i).toString());
            String userid=jsonObject.getString("USERID");
            SysUser sysUser=sysUserService.queryOneUser(userid);
            if (sysUser!=null&&sysUser.getUserId()!=null){
                //修改
                SysIdMapping sysIdMapping=sysIdMappingService.queryByTid(jsonObject.getString("GROUPID"));
                if (sysIdMapping==null){
                    continue;
                }
                sysUser.setOrgId(sysIdMapping.getSELFID().longValue());
                if(null==jsonObject.getString("DISTRICTCODE")|| "".equals(jsonObject.getString("DISTRICTCODE")) || "null".equals(jsonObject.getString("DISTRICTCODE"))){
                    //sysUser.setAreaId();
                }else {
                    sysUser.setAreaId(Long.parseLong(jsonObject.getString("DISTRICTCODE")));
                }

                em.persist(em.merge(sysUser));
                if (i % 1000 == 0) {
                    em.flush();
                    em.clear();
                }


            }else {
                //新增
                user.setUserId(userid);
                SysIdMapping sysIdMapping=sysIdMappingService.queryByTid(jsonObject.getString("GROUPID"));
                //if (sysIdMapping!=null){
                //在sysidmapping里面找到
                if (sysIdMapping==null){
                    continue;
                }
                user.setOrgId(sysIdMapping.getSELFID().longValue());
                // insertUserSql="insert into sysuser (USERID, ORGID, LOGONNAME,DISPLAYNAME) values (?,?,?,?)";
                //jdbcTemplate.update(sql,user.getUserId(),user.getOrgId(),"tuser","临时");
                if(null==jsonObject.getString("DISTRICTCODE")|| "".equals(jsonObject.getString("DISTRICTCODE")) || "null".equals(jsonObject.getString("DISTRICTCODE"))){
                    //sysUser.setAreaId();
                }else {
                    sysUser.setAreaId(Long.parseLong(jsonObject.getString("DISTRICTCODE")));
                }
                //新增
                param.put("USERID", user.getUserId());
                param.put("ORGID",user.getOrgId());
                param.put("LOGONNAME","tuser");
                param.put("DISPLAYNAME","临时");
                param.put("AREAID", user.getAreaId());
                paramList.add(param);
                if (i % 1000 == 0) {
                    namedParameterJdbcTemplate.batchUpdate(insertUserSql, SqlParameterSourceUtils.createBatch(paramList));
                    paramList = new ArrayList<>();
                }
                if(sysOrgService.findByOrgid(sysIdMapping.getSELFID().longValue()) == null) {
                    param1.put("ORGID", sysIdMapping.getSELFID().longValue());
                    param1.put("ORGNAME","临时");
                    param1.put("ORGENTERCODE","888888");
                    paramList1.add(param1);
                    if (i % 1000 == 0) {
                        namedParameterJdbcTemplate.batchUpdate(insertOrgSql, SqlParameterSourceUtils.createBatch(paramList1));
                        paramList1 = new ArrayList<>();
                    }
                }
//                }else {
//                    //在sysidmapping里面找不到
//                    SysIdMapping sysIdMapping1=new SysIdMapping();
//                    sysIdMapping1.setTID(jsonObject.getString("GROUPID"));
                //              sysIdMappingService.saveGroupidToOrgid(sysIdMapping1);
//                    user.setOrgId(sysIdMapping1.getSELFID().longValue());
//                    String sql="insert into sysuser (USERID, ORGID, LOGONNAME,DISPLAYNAME) values (?,?,?,?)";
//                    jdbcTemplate.update(sql,user.getUserId(),user.getOrgId(),"tuser","临时");
//
            }
        }
        em.flush();
        em.clear();
        namedParameterJdbcTemplate.batchUpdate(insertUserSql, SqlParameterSourceUtils.createBatch(paramList));
        namedParameterJdbcTemplate.batchUpdate(insertOrgSql, SqlParameterSourceUtils.createBatch(paramList1));
    }
    @Transactional(rollbackFor = Exception.class)
    public void syncidMapping(String data) throws SQLException {
        JSONArray jsonArray = (JSONArray) JSONArray.parse(data,Feature.IgnoreNotMatch);
        JSONObject jsonObject=null;
        HashMap<String, Object> ret=null;
        //List<SysIdMapping> list=new ArrayList<>();
        Date date2=null;
        for(int i=0;i<jsonArray.size();i++){
            jsonObject=JSONObject.parseObject(jsonArray.get(i).toString());
            SysIdMapping sysIdMapping=sysIdMappingService.queryByTid(jsonObject.getString("GROUPID"));
            if (sysIdMapping==null){
                //新增
                SysIdMapping sysIdMapping1=new SysIdMapping();
                sysIdMapping1.setTID(jsonObject.getString("GROUPID"));
                if(sysIdMapping1.getSELFID() == null || sysIdMapping1.getSELFID().intValue() == 0){
                    Long selfid = DBUtil.getSequence("sys_idmapping");
//                    Long selfid = jdbcTemplate.queryForObject("select SYS_IDMAPPING.nextval from dual",Long.class);
                    sysIdMapping1.setSELFID(selfid.intValue());
                }
                em.persist(sysIdMapping1);
            }
            if (i % 1000 == 0) {
                em.flush();
                em.clear();
            }
        }
        em.flush();
        em.clear();
    }
    /**
     * 全量同步用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncUserWithAll(String data, boolean flag){
        if(sysUserService== null){
            sysUserService = SysUtils.getBean(SysUserService.class);
        }
        //删除所有用户（除admin）
        if (flag) { // 不分批次时，删除表数据
            sysUserService.deleteAllUserExitsAdmin();
        }
        JSONArray jsonArray = (JSONArray) JSONArray.parse(data, Feature.IgnoreNotMatch);
        List<Map<String, Object>> paramList = new ArrayList<>();//存放新增
        JSONObject jsonObject=null;
        String insertSql="insert into sysuser (USERID, LOGONNAME, PASSWD, DISPLAYNAME, USERSTATE, USERTYPE, CARDID, TEL, MOBILE, EMAIL, USERADDR,CREATORID, CREATETIME, DEPARTMENT, CARDTYPE)" +
                " values (:USERID,:LOGONNAME,:PASSWD,:DISPLAYNAME,:USERSTATE,:USERTYPE,:CARDID,:TEL,:MOBILE,:EMAIL,:USERADDR,:CREATORID,:CREATETIME,:DEPARTMENT,:CARDTYPE)";
        for(int i=0;i<jsonArray.size();i++){
            Map<String, Object> param=new HashMap<>();//存放新增
            SysUser sysUser=new SysUser();
            jsonObject=JSONObject.parseObject(jsonArray.get(i).toString());
            if("admin".equals(jsonObject.getString("LOGINNAME"))) {
                continue;
            }
            sysUser.setUserId(jsonObject.getString("USERID"));
            if(null==jsonObject.getString("LOGINNAME")|| "".equals(jsonObject.getString("LOGINNAME")) || "null".equals(jsonObject.getString("LOGINNAME"))){
                sysUser.setLogonName("");
            }else {
                sysUser.setLogonName(jsonObject.getString("LOGINNAME"));
            }
            if(null==jsonObject.getString("PASSWD")|| "".equals(jsonObject.getString("PASSWD")) || "null".equals(jsonObject.getString("PASSWD"))){
                sysUser.setPassWD("");
            }else {
                sysUser.setPassWD(jsonObject.getString("PASSWD"));
            }
            if(null==jsonObject.getString("USERNAME")|| "".equals(jsonObject.getString("USERNAME")) || "null".equals(jsonObject.getString("USERNAME"))){
                sysUser.setDisplayName("");
            }else {
                sysUser.setDisplayName(jsonObject.getString("USERNAME"));
            }
            if(null==jsonObject.getString("USEFUL")|| "".equals(jsonObject.getString("USEFUL")) || "null".equals(jsonObject.getString("USEFUL"))){
                sysUser.setUserState("");
            }else {
                sysUser.setUserState(jsonObject.getString("USEFUL"));
            }
            if(null==jsonObject.getString("ISLEADER")|| "".equals(jsonObject.getString("ISLEADER")) || "null".equals(jsonObject.getString("ISLEADER"))){
                sysUser.setUserType("");
            }else {
                String isLeader = jsonObject.getString("ISLEADER");
                if("1".equals(isLeader)) {
                    isLeader = "3";
                } else {
                    isLeader = "4";
                }
                sysUser.setUserType(isLeader);
            }
            if(null==jsonObject.getString("IDCARD")|| "".equals(jsonObject.getString("IDCARD")) || "null".equals(jsonObject.getString("IDCARD"))){
                sysUser.setCardId("");
            }else {
                sysUser.setCardId(jsonObject.getString("IDCARD"));
            }
            if(null==jsonObject.getString("OFFICEPHONE")|| "".equals(jsonObject.getString("OFFICEPHONE")) || "null".equals(jsonObject.getString("OFFICEPHONE"))){
                sysUser.setTel("");
            }else {
                sysUser.setTel(jsonObject.getString("OFFICEPHONE"));
            }
            if(null==jsonObject.getString("PHONE")|| "".equals(jsonObject.getString("PHONE")) || "null".equals(jsonObject.getString("PHONE"))){
                sysUser.setMobile("");
            }else {
                sysUser.setTel(jsonObject.getString("PHONE"));
                sysUser.setMobile(jsonObject.getString("PHONE"));
            }
            if(null==jsonObject.getString("EMAIL")|| "".equals(jsonObject.getString("EMAIL")) || "null".equals(jsonObject.getString("EMAIL"))){
                sysUser.setEMail("");
            }else {
                sysUser.setEMail(jsonObject.getString("EMAIL"));
            }
            if(null==jsonObject.getString("MACADDR")|| "".equals(jsonObject.getString("MACADDR")) || "null".equals(jsonObject.getString("MACADDR"))){
                sysUser.setUserAddr("");
            }else {
                sysUser.setUserAddr(jsonObject.getString("MACADDR"));
            }
            if(null==jsonObject.getString("OWNER")|| "".equals(jsonObject.getString("OWNER")) || "null".equals(jsonObject.getString("OWNER"))){
                sysUser.setCreatorId("");
            }else {
                sysUser.setCreatorId(jsonObject.getString("OWNER"));
            }
            if(null==jsonObject.getString("CREATEDATE")|| "".equals(jsonObject.getString("CREATEDATE")) || "null".equals(jsonObject.getString("CREATEDATE"))){
                sysUser.setCreateTime(null);
            }else {
                sysUser.setCreateTime(new java.sql.Date(Long.valueOf(jsonObject.getString("CREATEDATE"))));
            }
            if(null==jsonObject.getString("DEPT")|| "".equals(jsonObject.getString("DEPT")) || "null".equals(jsonObject.getString("DEPT"))){
                sysUser.setDepartment("");
            }else {
                sysUser.setDepartment(jsonObject.getString("DEPT"));
            }
                //新增
                param.put("USERID", sysUser.getUserId());
                param.put("LOGONNAME", sysUser.getLogonName());
                param.put("PASSWD", sysUser.getPassWD());
                param.put("DISPLAYNAME", sysUser.getDisplayName());
                param.put("USERSTATE", sysUser.getUserState());
                param.put("USERTYPE", sysUser.getUserType());
                param.put("CARDID", sysUser.getCardId());
                param.put("TEL", sysUser.getTel());
                param.put("MOBILE", sysUser.getMobile());
                param.put("EMAIL", sysUser.getEMail());
                param.put("USERADDR", sysUser.getUserAddr());
                param.put("CREATORID", sysUser.getCreatorId());
                param.put("CREATETIME", sysUser.getCreateTime());
                param.put("DEPARTMENT", sysUser.getDepartment());
                param.put("CARDTYPE", "1");
                paramList.add(param);
                if (i % 1000 == 0) {
                    namedParameterJdbcTemplate.batchUpdate(insertSql, SqlParameterSourceUtils.createBatch(paramList));
                    paramList = new ArrayList<>();
                }

        }
        namedParameterJdbcTemplate.batchUpdate(insertSql, SqlParameterSourceUtils.createBatch(paramList));
    }
    /**
     * 全量同步用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncIdMappingWithAll(String data) throws SQLException {
        sysIdMappingService.deleteAll();
        em.flush();
        JSONArray jsonArray = (JSONArray) JSONArray.parse(data,Feature.IgnoreNotMatch);
        JSONObject jsonObject=null;
        HashMap<String, Object> ret=null;
        Date date2=null;
        for(int i = 0; i < jsonArray.size(); i++){
            jsonObject = jsonArray.getJSONObject(i);
            SysIdMapping sysIdMapping1=new SysIdMapping();
            sysIdMapping1.setTID(jsonObject.getString("GROUPID"));
            if(sysIdMapping1.getSELFID() == null || sysIdMapping1.getSELFID().intValue() == 0){
                Long selfid = DBUtil.getSequence("sys_idmapping");
//                Long selfid = jdbcTemplate.queryForObject("select SYS_IDMAPPING.nextval from dual",Long.class);
                sysIdMapping1.setSELFID(selfid.intValue());
            }
            em.persist(sysIdMapping1);
            if (i % 1000 == 0) {
                em.flush();
                em.clear();
            }
        }
        em.flush();
        em.clear();
    }
    @Transactional(rollbackFor = Exception.class)
    public void syncgroupWithAll(String data){
        JSONArray jsonArray = (JSONArray) JSONArray.parse(data,Feature.IgnoreNotMatch);
        JSONObject jsonObject=null;
        List<Map<String, Object>> paramList = new ArrayList<>();//存放新增
        String insertSql="insert into sysorg (ORGID, ORGNAME, ORGENTERCODE, REGIONCODE, PARENTID, SHORTNAME, LINKMAN, TEL, ORGADDR, ORGDESC,IDPATH)\n" +
                "values (:ORGID,:ORGNAME,:ORGENTERCODE, :REGIONCODE, :PARENTID,:SHORTNAME,:LINKMAN,:TEL,:ORGADDR,:ORGDESC,:IDPATH)";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        List<Map<String,Object>> maps=namedParameterJdbcTemplate.queryForList("select t.tid,t.selfid from sysidmapping t",paramMap);
        for(int i=0;i<jsonArray.size();i++){
            Map<String, Object> param=new HashMap<>();//存放新增
            SysOrg sysOrg=new SysOrg();
            jsonObject=JSONObject.parseObject(jsonArray.get(i).toString());
            if(null==jsonObject.getString("NAME")|| "".equals(jsonObject.getString("NAME")) || "null".equals(jsonObject.getString("NAME"))){
                sysOrg.setOrgname("");
            }else {
                sysOrg.setOrgname(jsonObject.getString("NAME"));
            }
            String orgentercode="";
            String str=jsonObject.getString("GROUPID");
            for(int j=0;j<str.length();j++) {
                if (str.charAt(j) >= 48 && str.charAt(j) <= 57) {
                    orgentercode += str.charAt(j);
                    if (orgentercode.length()==6){
                        break;
                    }
                }
            }
            if (orgentercode.length()<6) {
                for (int k = 0; orgentercode.length() < 6; k++) {
                    orgentercode += 0;
                }
            }
            String parentid = jsonObject.getString("PARENTID");
            if(null==jsonObject.getString("SHORTNAME")|| "".equals(jsonObject.getString("SHORTNAME")) || "null".equals(jsonObject.getString("SHORTNAME"))){
                sysOrg.setShortname("");
            }else {
                sysOrg.setShortname(jsonObject.getString("SHORTNAME"));
            }
            if(null==jsonObject.getString("LINKMAN")|| "".equals(jsonObject.getString("LINKMAN")) || "null".equals(jsonObject.getString("LINKMAN"))){
                sysOrg.setLinkman("");
            }else {
                sysOrg.setLinkman(jsonObject.getString("LINKMAN"));
            }
            if(null==jsonObject.getString("TEL")|| "".equals(jsonObject.getString("TEL")) || "null".equals(jsonObject.getString("TEL"))){
                sysOrg.setTel("");
            }else {
                sysOrg.setTel(jsonObject.getString("TEL"));
            }
            if(null==jsonObject.getString("ADDRESS")|| "".equals(jsonObject.getString("ADDRESS")) || "null".equals(jsonObject.getString("ADDRESS"))){
                sysOrg.setOrgaddr("");
            }else {
                sysOrg.setOrgaddr(jsonObject.getString("ADDRESS"));
            }
            if(null==jsonObject.getString("DESCRIPTION")|| "".equals(jsonObject.getString("DESCRIPTION")) || "null".equals(jsonObject.getString("DESCRIPTION"))){
                sysOrg.setOrgdesc("");
            }else {
                sysOrg.setOrgdesc(jsonObject.getString("DESCRIPTION"));
            }
            if(null==jsonObject.getString("STATUS")|| "".equals(jsonObject.getString("STATUS")) || "null".equals(jsonObject.getString("STATUS"))){
                sysOrg.setOrgstate("1");
            }else {
                sysOrg.setOrgstate(jsonObject.getString("STATUS"));
            }
            if(null==jsonObject.getString("OTHERINFO")|| "".equals(jsonObject.getString("OTHERINFO")) || "null".equals(jsonObject.getString("OTHERINFO"))){
                sysOrg.setIdpath("");
            }else {
                sysOrg.setIdpath(jsonObject.getString("OTHERINFO"));
            }
            if(null==jsonObject.getString("ORG")|| "".equals(jsonObject.getString("ORG")) || "null".equals(jsonObject.getString("ORG"))){
                sysOrg.setOrgentercode("");
            }else {
                sysOrg.setOrgentercode(jsonObject.getString("ORG"));
            }
            if(null==jsonObject.getString("DISTRICTCODE")|| "".equals(jsonObject.getString("DISTRICTCODE")) || "null".equals(jsonObject.getString("DISTRICTCODE"))){
                sysOrg.setRegioncode("");
            }else {
                sysOrg.setRegioncode(jsonObject.getString("DISTRICTCODE"));
            }
            if(null==jsonObject.getString("PRINCIPAL")|| "".equals(jsonObject.getString("PRINCIPAL")) || "null".equals(jsonObject.getString("PRINCIPAL"))){
                sysOrg.setLeader("");
            }else {
                sysOrg.setLeader(jsonObject.getString("PRINCIPAL"));
            }
            if(null==jsonObject.getString("CHARGEDEPT")|| "".equals(jsonObject.getString("CHARGEDEPT")) || "null".equals(jsonObject.getString("CHARGEDEPT"))){
                sysOrg.setSuperdept("");
            }else {
                sysOrg.setSuperdept(jsonObject.getString("CHARGEDEPT"));
            }
            if(null==jsonObject.getString("RATE")|| "".equals(jsonObject.getString("RATE")) || "null".equals(jsonObject.getString("RATE"))){
                sysOrg.setRate("");
            }else {
                sysOrg.setRate(jsonObject.getString("RATE"));
            }

            Long realParentid = Long.valueOf(0);
            Long Orgid=null;
            for(int k=0;k<maps.size();k++){
                if(maps.get(k).get("tid").toString().equals(jsonObject.getString("GROUPID"))){
                    Orgid=Long.valueOf(String.valueOf(maps.get(k).get("selfid")));
                }
                if(null==parentid|| "".equals(parentid) || "null".equals(parentid)){
                    if(Orgid!=null){
                        break;
                    }
                }else{
                    if (maps.get(k).get("tid").toString().equals(parentid)){
                        realParentid = Long.valueOf(String.valueOf(maps.get(k).get("selfid")));
                        if(Orgid!=null){
                            break;
                        }
                    }
                }
            }
            sysOrg.setParentid(realParentid);
            sysOrg.setOrgid(Orgid);
            transformOrgIdPath(sysOrg);
                //新增
                param.put("ORGID", sysOrg.getOrgid());
                param.put("ORGNAME",sysOrg.getOrgname());
                param.put("ORGENTERCODE",sysOrg.getOrgentercode());
                param.put("REGIONCODE", sysOrg.getRegioncode());
                param.put("PARENTID", sysOrg.getParentid());
                param.put("SHORTNAME",sysOrg.getShortname());
                param.put("LINKMAN",sysOrg.getLinkman());
                param.put("TEL", sysOrg.getTel());
                param.put("ORGADDR",sysOrg.getOrgaddr());
                param.put("ORGDESC",sysOrg.getOrgdesc());
                param.put("IDPATH", sysOrg.getIdpath());
                paramList.add(param);
                if (i % 1000 == 0) {
                    namedParameterJdbcTemplate.batchUpdate(insertSql, SqlParameterSourceUtils.createBatch(paramList));
                    paramList = new ArrayList<>();
                }
            }
        namedParameterJdbcTemplate.batchUpdate(insertSql, SqlParameterSourceUtils.createBatch(paramList));
    }

    /**
     * 同步用户组织关系全量
     * @param data
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncgrouprefWithAll(String data){
        JSONArray jsonArray = (JSONArray) JSONArray.parse(data,Feature.IgnoreNotMatch);
        JSONObject jsonObject=null;
        HashMap<String, Object> ret=null;
        List<Map<String, Object>> paramList = new ArrayList<>();
        String updateSql="update sysuser set ORGID=:ORGID where USERID=:USERID";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        List<Map<String,Object>> maps=namedParameterJdbcTemplate.queryForList("select t.tid,t.selfid from sysidmapping t",paramMap);
        for(int i=0;i<jsonArray.size();i++) {
            jsonObject = jsonArray.getJSONObject(i);
            Map<String, Object> param=new HashMap<>();//存放修改
            param.put("USERID", jsonObject.getString("USERID"));
            Long Orgid = null;
            for (int k = 0; k < maps.size(); k++) {
                if (maps.get(k).get("tid").toString().equals(jsonObject.getString("GROUPID"))) {
                    Orgid = Long.valueOf(String.valueOf(maps.get(k).get("selfid")));
                    break;
                }
            }
            param.put("ORGID", Orgid);
            paramList.add(param);
            if (i % 1000 == 0) {
                namedParameterJdbcTemplate.batchUpdate(updateSql, SqlParameterSourceUtils.createBatch(paramList));
                paramList = new ArrayList<>();
            }
        }
        namedParameterJdbcTemplate.batchUpdate(updateSql, SqlParameterSourceUtils.createBatch(paramList));

    }
}
