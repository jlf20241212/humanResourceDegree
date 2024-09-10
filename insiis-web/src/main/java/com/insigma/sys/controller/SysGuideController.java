package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.sys.dto.SysGuideDTO;
import com.insigma.sys.service.SysGuideService;
import com.insigma.web.support.annotation.OdinRequest;
import com.insigma.web.support.annotation.OdinRequestParam;
import com.insigma.web.support.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/sys/guide/Guide")
public class SysGuideController extends BaseController {

    @Autowired
    private SysGuideService sysGuideService;

    /**
     * 初始化
     *
     * @param f_form
     * @param size
     * @return
     */
    @OdinRequest(init = true)
    @PostMapping("/doInit")
    public ResponseMessage doInit(SysGuideDTO f_form, Integer size) {
        tableDataQuery(f_form, 1, size);
        return this.ok();
    }

    /**
     * 分页查询
     *
     * @param queryDTO
     * @param page
     * @param size
     * @param name
     * @return
     */
    @OdinRequest
    @PostMapping("/doGridQuery/{name}")
    public ResponseMessage doGridQuery(@OdinRequestParam("f_form") SysGuideDTO queryDTO,
                                       @OdinRequestParam("t_tableData_page") Integer page,
                                       @OdinRequestParam("t_tableData_size") Integer size,
                                       @PathVariable String name) {
        tableDataQuery(queryDTO, page, size);
        return this.ok();
    }

    /**
     * 查询
     *
     * @param queryDTO
     * @param size
     * @return
     */
    @OdinRequest
    @PostMapping("/query")
    public ResponseMessage query(@OdinRequestParam("f_form") SysGuideDTO queryDTO,
                                 Integer size) {
        tableDataQuery(queryDTO, 1, size);
        return this.ok();
    }

    private void tableDataQuery(SysGuideDTO queryDTO, Integer page, Integer size) {
        this.pageQuery("t_tableData", () -> {
            this.set("page", page); // 需要将前端表格中:currentPage定义的值设置成page的值
            return sysGuideService.queryGuideList(queryDTO, page, size);
        });
    }

    /**
     * 新增
     *
     * @return
     */
    @OdinRequest
    @PostMapping("/add")
    public ResponseMessage add() {
        this.clearForm("f_guideForm");
        this.set("dialogFormVisible", true);
        this.set("dialogFormTitle", "新增");
        return this.ok();
    }

    /**
     * 修改
     *
     * @param id
     * @return
     */
    @OdinRequest
    @PostMapping("/update")
    public ResponseMessage update(String id) {
        SysGuideDTO dto = sysGuideService.queryGuide(id);

        if(!ObjectUtils.isEmpty(dto.getAnswer_content())){
            String s = dto.getAnswer_content().replaceAll("<br/>", "\n");
            dto.setAnswer_content(s);
        }

        this.toForm("f_guideForm", dto);
        this.set("dialogFormVisible", true);
        this.set("dialogFormTitle", "修改");
        return this.ok();
    }

    /**
     * 批量删除
     *
     * @return
     */
    @OdinRequest(refresh = true)
    @PostMapping("/batchDelete")
    public ResponseMessage batchDelete() {
        List<SysGuideDTO> list = this.getCheckedTableData("t_tableData", SysGuideDTO.class);
        if (list.size() == 0) {
            return this.error("请勾选需要删除的内容！");
        }
        sysGuideService.deleteGuides(list);
        return this.ok("删除成功！");
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @OdinRequest
    @PostMapping("/delete")
    public ResponseMessage delete(String id) {
        sysGuideService.delete(id);
        this.setBackFunType("rQuery");
        return this.ok("删除成功！");
    }

    /**
     * 保存
     *
     * @param sysGuideDTO
     * @param name
     * @return
     */
    @OdinRequest
    @PostMapping("/doSave/{name}")
    public ResponseMessage doSave(@OdinRequestParam("f_guideForm") SysGuideDTO sysGuideDTO, @PathVariable String name) {
        sysGuideService.saveGuide(sysGuideDTO);
        this.set("dialogFormVisible", false);
//        doInit(new SysGuideDTO(),size);
        this.setBackFunType("rQuery");
        return this.ok("保存成功！");
    }

    /**
     * 点击问题名称触发热度+1
     */
    @GetMapping("/hot/{id}")
    public ResponseMessage hot(@PathVariable String id) {
        sysGuideService.hot(id);
        return this.ok();
    }

    /**
     * @Description: 根据不同选择进行详细的查询数据
     * @author GH
     * @version 2021/7/23 14:53
     * @return
     */
    @PostMapping("/findByQuestion_type")
    public ResponseMessage findByQuestion_type(@RequestBody JSONObject jsonObjecte) {
        Integer page = jsonObjecte.getInteger("page");
        Integer size = jsonObjecte.getInteger("size");
        String question_type = jsonObjecte.getString("question_type");
        String sort = jsonObjecte.getString("sort");
        String str = jsonObjecte.getString("str");
        return ResponseMessage.ok(sysGuideService.findByQuestion_type(question_type,sort,str,page,size));
    }


    /**
     * @Description: 查询首页引导信息
     * @author GH
     * @version 2021/7/30   13:09
     * @return
     */
    @GetMapping("/findFrontPage")
    public ResponseMessage findFrontPage() {
        List<HashMap<String, Object>> map = sysGuideService.findFrontPage();
        return ResponseMessage.ok(map);
    }


    /**
     * @Description: 更多详情页面
     * @author GH
     * @version 2021/7/30   13:09
     * @return
     */
    @PostMapping("/findDetailsPage/{id}")
    public ResponseMessage findDetailsPage(@PathVariable String id) {
        sysGuideService.hot(id);
        return ResponseMessage.ok(sysGuideService.findDetailsPage(id));
    }
}
