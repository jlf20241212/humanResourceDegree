package com.insigma.sys.service;

import com.insigma.framework.ResponseMessage;
import com.insigma.sys.dto.ScmMyFavoritesDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author tanyj
 * @Version 2022/8/23 09:53
 * @since 2.7.0
 **/
@Service
public interface ScmMyFavoritesService {

    /**
     * 新增我的收藏
     *
     * @param currentUserId     currentUserId
     * @param scmMyFavoritesDTO scmMyFavoritesDTO
     */
    ResponseMessage addScmMyFavorites(String currentUserId, ScmMyFavoritesDTO scmMyFavoritesDTO);

    /**
     * 根据用户id查询我的收藏
     *
     * @param userid userid
     * @return
     */
    List<ScmMyFavoritesDTO> getScmMyFavorites(String userid);

    /**
     * 根据用户id删除我的收藏
     *
     * @param currentUserId     currentUserId
     * @param scmMyFavoritesDTO scmMyFavoritesDTO
     */
    void deleteScmMyFavorites(String currentUserId, ScmMyFavoritesDTO scmMyFavoritesDTO);


    /**
     * 根据用户id更新我的收藏
     */
    void orderScmMyFavorites(String userId, ScmMyFavoritesDTO scmMyFavoritesDTO);
}
