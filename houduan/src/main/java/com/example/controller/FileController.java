package com.example.controller;

import cn.hutool.core.io.FileUtil;
import com.example.common.Result;
import com.example.entity.FileInfo;
import com.example.exception.CustomException;
import com.example.service.FileInfoService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件控制器，提供文件上传、下载、删除等功能
 */
@RestController
@RequestMapping("/files")
public class FileController {

    // 文件存储基础路径
    private static final String BASE_PATH = System.getProperty("user.dir") + "/src/main/resources/static/file/";

    @Resource
    private FileInfoService fileInfoService;

    /**
     * 上传图片接口
     * @param file 上传的文件
     * @param request HTTP请求对象
     * @return 返回上传结果，包含文件信息或错误信息
     * @throws IOException 可能抛出的IO异常
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file, HttpServletRequest request) throws IOException {
        // 获取原始文件名
        String originName = file.getOriginalFilename();
        if (originName == null) {
            return Result.error("1001", "文件名不能为空");
        }
        // 检查文件类型是否为图片
        if (!originName.contains("png")
                && !originName.contains("jpg")
                && !originName.contains("jpeg")
                && !originName.contains("gif")) {
            return Result.error("1001", "只能上传图片");
        }
        // 文件名加个时间戳，避免重名
        String fileName = FileUtil.mainName(originName) + System.currentTimeMillis() + "." + FileUtil.extName(originName);

        // 2. 文件上传
        FileUtil.writeBytes(file.getBytes(), BASE_PATH + fileName);

        // 3. 信息入库，获取文件id
        FileInfo info = new FileInfo();
        info.setOriginName(originName);
        info.setFileName(fileName);
        FileInfo addInfo = fileInfoService.add(info);
        if (addInfo != null) {
            return Result.success(addInfo);
        } else {
            return Result.error("4001", "上传失败");
        }
    }

        @PostMapping("/uploadwenjian")
    public Result uploadWenjian(MultipartFile file, HttpServletRequest request) throws IOException {
        String originName = file.getOriginalFilename();
        if (originName == null) {
            return Result.error("1001", "文件名不能为空");
        }

        // 文件名加个时间戳
        String fileName = FileUtil.mainName(originName) + System.currentTimeMillis() + "." + FileUtil.extName(originName);

        // 2. 文件上传
        FileUtil.writeBytes(file.getBytes(), BASE_PATH + fileName);

        // 3. 信息入库，获取文件id
        FileInfo info = new FileInfo();
        info.setOriginName(originName);
        info.setFileName(fileName);
        FileInfo addInfo = fileInfoService.add(info);
        if (addInfo != null) {
            return Result.success(addInfo);
        } else {
            return Result.error("4001", "上传失败");
        }
    }
	
	
        @PostMapping("/front/uploadwenjian")
    public Result uploadWenjianFront(MultipartFile file) throws IOException {
        String originName = file.getOriginalFilename();
        if (originName == null) {
            return Result.error("1001", "文件名不能为空");
        }

        // 文件名加个时间戳
        String fileName = FileUtil.mainName(originName) + System.currentTimeMillis() + "." + FileUtil.extName(originName);

        // 2. 文件上传
        FileUtil.writeBytes(file.getBytes(), BASE_PATH + fileName);

        // 3. 信息入库，获取文件id
        FileInfo info = new FileInfo();
        info.setOriginName(originName);
        info.setFileName(fileName);
        FileInfo addInfo = fileInfoService.add(info);
        if (addInfo != null) {
            return Result.success(addInfo);
        } else {
            return Result.error("4001", "上传失败");
        }
    }


    @PostMapping("/notice/upload")
    public Result<Map<String, String>> noticeUpload(MultipartFile file, HttpServletRequest request) throws IOException {
        String originName = file.getOriginalFilename();
        // 文件名加个时间戳
        String fileName = FileUtil.mainName(originName) + System.currentTimeMillis() + "." + FileUtil.extName(originName);
        // 2. 缩小尺寸
        Thumbnails.of(file.getInputStream()).width(400).toFile(BASE_PATH + fileName);

        // 3. 信息入库，获取文件id
        FileInfo info = new FileInfo();
        info.setOriginName(originName);
        info.setFileName(fileName);
        FileInfo addInfo = fileInfoService.add(info);

        Map<String, String> map = new HashMap<>(2);
        map.put("src", "/files/download/" + addInfo.getId());
        map.put("title", originName);
        return Result.success(map);
    }


    /**
     * 按 id 下载静态文件（图片等）。库中无记录或磁盘缺失时返回 404，避免抛异常刷屏日志。
     */
    @GetMapping("/download/{id}")
    public void download(@PathVariable String id, HttpServletResponse response) throws IOException {
        if (id == null || "null".equalsIgnoreCase(id)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        long fid;
        try {
            fid = Long.parseLong(id);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        FileInfo fileInfo = fileInfoService.findById(fid);
        if (fileInfo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        byte[] bytes;
        try {
            bytes = FileUtil.readBytes(BASE_PATH + fileInfo.getFileName());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (bytes == null || bytes.length == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.reset();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(
                fileInfo.getOriginName() != null ? fileInfo.getOriginName() : "file", "UTF-8"));
        response.addHeader("Content-Length", "" + bytes.length);
        response.setContentType("application/octet-stream");
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        toClient.write(bytes);
        toClient.flush();
        toClient.close();
    }

    @DeleteMapping("/{id}")
    public Result deleteFile(@PathVariable String id) {
        FileInfo fileInfo = fileInfoService.findById(Long.parseLong(id));
        if (fileInfo == null) {
            throw new CustomException("1001", "未查询到该文件");
        }
        String name = fileInfo.getFileName();
        // 先删除文件
        FileUtil.del(new File(BASE_PATH + name));
        // 再删除表记录
        fileInfoService.delete(Long.parseLong(id));

        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<FileInfo> getById(@PathVariable String id) {
        FileInfo fileInfo = fileInfoService.findById(Long.parseLong(id));
        if (fileInfo == null) {
            throw new CustomException("1001", "数据库未查到此文件");
        }
        try {
            FileUtil.readBytes(BASE_PATH + fileInfo.getFileName());
        } catch (Exception e) {
            throw new CustomException("1001", "此文件已被您删除");
        }
        return Result.success(fileInfo);
    }
}
