package com.insigma.sys.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @author GH
 * @ClassName: SysDownloadCenterDTO
 * @Description:
 * @version 2021/8/4  10:11
 */
@Data
public class SysDownloadCenterDTO implements Serializable {

    private String id;

    //材料名称
    private String material_name;

    //材料类型
    private String material_type;

    //材料大小
    private String material_size;

    //上传时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date upload_time;

}
