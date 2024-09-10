package com.insigma.sys.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by yinjh on 2020/6/15.
 */
@Data
public class MessageDTO implements Serializable {

    private String messageId;

    private String title;

    private String content;

    private String creator;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String flag; // 0：未读，1：已读

    private List<String> userIds;

    private Date startDate;

    private Date endDate;

    private String type;

}
