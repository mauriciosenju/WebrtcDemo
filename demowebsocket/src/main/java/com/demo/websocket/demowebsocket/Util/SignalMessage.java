package com.demo.websocket.demowebsocket.Util;

public class SignalMessage {
    private boolean offerToReceiveAudio;
    private boolean offerToReceiveVideo;
    private String type;
    private Object dst;
    private Object payload;
    private String sender;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public SignalMessage() {

    }

    public SignalMessage(String type, String sender, String receiver, Object data) {
        this.type = type;
        this.payload = data;
    }

    public Object getDst() {
        return dst;
    }

    public void setDst(Object dst) {
        this.dst = dst;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isOfferToReceiveAudio() {
        return offerToReceiveAudio;
    }

    public void setOfferToReceiveAudio(boolean offerToReceiveAudio) {
        this.offerToReceiveAudio = offerToReceiveAudio;
    }

    public boolean isOfferToReceiveVideo() {
        return offerToReceiveVideo;
    }

    public void setOfferToReceiveVideo(boolean offerToReceiveVideo) {
        this.offerToReceiveVideo = offerToReceiveVideo;
    }

}
