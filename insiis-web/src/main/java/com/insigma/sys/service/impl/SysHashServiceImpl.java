package com.insigma.sys.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.util.IDUtil;
import com.insigma.framework.util.SignUtil;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.SysHashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hash校验服务类
 *
 * @author yinjh
 * @version 2021/8/19
 */
@Service
public class SysHashServiceImpl implements SysHashService {

    @Value("${sys.hash.enabled:false}")
    private boolean hashEnabled;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private EntityManager entityManager;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveHash(Object entity) {
        if (!hashEnabled) {
            return;
        }
//        Object serializableId = entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        Map<String, Object> params = initParams(entity);
        String updateSql = "update syshash set record_hash=:recordHash where record_id=:recordId and record_type=:recordType";
        String insertSql = "insert into syshash(id, record_id, record_hash, record_type) values(:id, :recordId, :recordHash, :recordType)";
        int result = namedParameterJdbcTemplate.update(updateSql, params);
        if (result == 0) {
            params.put("id", IDUtil.generateUUID());
            namedParameterJdbcTemplate.update(insertSql, params);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAllHash(List<?> entities) {
        for (Object entity : entities) {
            saveHash(entity);
        }
    }

    @Override
    public boolean checkHash(Object entity) {
        if (!hashEnabled) {
            return true;
        }
        Map<String, Object> params = initParams(entity);
        String selectSql = "select record_hash from syshash where record_id=:recordId and record_type=:recordType";
        List<String> list = namedParameterJdbcTemplate.query(selectSql, params, (rs, i) -> rs.getString("record_hash"));
        if (list.size() > 0) {
            if (list.get(0).equals(params.get("recordHash"))) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Object> initParams(Object entity) {
        Object serializableId = entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        entity = entityManager.find(entity.getClass(), serializableId);
        Map map = JSONObject.parseObject(JSONObject.toJSONString(entity), Map.class);
        excludeSpecialKey(entity, map);
        formatSpecialKey(entity, map);
        Map<String, Object> params = new HashMap<>();
        params.put("recordHash", SignUtil.getSignCode(map));
        params.put("recordId", String.valueOf(serializableId));
        params.put("recordType", entity.getClass().getSimpleName());
        return params;
    }

    /**
     * 排除特殊key
     * @param entity
     * @param map
     */
    private void excludeSpecialKey(Object entity, Map map) {
        if (SysUser.class == entity.getClass()) {
            map.remove("failNO");
        }
    }

    /**
     * 格式化特殊类型的key
     * @param entity
     * @param map
     */
    private void formatSpecialKey(Object entity, Map map) {
        try {
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                ReflectionUtils.makeAccessible(field);
                if (field.getType().isAssignableFrom(Date.class)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    if (map.containsKey(field.getName())) {
                        map.put(field.getName(), sdf.format(field.get(entity)));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("格式化特殊key失败！", e);
        }
    }

}
