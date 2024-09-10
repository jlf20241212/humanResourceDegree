package com.insigma.sys.service;

import com.insigma.framework.db.PageInfo;
import com.insigma.web.support.entity.Aa01;

/**
 * @author yinjh
 * @version 2022/10/17
 * @since 2.7.0
 */
public interface ParamConfigService {
    PageInfo<Aa01> query(String aaa001, String aaa002, Integer page, Integer size);

    void save(Aa01 aa01, boolean isUpdate);

    void delete(String aaa001);
}
