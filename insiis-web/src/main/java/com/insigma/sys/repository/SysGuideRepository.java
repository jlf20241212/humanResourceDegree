package com.insigma.sys.repository;

import com.insigma.sys.entity.SysGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysGuideRepository extends JpaRepository<SysGuide, String>, JpaSpecificationExecutor<SysGuide> {

    @Query(value = "select sg.* from SYSGUIDE sg,SYSGUIDEHOT sgh where sg.ID=sgh.ID and sg.question_type=?1 ORDER BY sgh.HOT_COUNT  DESC", nativeQuery = true)
    List<SysGuide> findByQuestion_type(String question_type);


    @Query(value = "SELECT s.QUESTION_TYPE  FROM SYSGUIDE s  GROUP BY s.QUESTION_TYPE",nativeQuery = true)
    List<String> findQuestion_type();
}

