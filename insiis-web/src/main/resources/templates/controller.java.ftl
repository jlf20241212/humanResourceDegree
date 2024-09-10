package ${package.Controller};

import com.insigma.framework.ResponseMessage;
import com.insigma.web.support.annotation.OdinRequest;
import com.insigma.web.support.annotation.OdinRequestParam;
import com.insigma.web.support.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ${package.Service}.${table.serviceName};

<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>

/**
* <p>
* ${table.comment!} 前端控制器
* </p>
*
* @author ${author}
* @since ${date}
*/
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("${cfg.requestMapping}")
public class ${table.controllerName} extends BaseController {

    @Autowired
    private ${table.serviceName} ${table.serviceName?uncap_first};

    /**
    * 页面初始化
    */
    @OdinRequest(init = true)
    @PostMapping("/doInit")
    public ResponseMessage doInit() {
        return this.ok();
    }

    /**
    * 查询
    */
    @OdinRequest
    @PostMapping("/query")
    public ResponseMessage query() {
        this.set("page", 1); // 需要将前端ep-table中:currentPage定义的值设置成1
        return this.ok();
    }

    /**
    * 分页查询
    */
    @OdinRequest
    @PostMapping("/doGridQuery/{name}")
    public ResponseMessage doGridQuery(@OdinRequestParam("t_tableData_page") Integer page,
                                       @OdinRequestParam("t_tableData_size") Integer size,
                                       @PathVariable String name) {
        this.set("page", page); // 需要将t_tableData_page的值赋值到前端ep-table表格:currentPage的值中
        return this.ok();
    }

    /**
    * 保存
    */
    @OdinRequest(refresh = true)
    @PostMapping("/doSave/{name}")
    public ResponseMessage doSave(@PathVariable String name) {
        //this.refresh(); // 可以通过@OdinRequest(refresh = true)刷新，也可以手动调用this.refresh();来刷新
        return this.ok("保存成功！");
    }

}
