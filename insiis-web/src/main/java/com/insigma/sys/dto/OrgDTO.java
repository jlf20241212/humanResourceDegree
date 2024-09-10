package com.insigma.sys.dto;

import com.insigma.sys.entity.SysOrg;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author jinw
 * @version 2019/1/4
 * <p>epsoft - insiis7</p>
 */
@Data
public class OrgDTO extends SysOrg {

    /**
     * 机构险种编码集合
     */
    private List<String> instypes;

}
