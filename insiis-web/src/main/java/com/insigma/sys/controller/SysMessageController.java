package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.dfs.FSObject;
import com.insigma.framework.dfs.FSService;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.MessageDTO;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.SysMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

/**
 * Created by yinjh on 2020/6/16.
 */
@Slf4j
@RestController
@RequestMapping("/sys/message")
public class SysMessageController {

    @Autowired
    private SysMessageService sysMessageService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired(required = false)
    private FSService fsService;

    @PostMapping("/list")
    public ResponseMessage list(@RequestBody JSONObject jsonObject) throws SQLException {
        SysUser sysUser = currentUserService.getCurrentUser();
        MessageDTO queryDTO = jsonObject.toJavaObject(MessageDTO.class);
        Integer page = jsonObject.getInteger("page");
        Integer size = jsonObject.getInteger("size");
        PageInfo<MessageDTO> pageInfo = sysMessageService.getSysMessageList(sysUser, queryDTO, page, size, false);
        return ResponseMessage.ok(pageInfo);
    }

    @PostMapping("/save")
    public ResponseMessage save(@RequestBody MessageDTO messageDTO) {
        String userId = currentUserService.getCurrentUser().getUserId();
        if (ObjectUtils.isEmpty(messageDTO.getTitle())) {
            return ResponseMessage.error("消息标题不能为空！");
        }
        if (ObjectUtils.isEmpty(messageDTO.getContent())) {
            return ResponseMessage.error("消息内容不能为空！");
        }
        if (ObjectUtils.isEmpty(messageDTO.getType())) {
            return ResponseMessage.error("消息类型不能为空！");
        }
        if ("1".equals(messageDTO.getType())) {
            if (messageDTO.getUserIds() == null || messageDTO.getUserIds().size() == 0) {
                return ResponseMessage.error("消息通知目标不能为空！");
            }
        }
        sysMessageService.save(userId, messageDTO);
        return ResponseMessage.ok("保存成功！");
    }

    @GetMapping("/read")
    public ResponseMessage read(String messageId) {
        String userId = currentUserService.getCurrentUser().getUserId();
        MessageDTO messageDTO = sysMessageService.getSysMessage(userId, messageId);
        if ("1".equals(messageDTO.getType()) && "-1".equals(messageDTO.getFlag())) {
            return ResponseMessage.error("无权查看！");
        }
        if (!"1".equals(messageDTO.getFlag())) {
            sysMessageService.updateFlag(userId, messageId);
        }
        return ResponseMessage.ok(messageDTO);
    }


    @GetMapping("/static/images/{prefix}.{suffix}")
    public ResponseEntity view(@PathVariable String prefix, @PathVariable String suffix, String messageId) {
        try {
            SysUser sysUser = currentUserService.getCurrentUser();
            MessageDTO messageDTO = sysMessageService.getSysMessage(messageId);
            if (messageDTO != null && "1".equals(messageDTO.getType()) && !"1".equals(sysUser.getUserType())) {
                messageDTO = sysMessageService.getSysUserMessage(sysUser.getUserId(), messageId);
                if (messageDTO == null) {
                    return ResponseEntity.status(403).build();
                }
            }
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
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
