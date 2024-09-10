package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 应用地址配置表
 *
 * @author yinjh
 * @version 2022/3/28
 * @since 2.6.5
 */
@Data
@Table(name = "SYSAPPADDRESS")
@Entity
public class SysAppAddress {

    /**
     * 应用地址ID
     */
    @Id
    @GenericGenerator(name="idGenerator",strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(name = "ADDRESS_ID")
    private String addressId;

    /**
     * 门户地址
     */
    @Column(name = "PORTAL_URL")
    private String portalUrl;

    /**
     * 应用地址
     */
    @Column(name = "APP_URL")
    private String appUrl;

    /**
     * 应用ID
     */
    @Column(name = "APP_ID")
    private String appId;

}
