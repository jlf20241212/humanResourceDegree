package com.insigma.sys.repository;

import com.insigma.sys.entity.Aa26;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: caic
 * @version: 10:28 2019/1/14
 * @Description:
 */
@Repository
public interface Aa26Repository extends JpaRepository<Aa26,String> {
    List<Aa26> findByidpathStartingWith(String aab301);
    Aa26 findByAab301(String aab301);

    @Query(value = "select a from Aa26 a where a.aab301=?1 and ?2 like concat(a.idpath, '%')")
    List<Aa26> findByAab301AndIdPath(String aab301, String idpath);

    List<Aa26> findAa26sByAaa148OrderByAab301(String aaa148);

    List<Aa26> findAa26sByAaa148IsNullOrderByAab301();
}
