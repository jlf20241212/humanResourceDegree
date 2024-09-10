package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author yinjh
 * @version 2022/2/23
 */
@Data
@Entity
@Table(name = "SYSFEEDBACKANSWER")
public class SysFeedbackAnswer {

    @Id
    @GenericGenerator(name="idGenerator",strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;

    private String content;

    @Column(name = "USERID")
    private String userId;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "FEEDBACK_ID")
    private String feedbackId;

    private String type;

}
