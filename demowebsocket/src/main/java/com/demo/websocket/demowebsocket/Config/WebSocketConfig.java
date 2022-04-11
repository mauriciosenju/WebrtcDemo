package com.demo.websocket.demowebsocket.Config;

import com.demo.websocket.demowebsocket.Util.SignalingSocketHandler;
import com.demo.websocket.demowebsocket.Util.HttpHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalingSocketHandler(), "/peerjs").setAllowedOrigins("*")
                .addInterceptors(HttpHandshakeInterceptor());

    }

    @Bean
    public HandshakeInterceptor HttpHandshakeInterceptor() {
        return new HttpHandshakeInterceptor();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);

        return container;
    }

    @Bean
    public WebSocketHandler signalingSocketHandler() {
        return new SignalingSocketHandler();
    }

}
