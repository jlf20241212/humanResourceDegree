package com.insigma.sys.repository;

import com.insigma.sys.entity.SysGuideType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author GH
 * @ClassName: SysGuideTypeRepository
 * @Description:
 * @version 2021/8/213:10
 */

@Repository
public interface SysGuideTypeRepository extends JpaRepository<SysGuideType, String>, JpaSpecificationExecutor<SysGuideType> {

    @Query(value = "SELECT s.* FROM SYSGUIDETYPE s ORDER BY  s.ORDER_NO",nativeQuery = true)
    List<SysGuideType> findType_name();

}
