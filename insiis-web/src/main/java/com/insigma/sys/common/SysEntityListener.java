package com.insigma.sys.common;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.util.SysUtils;
import com.insigma.sys.service.SysHashService;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

/**
 * @author jinw
 * @version 2021/8/16
 * <p>epsoft - insiis7</p>
 */
@Slf4j
public class SysEntityListener {

    @PostPersist
    public void postPersist(Object entity) {
        saveEntityHashCode(entity, PostMode.SAVE);
    }

    @PostUpdate
    public void postUpdate(Object entity) {
        saveEntityHashCode(entity, PostMode.UPDATE);
    }

    @PostRemove
    public void postRemove(Object entity) {
        saveEntityHashCode(entity, PostMode.DELETE);
    }

    public void saveEntityHashCode(Object entity, PostMode postMode) {
        log.debug("Class: {}, Spe: {}", entity.getClass().getCanonicalName(), JSONObject.toJSONString(entity));
        SysHashService sysHashService = SysUtils.getBean(SysHashService.class);
        sysHashService.saveHash(entity);
    }

    private enum PostMode {
        SAVE,
        UPDATE,
        DELETE
    }

}
