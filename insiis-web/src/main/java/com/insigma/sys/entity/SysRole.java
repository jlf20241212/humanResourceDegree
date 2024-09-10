package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by zxy on 2019/1/4.
 */
@Entity
@Table(name="SYSROLE")
@Data
public class SysRole {
    @Id
    @GenericGenerator(name="idGenerator",strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String roleid;
    @Column(name="rolename")
    private String rolename;
    @Column(name="roledesc")
    private String roledesc;
    @Column(name="rolecode")
    private String rolecode;
    @Column(name="roletype")
    private String roletype;
    @Column(name="orgid" )
    private Long orgid;
    @Column(name="areaid" )
    private Long areaid;
    @Column(name="creatorid")
    private String creatorid;

}
