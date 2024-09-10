package com.insigma.sys.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.dfs.FSObject;
import com.insigma.framework.dfs.FSService;
import com.insigma.framework.dfs.exception.DfsException;
import com.insigma.framework.exception.AppException;
import com.insigma.framework.util.StringUtil;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.SysDownloadCenterDTO;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.SysDownloadCenterService;
import com.insigma.web.support.annotation.OdinRequest;
import com.insigma.web.support.annotation.OdinRequestParam;
import com.insigma.web.support.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author GH
 * @ClassName: SysDownloadCenterController
 * @Description:
 * @version 2021/8/4  10:13
 */
@Slf4j
@RestController
@RequestMapping("/sys/downloadcenter/DownloadCenter")
public class SysDownloadCenterController extends BaseController {

    @Autowired
    private SysDownloadCenterService sysDownloadCenterService;

    @Autowired(required = false)
    private FSService fsService;

    @Autowired
    private CurrentUserService currentUserService;

    /**
     * 初始化
     *
     * @param f_form
     * @param size
     * @return
     */
    @OdinRequest(init = true)
    @PostMapping("/doInit")
    public ResponseMessage doInit(SysDownloadCenterDTO f_form, Integer size) {
        tableDataQuery(f_form, 1, size);
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
    public ResponseMessage query(@OdinRequestParam("f_form") SysDownloadCenterDTO queryDTO,
                                 Integer size) {
        tableDataQuery(queryDTO, 1, size);
        return this.ok();
    }

    /**
     * @return
     * @Description: 分页查询
     * @author GH
     * @version 2021/8/4   13:29
     */
    @OdinRequest
    @PostMapping("/doGridQuery/{name}")
    public ResponseMessage doGridQuery(@OdinRequestParam("f_form") SysDownloadCenterDTO queryDTO,
                                       @OdinRequestParam("t_tableData_page") Integer page,
                                       @OdinRequestParam("t_tableData_size") Integer size,
                                       @PathVariable String name) {
        tableDataQuery(queryDTO, page, size);
        return this.ok();
    }


    private void tableDataQuery(SysDownloadCenterDTO queryDTO, Integer page, Integer size) {
        this.pageQuery("t_tableData", () -> {
            this.set("page", page); // 需要将前端表格中:currentPage定义的值设置成page的值
            return sysDownloadCenterService.queryDownloadCenterList(queryDTO, page, size);
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
        this.clearForm("f_downloadCenterForm");
        this.set("sysDownloadCenterDTO", new Object[]{});
        this.set("fileList", new Object[]{});
        this.set("dialogFormVisible", true);
        this.set("dialogFormTitle", "材料新增");
        return this.ok();
    }

    /**
     * 保存
     *
     * @param sysDownloadCenterDTO
     * @return
     */
    @OdinRequest(refresh = true) // refresh = true 请求结束后刷新页面
    @PostMapping("/doSave/{name}")
    public ResponseMessage doSave(@OdinRequestParam("f_downloadCenterForm") SysDownloadCenterDTO sysDownloadCenterDTO,
                                  @PathVariable String name, @RequestBody JSONObject jsonObjecte) throws DfsException {
        if(sysDownloadCenterDTO.getMaterial_type()==null|| "".equals(sysDownloadCenterDTO.getMaterial_type())){
            return this.error("请选择文件类型!");
        }
        //设置保留位数
        DecimalFormat df = new DecimalFormat("0.00");
        JSONArray array = jsonObjecte.getJSONArray("sysDownloadCenterDTO");
        List<SysDownloadCenterDTO> list = array.toJavaList(SysDownloadCenterDTO.class);
        if (list.size() < 1) {
            return ResponseMessage.error("请上传附件！");
        } else {
            for (SysDownloadCenterDTO downloadCenterDTO : list) {
                //录入数据库
                SysDownloadCenterDTO dto = new SysDownloadCenterDTO();
                dto.setId(downloadCenterDTO.getId());
                dto.setMaterial_name(downloadCenterDTO.getMaterial_name());
                String size;
                //字节
                long baty = Long.parseLong(downloadCenterDTO.getMaterial_size());
                //小于M用kb
                if (baty < 1048576 && baty > 0) {
                    String kb = df.format((double) baty / 1024);
                    size = kb + "KB";
                } else if (baty >= 1048576 && baty < 1073741824) { //用m表示
                    String m = df.format((double) baty / 1048576);
                    size = m + "M";
                } else { //G表示
                    String g = df.format((double) baty / 1073741824);
                    size = g + "G";
                }
                dto.setMaterial_size(size);
                dto.setMaterial_type(sysDownloadCenterDTO.getMaterial_type());
                dto.setUpload_time(new Date());
                sysDownloadCenterService.saveDownloadCenter(dto);
            }
            this.set("dialogFormVisible", false);
            return this.ok("保存成功!!!");
        }
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    @OdinRequest(refresh = true) // refresh = true 请求结束后刷新页面
    @PostMapping("/delete")
    public ResponseMessage delete(String id) throws DfsException {
        sysDownloadCenterService.delete(id);
        fsService.deleteObject(id);
        return this.ok("删除成功！");

    }

    //移除文件
    @GetMapping("/del/{id}")
    public ResponseMessage del(@PathVariable String id) throws DfsException {
        fsService.deleteObject(id);
        return this.ok("文件移除成功！");
    }

    //批量下载
    @GetMapping("/batchDownload/{ids}")
    public void batchDownload(@PathVariable String ids, HttpServletResponse response) throws DfsException {
        String[] s = ids.split(",");
        String zipFileName = "batchDownload.zip";
//        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment;filename=" + zipFileName);
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(response.getOutputStream());
            for (String fileId : s) {
                FSObject fsObject;
                try {
                    fsObject = fsService.getObject(fileId);
                } catch (DfsException e) {
                    log.error("{}文件不存在！", fileId);
                    continue;
                }
                InputStream is = fsObject.getInputStream();
                BufferedInputStream bis = null;
                try {
                    bis = new BufferedInputStream(is);
                    //将文件写入zip内，即将文件进行打包
                    zos.putNextEntry(new ZipEntry(fsObject.getName()));
                    //写入文件的方法，同上
                    int size = 0;
                    byte[] buffer = new byte[1024];
                    //设置读取数据缓存大小
                    while ((size = bis.read(buffer)) > 0) {
                        zos.write(buffer, 0, size);
                    }
                    //关闭输入输出流
                    zos.closeEntry();
                } catch (Exception e) {
                    throw e;
                } finally {
                    if (bis != null) {
                        bis.close();
                    }
                }
            }
        } catch (Exception e) {
            throw new AppException("批量下载失败");
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                }
            }
        }

    }


    /**
     * 下载
     *
     * @return
     */
    @GetMapping("/download/{id}")
    public void download(@PathVariable String id, HttpServletResponse response) throws IOException {
        FSObject fsObject = null;
        try {
            fsObject = fsService.getObject(id);
        } catch (Exception e) {
            response.sendError(404, "文件不存在！");
            return;
        }
        sysDownloadCenterService.saveDownloadLog(id, currentUserService.getCurrentUser().getLogonName());
        OutputStream out = null;
        InputStream in = fsObject.getInputStream();
        try {
            response.addHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fsObject.getName().getBytes("UTF-8"), "ISO8859-1"));
            out = response.getOutputStream();

            byte[] bytes = new byte[2048];
            int len;
            while ((len = in.read(bytes)) > 0) {
                out.write(bytes, 0, len);
            }
        } catch (Exception e) {
            log.error("文件下载异常", e);
        } finally {
            //释放资源
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("IO close error!", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("IO close error!", e);
                }
            }
        }
    }

    //上传
    @PostMapping("/upload")
    public ResponseMessage upload(@RequestParam MultipartFile file, HttpServletRequest request) throws IOException, DfsException {
        SysUser sysUser = currentUserService.getCurrentUser();
        if (!"1".equals(sysUser.getUserType())) {
            return ResponseMessage.error("只允许超级管理员上传附件！");
        }
        ResponseMessage rm;
        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        /*if (!".zip".equals(fileType)) {
            rm = ResponseMessage.ok("文件类型不对!!!");
            return rm;
        }*/
        //创建文件存储对象
        FSObject fsObject = new FSObject();
        fsObject.setName(file.getOriginalFilename());
        fsObject.setContentType(file.getContentType());
        fsObject.setInputStream(file.getInputStream());
        fsObject.setSize(file.getSize());
        //该id在文件存入后由mongodb返回
        String id = fsService.pubObject(fsObject);
        if (StringUtil.isNotEmpty(id)) {
            SysDownloadCenterDTO dto = new SysDownloadCenterDTO();
            dto.setId(id);
            dto.setMaterial_size(Long.toString(fsObject.getSize()));
            dto.setMaterial_name(fsObject.getName());
            rm = ResponseMessage.ok("文件上传成功!!!", dto);
        } else {
            rm = ResponseMessage.ok("文件上传失败!!!");
        }
        return rm;
    }


    //查询用户是否为超级管理员
    @GetMapping("/queryUser")
    public ResponseMessage queryUser() {
        SysUser sysUser = currentUserService.getCurrentUser();
        //超级管理员
        if ("1".equals(sysUser.getUserType())) {
            return ResponseMessage.ok(true);
        } else {
            return ResponseMessage.ok(false);
        }
    }
}
