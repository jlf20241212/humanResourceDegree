package com.insigma.sys.service;

import com.insigma.sys.entity.Aa26;

import java.util.List;

/**
 * @Author: caic
 * @version: 14:37 2019/1/22
 * @Description:
 */
public interface Aa26Service {
    Aa26 findByAab301(String aab301);

    List<Aa26> findByAa148(String aaa148);

    List<Aa26> findAll();
}
