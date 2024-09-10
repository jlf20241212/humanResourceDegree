package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.ScmMyFavoritesDTO;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.ScmMyFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



/**
 * @Author tanyj
 * @Version 2022/8/23 11:27
 * @since 2.7.0
 **/
@RestController
@RequestMapping("/scmMyFavorites")
public class ScmMyFavoritesController {

    @Autowired
    private ScmMyFavoritesService scmMyFavoritesService;

    @Autowired
    private CurrentUserService currentUserService;

    /**
     * 新增我的收藏
     *
     * @param jsonObject jsonObject
     */
    @PostMapping("/addScmMyFavorites")
    public ResponseMessage addScmMyFavorites(@RequestBody JSONObject jsonObject) {
        ScmMyFavoritesDTO scmMyFavoritesDTO = new ScmMyFavoritesDTO();
        scmMyFavoritesDTO.setTitle(jsonObject.getString("title"));
        scmMyFavoritesDTO.setFunctionRoutePath(jsonObject.getString("path"));
        SysUser currentUser = currentUserService.getCurrentUser();
        return scmMyFavoritesService.addScmMyFavorites(currentUser.getUserId(), scmMyFavoritesDTO);
    }

    /**
     * 根据用户id查询我的收藏
     *
     * @return
     */
    @RequestMapping("/getScmMyFavorites")
    public ResponseMessage getScmMyFavorites() {
        SysUser currentUser = currentUserService.getCurrentUser();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", scmMyFavoritesService.getScmMyFavorites(currentUser.getUserId()));
        jsonObject.put("total", scmMyFavoritesService.getScmMyFavorites(currentUser.getUserId()).size());
        return ResponseMessage.ok("查询成功", jsonObject);
    }

    /**
     * 根据用户id删除我的收藏
     *
     * @param jsonObject jsonObject
     */
    @RequestMapping("/deleteScmMyFavorites")
    public ResponseMessage deleteScmMyFavorites(@RequestBody JSONObject jsonObject) {
        ScmMyFavoritesDTO scmMyFavoritesDTO = new ScmMyFavoritesDTO();
        scmMyFavoritesDTO.setTitle(jsonObject.getString("title"));
        scmMyFavoritesDTO.setFunctionRoutePath(jsonObject.getString("functionRoutePath"));
        SysUser currentUser = currentUserService.getCurrentUser();
        scmMyFavoritesService.deleteScmMyFavorites(currentUser.getUserId(), scmMyFavoritesDTO);
        return ResponseMessage.ok();
    }

    /**
     * 根据用户id更新我的收藏
     *
     * @param jsonObject jsonObject
     */
    @RequestMapping("/orderScmMyFavorites")
    public ResponseMessage orderScmMyFavorites(@RequestBody JSONObject jsonObject){
        System.out.println(jsonObject);
        SysUser currentUser = currentUserService.getCurrentUser();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject oneSmf = jsonArray.getJSONObject(i);
            ScmMyFavoritesDTO scmMyFavoritesDTO = new ScmMyFavoritesDTO();
            scmMyFavoritesDTO.setTitle(oneSmf.getString("title"));
            scmMyFavoritesDTO.setFunctionRoutePath(oneSmf.getString("functionRoutePath"));
            scmMyFavoritesDTO.setOrderNo(i);
            scmMyFavoritesService.orderScmMyFavorites(currentUser.getUserId(), scmMyFavoritesDTO);
        }
        return ResponseMessage.ok();
    }
}
