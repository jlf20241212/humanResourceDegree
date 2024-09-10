package com.insigma.sys.service.impl;

import com.insigma.framework.db.PageInfo;
import com.insigma.framework.exception.AppException;
import com.insigma.sys.service.ParamConfigService;
import com.insigma.web.support.repository.Aa01Repository;
import com.insigma.web.support.entity.Aa01;
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
 * @author yinjh
 * @version 2022/10/17
 * @since 2.7.0
 */
@Service
public class ParamConfigServiceImpl implements ParamConfigService {

    @Autowired
    private Aa01Repository aa01Repository;

    @Override
    public PageInfo<Aa01> query(String aaa001, String aaa002, Integer page, Integer size) {
        PageInfo<Aa01> pageInfo = new PageInfo<>();

        Specification<Aa01> queryParams = (Specification<Aa01>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(null != aaa001 && !"".equals(aaa001)) {
                predicates.add(criteriaBuilder.like(root.get("aaa001"), "%" + aaa001 + "%"));
            }
            if(null != aaa002 && !"".equals(aaa002)) {
                predicates.add(criteriaBuilder.like(root.get("aaa002"), "%" + aaa002 + "%"));
            }
            Predicate[] p = new Predicate[predicates.size()];
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(p)));
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("aaa001")));
            return criteriaQuery.getRestriction();
        };
        // 分页
        Pageable pageable = PageRequest.of(page, size);
        Page<Aa01> aa01Page = aa01Repository.findAll(queryParams, pageable);
        pageInfo.setData(aa01Page.getContent());
        pageInfo.setTotal(aa01Page.getTotalElements());

        return pageInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Aa01 aa01, boolean isUpdate) {
        Aa01 aa01Temp = aa01Repository.findByAaa001(aa01.getAaa001());
        if (aa01Temp == null && isUpdate) {
            throw new AppException("参数类别【" + aa01.getAaa001() + "】不存在！");
        } else if (aa01Temp != null && !isUpdate) {
            throw new AppException("参数类别【" + aa01.getAaa001() + "】已存在！");
        }
        aa01Repository.saveAndFlush(aa01);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String aaa001) {
        aa01Repository.deleteById(aaa001);
    }
}
