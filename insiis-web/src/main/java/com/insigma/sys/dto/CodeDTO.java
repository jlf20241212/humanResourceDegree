package com.insigma.sys.dto;

import com.insigma.sys.entity.Aa10;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yinjh on 2019/1/9.
 */
@Data
public class CodeDTO implements Serializable {

    private Long total;

    private List<Aa10> data;

}
