package com.insigma.sys.repository;

import com.insigma.sys.entity.SysFunctionDoc;
import com.insigma.sys.entity.SysGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysFunctionDocRepository extends JpaRepository<SysFunctionDoc,String> {
    @Query(value = "select * from SYSFUNCTIONDOC t where t.functionid = ?1", nativeQuery = true)
    List<SysFunctionDoc> findByFunctionid(Long functionid);

    void deleteByFunctionid(Long functionid);

}
