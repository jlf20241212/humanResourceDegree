package com.insigma.business.demo.controller;

import com.insigma.business.demo.bpo.DemoPersonBPO;
import com.insigma.business.demo.dto.DemoPersonDTO;
import com.insigma.framework.ResponseMessage;
import com.insigma.web.support.annotation.OdinRequest;
import com.insigma.web.support.annotation.OdinRequestParam;
import com.insigma.web.support.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 人员增删改查demo
 * <p>
 * 实体类传参开发方式，需继承BaseController，并且需要实体类传参的方法上加上@OdinRequest
 * 在doInit方法上需要加上注解@OdinRequest(init = true)，init = true为初始化字典数据和模块信息，其他方法上只需加@OdinRequest即可，不需要参数
 * 方法参数如果不指定@OdinRequestParam注解，则按照参数名去取前端传过来的数据，并将其转化成实体类
 * 如果指定了@OdinRequestParam注解，并且设定了value值，则根据value值去取前端传过来的数据
 * 其中@OdinRequestParam参数required默认为true，即如果找不到对应的数据，则返回错误，当设置成false，则不会
 * 如果需要接收前端传过来的表格数据，则可以通过定义PageInfo<T>来接收数据
 *
 * @author yinjh
 * @since 2020-05-28
 */
@RestController
@RequestMapping("/business/demo/DemoPerson")
public class DemoPersonController extends BaseController {

    @Autowired
    private DemoPersonBPO demoPersonBPO;

    @OdinRequest(init = true)
    @PostMapping("/doInit")
    public ResponseMessage doInit(DemoPersonDTO f_form, Integer t_tableData_size) {
        tableDataQuery(f_form, 1, t_tableData_size);
        return this.ok();
    }

    @OdinRequest
    @PostMapping("/query")
    public ResponseMessage query(@OdinRequestParam("f_form") DemoPersonDTO queryDTO,
                                 //@OdinRequestParam("t_tableData") PageInfo<DemoPersonDTO> pageInfo, // 没用到，只作如何取表格数据的演示
                                 Integer t_tableData_size) {
        tableDataQuery(queryDTO, 1, t_tableData_size);
//        this.set("page", 1); // 需要将前端表格中:currentPage定义的值设置成1
        return this.ok();
    }

    @OdinRequest
    @PostMapping("/doGridQuery/{name}")
    public ResponseMessage doGridQuery(@OdinRequestParam("f_form") DemoPersonDTO queryDTO,
                                       @OdinRequestParam("t_tableData_page") Integer page,
                                       @OdinRequestParam("t_tableData_size") Integer size,
                                       @PathVariable String name) {
        tableDataQuery(queryDTO, page, size);
//        this.set("page", page); // 需要将t_tableData_page的值赋值到前端表格:currentPage的值中
        return this.ok();
    }

    @OdinRequest
    @PostMapping("/add")
    public ResponseMessage add() {
        this.clearForm("f_personForm");
        this.set("dialogFormVisible", true);
        this.set("dialogFormTitle", "新增人员");
        return this.ok();
    }

    @OdinRequest
    @PostMapping("/update")
    public ResponseMessage update(String id) {
        DemoPersonDTO dto = demoPersonBPO.queryPerson(id);
        this.toForm("f_personForm", dto);
        this.set("dialogFormVisible", true);
        this.set("dialogFormTitle", "修改人员");
        return this.ok();
    }

    @OdinRequest(refresh = true)
    @PostMapping("/batchDelete")
    public ResponseMessage batchDelete() {
        List<DemoPersonDTO> list = this.getCheckedTableData("t_tableData", DemoPersonDTO.class);
        if (list.size() == 0) {
            return this.error("请勾选需要删除的人员！");
        }
        List<String> ids = list.stream().map(o -> o.getId()).collect(Collectors.toList());
        demoPersonBPO.deletePersons(ids);
//        this.refresh();
        return this.ok("删除成功！");
    }

    @OdinRequest(refresh = true) // refresh = true 请求结束后刷新页面
    @PostMapping("/delete")
    public ResponseMessage delete(String id) {
        List<String> ids = Arrays.asList(id);
        demoPersonBPO.deletePersons(ids);
//        this.refresh();
        return this.ok("删除成功！");
    }

    @OdinRequest(refresh = true)
    @PostMapping("/doSave/{name}")
    public ResponseMessage doSave(@OdinRequestParam("f_personForm") DemoPersonDTO demoPersonDTO, @PathVariable String name) {

        if (ObjectUtils.isEmpty(demoPersonDTO.getId())) {
            DemoPersonDTO dto = demoPersonBPO.selectOne(demoPersonDTO);
            if (dto != null) {
                return this.error("不可添加重复数据!！");
            }
        }
        demoPersonBPO.savePerson(demoPersonDTO);
        this.set("dialogFormVisible", false);
        //this.refresh();
        return this.ok("保存成功！");
    }

    public void tableDataQuery(DemoPersonDTO queryDTO, Integer t_tableData_page, Integer t_tableData_size) {
        this.pageQuery("t_tableData", () -> {
            this.set("page", t_tableData_page); // 需要将前端表格中:currentPage定义的值设置成page的值
            return demoPersonBPO.queryPersonListByExport(queryDTO, t_tableData_page, t_tableData_size);
        }, (demoPersonDTO, checkbox) -> {
            if ("1".equals(demoPersonDTO.getSex())) {
//                checkbox.setDisabled(true); // 设置行复选框不可编辑
            } else {
//                checkbox.setChecked(true); // 设置行复选框已选中
            }
        });
    }

}
