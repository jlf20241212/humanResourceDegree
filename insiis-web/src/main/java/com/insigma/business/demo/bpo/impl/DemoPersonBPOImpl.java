package com.insigma.business.demo.bpo.impl;

import com.insigma.business.demo.blo.DemoPersonBLO;
import com.insigma.business.demo.bpo.DemoPersonBPO;
import com.insigma.business.demo.dto.DemoPersonDTO;
import com.insigma.framework.db.PageInfo;
import org.mohrss.leaf.uni.common.domain.bpo.impl.BPOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yinjh
 * @version 2021/3/12
 */
@Service
public class DemoPersonBPOImpl extends BPOImpl implements DemoPersonBPO {

    @Autowired
    private DemoPersonBLO demoPersonBLO;

    @Override
    public DemoPersonDTO queryPerson(String id) {
        DemoPersonDTO demoPersonDTO = demoPersonBLO.queryPerson(id);
        return demoPersonDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePersons(List<String> ids) {
        demoPersonBLO.deletePersons(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePerson(DemoPersonDTO demoPersonDTO) {
        demoPersonBLO.savePerson(demoPersonDTO);
    }

    @Override
    public PageInfo<DemoPersonDTO> queryPersonList(DemoPersonDTO queryDTO, Integer page, Integer size) {
        return demoPersonBLO.queryPersonList(queryDTO, page, size);
    }

    @Override
    public DemoPersonDTO selectOne(DemoPersonDTO demoPersonDTO) {
        return demoPersonBLO.selectOne(demoPersonDTO);
    }

    @Override
    public PageInfo<DemoPersonDTO> queryPersonListByExport(DemoPersonDTO queryDTO, Integer page, Integer size) {
        return demoPersonBLO.queryPersonListByExport(queryDTO, page, size);
    }
}
