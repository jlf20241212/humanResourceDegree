package com.insigma.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author tanyj
 * @Version 2022/8/22 17:13
 * @since 2.7.0
 **/
@Entity
@Data
@Table(name = "SCM_MY_FAVORITES")
public class ScmMyFavorites implements Serializable {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "FUNCTIONID")
    private int functionId;

    @Column(name = "FUNCTION_ROUTE_PATH")
    private String functionRoutePath;

    @Column(name = "USERID")
    private String userid;

    @Column(name = "FAVOR_TIME")
    private Date favorTime;

    @Column(name = "ORDER_NO")
    private int orderNo;

    @Column(name = "TITLE")
    private String title;
}

