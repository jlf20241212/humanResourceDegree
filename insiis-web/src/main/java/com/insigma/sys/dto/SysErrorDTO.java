package com.insigma.sys.dto;

import lombok.Data;

import java.util.Date;
@Data
public class SysErrorDTO {
    private String id;
    private String serviceName;
    private String functionName;
    private String errorCode;
    private String message;
    private Date createTime;
}
