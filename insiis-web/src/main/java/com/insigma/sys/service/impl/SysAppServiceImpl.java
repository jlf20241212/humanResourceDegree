package com.insigma.sys.service.impl;

import com.insigma.framework.db.JdbcPageHelper;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.util.DtoEntityUtil;
import com.insigma.framework.util.StringUtil;
import com.insigma.sys.dto.SysAppAddressDTO;
import com.insigma.sys.dto.SysAppDTO;
import com.insigma.sys.entity.SysApp;
import com.insigma.sys.entity.SysAppAddress;
import com.insigma.sys.repository.SysAppAddressRepository;
import com.insigma.sys.repository.SysAppRepository;
import com.insigma.sys.service.SysAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author GH
 * @version 2022/3/28
 * @since 2.6.5
 */

@Service
public class SysAppServiceImpl implements SysAppService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SysAppRepository sysAppRepository;
    @Autowired
    private SysAppAddressRepository sysAppAddressRepository;

    /**
     * 根据条件进行分页查询
     *
     * @param queryDTO 查询条件
     * @param page     页码
     * @param size     每页条数
     * @return 对象ID
     * @throws SQLException SQLException
     */
    @Override
    public PageInfo<SysAppDTO> pageQuery(SysAppDTO queryDTO, Integer page, Integer size) throws SQLException {
        List<Object> params = new ArrayList<>();
        StringBuffer sql = new StringBuffer("select * from SYSAPP where 1=1");
        if (!ObjectUtils.isEmpty(queryDTO.getAppName())) {
            sql.append(" and APP_NAME like ?");
            params.add("%" + queryDTO.getAppName() + "%");
        }

        if (!ObjectUtils.isEmpty(queryDTO.getAppCode())) {
            sql.append(" and APP_CODE like ?");
            params.add("%" + queryDTO.getAppCode() + "%");
        }
        if (!ObjectUtils.isEmpty(queryDTO.getActive())) {
            sql.append(" and ACTIVE = ?");
            params.add(queryDTO.getActive());
        }
        sql.append(" order by ORDER_NO asc");

        JdbcPageHelper pageHelper = new JdbcPageHelper(jdbcTemplate, page, size);
        List<SysAppDTO> list = pageHelper.queryPagination(sql.toString(), params, SysAppDTO.class).getData();
        for (SysAppDTO sysAppDTO : list) {
            String secretKey = sysAppDTO.getSecretKey();
            String substring = secretKey.substring(4, 12);
            String replace = secretKey.replace(substring, "********");
            sysAppDTO.setSecretKey(replace);
        }
        PageInfo<SysAppDTO> pageInfo = new PageInfo<>();
        pageInfo.setData(list);
        pageInfo.setTotal((long) list.size());
        return pageInfo;
    }

    /***
     * 保存应用信息
     * @param queryDTO 保存的数据对象
     * @param secretKey 安全key
     * @return 对象id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveSysAppDTO(SysAppDTO queryDTO, String secretKey) {
        String appId = null;
        if (StringUtil.isEmpty(queryDTO.getAppId())) { // 新增
            queryDTO.setActive("1");
            queryDTO.setSecretKey(secretKey);
            queryDTO.setCreateTime(new Date());
            Long apporder = sysAppRepository.selectMaxOrder();
            if (apporder != null && !"".equals(apporder)) {
                queryDTO.setOrderNo(apporder + 1);
            } else {
                queryDTO.setOrderNo((long) 1);
            }
        } else {
            SysApp sApp = sysAppRepository.getOne(queryDTO.getAppId());
            queryDTO.setAppId(sApp.getAppId());
            queryDTO.setSecretKey(sApp.getSecretKey());
            queryDTO.setCreateTime(sApp.getCreateTime());
            queryDTO.setActive(sApp.getActive());
            queryDTO.setOrderNo(sApp.getOrderNo());
            queryDTO.setUpdateTime(new Date());
        }
        SysApp sysApp = DtoEntityUtil.trans(queryDTO, SysApp.class);
        SysApp app = sysAppRepository.save(sysApp);
        if (app != null) {
            appId = app.getAppId();
        }
        return appId;
    }

    /***
     * 保存应用地址信息
     * @param list 应用地址信息的对象集合
     * @param appid 应用系统id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSysAppAddressDTO(List<SysAppAddressDTO> list, String appid) {
        for (SysAppAddressDTO sysAppAddressDTO : list) {
            if (StringUtil.isNotEmpty(sysAppAddressDTO.getAddressId())) {//修改
                sysAppAddressDTO.setAddressId(sysAppAddressDTO.getAddressId());
            }
            sysAppAddressDTO.setAppId(appid);
            SysAppAddress sysAppAddress = DtoEntityUtil.trans(sysAppAddressDTO, SysAppAddress.class);
            sysAppAddressRepository.save(sysAppAddress);
        }
    }

    /***
     * 根据应用ID查询对应的信息
     * @param appid 应用系统id
     * @return 应用系统对象
     */
    @Override
    public SysAppDTO querySysApp(String appid) {
        SysApp sysApp = sysAppRepository.getOne(appid);
        SysAppDTO appDTO = DtoEntityUtil.trans(sysApp, SysAppDTO.class);
        return appDTO;
    }

    /***
     * 根据应用ID查询所有应用地址信息
     * @param appid 应用系统id
     * @return 所有应用地址对象集合
     */
    @Override
    public List<SysAppAddressDTO> querySysAddressDTO(String appid) {
        List<SysAppAddress> sysAppAddresses = sysAppAddressRepository.querySysAddressDTO(appid);
        List<SysAppAddressDTO> dtos = DtoEntityUtil.trans(sysAppAddresses, SysAppAddressDTO.class);
        return dtos;
    }

    /***
     * 根据应用ID删除
     * @param appid 应用系统id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysApp(String appid) {
        sysAppRepository.deleteById(appid);
    }

    /***
     * 根据应用id删除所有对应的应用地址数据
     * @param appid 应用系统id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysAppAddressByAppId(String appid) {
        sysAppAddressRepository.deleteSysAppAddressByAppId(appid);
    }

    /***
     * 根据应用地址ID删除应用
     * @param addressId 应用地址id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysAppAddress(String addressId) {
        sysAppAddressRepository.deleteById(addressId);
    }

    /***
     * 将应用设置为有效
     * @param appid 应用系统id
     * @return
     */
    @Override
    public void toActiveSysApp(String appid) {
        jdbcTemplate.update("update SYSAPP set ACTIVE = '1'  where APP_ID=?", ps -> {
            ps.setString(1, appid);
        });
    }

    /***
     * 将应用设置为无效
     * @param appid 应用系统id
     * @return
     */
    @Override
    public void toNotActiveSysApp(String appid) {
        jdbcTemplate.update("update SYSAPP set ACTIVE = '0'  where APP_ID=?", ps -> {
            ps.setString(1, appid);
        });
    }

    /***
     * 查询所有的应用名称
     * @return 应用系统集合
     */
    @Override
    public List<SysAppDTO> querySysAppDTO() {
        List<SysApp> sysApps = sysAppRepository.findAll();
        List<SysAppDTO> dtos = DtoEntityUtil.trans(sysApps, SysAppDTO.class);
        return dtos;
    }

    /***
     * 验证编码是否重复
     * @return 布尔
     */
    @Override
    public boolean selectAppCode(String appCode) {
        boolean flag = false;
        SysApp sysApp = sysAppRepository.selectAppCode(appCode);
            if (sysApp != null) {
                flag = true;
            }
        return flag;
    }
}
