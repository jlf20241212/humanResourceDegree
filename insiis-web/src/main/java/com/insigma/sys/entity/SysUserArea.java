package com.insigma.sys.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: caic
 * @version: 10:39 2019/1/14
 * @Description:
 */
@Entity
@Table(name="SYSUSERAREA")
@Data
@IdClass(SysUserAreaPK.class)
public class SysUserArea implements Serializable {
    @Id
    @Column(name="userId")
    private String userId;
    @Id
    private String aab301;
}
