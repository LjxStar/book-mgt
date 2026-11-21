package com.bookmgt.controller;

import com.bookmgt.utils.OssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/test/oss")
@Tag(name = "OSS文件上传模块")
public class OssTestController {

    private final OssUtil ossUtil;

    public OssTestController(OssUtil ossUtil) {
        this.ossUtil = ossUtil;
    }

    /**
     * 上传文件测试
     * @param file 上传文件
     * @param folder 文件夹，示例：avatar/
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传", description = "上传文件到阿里云OSS，返回可访问的完整URL")
    @SecurityRequirement(name = "") // 免token，适配你全局鉴权的配置
    public String upload(
            @Parameter(description = "待上传文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "存放文件夹，默认 avatar/") @RequestParam(value = "folder", defaultValue = "avatar/") String folder
    ) throws IOException {
        return ossUtil.upload(file, folder);
    }

    /**
     * 删除文件测试
     * @param url oss返回的完整https地址
     * @return 提示
     */
    @PostMapping("/delete")
    @Operation(summary = "删除OSS文件", description = "传入OSS完整访问URL进行文件删除")
    @SecurityRequirement(name = "") // 免token
    public String delete(
            @Parameter(description = "OSS文件完整https地址", required = true) @RequestParam("url") String url
    ) {
        ossUtil.delete(url);
        return "删除成功";
    }
}
