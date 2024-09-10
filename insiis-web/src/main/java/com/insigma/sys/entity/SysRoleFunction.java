package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Administrator on 2019/1/15.
 */
@Entity
@Table(name="SYSROLEFUNCTION")
@Data
public class SysRoleFunction {
    @Id
    @GenericGenerator(name="idGenerator",strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String relationid;
    @Column(name="roleid")
    private String roleid;
    @Column(name="functionid")
    private Long functionid;
}
