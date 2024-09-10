package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONArray;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.exception.AppException;
import com.insigma.framework.util.TreeUtil;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.ImportConfigReDTO;
import com.insigma.sys.dto.MenuDTO;
import com.insigma.sys.service.MenuService;
import com.insigma.web.support.dto.ImportConfigDTO;
import com.insigma.web.support.dto.ImportConfigDetailDTO;
import com.insigma.web.support.fileaccess.excel.ExcelFactory;
import com.insigma.web.support.service.ImportConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yinjh
 * @version 2023/7/18
 * @since 2.7.0
 */
@RestController
@RequestMapping("/sys/import/config")
public class ImportConfigController {

    @Autowired
    private ImportConfigService importConfigService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private CurrentUserService currentUserService;

    @GetMapping("/queryMenuList")
    public ResponseMessage queryMenuList() {
        List<MenuDTO> menuDTOS = menuService.getMenuList(currentUserService.getCurrentUser());
        JSONArray jsonArray = TreeUtil.listToTree(JSONArray.parseArray(JSONArray.toJSONString(menuDTOS)), "functionid", "parentid", "children", "0");
        return ResponseMessage.ok("查询成功", jsonArray);
    }

    @GetMapping("/query")
    public ResponseMessage query(String configCode, String configName) {
        List<ImportConfigDTO> list = importConfigService.queryConfigList(configCode, configName);
        return ResponseMessage.ok(list);
    }

    @GetMapping("/queryConfig")
    public ResponseMessage queryConfig(String configId) {
        ImportConfigDTO config = importConfigService.queryConfig(configId);
        List<ImportConfigDetailDTO> details = importConfigService.queryConfigDetails(configId);
        ImportConfigReDTO reDTO = new ImportConfigReDTO();
        reDTO.setConfig(config);
        reDTO.setDetails(details);
        return ResponseMessage.ok(reDTO);
    }

    @PostMapping("/importExcelTemplate")
    public ResponseMessage importExcelTemplate(MultipartFile file, String sheetNameOrIndex, Integer headerRowIndex) {
        try {
            List<ImportConfigDetailDTO> list = new ArrayList<>();
            ExcelFactory.readSax(file.getInputStream(), sheetNameOrIndex, (sheetIndexOrName, rowIndex, rowList) -> {
                if (rowIndex == headerRowIndex) {
                    for (Object field : rowList) {
                        ImportConfigDetailDTO detailDTO = new ImportConfigDetailDTO();
                        detailDTO.setOriginFieldName(String.valueOf(field));
                        detailDTO.setFieldType("0");
                        list.add(detailDTO);
                    }
                }
            });
            return ResponseMessage.ok(list);
        } catch (Exception e) {
            throw new AppException("读取模板失败：" + e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseMessage save(@RequestBody ImportConfigReDTO reqDTO) {
        importConfigService.saveConfig(reqDTO.getConfig(), reqDTO.getDetails());
        return ResponseMessage.ok("保存成功");
    }

    @PostMapping("/delete/{configId}")
    public ResponseMessage delete(@PathVariable String configId) {
        importConfigService.deleteConfig(configId);
        return ResponseMessage.ok("删除成功");
    }
}
