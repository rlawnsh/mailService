package hello.mailService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${appname.http.auth-token-header.name}")
    private String principalRequestHeader;

    @Value("${appname.http.auth-token}")
    private List<String> principalRequestValue;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        ApiKeyAuthFilter filter = new ApiKeyAuthFilter(principalRequestHeader);
        filter.setAuthenticationManager(authentication -> {
            String principal = (String) authentication.getPrincipal();
            if (!principalRequestValue.contains(principal)) {
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
