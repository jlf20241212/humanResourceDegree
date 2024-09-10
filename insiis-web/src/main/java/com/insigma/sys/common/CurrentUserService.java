package com.insigma.sys.common;

import com.insigma.sys.entity.SysUser;

/**
 * 获取当前用户信息服务类
 * @author jinw
 * @version 2019/1/9
 * <p>epsoft - insiis7</p>
 */

public interface CurrentUserService {

    SysUser getCurrentUser();
}
