package com.insigma.business.demo.dao;

import com.insigma.business.demo.entity.DemoPerson;
import com.insigma.framework.mybatis.BaseDAO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yinjh
 * @since 2020-05-28
 */
@Mapper
public interface DemoPersonDAO extends BaseDAO<DemoPerson> {

    DemoPerson selectDemoPersonById(String id);

}
