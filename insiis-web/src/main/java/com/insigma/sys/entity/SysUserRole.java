package com.insigma.sys.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: caic
 * @version: 15:44 2019/1/12
 * @Description:
 */
@Entity
@Table(name="SYSUSERROLE")
@Data
@IdClass(SysUserRolePK.class)
public class SysUserRole implements Serializable {
    @Id
    @Column(name="userId")
    private String userId;
    @Id
    private String roleId;
    private String dispatchAuth;
}
