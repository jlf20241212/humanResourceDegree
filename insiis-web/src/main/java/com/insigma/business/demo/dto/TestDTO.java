package com.insigma.business.demo.dto;

import com.insigma.framework.data.firewall.desensitization.annotation.NameDesensitization;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yinjh
 * @since 2024/5/22
 */
@Data
public class TestDTO implements Serializable {

    @NameDesensitization
    private String name;

}
