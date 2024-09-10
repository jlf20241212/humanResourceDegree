package com.insigma.business.common.controller;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.db.PageInfo;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yinjh
 * @version 2022/6/15
 * @since 2.6.5
 */
@RestController
@RequestMapping("/common/search")
public class CommonSearchController {

    @Autowired
    private SysUserRepository sysUserRepository;

    @PostMapping("/person")
    public ResponseMessage personSearch(@RequestBody JSONObject jsonObject) {
        String keyWord = jsonObject.getString("keyWord");
        Integer page = jsonObject.getInteger("page");
        Integer size = jsonObject.getInteger("size");
        SysUser params = new SysUser();
        params.setLogonName(keyWord);
        page = page < 0 ? 0 : page;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("logonName")));

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("logonName", ExampleMatcher.GenericPropertyMatchers.contains());
        Page<SysUser> p = sysUserRepository.findAll(Example.of(params, matcher), pageable);
        PageInfo<SysUser> pageInfo = new PageInfo<>();
        pageInfo.setTotal(p.getTotalElements());
        pageInfo.setData(p.getContent());
        return ResponseMessage.ok(pageInfo);
    }

}
