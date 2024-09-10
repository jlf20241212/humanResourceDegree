package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 应用表
 *
 * @author yinjh
 * @version 2022/3/28
 * @since 2.6.5
 */
@Data
@Table(name = "SYSAPP")
@Entity
public class SysApp {

    /**
     * 应用ID
     */
    @Id
    @GenericGenerator(name="idGenerator",strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(name = "APP_ID")
    private String appId;

    /**
     * 应用编码
     */
    @Column(name = "APP_CODE")
    private String appCode;

    /**
     * 应用名称
     */
    @Column(name = "APP_NAME")
    private String appName;

    /**
     * 排序号
     */
    @Column(name = "ORDER_NO")
    private Long orderNo;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 是否有效，1：有效，0：无效
     */
    @Column(name = "ACTIVE")
    private String active;

    /**
     * 应用安全key
     */
    @Column(name = "SECRET_KEY")
    private String secretKey;
}
