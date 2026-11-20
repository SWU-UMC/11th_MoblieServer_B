package com.umc.movie.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// [8주차 선택] Swagger UI: http://localhost:8080/swagger-ui/index.html
// 오른쪽 위 Authorize 버튼에 로그인 응답의 accessToken을 넣으면
// 자물쇠가 붙은 API에 Authorization: Bearer {token} 헤더가 자동으로 붙는다
@Configuration
public class SwaggerConfig {

    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UMC 영화 평점 서비스 API")
                        .description("자물쇠 표시 API는 Authorization: Bearer {accessToken} 헤더가 필요합니다. "
                                + "토큰은 POST /auth/login으로 발급받습니다.")
                        .version("8주차"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
