package com.insigma.sys.service.impl;

import com.insigma.framework.db.JdbcPageHelper;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.exception.AppException;
import com.insigma.framework.util.DtoEntityUtil;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.FeedbackAnswerDTO;
import com.insigma.sys.dto.FeedbackDTO;
import com.insigma.sys.entity.SysFeedback;
import com.insigma.sys.entity.SysFeedbackAnswer;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.repository.SysFeedbackAnswerRepository;
import com.insigma.sys.repository.SysFeedbackRepository;
import com.insigma.sys.repository.SysUserRepository;
import com.insigma.sys.service.SysFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yinjh
 * @version 2022/2/23
 */
@Service
public class SysFeedbackServiceImpl implements SysFeedbackService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SysFeedbackRepository sysFeedbackRepository;

    @Autowired
    private SysFeedbackAnswerRepository sysFeedbackAnswerRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @Override
    public PageInfo<FeedbackDTO> page(FeedbackDTO queryDTO, Integer page, Integer size) throws SQLException {
        SysUser sysUser = currentUserService.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        StringBuffer sql = new StringBuffer();
        sql.append("select sf.*, su.displayname from sysfeedback sf, sysuser su where sf.userid=su.userid");
        if (!ObjectUtils.isEmpty(queryDTO.getTitle())) {
            sql.append(" and sf.title like :title");
            params.put("title", "%" + queryDTO.getTitle() + "%");
        }
        if (!ObjectUtils.isEmpty(queryDTO.getAnswered())) {
            sql.append(" and sf.answered = :answered");
            params.put("answered", queryDTO.getAnswered());
        }
        if (queryDTO.getDateRange() != null && queryDTO.getDateRange().size() == 2) {
            sql.append(" and sf.create_time > :startDate and sf.create_time < :endDate");
            params.put("startDate", queryDTO.getDateRange().get(0));
            params.put("endDate", queryDTO.getDateRange().get(1));
        }
        if (!"1".equals(sysUser.getUserType())) {
            // 不是超级管理员，则只能看到自己提的建议
            sql.append(" and su.userid = :userId");
            params.put("userId", sysUser.getUserId());
        }
        sql.append(" order by sf.create_time desc");
        JdbcPageHelper pageHelper = new JdbcPageHelper(jdbcTemplate, page, size);
        PageInfo<FeedbackDTO> pageInfo = pageHelper.queryPagination(sql.toString(), params, rs -> {
            FeedbackDTO feedbackDTO = new FeedbackDTO();
            feedbackDTO.setId(rs.getString("id"));
            feedbackDTO.setTitle(rs.getString("title"));
            feedbackDTO.setAnswered(rs.getString("answered"));
            feedbackDTO.setCreateTime(rs.getTimestamp("create_time"));
            feedbackDTO.setDisplayName(rs.getString("displayname"));
            return feedbackDTO;
        });
        return pageInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(FeedbackDTO feedbackDTO) {
        SysUser sysUser = currentUserService.getCurrentUser();
        if ("1".equals(sysUser.getUserType())) {
            throw new AppException("超级管理员不可新增意见反馈！");
        }
        SysFeedback feedback = new SysFeedback();
        feedback.setTitle(feedbackDTO.getTitle());
        feedback.setContent(feedbackDTO.getContent());
        feedback.setUserId(sysUser.getUserId());
        feedback.setCreateTime(new Date());
        feedback.setAnswered("0");
        sysFeedbackRepository.saveAndFlush(feedback);
    }

    @Override
    public FeedbackDTO get(String id) {
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        SysFeedback sysFeedback = sysFeedbackRepository.findById(id).orElseThrow(() -> new AppException("意见不存在！"));
        feedbackDTO.setId(sysFeedback.getId());
        feedbackDTO.setTitle(sysFeedback.getTitle());
        feedbackDTO.setContent(sysFeedback.getContent());
        feedbackDTO.setCreateTime(sysFeedback.getCreateTime());
        feedbackDTO.setUserId(sysFeedback.getUserId());
        SysUser sysUser = sysUserRepository.findById(sysFeedback.getUserId()).orElseThrow(() -> new AppException("用户不存在"));
        feedbackDTO.setDisplayName(sysUser.getDisplayName());
        List<SysFeedbackAnswer> answerList = sysFeedbackAnswerRepository.findAllByFeedbackIdOrderByCreateTimeAsc(id);
        feedbackDTO.setAnswerList(DtoEntityUtil.trans(answerList, FeedbackAnswerDTO.class));
        return feedbackDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAnswer(FeedbackAnswerDTO feedbackAnswerDTO) {
        SysUser sysUser = currentUserService.getCurrentUser();
        String type = "1".equals(sysUser.getUserType()) ? "1" : "0";
        SysFeedback sysFeedback = sysFeedbackRepository.findById(feedbackAnswerDTO.getFeedbackId()).orElseThrow(() -> new AppException("意见不存在！"));
        sysFeedback.setAnswered(type);
        sysFeedbackRepository.saveAndFlush(sysFeedback);

        SysFeedbackAnswer sysFeedbackAnswer = new SysFeedbackAnswer();
        sysFeedbackAnswer.setContent(feedbackAnswerDTO.getContent());
        sysFeedbackAnswer.setCreateTime(new Date());
        sysFeedbackAnswer.setFeedbackId(feedbackAnswerDTO.getFeedbackId());
        sysFeedbackAnswer.setUserId(sysUser.getUserId());
        sysFeedbackAnswer.setType(type);
        sysFeedbackAnswerRepository.saveAndFlush(sysFeedbackAnswer);
    }
}
