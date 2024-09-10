package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.exception.AppException;
import com.insigma.sys.dto.CodeDTO;
import com.insigma.sys.entity.Aa10;
import com.insigma.sys.service.SysCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by yinjh on 2019/1/9.
 */
@RestController
@RequestMapping("/sys/code")
public class SysCodeController {

    @Autowired
    private SysCodeService sysCodeService;

    @PostMapping("/initCodeTypes")
    public ResponseMessage initCodeTypes(@RequestBody JSONObject jsonObject) {
        JSONObject codeTypes = sysCodeService.getCodeTypes(jsonObject);
        return ResponseMessage.ok(codeTypes);
    }

    @GetMapping("/query")
    public ResponseMessage query(String aaa100, String aaa103, Integer page, Integer size) {
        CodeDTO codeDTO = sysCodeService.query(aaa100, aaa103, page, size);
        return ResponseMessage.ok(null, codeDTO);
    }

    @PostMapping("/save")
    public ResponseMessage save(@RequestBody Aa10 aa10) {
        sysCodeService.saveCode(aa10);
        return ResponseMessage.ok("保存成功");
    }

    @PostMapping("/delete")
    public ResponseMessage delete(@RequestBody JSONObject jsonObject) throws AppException {
        try {
            Long aaz093=Long.valueOf(jsonObject.get("aaz093").toString());
            sysCodeService.deleteCode(aaz093);
        }catch (Exception e){
            throw new AppException("数据转换异常！");
        }
        return ResponseMessage.ok("删除成功");
    }
}
