package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author yinjh
 * @version 2021/10/9
 */
@Data
@Table(name = "SYSWARNINGLOG")
@Entity
public class SysWarningLog {

    @Id
    @GenericGenerator(name="idGenerator",strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;

    private String userid;

    private String sessionid;

    private String content;

    @Column(name="create_time")
    private Date createTime;

}
