package org.syaku.security.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Seok Kyun. Choi.
 * @since 2019-06-24
 */
@Log4j2
@EnableWebSecurity
public class WebSecurityConfiguration {
    @Log4j2
    @Configuration
    static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Bean
        public RoleHierarchy roleHierarchy() {
            RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();

            Map<String, List<String>> roleHierarchyMap = new HashMap<>();
            roleHierarchyMap.put("ROLE_ADMIN", Arrays.asList("ROLE_MANAGER"));
            roleHierarchyMap.put("ROLE_MANAGER", Arrays.asList("ROLE_POST", "ROLE_COMMENT", "ROLE_FILE"));
            roleHierarchyMap.put("ROLE_USER", Arrays.asList("ROLE_POST"));

            String roles = RoleHierarchyUtils.roleHierarchyFromMap(roleHierarchyMap);
            log.debug(roles);
            roleHierarchy.setHierarchy(roles);

            // 혹은 아래와 같이 작성할 수 있다.
            // roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_MANAGER\nROLE_MANAGER > ROLE_POST\nROLE_MANAGER > ROLE_COMMENT\nROLE_MANAGER > ROLE_FILE\nROLE_USER > ROLE_POST");
            return roleHierarchy;
        }

        @Bean
        public SecurityExpressionHandler<FilterInvocation> expressionHandler() {
            DefaultWebSecurityExpressionHandler webSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
            webSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
            log.debug(webSecurityExpressionHandler);
            return webSecurityExpressionHandler;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .csrf()
                .and().authorizeRequests()
                // 인가 설정에서 순서도 중요하다. 먼저 판단하기 위해 상위에 배치해야 한다.
                .antMatchers(HttpMethod.POST, "/api/blog").hasRole("POST")
                .antMatchers("/api/blog/**").hasRole("MANAGER")
                .anyRequest()
                .authenticated()
                .expressionHandler(expressionHandler());
        }
    }
}
