package com.insigma.sys.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author GH
 * @version 2022/3/28
 * @since 2.6.5
 */

@Data
public class SysAppDTO implements Serializable {
    private String appId;
    private String appCode;
    private String appName;
    private Long orderNo;
    private Date createTime;
    private Date updateTime;
    private String active;
    private String secretKey;
}
