package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author GH
 * @ClassName: SysDownloadCenter
 * @Description: 下载中心实体类
 * @version 2021/8/4  9:53
 */

@Entity
@Table(name="SYSDOWNLOADCENTER")
@Data
public class SysDownloadCenter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    //材料名称
    private String material_name;

    //材料类型
    private String material_type;

    //材料大小
    private String material_size;

    //上传时间
    private Date upload_time;

}
