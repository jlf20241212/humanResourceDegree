package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.dfs.FSObject;
import com.insigma.framework.dfs.FSService;
import com.insigma.sys.dto.FeedbackAnswerDTO;
import com.insigma.sys.dto.FeedbackDTO;
import com.insigma.sys.service.SysFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yinjh
 * @version 2022/2/23
 */
@RestController
@RequestMapping("/sys/feedback")
public class SysFeedbackController {

    @Autowired
    private SysFeedbackService sysFeedbackService;

    @Autowired(required = false)
    private FSService fsService;

    @PostMapping("/query")
    public ResponseMessage query(@RequestBody JSONObject jsonObject) throws SQLException {
        FeedbackDTO queryDTO = jsonObject.toJavaObject(FeedbackDTO.class);
        Integer page = jsonObject.getInteger("page");
        Integer size = jsonObject.getInteger("size");
        PageInfo<FeedbackDTO> pageInfo = sysFeedbackService.page(queryDTO, page, size);
        return ResponseMessage.ok(pageInfo);
    }

    @PostMapping("/save")
    public ResponseMessage save(@RequestBody FeedbackDTO feedbackDTO) {
        if (ObjectUtils.isEmpty(feedbackDTO.getTitle())) {
            return ResponseMessage.error("意见标题不能为空！");
        }
        if (ObjectUtils.isEmpty(feedbackDTO.getContent())) {
            return ResponseMessage.error("意见内容不能为空！");
        }
        sysFeedbackService.save(feedbackDTO);
        return ResponseMessage.ok("提交成功！");
    }

    @PostMapping("/get/{id}")
    public ResponseMessage get(@PathVariable String id) {
        if (ObjectUtils.isEmpty(id)) {
            return ResponseMessage.error("意见ID不能为空！");
        }
        FeedbackDTO feedbackDTO = sysFeedbackService.get(id);
        return ResponseMessage.ok(feedbackDTO);
    }

    @PostMapping("/saveAnswer")
    public ResponseMessage saveAnswer(@RequestBody FeedbackAnswerDTO feedbackAnswerDTO) {
        if (ObjectUtils.isEmpty(feedbackAnswerDTO.getFeedbackId())) {
            return ResponseMessage.error("意见ID不能为空！");
        }
        if (ObjectUtils.isEmpty(feedbackAnswerDTO.getContent())) {
            return ResponseMessage.error("回复内容不能为空！");
        }
        sysFeedbackService.saveAnswer(feedbackAnswerDTO);
        return ResponseMessage.ok("提交成功！");
    }

    @PostMapping("/images")
    public ResponseMessage upload(MultipartFile file) {
        try {
            if (fsService == null) {
                return ResponseMessage.error("未启用FS");
            }
            if (ObjectUtils.isEmpty(file.getContentType())) {
                return ResponseMessage.error("content-type不能为空");
            }
            if (!file.getContentType().toLowerCase().contains("image")) {
                return ResponseMessage.error("图片格式不正确");
            }
            FSObject fsObject = new FSObject();
            fsObject.setName(file.getOriginalFilename());
            fsObject.setUploadDate(new Date());
            fsObject.setInputStream(file.getInputStream());
            fsObject.setContentType(file.getContentType());
            fsObject.setSize(file.getSize());

            Map<String, String> result = new HashMap<>();
            result.put("location", "/sys/feedback/static/images/" + fsService.pubObject(fsObject) + file.getContentType().substring(file.getContentType().lastIndexOf("/")).toLowerCase().replace("/", "."));
            return ResponseMessage.ok(result);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/static/images/{prefix}.{suffix}")
    public ResponseEntity view(@PathVariable String prefix, @PathVariable String suffix) {
        try {
            if (fsService == null) {
                return ResponseEntity.notFound().build();
            }
            FSObject fsObject;
            try {
                fsObject = fsService.getObject(prefix);
            } catch (Exception e) {
                fsObject = null;
            }
            if (fsObject == null || !fsObject.getContentType().toLowerCase().contains(suffix.toLowerCase())) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .header("Pragma", "no-cache")
                    .header("Cache-Control", "no-cache")
                    .header("Expires", "0")
                    .header("Content-Type", fsObject.getContentType())
                    .header("Content-Length", String.valueOf(fsObject.getSize()))
                    .body(new InputStreamResource(fsObject.getInputStream()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
