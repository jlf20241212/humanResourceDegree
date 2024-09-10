package com.insigma.sys.dto;

import lombok.Data;

import java.util.Date;


@Data
public class SysFunctionDocDTO {

    private long functionid;
    private String overview;
    private String detailed_view;
    private Date update_time;
    private String update_name;
}
