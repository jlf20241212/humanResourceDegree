package com.insigma.sys.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.exception.AppException;
import com.insigma.sys.dto.CodeDTO;
import com.insigma.sys.entity.Aa10;
import com.insigma.sys.repository.SysCodeRepository;
import com.insigma.sys.service.SysCodeService;
import com.insigma.web.support.repository.CodeTypeRepository;
import com.insigma.web.support.entity.CodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinjh on 2019/1/9.
 */
@Service
public class SysCodeServiceImpl implements SysCodeService {

    @Autowired
    private SysCodeRepository sysCodeRepository;

    @Autowired
    private CodeTypeRepository codeTypeRepository;

    @Override
    public CodeDTO query(String aaa100, String aaa103, Integer page, Integer size) {

        CodeDTO codeDTO = new CodeDTO();

        Specification queryParams = (Specification<Aa10>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(null != aaa100 && !"".equals(aaa100)) {
                predicates.add(criteriaBuilder.like(root.get("aaa100"), "%" + aaa100 + "%"));
            }
            if(null != aaa103 && !"".equals(aaa103)) {
                predicates.add(criteriaBuilder.like(root.get("aaa103"), "%" + aaa103 + "%"));
            }
            Predicate[] p = new Predicate[predicates.size()];
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(p)));
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("aaa100")), criteriaBuilder.asc(root.get("aaa102")));
            return criteriaQuery.getRestriction();
        };
        // 分页
        Pageable pageable = PageRequest.of(page, size);
        Page<Aa10> codePage = sysCodeRepository.findAll(queryParams, pageable);
        System.out.println(codePage);
        codeDTO.setData(codePage.getContent());
        codeDTO.setTotal(codePage.getTotalElements());

        return codeDTO;
    }

    @Override
    public JSONObject getCodeTypes(JSONObject jsonObject) {
        // 获取CodeTypes start
        List<String> list = new ArrayList<>();
        // 将Set中的数据拷贝到List中
        list.addAll(jsonObject.keySet());
        List<CodeType> codeTypeList = codeTypeRepository.findByCodetypeInOrderByCodetypeAscKeyAsc(list);
        //System.out.println(codeTypeList);
        JSONObject codeTypes = new JSONObject();
        codeTypeList.stream()
                .filter(ct -> jsonObject.getJSONArray(ct.getCodetype()).size() == 0)
                .forEach(ct -> {
                    JSONObject ctObj = new JSONObject();
                    ctObj.put("key", ct.getKey());
                    ctObj.put("value", ct.getValue());
                    if(codeTypes.getJSONArray(ct.getCodetype()) == null) {
                        JSONArray jsonArray = new JSONArray();
                        codeTypes.put(ct.getCodetype(), jsonArray);
                    }
                    codeTypes.getJSONArray(ct.getCodetype()).add(ctObj);
                });
        return jsonObject.fluentPutAll(codeTypes);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveCode(Aa10 aa10) {
        if (aa10.getAaz093() != null) {
            Aa10 aa10Temp = sysCodeRepository.findByAaa100AndAaa102(aa10.getAaa100(), aa10.getAaa102());
            if (!aa10Temp.getAaz093().equals(aa10.getAaz093())) {
                throw new AppException("代码值重复！");
            }
        }
        sysCodeRepository.save(aa10);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCode(Long aaz093) throws AppException {
        if(aaz093 == null) {
            throw new AppException("数据异常！");
        }
        Aa10 aa10 = sysCodeRepository.getOne(aaz093);
        if(aa10 == null) {
            throw new AppException("当前数据已删除或不存在！");
        }
        sysCodeRepository.delete(aa10);
    }
}
