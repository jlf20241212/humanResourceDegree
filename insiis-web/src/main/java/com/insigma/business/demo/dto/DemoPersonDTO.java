package com.insigma.business.demo.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yinjh on 2020/5/28.
 */
@Data
public class DemoPersonDTO implements Serializable {
    private Boolean checked;//用于设置复选框是否选中

    private Boolean disabled;//用于设置复选框是否可编辑

    private String id;

    private String name;

    private String sex;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;

    private String mobile;

    private String address;

    private String startTime;

    private Date create_time;

}
