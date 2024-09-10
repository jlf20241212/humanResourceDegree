package com.insigma.sys.service;

import com.insigma.framework.db.PageInfo;
import com.insigma.sys.dto.FeedbackAnswerDTO;
import com.insigma.sys.dto.FeedbackDTO;

import java.sql.SQLException;

/**
 * @author yinjh
 * @version 2022/2/23
 */
public interface SysFeedbackService {
    PageInfo<FeedbackDTO> page(FeedbackDTO queryDTO, Integer page, Integer size) throws SQLException;

    void save(FeedbackDTO feedbackDTO);

    FeedbackDTO get(String id);

    void saveAnswer(FeedbackAnswerDTO feedbackAnswerDTO);
}
