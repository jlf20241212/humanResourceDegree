package com.insigma.sys.service;

import com.alibaba.fastjson.JSONObject;
import com.insigma.sys.dto.MenuDTO;
import com.insigma.sys.entity.SysMenu;
import com.insigma.sys.entity.SysUser;

import java.util.List;
import java.util.Map;


public interface MenuService {

    //查询tree
    List<MenuDTO> queryAllMenu();

    //根据id查找menu对象
    SysMenu findMenuById(long id);

    void save(SysMenu sysMenu);

    void update(SysMenu sysMenu);

    void delete(List<SysMenu> list, boolean isWithRole);

    List<SysMenu> findTreesByPId(long id);

    SysMenu queryByFuncID(long parentid);

    JSONObject getCodeTypes(JSONObject jsonObject);

    boolean findFunctionRoles(SysMenu sysMenu);

    List<MenuDTO> getVirtualMenuList(SysUser user);

    List<MenuDTO> queryMenuByFuntype(String funtype);

    List<MenuDTO> getMenuList(SysUser user);

    //将pagedata转化为SysMenubean
    SysMenu getSysMenuBean(Map<String, Object> map);

    //判断location是否存在
    boolean isManyLocations(SysMenu sysMenu);

    //找到子节点
    List<SysMenu> findNodes(String idpath);

    //查询子节点不包括按钮
    List<SysMenu> findNodesNoAnNiu(Long id);

    List<MenuDTO> queryMenuByFuntypeAndActive(String funtype);

    //查找等级
    List findNodeslevel(Long id);

    List<MenuDTO> queryMenusByUserId(String userId);

    List<String> getButtonAuthList(SysUser sysUser, String location);

    //更新排序
    String updateNodeOrder(MenuDTO nodeBy,MenuDTO nodeTo);
}
