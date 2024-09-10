package com.insigma.sys.service.impl;
import com.insigma.framework.exception.AppException;
import com.insigma.framework.util.DtoEntityUtil;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.SysFunctionDocDTO;
import com.insigma.sys.entity.SysFunctionDoc;
import com.insigma.sys.repository.SysFunctionDocRepository;
import com.insigma.sys.service.SysFunctionDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@Service
public class SysFunctionDocServiceImpl implements SysFunctionDocService {

    @Autowired
    private SysFunctionDocRepository sysFunctionDocRepository;
    @Autowired
    private CurrentUserService currentUserService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDoc(SysFunctionDocDTO sysFunctionDocDTO) {
        String overviewStr = sysFunctionDocDTO.getOverview();
        String detailed_viewStr = sysFunctionDocDTO.getDetailed_view();
        Long functionid = sysFunctionDocDTO.getFunctionid();
        //检测传入数据是否为空
        if(ObjectUtils.isEmpty(overviewStr)||ObjectUtils.isEmpty(detailed_viewStr)||functionid==null )
            throw new AppException("传入数据异常！");
        try {
            SysFunctionDoc sysFunctionDoc = DtoEntityUtil.trans(sysFunctionDocDTO, SysFunctionDoc.class);
            //设置添加/修改时间
            sysFunctionDoc.setUpdate_time(new Date());
            //设置修改人名称
            String logonName = currentUserService.getCurrentUser().getLogonName();
            sysFunctionDoc.setUpdate_name(logonName);
            sysFunctionDocRepository.save(sysFunctionDoc);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SysFunctionDocDTO queryByFunctionID(Long functionid) {
        List<SysFunctionDoc> sysFunctionDocList=sysFunctionDocRepository.findByFunctionid(functionid);
        if (sysFunctionDocList.size() > 0) {
            SysFunctionDoc sysFunctionDoc = sysFunctionDocList.get(0);
            return DtoEntityUtil.trans(sysFunctionDoc, SysFunctionDocDTO.class);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFunctionid(Long functionid) {
        try {
            sysFunctionDocRepository.deleteByFunctionid(functionid);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}

