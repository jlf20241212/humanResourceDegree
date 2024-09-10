package com.insigma.sys.service;

import com.insigma.framework.db.PageInfo;
import com.insigma.sys.dto.SysGuideDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public interface SysGuideService {

    PageInfo<SysGuideDTO> queryGuideList(SysGuideDTO queryDTO, Integer page, Integer size)throws SQLException;

    SysGuideDTO queryGuide(String id);

    void delete(String id);

    void deleteGuides( List<SysGuideDTO> list);

    void saveGuide(SysGuideDTO sysGuideDTO);

    void hot(String id);

    PageInfo<SysGuideDTO> findByQuestion_type(String question_type, String sort, String str, Integer page, Integer size);

    List<HashMap<String, Object>> findFrontPage();

    SysGuideDTO findDetailsPage(String id);
}
