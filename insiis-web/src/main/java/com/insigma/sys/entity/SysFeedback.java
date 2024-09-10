package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author yinjh
 * @version 2022/2/23
 */
@Data
@Entity
@Table(name = "SYSFEEDBACK")
public class SysFeedback {

    @Id
    @GenericGenerator(name="idGenerator",strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;

    private String title;

    private String content;

    @Column(name = "USERID")
    private String userId;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    private String answered;

}
