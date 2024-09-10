package com.insigma.sys.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author GH
 * @version 2022/3/28
 * @since 2.6.5
 */

@Data
public class SysAppAddressDTO implements Serializable {
    private String addressId;
    private String portalUrl;
    private String appUrl;
    private String appId;
}
