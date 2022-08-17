package hello.mailService.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "http")
@Slf4j
public class SecurityConfig {

    @Value("${http.auth-token-header.name}")
    private String principalRequestHeader;
    public static HashMap<String, String> authToken;

    public HashMap<String, String> getAuthToken() {
        return authToken;
    }

    public void setAuthToken(HashMap<String, String> authToken) {
        this.authToken = authToken;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        ApiKeyAuthFilter filter = new ApiKeyAuthFilter(principalRequestHeader);
        filter.setAuthenticationManager(authentication -> {
            String principal = (String) authentication.getPrincipal();
            if (!authToken.containsValue(principal)) {
                log.info("에러 발생");
                throw new BadCredentialsException("The API key was not found or not the expected value");
            }
            authentication.setAuthenticated(true);
            return authentication;
        });

        http
                .cors().disable()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(filter)
                .authorizeRequests()
                .antMatchers("/index").permitAll()
                .anyRequest()
                .authenticated();

        return http.build();
    }
}
