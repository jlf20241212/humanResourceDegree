package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name="SYSGUIDE")
@Data
public class SysGuide implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @Id
    @GenericGenerator(name="idGenerator",strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;

    /**
     * 问题名称
     */
    private String question_name;

    /**
     * 问题类型
     */
    private String question_type;

    /**
     * 答案类型
     */
    private String answer_type;

    /**
     * 答案内容
     */
    private String answer_content;

    /**
     * 答案链接
     */
    private String link_url;

    /**
     * 创建时间
     */
    private Date create_time;

}
