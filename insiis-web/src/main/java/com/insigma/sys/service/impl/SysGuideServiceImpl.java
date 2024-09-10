package com.insigma.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.insigma.business.demo.entity.DemoPerson;
import com.insigma.framework.db.JdbcPageHelper;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.util.DtoEntityUtil;
import com.insigma.framework.web.securities.service.SysCacheService;
import com.insigma.sys.dto.SysGuideDTO;
import com.insigma.sys.entity.Aa10;
import com.insigma.sys.entity.SysGuide;
import com.insigma.sys.entity.SysGuideHot;
import com.insigma.sys.entity.SysGuideType;
import com.insigma.sys.repository.SysCodeRepository;
import com.insigma.sys.repository.SysGuideHotRepository;
import com.insigma.sys.repository.SysGuideRepository;
import com.insigma.sys.repository.SysGuideTypeRepository;
import com.insigma.sys.service.SysGuideService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service("SysGuideService")
public class SysGuideServiceImpl implements SysGuideService {

    @Autowired
    private SysGuideRepository sysGuideRepository;

    @Autowired
    private SysGuideHotRepository sysGuideHotRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SysCacheService sysCacheService;

    @Autowired
    private SysCodeRepository sysCodeRepository;

    @Autowired
    private SysGuideTypeRepository sysGuideTypeRepository;


    /**
     * 分页查询
     *
     * @param queryDTO
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<SysGuideDTO> queryGuideList(SysGuideDTO queryDTO, Integer page, Integer size) throws SQLException {
        //用于保存查询条件
        Map<String, Object> params = new HashMap<>();

        //创建SQL语句
        StringBuffer sql = new StringBuffer("select sg.QUESTION_TYPE, " +
                "   sg.QUESTION_NAME, " +
                "   sg.ID," +
                "   sg.CREATE_TIME, " +
                "   sgh.HOT_COUNT " +
                "from SYSGUIDE sg, " +
                "   SYSGUIDEHOT sgh " +
                "where sg.ID=sgh.ID ");

        if (!ObjectUtils.isEmpty(queryDTO.getQuestion_type())) {
            //拼接在上面的SQL上
            sql.append(" and sg.QUESTION_TYPE = :question_type");
            params.put("question_type", queryDTO.getQuestion_type());
        }

        if (!ObjectUtils.isEmpty(queryDTO.getQuestion_name())) {
            sql.append(" and sg.QUESTION_NAME like :question_name");
            params.put("question_name", "%" + queryDTO.getQuestion_name() + "%");
        }

        sql.append(" ORDER BY sg.CREATE_TIME DESC");

        //热度排序  可以设置当某个字段为不同的状态的时候就按不同的方法去排序
//        s.append(" ORDER BY sgh.HOT_COUNT DESC");

        JdbcPageHelper pageHelper = new JdbcPageHelper(jdbcTemplate, page, size);
        PageInfo<SysGuideDTO> pageInfo = pageHelper.queryPagination(sql.toString(), params, rs -> {
            SysGuideDTO dto = new SysGuideDTO();
            //赋值
            dto.setHot_count(Long.parseLong(rs.getString("hot_count")));
            dto.setQuestion_type(rs.getString("question_type"));
            dto.setQuestion_name(rs.getString("question_name"));
            dto.setCreate_time(rs.getTimestamp("create_time"));
            dto.setId(rs.getString("id"));
            return dto;
        });
        return pageInfo;
    }


    /**
     * 查询
     *
     * @param id
     * @return
     */
    @Override
    public SysGuideDTO queryGuide(String id) {
        //根据id查询数据  要不然为空
        SysGuide guide = sysGuideRepository.findById(id).orElse(null);
        SysGuideDTO dto = DtoEntityUtil.trans(guide, SysGuideDTO.class);
        return dto;
    }

    /**
     * 批量删除
     *
     * @param list
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGuides(List<SysGuideDTO> list) {
        List<SysGuide> guideList = DtoEntityUtil.trans(list, SysGuide.class);
        List<SysGuideHot> ids = new ArrayList<>();
        for (SysGuide sysGuide : guideList) {
            SysGuideHot sysGuideHot = new SysGuideHot();
            sysGuideHot.setId(sysGuide.getId());
            ids.add(sysGuideHot);
        }
        //删除sysguide表数据
        sysGuideRepository.deleteInBatch(guideList);

        //删除与其对应的sysguidehot表数据
        sysGuideHotRepository.deleteAll(ids);
    }


    /**
     * 保存
     *
     * @param sysGuideDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGuide(SysGuideDTO sysGuideDTO) {

        String answer_content = sysGuideDTO.getAnswer_content();

        if (!ObjectUtils.isEmpty(answer_content)) {
            String all = answer_content.replaceAll("\n", "<br/>");
            sysGuideDTO.setAnswer_content(all);
        };

        String answer_type = sysGuideDTO.getAnswer_type();

        if (answer_type.equals("0")) {//文本
            sysGuideDTO.setLink_url("");
        }
        if (answer_type.equals("1")) {//链接
            sysGuideDTO.setAnswer_content("");
        }
        SysGuide sysGuide = DtoEntityUtil.trans(sysGuideDTO, SysGuide.class);

        //设置添加/修改时间
        sysGuide.setCreate_time(new Date());

        //save方法回去判断是否含有id  有进行修改  反之进行添加
        SysGuide guide = sysGuideRepository.save(sysGuide);
        SysGuideHot guideHot = sysGuideHotRepository.findById(guide.getId()).orElse(null);
        SysGuideHot sysGuideHot = new SysGuideHot();
        if (guideHot == null) {//没数据表示 新增
            sysGuideHot.setHot_count(0);
        } else {//有数据表示 编辑
            long hot_count = guideHot.getHot_count();
            sysGuideHot.setHot_count(hot_count);
        }
        sysGuideHot.setId(guide.getId());
        sysGuideHotRepository.save(sysGuideHot);
        // 清除菜单缓存
//        sysCacheService.clearFunctionCache(sysGuideDTO.getAnswer_content());
//        sysCacheService.clearFunctionCache(sysGuideDTO.getLink_url());

    }


    /**
     * 删除
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        sysGuideRepository.deleteById(id);
        sysGuideHotRepository.deleteById(id);
    }

    /**
     * 热度
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void hot(String id) {
        SysGuideHot sysGuideHot = sysGuideHotRepository.findById(id).orElse(null);
        sysGuideHot.setHot_count(sysGuideHot.getHot_count() + 1L);
        sysGuideHotRepository.save(sysGuideHot);
    }

    /**
     * @return
     * @Description: 根据不同选择来查询数据
     * @author GH
     * @version 2021/7/23 15:05
     */
    @SneakyThrows
    @Override
    public PageInfo<SysGuideDTO> findByQuestion_type(String question_type, String sort, String str, Integer page, Integer size) {

        //用于保存查询条件
        Map<String, Object> params = new HashMap<>();

        //创建SQL语句
        StringBuffer sql = new StringBuffer("select sg.QUESTION_TYPE, " +
                "   sg.QUESTION_NAME, " +
                "   sg.ID," +
                "   sgh.HOT_COUNT " +
                "from SYSGUIDE sg, " +
                "   SYSGUIDEHOT sgh " +
                "where sg.ID=sgh.ID ");

        if (!ObjectUtils.isEmpty(question_type)) {
            //拼接在上面的SQL上
            sql.append(" and sg.QUESTION_TYPE = :question_type");
            params.put("question_type", question_type);
        }

        //其他选项(关键字)
        if (!ObjectUtils.isEmpty(str)) {
            //拼接在上面的SQL上
            sql.append(" and sg.QUESTION_NAME like :question_name");
            params.put("question_name", "%" + str + "%");
        }

        //排序
        if (!ObjectUtils.isEmpty(sort)) {
            if (sort.equals("热度")) {
                sql.append(" ORDER BY sgh.HOT_COUNT DESC");
            } else if (sort.equals("时间")) {
                sql.append(" ORDER BY sg.CREATE_TIME DESC");
            }
        } else {
            sql.append(" ORDER BY sg.CREATE_TIME DESC");
        }

        JdbcPageHelper pageHelper = new JdbcPageHelper(jdbcTemplate, page, size);
        PageInfo<SysGuideDTO> pageInfo = pageHelper.queryPagination(sql.toString(), params, rs -> {
            SysGuideDTO dto = new SysGuideDTO();
            //赋值
            dto.setHot_count(Long.parseLong(rs.getString("hot_count")));

            Aa10 aa10 = sysCodeRepository.findByAaa100AndAaa102("GUIDE_QUESTION_TYPE", rs.getString("question_type"));
            dto.setQuestion_type(aa10.getAaa103());
            dto.setQuestion_name(rs.getString("question_name"));
            dto.setId(rs.getString("id"));
            return dto;
        });
        return pageInfo;
    }

    /**
     * @return
     * @Description: 查询首页引导信息
     * @author GH
     * @version 2021/7/30   13:10
     */
    @Override
    public List<HashMap<String, Object>> findFrontPage() {
        //查询所有问题类型
//        List<String> question_type = sysGuideRepository.findQuestion_type();

        List<SysGuideType> sysGuideTypes = sysGuideTypeRepository.findType_name();
        List<String> question_type = sysGuideTypes.stream().map(o -> o.getType_code()).collect(Collectors.toList());

        List<HashMap<String, Object>> arrayList = new ArrayList<>();

        for (String s : question_type) {//0 {} 1{} 2{}
//            Aa10 type = sysCodeRepository.findByAaa100AndAaa102("GUIDE_QUESTION_TYPE", s);
            SysGuideType sysGuideType = sysGuideTypeRepository.findById(s).orElse(null);
            //键是类型  值是问题名称
            HashMap<String, Object> map = new HashMap<>();
            //所有的问题名称
            List<SysGuideDTO> list = new ArrayList<>();
            //每个类型的所有内容
            List<SysGuide> sysGuides = sysGuideRepository.findByQuestion_type(s);
            for (SysGuide sysGuide : sysGuides) {
                SysGuideDTO sysGuideDTO = new SysGuideDTO();
                String name = sysGuide.getQuestion_name();
                String id = sysGuide.getId();
                sysGuideDTO.setId(id);
                sysGuideDTO.setQuestion_name(name);
                list.add(sysGuideDTO);
            }
//            map.put("img", sysGuideType.getType_icon());
            map.put("img", "");
            map.put("type", sysGuideType.getType_name());
            map.put("question_type", s);
            map.put("question_name", list);
            arrayList.add(map);
        }

        return arrayList;
    }

    /**
     * @return
     * @Description: 查询详细页面信息
     * @author GH
     * @version 2021/7/30   13:10
     */
    @Override
    public SysGuideDTO findDetailsPage(String id) {
        //根据id查询数据  要不然为空
        SysGuide guide = sysGuideRepository.findById(id).orElse(null);
        SysGuideHot sysGuideHot = sysGuideHotRepository.findById(id).orElse(null);
        String question_type = guide.getQuestion_type();
        Aa10 type = sysCodeRepository.findByAaa100AndAaa102("GUIDE_QUESTION_TYPE", question_type);
        SysGuideDTO dto = DtoEntityUtil.trans(guide, SysGuideDTO.class);
        dto.setQuestion_type(type.getAaa103());
        dto.setHot_count(sysGuideHot.getHot_count());
        return dto;
    }
}
