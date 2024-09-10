package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.dfs.FSObject;
import com.insigma.framework.dfs.FSService;
import com.insigma.framework.util.IDUtil;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.LazyTreeNode;
import com.insigma.sys.dto.MessageDTO;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.SysMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yinjh
 * @version 2021/8/23
 */
@RestController
@RequestMapping("/sys/message/manage")
public class SysMessageManageController {

    @Autowired
    private SysMessageService sysMessageService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired(required = false)
    private FSService fsService;

    @PostMapping("/init")
    public ResponseMessage init() {
        return ResponseMessage.ok("", IDUtil.generateUUID());
    }

    @PostMapping("/list")
    public ResponseMessage list(@RequestBody JSONObject jsonObject) throws SQLException {
        SysUser sysUser = currentUserService.getCurrentUser();
        MessageDTO queryDTO = jsonObject.toJavaObject(MessageDTO.class);
        Integer page = jsonObject.getInteger("page");
        Integer size = jsonObject.getInteger("size");
        PageInfo<MessageDTO> pageInfo = sysMessageService.getSysMessageList(sysUser, queryDTO, page, size, true);
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
        MessageDTO message = sysMessageService.getSysMessage(messageDTO.getMessageId());
        if (message == null) {
            sysMessageService.save(userId, messageDTO);
        } else {
            sysMessageService.update(userId, messageDTO);
        }
        return ResponseMessage.ok("保存成功！");
    }

    @GetMapping("/read")
    public ResponseMessage read(String messageId) {
        MessageDTO messageDTO = sysMessageService.getSysMessage(messageId);
        return ResponseMessage.ok(messageDTO);
    }

    @PostMapping("/delete")
    public ResponseMessage delete(@RequestBody MessageDTO messageDTO) {
        if (ObjectUtils.isEmpty(messageDTO.getMessageId())) {
            return ResponseMessage.error("消息ID不能为空！");
        }
        sysMessageService.deleteSysMessage(messageDTO.getMessageId());
        return ResponseMessage.ok("删除成功！");
    }

    @GetMapping("/userOrgTree")
    public ResponseMessage getUserOrgTree(String parentId) {
        List<LazyTreeNode<String>> list = sysMessageService.getUserOrgTree(parentId);
        return ResponseMessage.ok(list);
    }

    @PostMapping("/images")
    public ResponseMessage upload(MultipartFile file, String messageId) {
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
            result.put("location", "/sys/message/static/images/" + fsService.pubObject(fsObject) + file.getContentType().substring(file.getContentType().lastIndexOf("/")).toLowerCase().replace("/", ".") + "?messageId=" + messageId);
            return ResponseMessage.ok(result);
        } catch (Exception e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
}
