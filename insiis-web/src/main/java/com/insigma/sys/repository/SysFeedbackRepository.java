package com.insigma.sys.repository;

import com.insigma.sys.entity.SysFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author yinjh
 * @version 2022/2/23
 */
@Repository
public interface SysFeedbackRepository extends JpaRepository<SysFeedback, String> {
}
