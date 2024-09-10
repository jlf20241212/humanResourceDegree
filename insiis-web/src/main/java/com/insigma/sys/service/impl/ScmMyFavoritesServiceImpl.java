package com.insigma.sys.service.impl;

import com.insigma.framework.ResponseMessage;
import com.insigma.framework.util.IDUtil;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.ScmMyFavoritesDTO;
import com.insigma.sys.service.ScmMyFavoritesService;
import com.insigma.web.support.entity.MdParam;
import com.insigma.web.support.util.SysFunctionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author tanyj
 * @Version 2022/8/23 10:00
 * @since 2.7.0
 **/
@Service
public class ScmMyFavoritesServiceImpl implements ScmMyFavoritesService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CurrentUserService currentUserService;
    /**
     * 新增我的收藏
     *
     * @param scmMyFavoritesDTO scmMyFavoritesDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage addScmMyFavorites(String currentUserId, ScmMyFavoritesDTO scmMyFavoritesDTO) {
        if(scmMyFavoritesDTO != null && scmMyFavoritesDTO.getFunctionRoutePath() != null){
            String sql = "select count(1) from SCM_MY_FAVORITES where USERID = ? and FUNCTION_ROUTE_PATH = ?";
            int count = jdbcTemplate.queryForObject(sql, Integer.class, currentUserId, scmMyFavoritesDTO.getFunctionRoutePath());
            if (count > 0) {
                return ResponseMessage.error("该功能已收藏");
            }else {
                ScmMyFavoritesDTO sfd = this.getMdParamByPath(scmMyFavoritesDTO.getFunctionRoutePath());
                String insertSql = "insert into SCM_MY_FAVORITES(ID,FUNCTIONID,FUNCTION_ROUTE_PATH,USERID,FAVOR_TIME,ORDER_NO,TITLE) values(?,?,?,?,?,?,?)";
                scmMyFavoritesDTO.setId(IDUtil.generateUUID());
                scmMyFavoritesDTO.setFavorTime(new Date());
                scmMyFavoritesDTO.setOrderNo(getMaxOrderNo() + 1);
                jdbcTemplate.update(insertSql, scmMyFavoritesDTO.getId(), sfd.getFunctionId(), scmMyFavoritesDTO.getFunctionRoutePath(), currentUserId, scmMyFavoritesDTO.getFavorTime(), scmMyFavoritesDTO.getOrderNo(), scmMyFavoritesDTO.getTitle());
                return ResponseMessage.ok("收藏成功");
            }
        }
        return ResponseMessage.error("未获取tab详情");
    }

    /**
     * 根据用户id查询我的收藏
     *
     * @param userid userid
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ScmMyFavoritesDTO> getScmMyFavorites(String userid) {
        String sql = "select * from SCM_MY_FAVORITES where USERID = ? order by ORDER_NO";
        List<ScmMyFavoritesDTO> list = jdbcTemplate.query(sql, new Object[]{userid}, (resultSet, i) -> {
            ScmMyFavoritesDTO scmMyFavoritesDTO = new ScmMyFavoritesDTO();
            scmMyFavoritesDTO.setId(resultSet.getString("ID"));
            scmMyFavoritesDTO.setFunctionId(resultSet.getInt("FUNCTIONID"));
            scmMyFavoritesDTO.setFunctionRoutePath(resultSet.getString("FUNCTION_ROUTE_PATH"));
            scmMyFavoritesDTO.setUserid(resultSet.getString("USERID"));
            scmMyFavoritesDTO.setFavorTime(resultSet.getDate("FAVOR_TIME"));
            scmMyFavoritesDTO.setOrderNo(resultSet.getInt("ORDER_NO"));
            scmMyFavoritesDTO.setTitle(resultSet.getString("TITLE"));
            scmMyFavoritesDTO.setTableDelFlag(false);
            ScmMyFavoritesDTO smf = this.getMdParamByPath(scmMyFavoritesDTO.getFunctionRoutePath());
            if(smf != null){
                scmMyFavoritesDTO.setIcon(smf.getIcon());
            }
            return scmMyFavoritesDTO;
        });
        return list;
    }

    /**
     * 根据用户id删除我的收藏
     *
     * @param scmMyFavoritesDTO scmMyFavoritesDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteScmMyFavorites(String currentUserId, ScmMyFavoritesDTO scmMyFavoritesDTO) {
        String sql = "delete from SCM_MY_FAVORITES where USERID = ? and FUNCTION_ROUTE_PATH = ?";
        jdbcTemplate.update(sql, currentUserId, scmMyFavoritesDTO.getFunctionRoutePath());
    }


    /**
     * 根据用户id更新我的收藏
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void orderScmMyFavorites(String userId, ScmMyFavoritesDTO scmMyFavoritesDTO) {
        String sql = "update SCM_MY_FAVORITES set ORDER_NO = ? where USERID = ? and FUNCTION_ROUTE_PATH = ?";
        jdbcTemplate.update(sql, scmMyFavoritesDTO.getOrderNo(), userId, scmMyFavoritesDTO.getFunctionRoutePath());
    }

    public int getMaxOrderNo(){
        String sqlCount = "select count(1) from SCM_MY_FAVORITES where USERID = ?";
        int count = jdbcTemplate.queryForObject(sqlCount, new Object[]{currentUserService.getCurrentUser().getUserId()}, Integer.class);
        if(count == 0) {
            return 0;
        }else {
            String sql = "select MAX(ORDER_NO) from SCM_MY_FAVORITES where USERID = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{currentUserService.getCurrentUser().getUserId()}, Integer.class);
        }
    }

    public ScmMyFavoritesDTO getMdParamByPath(String path){
        String sql = "select * from SYSFUNCTION where LOCATION = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{path}, (resultSet, i) -> {
            ScmMyFavoritesDTO scmMyFavoritesDTO = new ScmMyFavoritesDTO();
            scmMyFavoritesDTO.setFunctionId(resultSet.getInt("FUNCTIONID"));
            scmMyFavoritesDTO.setFunctionRoutePath(resultSet.getString("LOCATION"));
            scmMyFavoritesDTO.setTitle(resultSet.getString("TITLE"));
            scmMyFavoritesDTO.setIcon(resultSet.getString("ICON"));
            return scmMyFavoritesDTO;
        });
    }
}
