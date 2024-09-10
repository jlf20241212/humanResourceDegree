package com.insigma.sys.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.commons.syslog.Syslog;
import com.insigma.sys.entity.Aa26;
import com.insigma.sys.entity.SysOrg;
import com.insigma.sys.entity.SysUserArea;
import com.insigma.sys.repository.Aa26Repository;
import com.insigma.sys.repository.SysOrgRepository;
import com.insigma.sys.repository.SysUserAreaRepository;
import com.insigma.sys.service.SysOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @Author: caic
 * @version: 14:11 2019/1/22
 * @Description:
 */
@Service("SysOrgService")
public class SysOrgServiceImpl implements SysOrgService {
    @Autowired
    SysOrgRepository sysOrgRepository;
    @Autowired
    Aa26Repository aa26Repository;
    @Autowired
    SysUserAreaRepository sysUserAreaRepository;

    @Syslog("保存机构：${sysOrg.orgname}")
    @Override
    @Transactional
    public void save(SysOrg sysOrg) {
        sysOrgRepository.save(sysOrg);
    }

    @Syslog("删除所有机构")
    @Override
    @Transactional
    public void deleteAllOrgs() {
        sysOrgRepository.deleteAll();
    }

    @Syslog("删除机构：${id}")
    @Override
    @Transactional
    public void deleteByOrgId(Long id) {
        sysOrgRepository.deleteById(id);
    }

    @Override
    public SysOrg findByOrgid(Long orgId) {
        return sysOrgRepository.findByOrgid(orgId);
    }

    @Override
    public List<SysOrg> findByRegioncode(String areaId) {
        return sysOrgRepository.findByRegioncode(areaId);
    }

    @Override
    public List<SysOrg> queryAllOrg() {
        List<SysOrg> sysOrgs=sysOrgRepository.findAllOrg();
        return sysOrgs;
    }

    @Override
    public List<Aa26> getAA26() {
        List<Aa26> list;
        list = aa26Repository.findAll();
        return list;
    }

    @Override
    public Aa26 queryAa26By301(String aab301) {
        Aa26 aa26=aa26Repository.findByAab301(aab301);
        return aa26;
    }

    @Syslog("保存机构：${sysOrg.orgname}")
    @Override
    @Transactional
    public void saveOrg(SysOrg sysOrg) {
        sysOrgRepository.save(sysOrg);
        if (sysOrg.getParentid()!=null){
            SysOrg sysOrg2=sysOrgRepository.findByOrgid(sysOrg.getParentid());
            sysOrg.setIdpath(sysOrg2.getIdpath()+"/"+sysOrg.getOrgid());
        }else {
            sysOrg.setIdpath(sysOrg.getOrgid().toString());
        }
        sysOrgRepository.save(sysOrg);
    }

    @Syslog("删除机构：${list[0].orgname}")
    @Override
    @Transactional
    public void delOrg(List<SysOrg> list, boolean o) {
        for (int i=0;i<list.size();i++){
            sysOrgRepository.delete(list.get(i));
            if (o==true){
                List<SysUserArea> s= sysUserAreaRepository.findByAreaId(list.get(i).getOrgentercode());
                if (s.size()>0){
                    for (SysUserArea s1:s){
                        sysUserAreaRepository.delete(s1);
                    }
                }
            }

            }
        }
    @Override
    public SysOrg getSysOrgBean(Map<String,Object> form) {
        SysOrg sysOrg=new SysOrg();
        sysOrg.setOrgname(form.get("orgname").toString());
        sysOrg.setOrgentercode(form.get("orgentercode").toString());
        sysOrg.setShortname(form.get("shortname").toString());
        if (form.get("parentid")!=null){
            sysOrg.setParentid(Long.valueOf(form.get("parentid").toString()));
        }
        Map<String,Object> regioncode= (Map<String, Object>) form.get("regioncode");
        List<String> list= (List<String>) regioncode.get("value");
            if (list.size()==0){
                sysOrg.setRegioncode("");
            }else if (list.size()>0){
                sysOrg.setRegioncode(list.get(list.size()-1));
            }
            sysOrg.setLinkman(form.get("linkman").toString());
        sysOrg.setLeader(form.get("leader").toString());
        sysOrg.setTel(form.get("tel").toString());
        sysOrg.setOrgaddr(form.get("orgaddr").toString());
        sysOrg.setOrgdesc(form.get("orgdesc").toString());
        if (!form.get("orgorder").toString().equals("")){
            sysOrg.setOrgorder(Integer.parseInt(form.get("orgorder").toString()));
        }
        sysOrg.setOrgstate(form.get("orgstate").toString());
        sysOrg.setSuperdept(form.get("superdept").toString());
        sysOrg.setOrgautocode(form.get("orgautocode").toString());
        sysOrg.setZip(form.get("zip").toString());
        sysOrg.setIdpath(form.get("idpath").toString());
        if (!form.get("orgid").toString().equals("")){
            sysOrg.setOrgid( Long.valueOf(form.get("orgid").toString()));
        }
        return sysOrg;
    }

    @Override
    public boolean findByArea(List<SysOrg> list) {
        for (int i=0;i<list.size();i++){
            List<SysUserArea> s =sysUserAreaRepository.findByAreaId(list.get(i).getOrgentercode());
            if (s.size()>0){
                return true;
            }
        }
        return false;
    }

    @Override
    public List findNodes(long node) {
        SysOrg sysOrg = sysOrgRepository.findByOrgid(node);
        List<SysOrg> sysOrgs=sysOrgRepository.findByIdpathStartingWith(sysOrg.getIdpath());
        return sysOrgs;
    }
    public JSONObject findOrgid(JSONObject jsonObject,String node){
        JSONArray jsonArray= (JSONArray)jsonObject.get("children");
        JSONObject jsonObject1=new JSONObject();
        for (int j=0;j<jsonArray.size();j++){
            JSONObject job1 = jsonArray.getJSONObject(j);
            if (job1.get("orgid").toString().equals(node)){
                jsonObject1=job1;
                break;
            }else if (job1.containsKey("children")==true){
                jsonObject1=findOrgid(job1,node);
                if (jsonObject1!=null){
                    break;
                }
            }
        }
        return jsonObject1;
    }

//
//    public List  forChildren(JSONObject job,List list){
//        if (job.containsKey("children")==true){
//            JSONArray jsonArray= (JSONArray) job.get("children");
//            if (jsonArray.size()>0){
//                for(int j=0;j<jsonArray.size();j++){
//                    JSONObject job1 = jsonArray.getJSONObject(j);
//                    list.add(job1.get("orgid").toString());
//                    forChildren(job1,list);
//                }
//            }
//        }
//        return list;
//    }

    @Override
    public Boolean isMany(SysOrg sysOrg) {
        Boolean flag=false;
        List<SysOrg> sysOrgs=sysOrgRepository.findByNameOrCode(sysOrg.getOrgname(),sysOrg.getOrgentercode());
        if (sysOrgs.size() > 0) {
            if (null==sysOrg.getOrgid()){
                return true;
            }
            for (SysOrg sysOrg1 : sysOrgs) {
                if (sysOrg1.getOrgname()==sysOrg.getOrgname()&&sysOrg.getOrgid()==null){
                    flag = true;
                    break;
                }else if (sysOrg1.getOrgentercode()==sysOrg.getOrgentercode()&&sysOrg.getOrgid()==null){
                    flag = true;
                    break;
                }else if (sysOrg.getOrgid()!=null&&!sysOrg1.getOrgid().equals(sysOrg.getOrgid())){
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
}
