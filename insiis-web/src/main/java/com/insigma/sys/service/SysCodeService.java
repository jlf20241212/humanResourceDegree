package com.insigma.sys.service;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.exception.AppException;
import com.insigma.sys.dto.CodeDTO;
import com.insigma.sys.entity.Aa10;

/**
 * Created by yinjh on 2019/1/9.
 */
public interface SysCodeService {

    CodeDTO query(String aaa100, String aaa103, Integer page, Integer size);

    JSONObject getCodeTypes(JSONObject jsonObject);

    void saveCode(Aa10 aa10);

    void deleteCode(Long aaz093) throws AppException;
}
