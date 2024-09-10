package com.insigma.sys.repository;

import com.insigma.sys.entity.SysError;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysErrorRepository extends JpaRepository<SysError, String> {
    List<SysError> findAll(Specification<SysError> spec);
    @Query(value = "select s from SysError s where s.errorCode=?1")
    SysError findByErrCode(String code);
}
