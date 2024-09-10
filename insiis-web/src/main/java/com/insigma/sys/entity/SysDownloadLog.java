package com.insigma.sys.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author yinjh
 * @version 2021/12/9
 */
@Entity
@Table(name = "SYSDOWNLOADLOG")
@Data
public class SysDownloadLog {

    @Id
    @GenericGenerator(name="idGenerator",strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;

    @Column(name = "FILE_ID")
    private String fileId;

    private String downloader;

    @Column(name = "DOWNLOAD_TIME")
    private Date downloadTime;

}
