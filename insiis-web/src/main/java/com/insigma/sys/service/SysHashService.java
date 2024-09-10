package com.insigma.sys.service;

import java.util.List;

/**
 * @author yinjh
 * @version 2021/8/19
 */
public interface SysHashService {
    void saveHash(Object entity);

    void saveAllHash(List<?> entities);

    boolean checkHash(Object entity);
}
