package com.insigma.sys.service;

import com.insigma.framework.db.PageInfo;
import com.insigma.sys.dto.LazyTreeNode;
import com.insigma.sys.dto.MessageDTO;
import com.insigma.sys.entity.SysUser;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by yinjh on 2020/6/15.
 */
public interface SysMessageService {

    void save(String currentUserId, MessageDTO messageDTO);

    void update(String currentUserId, MessageDTO messageDTO);

    PageInfo<MessageDTO> getSysMessageList(SysUser sysUser, MessageDTO queryDTO, Integer page, Integer size, boolean isManage) throws SQLException;

    MessageDTO getSysMessage(String messageId);

    MessageDTO getSysMessage(String userId, String messageId);

    MessageDTO getSysUserMessage(String userId, String messageId);

    void updateFlag(String userId, String messageId);

    void deleteSysMessage(String messageId);

    List<LazyTreeNode<String>> getUserOrgTree(String parentId);
}
