package com.insigma.sys.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.insigma.sys.entity.SysFeedbackAnswer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author yinjh
 * @version 2022/2/23
 */
@Data
public class FeedbackDTO implements Serializable {

    private String id;

    private String title;

    private String content;

    private String userId;

    private String displayName;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String answered;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private List<Date> dateRange;

    List<FeedbackAnswerDTO> answerList;

}
