package com.demo.websocket.demowebsocket.Filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class filterRequest extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String session = request.getSession().getId();
        System.out.println();
        System.out.println("cookies " + request.getCookies().length);
        System.out.println("url " + request.getRequestURI());
        System.out.println("ip " + request.getRemoteAddr());
        System.out.println(session);

        chain.doFilter(request, response);

    }
}
