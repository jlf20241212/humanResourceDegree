package com.insigma.sys.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SysOperateLogDTO {
    private String operatelogid;
    private String userid;
    private String logonlogid;
    private String logonip;
    private String operate;
    private String url;
    private Date begintime;
    private Date endtime;
    private String functionid;
    private String description;
    private String logonname;
    private String displayname;
    private String title;
    private String opDate;
}
