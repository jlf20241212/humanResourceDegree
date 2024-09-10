package com.insigma.sys.repository;

import com.insigma.sys.entity.Aa10;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yinjh on 2019/1/9.
 */
@Repository
public interface SysCodeRepository extends JpaRepository<Aa10, Long> {

    Page<Aa10> findAll(Specification queryParams, Pageable pageable);

    /**
     * 通用查询aa10的方法
     * @author caic
     * @param aaa100
     * @return
     */
    @Query(value = "select t from Aa10 t where t.aaa100=?1")
    List<Aa10> findByAaa100(String aaa100);

    Aa10 findByAaa100AndAaa102(String aaa100,String aaa102);
}
