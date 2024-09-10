package com.insigma.sys.dto;

import lombok.Data;

import java.util.Date;
@Data
public class SysLogonLogDTO {

    private String logonlogid;
    private String userid;
    private String logonip;
    private Date logontime;
    private Date logofftime;
    private String successflag = "1";
    private String failreason;
    private String logoffreason;
    private String sessionid;
    private String logonname;
    private String displayname;
    private String opDate;
    private String browser;
    private String os;
}
