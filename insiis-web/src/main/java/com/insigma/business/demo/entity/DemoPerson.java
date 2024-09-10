package com.insigma.business.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author yinjh
 * @since 2020-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("DEMO_PERSON")
public class DemoPerson implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("ID")
    private String id;

    @TableField("NAME")
    private String name;

    @TableField("SEX")
    private String sex;

    @TableField("BIRTHDAY")
    private Date birthday;

    @TableField("MOBILE")
    private String mobile;

    @TableField("ADDRESS")
    private String address;

    @TableField("PRSENO")
    private Long prseno;

    @TableField("CREATE_TIME")
    private Date create_time;


}
