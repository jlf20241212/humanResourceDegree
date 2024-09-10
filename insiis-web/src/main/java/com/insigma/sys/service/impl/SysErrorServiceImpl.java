package com.insigma.sys.service.impl;

import com.insigma.framework.constants.SysConst;
import com.insigma.framework.util.SysUtils;
import com.insigma.sys.entity.SysError;
import com.insigma.sys.repository.SysErrorRepository;
import com.insigma.sys.service.SysErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;

@Service
public class SysErrorServiceImpl implements SysErrorService {
    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SysErrorRepository sysErrorRepository;
    @Override
    public List<SysError> queryAllErrors() {
        return sysErrorRepository.findAll();
    }

    @Override
    public List<SysError> findAllBySome(String serviceName, String functionName, String errorCode) {
        Specification queryParams = (Specification<SysError>) (root, criteriaQuery, criteriaBuilder) -> {
            try {
                List<Predicate> predicates = new ArrayList<>();
                if (null != serviceName && !"".equals(serviceName)) {
                    predicates.add((Predicate) criteriaBuilder.like(root.get("serviceName"), "%" +serviceName+ "%"));
                }
                if (null != functionName && !"".equals(functionName)) {
                    predicates.add((Predicate) criteriaBuilder.like(root.get("functionName"), "%" + functionName + "%"));
                }
                if (null != errorCode && !"".equals(errorCode)) {
                    predicates.add((Predicate) criteriaBuilder.like(root.get("errorCode"),"%"+errorCode+ "%"));
                }
                Predicate[] p = new Predicate[predicates.size()];
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(p)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return criteriaQuery.getRestriction();
        };
        //Pageable pageable = PageRequest.of(page, size);
        List<SysError> sysErrors = sysErrorRepository.findAll(queryParams);
        return sysErrors;
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void save(SysError sysError) {
        if (stringRedisTemplate == null) {
            stringRedisTemplate = SysUtils.getBean(StringRedisTemplate.class);
        }
        // xxxx add
        sysErrorRepository.save(sysError);
        stringRedisTemplate.opsForValue().set(SysConst.ERR_CACHE_KEY + sysError.getErrorCode(), sysError.getMessage());

    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void deleteError(SysError sysError) {
        if (stringRedisTemplate == null) {
            stringRedisTemplate = SysUtils.getBean(StringRedisTemplate.class);
        }
        sysErrorRepository.delete(sysError);
        stringRedisTemplate.delete(SysConst.ERR_CACHE_KEY + sysError.getErrorCode());
    }

    @Override
    public SysError getSysErrorBean(Map<String, HashMap<String,String>> map) {
        SysError sysError=new SysError();
        if (!map.get("id").get("value").equals("")){
            sysError.setId(map.get("id").get("value"));
        }
        if (!map.get("serviceName").get("value").equals("")){
            sysError.setServiceName(map.get("serviceName").get("value"));
        }
        if (!map.get("functionName").get("value").equals("")){
            sysError.setFunctionName(map.get("functionName").get("value"));
        }
        if (!map.get("errorCode").get("value").equals("")){
            sysError.setErrorCode(map.get("errorCode").get("value"));
        }
        if (!map.get("message").get("value").equals("")){
            sysError.setMessage(map.get("message").get("value"));
        }
        Date date = new Date();
        sysError.setCreateTime(date);
        return sysError;
    }

    @Override
    public SysError getSysErrorBean2(Map<String, String> map) {
        SysError sysError=new SysError();
        if (!map.get("id").equals("")){
            sysError.setId(map.get("id"));
        }
        if (!map.get("serviceName").equals("")){
            sysError.setServiceName(map.get("serviceName"));
        }
        if (!map.get("functionName").equals("")){
            sysError.setFunctionName(map.get("functionName"));
        }
        if (!map.get("errorCode").equals("")){
            sysError.setErrorCode(map.get("errorCode"));
        }
        if (!map.get("message").equals("")){
            sysError.setMessage(map.get("message"));
        }
        Date date = new Date();
        sysError.setCreateTime(date);
        return sysError;
    }

    @Override
    public SysError getSysErrorBean1(Map<String, HashMap<String,String>> map) {
        SysError sysError=new SysError();
        if (!map.get("serviceName1").get("value").equals("")){
            sysError.setServiceName(map.get("serviceName1").get("value"));
        }
        if (!map.get("functionName1").get("value").equals("")){
            sysError.setFunctionName(map.get("functionName1").get("value"));
        }
        if (!map.get("errorCode1").get("value").equals("")){
            sysError.setErrorCode(map.get("errorCode1").get("value"));
        }
        if (!map.get("message1").get("value").equals("")){
            sysError.setMessage(map.get("message1").get("value"));
        }
        Date date = new Date();
        sysError.setCreateTime(date);
        return sysError;
    }

    @Override
    public boolean queryByErrCode(String code) {
        SysError sysError=sysErrorRepository.findByErrCode(code);
        if (sysError!=null){
            return true;
        }
        return false;
    }
}
