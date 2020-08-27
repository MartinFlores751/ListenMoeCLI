package com.github.MartinFlores751.jpop;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.net.URI;

public class JMoeClient extends WebSocketClient {
    private final Logger logger;
    private int heartBeat;

    public JMoeClient(URI serverUri) {
        super(serverUri);
        logger = LoggerFactory.getLogger(JMoeClient.class);
        logger.info("Hello World!");
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info(handshakedata.toString());
    }

    @Override
    public void onMessage(String message) {
        logger.info(message);
        JsonObject obj = Json.createReader(new StringReader(message)).readObject();
        switch(obj.getInt("op")) {
            case 0: heartBeat = obj.getJsonObject("d").getInt("heartbeat");
                break;
            case 1:
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info(reason);
    }

    @Override
    public void onError(Exception ex) {
        logger.info(ex.toString());
    }
}
