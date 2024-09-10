package com.insigma.business.demo.blo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insigma.business.demo.blo.DemoPersonBLO;
import com.insigma.business.demo.dao.DemoPersonDAO;
import com.insigma.business.demo.dto.DemoPersonDTO;
import com.insigma.business.demo.entity.DemoPerson;
import com.insigma.framework.db.JdbcPageHelper;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.util.DtoEntityUtil;
import com.insigma.framework.util.IDUtil;
import com.insigma.web.support.util.SysFunctionManager;
import lombok.SneakyThrows;
import org.mohrss.leaf.uni.common.domain.blo.impl.BLOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yinjh
 * @version 2021/3/12
 */
@Service
public class DemoPersonBLOImpl extends BLOImpl implements DemoPersonBLO {

    @Autowired
    private DemoPersonDAO demoPersonDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public DemoPersonDTO queryPerson(String id) {
        DemoPerson dp = demoPersonDAO.selectDemoPersonById(id);
//        DemoPerson dp = demoPersonDAO.sqlSession().selectOne("selectDemoPersonById", id);
//        DemoPerson dp = demoPersonDAO.getById(id);
        DemoPersonDTO dto = DtoEntityUtil.trans(dp, DemoPersonDTO.class);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePersons(List<String> ids) {
        demoPersonDAO.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePerson(DemoPersonDTO demoPersonDTO) {
        DemoPerson dp = DtoEntityUtil.trans(demoPersonDTO, DemoPerson.class);
        String id = null;
        if (ObjectUtils.isEmpty(demoPersonDTO.getId())) { // 新增

            id = IDUtil.generateUUID();
        } else {
            id = demoPersonDTO.getId();
        }
        dp.setId(id);
        dp.setCreate_time(new Date());
//        HList hList = new HList(JSON.parseObject(JSON.toJSONString(dp)));
//        hList.setNVarchar2Columns("address");
//        hList.save("demo_person", "address;name", "id", HList.SAVEMODE_INSERT_OR_UPDATE);
        demoPersonDAO.insertOrUpdate(dp);
    }

    @SneakyThrows
    @Override
    public PageInfo<DemoPersonDTO> queryPersonList(DemoPersonDTO queryDTO, Integer page, Integer size) {
        //查询条件为时间段选项
        SimpleDateFormat st = new SimpleDateFormat("yyyy-MM-dd");
        Date oTime = null;
        Date pTime = null;
        if (!ObjectUtils.isEmpty(queryDTO.getStartTime())) {
            oTime = st.parse(queryDTO.getStartTime().split(",")[0]);
            pTime = st.parse(queryDTO.getStartTime().split(",")[1]);
        }

        LambdaQueryWrapper<DemoPerson> wrapper = new QueryWrapper<DemoPerson>().lambda();
        wrapper.like(!ObjectUtils.isEmpty(queryDTO.getName()), DemoPerson::getName, "%" + queryDTO.getName() + "%")
                .eq(!ObjectUtils.isEmpty(queryDTO.getSex()), DemoPerson::getSex, queryDTO.getSex())
                .ge(!ObjectUtils.isEmpty(queryDTO.getStartTime()), DemoPerson::getBirthday, oTime)
                .le(!ObjectUtils.isEmpty(queryDTO.getStartTime()), DemoPerson::getBirthday, pTime)
                .orderByDesc(DemoPerson::getCreate_time);

        Page<DemoPerson> dpPage = new Page<>(page, size);

        IPage<DemoPerson> mapIPage = demoPersonDAO.page(dpPage, wrapper);
        List<DemoPerson> records = mapIPage.getRecords();
        List<DemoPersonDTO> list = DtoEntityUtil.trans(records, DemoPersonDTO.class);

        PageInfo<DemoPersonDTO> pageInfo = new PageInfo<>();
        pageInfo.setTotal(mapIPage.getTotal());
        pageInfo.setData(list);
        return pageInfo;
    }

    @Override
    public DemoPersonDTO selectOne(DemoPersonDTO demoPersonDTO) {
        LambdaQueryWrapper<DemoPerson> wrapper = new QueryWrapper<DemoPerson>().lambda();
        wrapper.eq(!ObjectUtils.isEmpty(demoPersonDTO.getName()), DemoPerson::getName, demoPersonDTO.getName())
                .eq(!ObjectUtils.isEmpty(demoPersonDTO.getSex()), DemoPerson::getSex, demoPersonDTO.getSex())
                .eq(!ObjectUtils.isEmpty(demoPersonDTO.getBirthday()), DemoPerson::getBirthday, demoPersonDTO.getBirthday())
                .eq(!ObjectUtils.isEmpty(demoPersonDTO.getMobile()), DemoPerson::getMobile, demoPersonDTO.getAddress())
                .eq(!ObjectUtils.isEmpty(demoPersonDTO.getAddress()), DemoPerson::getAddress, demoPersonDTO.getAddress());
        //查询有没有相同的数据
        DemoPerson demoPerson = demoPersonDAO.selectOne(wrapper);
        DemoPersonDTO personDTO = DtoEntityUtil.trans(demoPerson, DemoPersonDTO.class);
        return personDTO;
    }

    @SneakyThrows
    @Override
    public PageInfo<DemoPersonDTO> queryPersonListByExport(DemoPersonDTO queryDTO, Integer page, Integer size) {
        Map<String, Object> params = new HashMap<>();
        StringBuffer sql = new StringBuffer("select * from demo_person where 1=1 ");
        if (!ObjectUtils.isEmpty(queryDTO.getName())) {
            sql.append(" and name like :name");
            params.put("name", "%" + queryDTO.getName() + "%");
        }
        if (!ObjectUtils.isEmpty(queryDTO.getSex())) {
            sql.append(" and sex = :sex");
            params.put("sex", queryDTO.getSex());
        }
        if (!ObjectUtils.isEmpty(queryDTO.getStartTime())) {
            //查询条件为时间段选项
            SimpleDateFormat st = new SimpleDateFormat("yyyy-MM-dd");
            Date oTime = st.parse(queryDTO.getStartTime().split(",")[0]);
            Date pTime = st.parse(queryDTO.getStartTime().split(",")[1]);
            sql.append(" and birthday > :startTime and birthday < :endTime");
            params.put("startTime", oTime);
            params.put("endTime", pTime);
        }
        JdbcPageHelper jdbcPageHelper = new JdbcPageHelper(jdbcTemplate, page, size, "grid", SysFunctionManager.getFunctionId());
        return jdbcPageHelper.queryPagination(sql.toString(), params, DemoPersonDTO.class);
    }

}
