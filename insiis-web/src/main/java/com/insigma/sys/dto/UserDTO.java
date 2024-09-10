package com.insigma.sys.dto;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author: caic
 * @version: 16:15 2019/1/11
 * @Description:
 */
@Entity
@Table(name="sysuser")
@Data
public class UserDTO {
    @Id
    private String userId;
    private String logonName;
    private String displayName;
    private Long orgId;
    private String userState;
    private String userType;
    private String areaId;
    private String cardType;
    private String cardId;
    private String tel;
    private String visible;

//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinColumn(name = "userType",referencedColumnName="aaa102",insertable=false,updatable=false)
//    private Aa10 userTypeCode;

//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinColumn(name = "userState",referencedColumnName="aaa102")
//    private Aa10 userStateCode;
}
