package com.bookmgt.config;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.StaticCredentialsProvider;
import com.bookmgt.config.properties.OssProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {

    private final OssProperties ossProperties;

    public OssConfig(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    @Bean
    public OSSClient ossClient() {
        StaticCredentialsProvider provider = new StaticCredentialsProvider(
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );
        return OSSClient.newBuilder()
                .credentialsProvider(provider)
                .region(ossProperties.getRegion())
                .endpoint(ossProperties.getEndpoint())
                .build();
    }
}
