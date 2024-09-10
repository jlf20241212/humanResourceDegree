package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "SYSERROR")
@Entity
public class SysError implements Serializable {
    @Id
    @GenericGenerator(name="idGenerator",strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    private String serviceName;
    private String functionName;
    private String errorCode;
    private String message;
    private Date createTime;
}
