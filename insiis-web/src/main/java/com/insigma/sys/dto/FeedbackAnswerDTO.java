package com.insigma.sys.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yinjh
 * @version 2022/2/23
 */
@Data
public class FeedbackAnswerDTO implements Serializable {

    private String id;

    private String content;

    private String userId;

    private String displayName;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String type;

    private String feedbackId;

}
