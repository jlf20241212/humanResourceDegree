package com.insigma.sys.service.impl;

import com.insigma.framework.db.JdbcPageHelper;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.util.DtoEntityUtil;
import com.insigma.sys.dto.SysDownloadCenterDTO;
import com.insigma.sys.entity.SysDownloadCenter;
import com.insigma.sys.entity.SysDownloadLog;
import com.insigma.sys.repository.SysDownloadCenterRepository;
import com.insigma.sys.repository.SysDownloadLogRepository;
import com.insigma.sys.service.SysDownloadCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GH
 * @ClassName: SysDownloadCenterServiceImpl
 * @Description:
 * @version 2021/8/4  10:16
 */
@Service("SysDownloadCenterService")
public class SysDownloadCenterServiceImpl implements SysDownloadCenterService {

    @Autowired
    private SysDownloadCenterRepository sysDownloadCenterRepository;

    @Autowired
    private SysDownloadLogRepository sysDownloadLogRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //查询
    @Override
    public PageInfo<SysDownloadCenterDTO> queryDownloadCenterList(SysDownloadCenterDTO queryDTO, Integer page, Integer size) throws SQLException {
        //用于保存查询条件
        Map<String, Object> params = new HashMap<>();
        //创建SQL语句
        StringBuffer sql = new StringBuffer("SELECT sdc.ID, " +
                "   sdc.MATERIAL_TYPE, " +
                "   sdc.MATERIAL_SIZE," +
                "   sdc.MATERIAL_NAME, " +
                "   sdc.UPLOAD_TIME " +
                "from SYSDOWNLOADCENTER sdc " +
                "where 1=1 ");

        if (!ObjectUtils.isEmpty(queryDTO.getMaterial_type())) {
            //拼接在上面的SQL上
            sql.append(" and sdc.MATERIAL_TYPE = :material_type");
            params.put("material_type", queryDTO.getMaterial_type());
        }

        if (!ObjectUtils.isEmpty(queryDTO.getMaterial_name())) {
            sql.append(" and sdc.MATERIAL_NAME like :material_name");
            params.put("material_name", "%" + queryDTO.getMaterial_name() + "%");
        }
        sql.append(" ORDER BY sdc.UPLOAD_TIME DESC");

        JdbcPageHelper pageHelper = new JdbcPageHelper(jdbcTemplate, page, size);
        PageInfo<SysDownloadCenterDTO> pageInfo = pageHelper.queryPagination(sql.toString(), params, rs -> {
            SysDownloadCenterDTO dto = new SysDownloadCenterDTO();
            //赋值
            dto.setMaterial_type(rs.getString("material_type"));
            dto.setMaterial_size(rs.getString("material_size"));
            dto.setMaterial_name(rs.getString("material_name"));
            dto.setUpload_time(rs.getTimestamp("upload_time"));
            dto.setId(rs.getString("id"));
            return dto;
        });
        return pageInfo;
    }

    //新增
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDownloadCenter(SysDownloadCenterDTO sysDownloadCenterDTO) {
        SysDownloadCenter sysDownloadCenter = DtoEntityUtil.trans(sysDownloadCenterDTO, SysDownloadCenter.class);
        sysDownloadCenterRepository.save(sysDownloadCenter);
    }

    //删除
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        sysDownloadCenterRepository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDownloadLog(String id, String logonName) {
        SysDownloadLog log = new SysDownloadLog();
        log.setFileId(id);
        log.setDownloader(logonName);
        log.setDownloadTime(new Date());
        sysDownloadLogRepository.saveAndFlush(log);
    }
}
