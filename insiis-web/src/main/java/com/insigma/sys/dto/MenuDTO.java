package com.insigma.sys.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MenuDTO implements Serializable {
    private long functionid; //功能ID
    private String path;//链接
    private String title;//标题
    private long parentid;//父功能ID
    private String icon;//节点图标
    private int funorder;//排序号
    private String nodetype;//节点类型：1-菜单节点，2-菜单叶子
    private String description; // 功能描述
    private String appid;//所属应用系统
    private String openmode;//打开模式：1-tab页打开，2-浏览器tab页打开
}
