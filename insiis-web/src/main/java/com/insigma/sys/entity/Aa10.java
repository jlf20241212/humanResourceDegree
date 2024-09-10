package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by yinjh on 2019/1/9.
 */
@Data
@Entity
@Table(name = "AA10")
public class Aa10 implements Serializable {

    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_AAZ093")
//    @SequenceGenerator(name = "sq_aaz093", sequenceName = "sq_aaz093", allocationSize = 1)
    /*@TableGenerator(name = "sq_aaz093",
            table = "sys_sequence",
            pkColumnName = "sequence_name",
            valueColumnName = "sequence_next_value",
            pkColumnValue = "sq_aaz093",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "sq_aaz093")*/
    @GenericGenerator( name="sq_aaz093", strategy="com.insigma.framework.db.generator.TableOrSequenceGenerator",
            parameters = {
                    @Parameter( name = "table_name", value = "sys_sequence"),
                    @Parameter( name = "value_column_name", value = "sequence_next_value"),
                    @Parameter( name = "segment_column_name",value = "sequence_name"),
                    @Parameter( name = "segment_value", value = "sq_aaz093"),
                    @Parameter( name = "increment_size", value = "10"),
                    @Parameter( name = "optimizer",value = "pooled-lo")
            })
    @GeneratedValue(generator="sq_aaz093")
    private Long aaz093;

    private String aaa100;

    private String aaa102;

    private String aaa103;

    private String aaa105;

    private String aae100;

    private String aaa104;

}
