package com.insigma.sys.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Author: caic
 * @version: 15:42 2019/1/13
 * @Description: SysUserRole表的联合主键
 */
@Embeddable
@Data
public class SysUserRolePK implements Serializable {

    private String userId;
    private String roleId;
}
