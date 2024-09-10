package com.insigma.sys.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zxy on 2019/1/7.
 */
@Data
public class RoleDTO implements Serializable {
    private String rolename;
    private String roledesc;
    private String roletype;
    private int orgid;
    private int areaid;
    private String creatorid;
}
