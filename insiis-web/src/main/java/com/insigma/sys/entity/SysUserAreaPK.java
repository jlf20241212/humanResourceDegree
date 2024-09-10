package com.insigma.sys.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *SysUserArea联合主键
 * @Author: caic
 * @version: 10:39 2019/1/14
 * @Description:
 */
@Embeddable
@Data
public class SysUserAreaPK implements Serializable {
    private String userId;
    private String aab301;
}
