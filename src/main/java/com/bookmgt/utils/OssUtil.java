package com.bookmgt.utils;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.models.DeleteObjectRequest;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.bookmgt.config.properties.OssProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OssUtil {

    private final OSSClient ossClient;
    private final OssProperties ossProperties;

    /**
     * 文件上传
     * @param file 文件
     * @param folder 目录，如 avatar/
     * @return 完整访问url
     */
    public String upload(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new RuntimeException("文件名称不能为空");
        }
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectKey = folder + UUID.randomUUID() + suffix;

        try (InputStream inputStream = file.getInputStream()) {
            BinaryData body = BinaryData.fromStream(inputStream);
            PutObjectRequest request = PutObjectRequest.newBuilder()
                    .bucket(ossProperties.getBucketName())
                    .key(objectKey)
                    .body(body)
                    .build();
            ossClient.putObject(request);
        }
        // 拼接访问地址
        return String.format("https://%s.%s/%s", ossProperties.getBucketName(), ossProperties.getEndpoint(), objectKey);
    }

    /**
     * 删除文件
     * @param fullUrl 文件完整url
     */
    public void delete(String fullUrl) {
        String prefix = "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/";
        String objectKey = fullUrl.substring(prefix.length());
        DeleteObjectRequest deleteReq = DeleteObjectRequest.newBuilder()
                .bucket(ossProperties.getBucketName())
                .key(objectKey)
                .build();
        ossClient.deleteObject(deleteReq);
    }
}
