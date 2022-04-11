package com.demo.websocket.demowebsocket.Util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

    private Utils() {
    }

    // Jackson JSON converter
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static SignalMessage getObject(final String message) throws Exception {
        return objectMapper.readValue(message, SignalMessage.class);
    }

    public static String getString(final SignalMessage message) throws Exception {
        return objectMapper.writeValueAsString(message);
    }
}