package ${cfg.bpoPath}.impl;

import ${package.Mapper}.${table.mapperName};
import ${cfg.bloPath}.${entity}BLO;
import ${cfg.bpoPath}.${entity}BPO;
import org.mohrss.leaf.core.framework.domain.bpo.impl.BPOImpl;
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
public class ${entity}BPOImpl extends BPOImpl implements ${entity}BPO {

    @Autowired
    private ${entity}BLO ${entity?uncap_first}BLO;

}