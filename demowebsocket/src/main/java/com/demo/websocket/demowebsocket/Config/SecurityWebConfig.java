package com.demo.websocket.demowebsocket.Config;

import com.demo.websocket.demowebsocket.Filter.filterRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.server.session.HeaderWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class SecurityWebConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private filterRequest jwtAuthenticationTokenFilter;

    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        HeaderWebSessionIdResolver resolver = new HeaderWebSessionIdResolver();
        resolver.setHeaderName("JSESSIONID");
        return resolver;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeRequests()
                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("USER")
                .antMatchers("/client/**").hasAnyRole("CLIENT")
                .antMatchers("/logout").hasAnyRole("ADMIN", "USER", "CLIENT")
                .antMatchers("/Principal").hasAnyRole("ADMIN", "USER", "CLIENT")
                .antMatchers("/**").permitAll().and().headers()
                .frameOptions().sameOrigin().cacheControl()
                .and().and().logout()
                .invalidateHttpSession(true).logoutSuccessUrl("/").deleteCookies("JSESSIONID")
                .deleteCookies("my-remember-me-cookie").permitAll().and().rememberMe()
                .rememberMeCookieName("my-remember-me-cookie").and().exceptionHandling().accessDeniedPage("/error2")
                .and()
                .sessionManagement().sessionFixation().migrateSession()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and()
                .csrf().disable();
        super.configure(http);

    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity
                .ignoring()
                .antMatchers(HttpMethod.POST, "/**")
                // .antMatchers(HttpMethod.OPTIONS, "/**")
                .and()
                .ignoring()
                .antMatchers(HttpMethod.GET, "/**");
    }

}