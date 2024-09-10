package com.insigma.sys.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.commons.syslog.Syslog;
import com.insigma.framework.web.securities.service.SysCacheService;
import com.insigma.sys.common.SysManageMode;
import com.insigma.sys.dto.MenuDTO;
import com.insigma.sys.entity.SysMenu;
import com.insigma.sys.entity.SysRoleFunction;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.entity.SysUserRole;
import com.insigma.sys.repository.MenuRepository;
import com.insigma.sys.repository.SysRoleFunctionRepository;
import com.insigma.sys.repository.SysUserRoleRepository;
import com.insigma.sys.service.MenuService;
import com.insigma.web.support.repository.CodeTypeRepository;
import com.insigma.web.support.entity.CodeType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private CodeTypeRepository codeTypeRepository;
    @Autowired
    private SysRoleFunctionRepository sysRoleFunctionRepository;
    @Autowired
    private SysUserRoleRepository sysUserRoleRepository;
    @Autowired
    private SysCacheService sysCacheService;
    @Override
    public List<MenuDTO> queryAllMenu() {
        List<MenuDTO> menuDTOS = new ArrayList<>();
        List<SysMenu> sysMenuList = menuRepository.findTrees();
        sysMenuList.stream().forEach(sysMenu -> {
            MenuDTO menuDTO = new MenuDTO();
            BeanUtils.copyProperties(sysMenu, menuDTO);
            menuDTO.setPath(sysMenu.getLocation());
            menuDTOS.add(menuDTO);
        });

        return menuDTOS;
    }

    @Override
    public SysMenu findMenuById(long id) {
        SysMenu sysMenu =menuRepository.findById(id);
        return sysMenu;
    }

    @Syslog("保存菜单：${sysMenu.title}")
    @Override
    @Transactional(rollbackFor=Exception.class)
    public void save(SysMenu sysMenu) {
        String s = "";
        List<SysMenu> m = new ArrayList<>();

        if(sysMenu.getIdpath() != null){
            s = sysMenu.getIdpath();
            m = findNodes(sysMenu.getIdpath());
        }
        menuRepository.save(sysMenu);
        SysMenu sysMenu1 = findMenuById(sysMenu.getParentid());
        if(sysMenu1 == null){
            sysMenu.setIdpath("0/" + sysMenu.getFunctionid());
        }else {
            sysMenu.setIdpath(sysMenu1.getIdpath() + "/" + sysMenu.getFunctionid());
        }
        menuRepository.save(sysMenu);
        if(!"".equals(s) && m.size() > 0){
            for (SysMenu sm : m){
                sm.setIdpath(sm.getIdpath().replace(s, sysMenu.getIdpath()));
                menuRepository.save(sm);
            }
        }
        // 清除菜单缓存
        sysCacheService.clearFunctionCache(sysMenu.getLocation());
    }

    @Override
    public void update(SysMenu sysMenu) {
        menuRepository.update(sysMenu.getFunctionid(),sysMenu.getFunorder(),sysMenu.getParentid());

    }

    @Syslog("删除菜单：${list[0].title}")
    @Override
    @Transactional(rollbackFor=Exception.class)
    public void delete(List<SysMenu> list, boolean isWithRole) {
        for (int i=0;i<list.size();i++){
            menuRepository.delete(list.get(i));
            if (isWithRole){
                //和角色有关系
                List<SysRoleFunction>  sysFunctionRole=sysRoleFunctionRepository.queryRoleBy(list.get(i).getFunctionid());
                for (SysRoleFunction s:sysFunctionRole){
                    sysRoleFunctionRepository.delete(s);
                }
            }
        }
        // 清空所有菜单缓存
        sysCacheService.clearFunctionCache(null);
    }


    @Override
    public List<SysMenu> findTreesByPId(long id) {
        List<SysMenu> menus1=menuRepository.findTreesByPId(id);
        return menus1;
    }

    @Override
    public SysMenu queryByFuncID(long parentid) {
        SysMenu sysMenu =menuRepository.findById(parentid);
        return sysMenu;
    }
    public JSONObject getCodeTypes(JSONObject jsonObject) {
        //
        List<String> list = new ArrayList<>();
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
                    if(codeTypes.getJSONArray(ct.getCodetype()) == null) {
                        JSONArray jsonArray = new JSONArray();
                        codeTypes.put(ct.getCodetype(), jsonArray);
                    }
                    codeTypes.getJSONArray(ct.getCodetype()).add(ctObj);
                });
        return jsonObject.fluentPutAll(codeTypes);
    }

    @Override
    public boolean findFunctionRoles(SysMenu sysMenu) {
        boolean flag=false;
        List<SysMenu> list=findNodes(sysMenu.getIdpath());
        for (SysMenu list1:list){
            List<SysRoleFunction> sysFunctionRoles=sysRoleFunctionRepository.queryRoleBy(list1.getFunctionid());
            if (sysFunctionRoles.size()>0){
                flag=true;
                break;
            }
        }
        return flag;
    }

    @Override
    public List<MenuDTO> queryMenuByFuntype(String funtype) {

        List<MenuDTO> menuDTOS = new ArrayList<>();

        List<SysMenu> sysMenuList = menuRepository.findTreesByFuntype(funtype);
        sysMenuList.stream().forEach(sysMenu -> {
            MenuDTO menuDTO = new MenuDTO();
            BeanUtils.copyProperties(sysMenu, menuDTO);
            menuDTO.setPath(sysMenu.getLocation());
            menuDTOS.add(menuDTO);
        });

        return menuDTOS;
    }

    @Override
    public List<MenuDTO> getMenuList(SysUser user) {
        List<MenuDTO> menuDTOS = new ArrayList<>();
        List<SysMenu> sysMenuList;
        // 如果是超级管理员则显示所有菜单
        if("1".equals(user.getUserType())) {
            sysMenuList = menuRepository.getMenuList();
            // if(SysManageMode.isTripleMode()) {//三员制
            // 1、查询用户所拥有的角色
            // List<SysUserRole> sysUserRoles = sysUserRoleRepository.findByUserId(user.getUserId());
            // // 2、查询角色们所拥有的菜单
            // List<String> roleIds = sysUserRoles.stream().map(role -> role.getRoleId()).collect(Collectors.toList());
            // if(roleIds.size() > 0) {
            //     sysMenuList = menuRepository.getMenuList(roleIds);
            // } else {
            //     sysMenuList = new ArrayList<>();
            // }
            // }
        } else {
            // 1、查询用户所拥有的角色
            List<SysUserRole> sysUserRoles = sysUserRoleRepository.findByUserId(user.getUserId());
            // 2、查询角色们所拥有的菜单
            List<String> roleIds = sysUserRoles.stream().map(role -> role.getRoleId()).collect(Collectors.toList());
            if(roleIds.size() > 0) {
                sysMenuList = menuRepository.getMenuList(roleIds);
            } else {
                sysMenuList = new ArrayList<>();
            }
        }
        sysMenuList.stream().forEach(sysMenu -> {
            MenuDTO menuDTO = new MenuDTO();
            BeanUtils.copyProperties(sysMenu, menuDTO);
            menuDTO.setPath(sysMenu.getLocation());
            menuDTO.setAppid(sysMenu.getAppId());
            menuDTOS.add(menuDTO);
        });
        return menuDTOS;
    }

    @Override
    public List<MenuDTO> getVirtualMenuList(SysUser user) {
        List<MenuDTO> menuDTOS = new ArrayList<>();
        List<SysMenu> sysMenuList;
        sysMenuList = menuRepository.getMenuList();

        sysMenuList.stream().forEach(sysMenu -> {
            MenuDTO menuDTO = new MenuDTO();
            BeanUtils.copyProperties(sysMenu, menuDTO);
            menuDTO.setPath(sysMenu.getLocation());
            menuDTOS.add(menuDTO);
        });
        return menuDTOS;
    }
    @Override
    public SysMenu getSysMenuBean(Map<String,Object> map) {
        SysMenu sysMenu =new SysMenu();
        sysMenu.setTitle(map.get("title").toString());
        sysMenu.setLocation(map.get("location").toString());
        sysMenu.setFuntype(map.get("funtype").toString());
        sysMenu.setNodetype(map.get("nodetype").toString());
        sysMenu.setActive(map.get("active").toString());
        sysMenu.setDescription(map.get("description").toString());
        sysMenu.setDeveloper(map.get("developer").toString());
        if (!ObjectUtils.isEmpty(map.get("openmode"))) {
            sysMenu.setOpenmode(map.get("openmode").toString());
        }else{
            sysMenu.setOpenmode(null);
        }
        if (!ObjectUtils.isEmpty(map.get("funorder"))) {
            sysMenu.setFunorder(Integer.parseInt(map.get("funorder").toString()));
        }else {
            sysMenu.setFunorder(0);
        }
        if (!ObjectUtils.isEmpty(map.get("funcode"))) {
            sysMenu.setFuncode(map.get("funcode").toString());
        }
        sysMenu.setIcon(map.get("icon").toString());
        sysMenu.setIslog(map.get("islog").toString());
        sysMenu.setAuflag(map.get("auflag").toString());
        sysMenu.setRbflag(map.get("rbflag").toString());
        sysMenu.setDigest(map.get("digest").toString());
        sysMenu.setSlevel(map.get("slevel").toString());
        if (!ObjectUtils.isEmpty(map.get("appId"))) {
            sysMenu.setAppId(map.get("appId").toString());
        }else {
            sysMenu.setAppId(null);
        }

//        sysMenu.setParentid((long) Integer.parseInt(map.get("parentid").toString()));
//        if (!map.get("functionid").toString().equals("")){
//            sysMenu.setFunctionid((long)Integer.parseInt(map.get("functionid").toString()));
//        }
        sysMenu.setParentid( map.get("parentid")==null?0:Long.valueOf(String.valueOf(map.get("parentid"))));
        if (!ObjectUtils.isEmpty(map.get("functionid"))) {
            sysMenu.setFunctionid(Long.valueOf((String.valueOf(map.get("functionid")))));
        }
        if (!ObjectUtils.isEmpty(map.get("idpath"))) {
            sysMenu.setIdpath(String.valueOf(map.get("idpath")));
        }
        return sysMenu;
    }

    @Override
    public boolean isManyLocations(SysMenu sysMenu) {
        boolean flag = false;
        // 配置了菜单url且不是节点
        if (!ObjectUtils.isEmpty(sysMenu.getLocation()) && !"1".equals(sysMenu.getNodetype())) {
            List<SysMenu> sysMenus = menuRepository.findLocation(sysMenu.getLocation());
            // 新增
            if (ObjectUtils.isEmpty(sysMenu.getFunctionid())) {
                if (sysMenus.size() > 0) {
                    flag = true;
                }
            } else { // 修改
                if (sysMenus.size() > 0 && !sysMenu.getFunctionid().equals(sysMenus.get(0).getFunctionid())) {
                    flag = true;
                }
            }
        }
        return flag;

    }

    @Override
    public List<SysMenu> findNodes(String  idpath) {
        List<SysMenu> sysMenus=menuRepository.findByIdpathStartingWith(idpath);
        return sysMenus;
//        JSONArray treedatas=JSONArray.parseArray(trees);
//        List<String> list=new ArrayList();
//        if(treedatas.size()>0){
//            for(int i=0;i<treedatas.size();i++){
//                JSONObject job = treedatas.getJSONObject(i);// 遍历 jsonarray 数组，把每一个对象转成 json 对象
//                if (job.get("functionid").toString().equals(node)){
//                    list.add(node);
//                    forChildren(job,list);
//                    break;
//                }else if (job.containsKey("children")){
//                    JSONObject jsonObject=findFunctionid(job,node);
//                    if (jsonObject.size()==0){
//                        continue;
//                    }else {
//                        list.add(node);
//                        forChildren(jsonObject,list);
//                        break;
//                    }
//                }
//            }
//        }
//        return list;
    }

    @Override
    public List<SysMenu> findNodesNoAnNiu(Long id) {
        List<SysMenu> sysMenus=menuRepository.findNodesNoAnNiu(id);
        return sysMenus;
    }

    public JSONObject findFunctionid(JSONObject jsonObject,String node){
        JSONArray jsonArray= (JSONArray)jsonObject.get("children");
        JSONObject jsonObject1=new JSONObject();
        for (int j=0;j<jsonArray.size();j++){
            JSONObject job1 = jsonArray.getJSONObject(j);
            if (job1.get("functionid").toString().equals(node)){
                jsonObject1=job1;
                break;
            }else if (job1.containsKey("children")){
                jsonObject1=findFunctionid(job1,node);
                if (jsonObject1!=null){
                    break;
                }
            }
        }
        return jsonObject1;
    }


//    public List  forChildren(JSONObject job,List list){
//        if (job.containsKey("children")==true){
//            JSONArray jsonArray= (JSONArray) job.get("children");
//            if (jsonArray.size()>0){
//                for(int j=0;j<jsonArray.size();j++){
//                    JSONObject job1 = jsonArray.getJSONObject(j);
//                    list.add(job1.get("functionid").toString());
//                    forChildren(job1,list);
//                }
//            }
//        }
//        return list;
//    }

    @Override
    public List<MenuDTO> queryMenuByFuntypeAndActive(String funtype) {

        List<MenuDTO> menuDTOS = new ArrayList<>();
        String originFuntype=funtype;
        if(SysManageMode.isTripleMode()){//三员制
            if("3".equals(funtype)){//安全管理员角色按照系统管理员去查管理类和通用类菜单
                funtype="1";
            }else if("4".equals(funtype)){//审计管理员角色按照系统管理员去查管理类和通用类菜单
                funtype="1";
            }
        }
        List<SysMenu> sysMenuList = menuRepository.findTreesByFuntypeAndActive(funtype);

        if(SysManageMode.isTripleMode()) {//三员制
            List<String> location=new ArrayList<String>();
            if("3".equals(originFuntype)){
                location.add("/sys/user");
                location.add("/sys/usertms");
            }else if("4".equals(originFuntype)){
                location.add("/sys/role");
                location.add("/sys/grant");
                location.add("/sys/user");
                location.add("/sys/usertms");
            }
            location.add("/sys/user");
            removeMenu(location,sysMenuList);
        }else{
            List<String> location=new ArrayList<String>();
            location.add("/sys/grant");
            location.add("/sys/usertms");
            removeMenu(location,sysMenuList);
        }

        Set<Long> ids = new HashSet<>();

        sysMenuList.stream().forEach(sysMenu -> {
            MenuDTO menuDTO = new MenuDTO();
            BeanUtils.copyProperties(sysMenu, menuDTO);
            menuDTO.setPath(sysMenu.getLocation());

            // 如果是按钮，并且父ID没有add进ids中，构造基本功能节点并放在第一位
            if ("3".equals(menuDTO.getNodetype()) && !ids.contains(menuDTO.getParentid())) {
                MenuDTO base = new MenuDTO();
                // 虚拟一个
                base.setFunctionid(-menuDTO.getParentid());
                base.setParentid(menuDTO.getParentid());
                base.setTitle("基本功能");
                menuDTOS.add(base);
                ids.add(menuDTO.getParentid());
            }

            menuDTOS.add(menuDTO);

        });

        return menuDTOS;
    }

    @Override
    public List findNodeslevel(Long id) {

        return menuRepository.findNodeslevel(id);
    }

    @Override
    public List<MenuDTO> queryMenusByUserId(String userId) {
        List<MenuDTO> menuDTOS = new ArrayList<>();
        List<SysMenu> sysMenuList = menuRepository.findTreesByUserId(userId);
        sysMenuList.stream().forEach(sysMenu -> {
            MenuDTO menuDTO = new MenuDTO();
            BeanUtils.copyProperties(sysMenu, menuDTO);
            menuDTO.setPath(sysMenu.getLocation());
            menuDTOS.add(menuDTO);
        });
        return menuDTOS;
    }

    @Override
    public List<String> getButtonAuthList(SysUser user, String location) {
        List<String> buttonAuthList = new ArrayList<>();
        List<SysMenu> menuList = menuRepository.findLocation(location);
        if (menuList.size() > 0) {
            Long parentId = menuList.get(0).getFunctionid();
            if("1".equals(user.getUserType())) {
                buttonAuthList = menuRepository.getAllButtonAuthList(parentId);
            } else {
                // 1、查询用户所拥有的角色
                List<SysUserRole> sysUserRoles = sysUserRoleRepository.findByUserId(user.getUserId());
                // 2、查询角色们所拥有的按钮
                List<String> roleIds = sysUserRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
                if(roleIds.size() > 0) {
                    buttonAuthList = menuRepository.getButtonAuthList(roleIds, parentId);
                }
            }
        }
        return buttonAuthList;
    }

    @Override
    public String updateNodeOrder(MenuDTO nodeBy, MenuDTO nodeTo) {
        String message = null;
        List<MenuDTO> allMenuList = this.queryAllMenu();
        //获取当前拖拽节点的父节点
        Long pid = nodeBy.getParentid();
        //获取当前拖拽节点的父节点下所有的child
        List<SysMenu> childList = menuRepository.findTreesByPId(pid);
        //转化
        List<MenuDTO> childListByMenuDTO = new ArrayList<>();
        childList.stream().forEach(sysMenu -> {
            MenuDTO menuDTO = new MenuDTO();
            BeanUtils.copyProperties(sysMenu, menuDTO);
            menuDTO.setPath(sysMenu.getLocation());
            childListByMenuDTO.add(menuDTO);
        });
        //获取nodeBy的下标
        int nodeByIndex = childListByMenuDTO.indexOf(nodeBy);
        if(nodeByIndex == -1){
            message = "未获取被拖拽node下标";
            return message;
        }
        //获取nodeTo的下标
        int nodeToIndex = childListByMenuDTO.indexOf(nodeTo);
        if(nodeToIndex == -1){
            message = "未获取目标对象node下标";
            return message;
        }
        //截取从nodeBy到nodeTo的list
        if(nodeBy.getFunorder() > nodeTo.getFunorder()){
            //上移
            nodeByIndex = nodeByIndex + 1;
            List<MenuDTO> subList = childListByMenuDTO.subList(nodeToIndex,nodeByIndex);
            for(int i = 0;i < subList.size();i++){
                //获取ID与order
                Long nodeFunctionId = subList.get(i).getFunctionid();
                int funOrder = subList.get(i).getFunorder();
                if(i == subList.size()-1){
                    //获取subList顶端的node赋值给拖动的目标node
                    menuRepository.updateNodeOrder(nodeFunctionId,subList.get(0).getFunorder());
                    continue;
                }
                //前序+1，更新
                menuRepository.updateNodeOrder(nodeFunctionId,funOrder+1);
            }
            message = "向下tuo";
        }else if(nodeBy.getFunorder() < nodeTo.getFunorder()){
            //下移
            List<MenuDTO> subList = childListByMenuDTO.subList(nodeByIndex,nodeToIndex+1);
            for(int i = 0;i < subList.size();i++){
                //获取ID与order
                Long nodeFunctionId = subList.get(i).getFunctionid();
                int funOrder = subList.get(i).getFunorder();
                if(i == 0){
                    //获取subList底端的node赋值给拖动的目标node
                    menuRepository.updateNodeOrder(nodeFunctionId,subList.get(subList.size() - 1).getFunorder());
                    continue;
                }
                //前序-1，更新
                menuRepository.updateNodeOrder(nodeFunctionId,funOrder-1);
            }
        }
        return message;
    }

    public void removeMenu(List<String> location,List<SysMenu> sysMenuList){
        List listRemove=new ArrayList();
        for(int i=0;i<sysMenuList.size();i++){
            for(int j=0;j<location.size();j++){
                if(sysMenuList.get(i).getLocation()!=null && !"".equals(sysMenuList.get(i).getLocation())){
                    if(sysMenuList.get(i).getLocation().equals(location.get(j))){
                        listRemove.add(sysMenuList.get(i).getLocation());
                    }
                }
            }
        }

        if(listRemove.size()>0){
            for(int i=0;i<listRemove.size();i++){
                for(int j=0;j<sysMenuList.size();j++){
                    if(listRemove.get(i).toString().equals(sysMenuList.get(j).getLocation())){
                        sysMenuList.remove(j);
                        break;
                    }
                }

            }
        }
    }
}
