package com.insigma.sys.dto;


import com.insigma.sys.entity.SysRole;

import java.util.List;

/**
 * Created by Administrator on 2019/1/9.
 */
public class SysRoleDTO {
    private Long total;
    private List<SysRole> data;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<SysRole> getData() {
        return data;
    }

    public void setData(List<SysRole> data) {
        this.data = data;
    }
}
