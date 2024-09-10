package com.insigma.sys.service.impl;

import com.insigma.sys.entity.Aa26;
import com.insigma.sys.repository.Aa26Repository;
import com.insigma.sys.service.Aa26Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @Author: caic
 * @version: 14:38 2019/1/22
 * @Description:
 */
@Service("Aa26Service")
public class Aa26ServiceImpl implements Aa26Service {
    @Autowired
    Aa26Repository aa26Repository;

    @Override
    public Aa26 findByAab301(String aab301) {
        return aa26Repository.findByAab301(aab301);
    }

    @Override
    public List<Aa26> findByAa148(String aaa148) {
        if (ObjectUtils.isEmpty(aaa148)) {
            return aa26Repository.findAa26sByAaa148IsNullOrderByAab301();
        }
        return aa26Repository.findAa26sByAaa148OrderByAab301(aaa148);
    }

    @Override
    public List<Aa26> findAll() {
        return aa26Repository.findAll();
    }
}
