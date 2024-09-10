package com.insigma.sys.dto;

import com.insigma.web.support.dto.ImportConfigDTO;
import com.insigma.web.support.dto.ImportConfigDetailDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yinjh
 * @version 2023/7/18
 * @since 2.7.0
 */
@Data
public class ImportConfigReDTO implements Serializable {

    private ImportConfigDTO config;

    private List<ImportConfigDetailDTO> details;
}
