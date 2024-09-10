package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author jinw
 * @version 2019/1/7
 * <p>epsoft - insiis7</p>
 */
@Entity
@Data
@Table(name = "sysorg")
public class SysOrg implements Serializable {
    @Id
    /*@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "id_sq_orgid")
    @SequenceGenerator(name = "id_sq_orgid",sequenceName = "sq_orgid",allocationSize = 1)*/
    @GenericGenerator( name="sq_orgid", strategy="com.insigma.framework.db.generator.TableOrSequenceGenerator",
            parameters = {
                    @Parameter( name = "table_name", value = "sys_sequence"),
                    @Parameter( name = "value_column_name", value = "sequence_next_value"),
                    @Parameter( name = "segment_column_name",value = "sequence_name"),
                    @Parameter( name = "segment_value", value = "sq_orgid"),
                    @Parameter( name = "increment_size", value = "10"),
                    @Parameter( name = "optimizer",value = "pooled-lo")
            })
    @GeneratedValue(generator="sq_orgid")
    private Long orgid;
    private String orgname;
    private String orgentercode;
    private Long parentid;
    private String shortname;
    private String regioncode;
    private String leader;
    private String linkman;
    private String tel;
    private String orgaddr;
    private String orgdesc;
    private Integer orgorder;
    private String orgstate;
    private String superdept;
    private String orgautocode;
    private String zip;
    private String idpath;
    private String rate;

}
