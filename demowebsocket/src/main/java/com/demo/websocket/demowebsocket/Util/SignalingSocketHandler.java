package com.demo.websocket.demowebsocket.Util;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SignalingSocketHandler extends TextWebSocketHandler {

    /**
     * Cache of sessions by users.
     */
    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // put the session to the "cache".
        System.out.println("Conecction Established: " + session.getUri());

        System.out.println("peerId? : " + session.getAttributes().get("peerId"));

        final SignalMessage menOut = new SignalMessage();
        menOut.setType("NEW");
        menOut.setSender((String) session.getAttributes().get("peerId"));

        final SignalMessage menOut2 = new SignalMessage();
        menOut2.setType("OLD");

        sessions.forEach(webSocket -> {
            try {
                menOut.setDst(webSocket.getAttributes().get("peerId"));
                webSocket.sendMessage(new TextMessage(Utils.getString(menOut)));

                menOut2.setDst(session.getAttributes().get("peerId"));
                menOut2.setSender((String) webSocket.getAttributes().get("peerId"));
                session.sendMessage(new TextMessage(Utils.getString(menOut2)));
            } catch (Exception e) {
                System.out.print("Error " + e);
            }
        });
        sessions.add(session);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        removeUserAndSendLogout(session);
        System.out.println("Desconectando Normalmente");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        removeUserAndSendLogout(session);
        System.out.println("Desconectando por Error");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SignalMessage signalMessage = Utils.getObject(message.getPayload());

        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen() && !session.getId().equals(webSocketSession.getId())) {
                if (message.getPayload().contains("offer")) {
                    if (signalMessage.getDst().equals(webSocketSession.getAttributes().get("peerId"))) {
                        signalMessage.setSender((String) session.getAttributes().get("peerId"));
                        System.out.println("destinatrio: " + signalMessage.getDst());
                        System.out.println("type: " + signalMessage.getType());
                        signalMessage.setDst(webSocketSession.getAttributes().get("peerId"));
                        webSocketSession.sendMessage(new TextMessage(Utils.getString(signalMessage)));
                    }
                } else if (message.getPayload().contains("answer")) {
                    if (signalMessage.getDst().equals(webSocketSession.getAttributes().get("peerId"))) {

                        final SignalMessage menOut = new SignalMessage();
                        menOut.setType(signalMessage.getType());
                        menOut.setPayload(signalMessage.getPayload());
                        menOut.setDst(webSocketSession.getAttributes().get("peerId"));
                        menOut.setSender((String) session.getAttributes().get("peerId"));
                        System.out.println("destinatario: " + webSocketSession.getAttributes().get("peerId"));
                        System.out.println("type: " + signalMessage.getType());
                        webSocketSession.sendMessage(new TextMessage(Utils.getString(menOut)));
                    }
                } else if (message.getPayload().contains("candidate")) {
                    if (signalMessage.getDst().equals(webSocketSession.getAttributes().get("peerId"))) {
                        signalMessage.setSender((String) session.getAttributes().get("peerId"));
                        System.out.println("destinatrio: " + signalMessage.getDst());
                        System.out.println("type: " + signalMessage.getType());
                        signalMessage.setDst(webSocketSession.getAttributes().get("peerId"));
                        webSocketSession.sendMessage(new TextMessage(Utils.getString(signalMessage)));
                    }
                } else {
                    webSocketSession.sendMessage(message);

                }

            }
        }
    }

    private void removeUserAndSendLogout(WebSocketSession session) throws IOException {
        System.out.println("removeUserAndSendLogout");
        sessions.remove(session);

        // send the message to all other peers, somebody(sessionId) leave.
        final SignalMessage menOut = new SignalMessage();
        menOut.setType("LEAVE");
        menOut.setSender((String) session.getAttributes().get("peerId"));
        menOut.setPayload((String) session.getAttributes().get("user"));

        sessions.forEach(webSocket -> {
            try {
                menOut.setDst(webSocket.getAttributes().get("peerId"));
                webSocket.sendMessage(new TextMessage(Utils.getString(menOut)));
            } catch (Exception e) {
                System.out.print("Error " + e);
            }
        });
        session.close();
    }
}