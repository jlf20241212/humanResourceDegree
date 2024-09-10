package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="SYSFUNCTION")
@Data
public class SysMenu implements Serializable {
    @Id
    /*@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_function")
    @SequenceGenerator(name = "sq_function", sequenceName = "sq_function", allocationSize = 1)*/
    @GenericGenerator( name="sq_function", strategy="com.insigma.framework.db.generator.TableOrSequenceGenerator",
            parameters = {
                    @Parameter( name = "table_name", value = "sys_sequence"),
                    @Parameter( name = "value_column_name", value = "sequence_next_value"),
                    @Parameter( name = "segment_column_name",value = "sequence_name"),
                    @Parameter( name = "segment_value", value = "sq_function"),
                    @Parameter( name = "increment_size", value = "10"),
                    @Parameter( name = "optimizer",value = "pooled-lo")
            })
    @GeneratedValue(generator="sq_function")
    private Long functionid; //功能ID
    private String location;//链接
    private String title;//标题
    private Long parentid;//父功能ID
    private int funorder;//排序号
    private String nodetype;//节点类型：1-菜单节点，2-菜单叶子
    private String islog;//是否记录日志 1-记,0-不记
    private String developer;//开发人员
    private String icon;//节点图标
    private String description;//功能的中文描述
    private String funtype;//功能分类,0通用1系统管理2业务功能（扩展：个人、单位、机构）
    private String active;//是否有效
    private String funcode;//功能编号
    //    private String filename;//文件名
//    private String filepath;//文件路径
    private String auflag;//审核标志，0-不自动审核，1-自动审核
    private String rbflag;//操作日志回退标志，0-不可以，1-可以
    private String idpath;
    private String digest; // 摘要字段
    private String slevel;//密级字段 0-普通，1-秘密，2-机密
    @Column(name="APPID")
    private String appId;//所属应用系统
    private String openmode;//打开模式：1-tab页打开，2-浏览器tab页打开
}