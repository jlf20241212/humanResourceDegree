package com.insigma.sys.service.impl;

import com.insigma.framework.db.JdbcPageHelper;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.util.IDUtil;
import com.insigma.sys.dto.LazyTreeNode;
import com.insigma.sys.dto.MessageDTO;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.SysMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by yinjh on 2020/6/15.
 */
@Service
public class SysMessageServiceImpl implements SysMessageService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String currentUserId, MessageDTO messageDTO) {
        String sql = "insert into sysmessage(message_id, title, content, create_time, creator, type) values(?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, ps -> {
            ps.setString(1, messageDTO.getMessageId());
            ps.setString(2, messageDTO.getTitle());
            ps.setString(3, messageDTO.getContent());
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setString(5, currentUserId);
            ps.setString(6, messageDTO.getType());
        });
        if ("1".equals(messageDTO.getType())) {
            insertSysUserMessageList(messageDTO.getMessageId(), messageDTO.getUserIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(String currentUserId, MessageDTO messageDTO) {
        String sql = "update sysmessage set title = ?, content = ?, create_time = ?, creator = ?, type = ? where message_id = ?";
        jdbcTemplate.update(sql, ps -> {
            ps.setString(1, messageDTO.getTitle());
            ps.setString(2, messageDTO.getContent());
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, currentUserId);
            ps.setString(5, messageDTO.getType());
            ps.setString(6, messageDTO.getMessageId());
        });
        sql = "delete from sysusermessage where message_id = ?";
        jdbcTemplate.update(sql, ps -> ps.setString(1, messageDTO.getMessageId()));
        if ("1".equals(messageDTO.getType())) {
            insertSysUserMessageList(messageDTO.getMessageId(), messageDTO.getUserIds());
        }
    }

    @Override
    public PageInfo<MessageDTO> getSysMessageList(SysUser sysUser, MessageDTO queryDTO, Integer page, Integer size, boolean isManage) throws SQLException {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", sysUser.getUserId());
        StringBuffer sql = new StringBuffer();
        if (isManage) {
            sql.append("select t.message_id, t.title, t.content, t.create_time, t.type, su.displayname" +
                    "   from sysmessage t, sysuser su" +
                    "   where t.creator=su.userid ");
            if (!"1".equals(sysUser.getUserType())) {
                sql.append(" and su.userid=:userId");
            }
        } else {
            sql.append("select t.* from (select sm.message_id, " +
                    "   sm.title, " +
                    "   sm.content, " +
                    "   sm.create_time, " +
                    "   sm.type, " +
                    "   su.displayname, " +
                    "   sum.flag " +
                    "from sysmessage sm, " +
                    "   sysusermessage sum, " +
                    "   sysuser su " +
                    "where sm.message_id = sum.message_id " +
                    "   and sm.creator = su.userid " +
                    "   and sum.user_id = :userId " +
                    "   and sm.type = '1' " +
                    "union all " +
                    "select sm.message_id, " +
                    "   sm.title, " +
                    "   sm.content, " +
                    "   sm.create_time, " +
                    "   sm.type, " +
                    "   su.displayname, " +
                    "   case when sum.flag is null then '0' end " +
                    "from sysuser su, sysmessage sm left join sysusermessage sum " +
                    "   on sm.message_id = sum.message_id and sum.user_id = :userId " +
                    "where sm.creator = su.userid and sm.type = '0') t where 1=1 ");
        }
        if (!ObjectUtils.isEmpty(queryDTO.getTitle())) {
            sql.append(" and t.title like :title");
            params.put("title", "%" + queryDTO.getTitle() + "%");
        }
        if (!ObjectUtils.isEmpty(queryDTO.getFlag())) {
            sql.append(" and t.flag = :flag");
            params.put("flag", queryDTO.getFlag());
        }
        if (queryDTO.getStartDate() != null) {
            sql.append(" and t.create_time > :startDate");
            params.put("startDate", queryDTO.getStartDate());
        }
        if (queryDTO.getEndDate() != null) {
            sql.append(" and t.create_time < :endDate");
            Calendar c = Calendar.getInstance();
            c.setTime(queryDTO.getEndDate());
            c.add(Calendar.DAY_OF_MONTH, 1);
            params.put("endDate", c.getTime());
        }
        sql.append(" order by t.create_time desc, t.message_id");
        JdbcPageHelper pageHelper = new JdbcPageHelper(jdbcTemplate, page, size);
        PageInfo<MessageDTO> pageInfo = pageHelper.queryPagination(sql.toString(), params, rs -> {
            MessageDTO dto = new MessageDTO();
            dto.setMessageId(rs.getString("message_id"));
            dto.setTitle(rs.getString("title"));
            dto.setType(rs.getString("type"));
            if (ObjectUtils.isEmpty(dto.getType())) {
                dto.setType("0");
            }
//            dto.setContent(rs.getString("content"));
            dto.setCreator(rs.getString("displayname"));
            dto.setCreateTime(rs.getTimestamp("create_time"));
            if (!isManage) {
                dto.setFlag(rs.getString("flag"));
            }
            return dto;
        });
        return pageInfo;
    }

    @Override
    public MessageDTO getSysMessage(String messageId) {
        String sql = "select sm.message_id, " +
                "   sm.title, " +
                "   sm.content, " +
                "   sm.create_time, " +
                "   sm.type, " +
                "   su.displayname " +
                "from sysmessage sm, " +
                "   sysuser su " +
                "where sm.creator=su.userid" +
                "   and sm.message_id=?";
        List<MessageDTO> list = jdbcTemplate.query(sql, new Object[] {messageId}, (rs, i) -> {
            MessageDTO dto = new MessageDTO();
            dto.setMessageId(rs.getString("message_id"));
            dto.setTitle(rs.getString("title"));
            dto.setContent(rs.getString("content"));
            dto.setType(rs.getString("type"));
            if (ObjectUtils.isEmpty(dto.getType())) {
                dto.setType("0");
            }
            dto.setCreator(rs.getString("displayname"));
            dto.setCreateTime(rs.getTimestamp("create_time"));
            return dto;
        });
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public MessageDTO getSysMessage(String userId, String messageId) {
        String sql = "select sm.message_id, " +
                "   sm.title, " +
                "   sm.content, " +
                "   sm.create_time, " +
                "   sm.type, " +
                "   su.displayname, " +
                "   case when sum.flag is null then '-1' else sum.flag end as flag " +
                "from sysuser su, " +
                "   sysmessage sm left join sysusermessage sum " +
                "   on sm.message_id = sum.message_id and sum.user_id = ? " +
                "where sm.creator=su.userid and sm.message_id=?  ";
        List<MessageDTO> list = jdbcTemplate.query(sql, new Object[] {userId, messageId}, (rs, i) -> {
            MessageDTO dto = new MessageDTO();
            dto.setMessageId(rs.getString("message_id"));
            dto.setTitle(rs.getString("title"));
            dto.setContent(rs.getString("content"));
            dto.setType(rs.getString("type"));
            dto.setCreator(rs.getString("displayname"));
            dto.setCreateTime(rs.getTimestamp("create_time"));
            dto.setFlag(rs.getString("flag"));
            return dto;
        });
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public MessageDTO getSysUserMessage(String userId, String messageId) {
        String sql = "select sm.message_id, " +
                "   sm.title, " +
                "   sm.content, " +
                "   sm.create_time, " +
                "   sm.type, " +
                "   su.displayname " +
                "from sysuser su, " +
                "   sysmessage sm, sysusermessage sum " +
                "where sm.message_id = sum.message_id and (sum.user_id = ? or sm.creator = ?)" +
                "   and sm.creator=su.userid and sm.message_id=?  ";
        List<MessageDTO> list = jdbcTemplate.query(sql, new Object[] {userId, userId, messageId}, (rs, i) -> {
            MessageDTO dto = new MessageDTO();
            dto.setMessageId(rs.getString("message_id"));
            dto.setTitle(rs.getString("title"));
            dto.setContent(rs.getString("content"));
            dto.setType(rs.getString("type"));
            dto.setCreator(rs.getString("displayname"));
            dto.setCreateTime(rs.getTimestamp("create_time"));
            return dto;
        });
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFlag(String userId, String messageId) {
        String sql = "update sysusermessage set flag='1' where user_id=? and message_id=?";
        int cnt = jdbcTemplate.update(sql, ps -> {
            ps.setString(1, userId);
            ps.setString(2, messageId);
        });
        if (cnt == 0) {
            sql = "select type from sysmessage where message_id = ?";
            List<String> list = jdbcTemplate.query(sql, new Object[]{messageId}, (rs, i) -> rs.getString("type"));
            if (list != null && list.size() > 0) {
                String type = list.get(0);
                if ("0".equals(type)) {
                    sql = "insert into sysusermessage(id_, user_id, message_id, flag) values(?, ?, ?, ?)";
                    jdbcTemplate.update(sql, ps -> {
                        ps.setString(1, IDUtil.generateUUID());
                        ps.setString(2, userId);
                        ps.setString(3, messageId);
                        ps.setString(4, "1");
                    });
                }
            }

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysMessage(String messageId) {
        String sql = "delete from sysmessage where message_id=?";
        jdbcTemplate.update(sql, ps -> ps.setString(1, messageId));
        sql = "delete from sysusermessage where message_id=?";
        jdbcTemplate.update(sql, ps -> ps.setString(1, messageId));
    }

    @Override
    public List<LazyTreeNode<String>> getUserOrgTree(String parentId) {
        List<Object> params = new ArrayList<>();
        StringBuffer orgSql = new StringBuffer("select * from sysorg where ");
        if (ObjectUtils.isEmpty(parentId)) {
            orgSql.append("parentid is null");
        } else {
            orgSql.append("parentid = ?");
            params.add(parentId);
        }
        orgSql.append(" order by orgorder, orgid");
        List<LazyTreeNode<String>> orgList = jdbcTemplate.query(orgSql.toString(), params.toArray(), (rs, i) -> {
            LazyTreeNode<String> node = new LazyTreeNode<>();
            node.setId(rs.getString("orgid"));
            node.setParent(rs.getString("parentid"));
            node.setLabel(rs.getString("orgname"));
            node.setExtra("1");
            node.setIsLeaf(false);
            return node;
        });
        List<LazyTreeNode<String>> userList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(parentId)) {
            String sql = "select * from sysuser where orgid=? order by logonname";
            userList = jdbcTemplate.query(sql, new Object[] {parentId}, (rs, i) -> {
                LazyTreeNode<String> node = new LazyTreeNode<>();
                node.setId(rs.getString("userid"));
                node.setParent(rs.getString("orgid"));
                node.setLabel(rs.getString("displayname"));
                node.setExtra("2");
                node.setIsLeaf(true);
                return node;
            });
        }
        orgList.addAll(userList);
        return orgList;
    }

    private void insertSysUserMessageList(String messageId, List<String> userIds) {
        String sql = "insert into sysusermessage(id_, user_id, message_id, flag) values(?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, IDUtil.generateUUID());
                ps.setString(2, userIds.get(i));
                ps.setString(3, messageId);
                ps.setString(4, "0");
            }

            @Override
            public int getBatchSize() {
                return userIds.size();
            }
        });
    }
}
