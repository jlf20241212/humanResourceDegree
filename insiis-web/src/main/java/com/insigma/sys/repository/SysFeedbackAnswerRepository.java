package com.insigma.sys.repository;

import com.insigma.sys.entity.SysFeedbackAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yinjh
 * @version 2022/2/23
 */
@Repository
public interface SysFeedbackAnswerRepository extends JpaRepository<SysFeedbackAnswer, String> {
    List<SysFeedbackAnswer> findAllByFeedbackIdOrderByCreateTimeAsc(String feedbackId);
}
