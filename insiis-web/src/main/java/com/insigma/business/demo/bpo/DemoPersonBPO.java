package com.insigma.business.demo.bpo;

import com.insigma.business.demo.dto.DemoPersonDTO;
import com.insigma.framework.db.PageInfo;
import org.mohrss.leaf.core.framework.domain.bpo.IBPO;

import java.util.List;

/**
 * @author yinjh
 * @version 2021/3/12
 */
public interface DemoPersonBPO extends IBPO {

    DemoPersonDTO queryPerson(String id);

    void deletePersons(List<String> ids);

    void savePerson(DemoPersonDTO demoPersonDTO);

    PageInfo<DemoPersonDTO> queryPersonList(DemoPersonDTO queryDTO, Integer page, Integer size);


    DemoPersonDTO selectOne(DemoPersonDTO demoPersonDTO);

    PageInfo<DemoPersonDTO> queryPersonListByExport(DemoPersonDTO queryDTO, Integer page, Integer size);
}
