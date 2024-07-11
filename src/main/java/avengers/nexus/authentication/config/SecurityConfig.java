package avengers.nexus.authentication.config;

import avengers.nexus.authentication.filter.OauthAuthenticationFilter;
import avengers.nexus.authentication.jwt.JWTUtil;
import avengers.nexus.gauth.service.GauthService;
import avengers.nexus.github.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final GauthService gauthService;
    private final GithubService githubService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public OauthAuthenticationFilter gauthAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        OauthAuthenticationFilter filter = new OauthAuthenticationFilter("/login/gauth", authenticationManager, jwtUtil, gauthService);
        filter.setFilterProcessesUrl("/login/gauth");
        return filter;
    }
    @Bean
    public OauthAuthenticationFilter githubAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        OauthAuthenticationFilter filter = new OauthAuthenticationFilter("/login/github", authenticationManager, jwtUtil, githubService);
        filter.setFilterProcessesUrl("/login/github");
        return filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeRequests)->
                        authorizeRequests
                                .requestMatchers("/user/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/project/**","/post/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(gauthAuthenticationFilter(http.getSharedObject(AuthenticationManager.class)), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(githubAuthenticationFilter(http.getSharedObject(AuthenticationManager.class)), UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }
}
