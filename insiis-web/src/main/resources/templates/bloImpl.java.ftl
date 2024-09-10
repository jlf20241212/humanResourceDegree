package ${cfg.bloPath}.impl;

import ${package.Mapper}.${table.mapperName};
import ${cfg.bloPath}.${entity}BLO;
import org.mohrss.leaf.core.framework.domain.blo.impl.BLOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* <p>
* ${table.comment!} 服务实现类
* </p>
*
* @author ${author}
* @since ${date}
*/
@Service
public class ${entity}BLOImpl extends BLOImpl implements ${entity}BLO {

    @Autowired
    private ${table.mapperName} ${table.mapperName?uncap_first};

}