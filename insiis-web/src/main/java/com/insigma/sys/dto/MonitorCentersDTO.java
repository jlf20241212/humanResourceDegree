package com.insigma.sys.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author GH
 * @ClassName: MonitorCentersDTO
 * @Description:
 * @version 2021/12/7  15:49
 */
@Data
public class MonitorCentersDTO implements Serializable {
    private String ip;
    private String appName;
    private String timeStart;
    private String timeEnd;
    private String start;
    private String rows;
    private String uriFieldName;
    private String sessionFieldName;
    private String sqlFieldName;
    private String sort;

    private String appId;
    private String title;
    private String company;
    private Date ctime;
}
