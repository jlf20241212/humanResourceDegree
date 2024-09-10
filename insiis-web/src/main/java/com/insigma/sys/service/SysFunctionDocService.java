package com.insigma.sys.service;

import com.insigma.sys.dto.SysFunctionDocDTO;

public interface SysFunctionDocService {

    void saveDoc(SysFunctionDocDTO sysFunctionDocDTO);
    SysFunctionDocDTO queryByFunctionID(Long functionid);
    void deleteByFunctionid(Long functionid);
}
