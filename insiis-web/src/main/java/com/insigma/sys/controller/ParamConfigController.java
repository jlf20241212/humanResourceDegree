package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.exception.AppException;
import com.insigma.sys.service.ParamConfigService;
import com.insigma.web.support.entity.Aa01;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author yinjh
 * @version 2022/10/17
 * @since 2.7.0
 */
@RestController
@RequestMapping("/sys/param/config")
public class ParamConfigController {

    @Autowired
    private ParamConfigService paramConfigService;

    @GetMapping("/query")
    public ResponseMessage query(String aaa001, String aaa002, Integer page, Integer size) {
        PageInfo<Aa01> pageInfo = paramConfigService.query(aaa001, aaa002, page, size);
        return ResponseMessage.ok(null, pageInfo);
    }

    @PostMapping("/insert")
    public ResponseMessage insert(@RequestBody Aa01 aa01) {
        paramConfigService.save(aa01, false);
        return ResponseMessage.ok("保存成功");
    }

    @PostMapping("/update")
    public ResponseMessage update(@RequestBody Aa01 aa01) {
        paramConfigService.save(aa01, true);
        return ResponseMessage.ok("修改成功");
    }

    @PostMapping("/delete")
    public ResponseMessage delete(@RequestBody JSONObject jsonObject) throws AppException {
        String aaa001 = jsonObject.getString("aaa001");
        paramConfigService.delete(aaa001);
        return ResponseMessage.ok("删除成功");
    }

}
