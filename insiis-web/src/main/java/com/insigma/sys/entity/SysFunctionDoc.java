package com.insigma.sys.entity;

import lombok.Data;
import oracle.sql.BLOB;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
@Data
@Entity
@Table(name = "SYSFUNCTIONDOC")
public class SysFunctionDoc {
    @Id
    private long functionid;
    private String overview;
    private String detailed_view;
    private Date update_time;
    private String update_name;

}
