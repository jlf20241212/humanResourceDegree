package com.insigma.sys.entity;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name="SYSGUIDEHOT")
@Data
public class SysGuideHot implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @Id
    @Column(name="ID")
    private String id;

    /**
     * 热度
     */
    @Column(name="HOT_COUNT")
    private long hot_count;
}
