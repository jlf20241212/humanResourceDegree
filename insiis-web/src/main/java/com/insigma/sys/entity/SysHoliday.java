package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author yinjh
 * @version 2021/6/16
 */
@Data
@Entity
@Table(name = "SYSHOLIDAY")
public class SysHoliday {

    @Id
    @GenericGenerator(name="idGenerator",strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;

    private int year;

    private int month;

    private int day;
}
