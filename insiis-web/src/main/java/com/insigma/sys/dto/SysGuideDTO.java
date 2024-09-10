package com.insigma.sys.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SysGuideDTO implements Serializable {

    private String id;

    private String question_name;

    private String question_type;

    private String answer_type;

    private String answer_content;

    private String link_url;

    //@JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;

    //热度
    private long hot_count;

}
