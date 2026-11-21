package com.bookmgt.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI3 接口文档配置（Knife4j）
 * 仅开发/测试环境启用，生产环境关闭 knife4j.enable=false
 */
@Configuration
@ConditionalOnProperty(prefix = "knife4j", name = "enable", havingValue = "true")
public class OpenApiConfig {

    private static final String BEARER_AUTH_SCHEME = "BearerToken";

    @Bean
    public OpenAPI customOpenAPI() {
        Info apiInfo = new Info()
                .title("书籍共享与交换系统")
                .version("2.0")
                .description("书籍共享与交换系统后端接口");
        return new OpenAPI()
                .info(apiInfo)
                // 全局开启鉴权
                // .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH_SCHEME, createJwtSecurityScheme())
                );
    }

    /**
     * 构造JWT Bearer认证方案
     */
    private SecurityScheme createJwtSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("请输入JWT Token");
    }
}
