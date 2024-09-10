package com.insigma.sys.service;

import com.insigma.framework.ResponseMessage;
//import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by yinjh on 2019/1/30.
 */
//@FeignClient(value = "insiis-service")
public interface OpLogService {
    @PostMapping(value = "/oplog/list")
    ResponseMessage queryOpLogList(@RequestParam("params") String params);

    @GetMapping("/oplog/orisource")
    ResponseMessage queryOrisource(@RequestParam("opseno") Long opseno);

    @GetMapping("/oplog/rollback")
    ResponseMessage rollback(@RequestParam("opseno") Long opseno, @RequestParam("who") String who);

}
