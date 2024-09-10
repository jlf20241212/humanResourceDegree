package com.insigma.sys.entity;

import com.insigma.framework.data.firewall.desensitization.annotation.IdCardDesensitization;
import com.insigma.sys.common.SysEntityListener;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: caic
 * @version: 14:50 2019/1/7
 * @Description:
 */
@Data
@Table(name = "SYSUSER")
@Entity
@EntityListeners(SysEntityListener.class)
public class SysUser implements Serializable {
    @Id
    @GenericGenerator(name="idGenerator",strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String userId;
    @Column(name="logonName")
    private String logonName;
    private String passWD;
    private String displayName;
    private Long areaId;
    private Long orgId;
    @Column(name="userState")
    private String userState;
    private String userType;
    private String cardType;
    @IdCardDesensitization
    private String cardId;
    private String tel;
    private String mobile;
    private String eMail;
    private String userAddr;
    private String remark;
    private String creatorId;
    private Date createTime;
    private Date lockTime;
    private Date unlockTime;
    private String lockReason;
    private Date userExpireDate;
    private Long failNO;
    private String pwExpireType;
    private Date pwExpireDate;
    private Date pwEditDate;
    private String signState;
    private String department;
    private String slevel;

    @Transient
    List<SysUserRole> sysUserRoleList;

    public SysUser() {
    }

    public SysUser(String userId, String logonName, String displayName, long orgId, String userState, String userType,String slevel){
        this.userId=userId;
        this.logonName=logonName;
        this.displayName=displayName;
        this.orgId=orgId;
        this.userState=userState;
        this.userType=userType;
        this.slevel=slevel;
    }
}
