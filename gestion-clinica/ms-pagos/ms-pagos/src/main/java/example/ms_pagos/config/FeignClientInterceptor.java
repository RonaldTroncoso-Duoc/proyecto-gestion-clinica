package example.ms_pagos.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@RequiredArgsConstructor
public class FeignClientInterceptor {

    @Bean
    public RequestInterceptor requestInterceptor() {

        return requestTemplate -> {

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes)
                            RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return;
            }

            HttpServletRequest request =
                    attributes.getRequest();

            String authUser =
                    request.getHeader("X-Auth-User");

            String authRoles =
                    request.getHeader("X-Auth-Roles");

            if (authUser != null) {

                requestTemplate.header(
                        "X-Auth-User",
                        authUser
                );
            }

            if (authRoles != null) {

                requestTemplate.header(
                        "X-Auth-Roles",
                        authRoles
                );
            }
        };
    }
}