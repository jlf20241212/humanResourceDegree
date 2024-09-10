package com.insigma.sys.repository;

import com.insigma.sys.entity.SysIdMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SysIdMappingRespository extends JpaRepository<SysIdMapping,String> {
    @Query(value = "select s from SysIdMapping s where s.TID=?1")
    SysIdMapping findByTID(String Tid);
}
