package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author GH
 * @ClassName: SysGuideType
 * @Description:
 * @version 2021/8/213:05
 */

@Entity
@Table(name="SYSGUIDETYPE")
@Data
public class SysGuideType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name="idGenerator",strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;

    //分类编码
    private String type_code;

    //分类名称
    private String type_name;

    //分类图标
    private String type_icon;

    //排序号
    private Integer order_no;

}
