package com.insigma.business.demo.blo;

import com.insigma.business.demo.dto.DemoPersonDTO;
import com.insigma.framework.db.PageInfo;
import org.mohrss.leaf.core.framework.domain.blo.IBLO;

import java.util.List;

/**
 * @author yinjh
 * @version 2021/3/12
 */
public interface DemoPersonBLO extends IBLO {

    DemoPersonDTO queryPerson(String id);

    void deletePersons(List<String> ids);

    void savePerson(DemoPersonDTO demoPersonDTO);

    PageInfo<DemoPersonDTO> queryPersonList(DemoPersonDTO queryDTO, Integer page, Integer size);

    DemoPersonDTO selectOne(DemoPersonDTO demoPersonDTO);

    PageInfo<DemoPersonDTO> queryPersonListByExport(DemoPersonDTO queryDTO, Integer page, Integer size);
}
