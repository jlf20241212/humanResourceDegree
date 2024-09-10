package com.insigma.sys.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/**
 * @Author tanyj
 * @Version 2022/8/22 17:13
 * @since 2.7.0
 **/
@Data
public class ScmMyFavoritesDTO {

    private String id;

    private Integer functionId;

    private String functionRoutePath;

    private String userid;

    private Date favorTime;

    private Integer orderNo;

    private String title;

    private Boolean tableDelFlag;

    private String icon;
}

