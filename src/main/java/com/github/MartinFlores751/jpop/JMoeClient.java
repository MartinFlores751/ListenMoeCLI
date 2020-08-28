package com.github.MartinFlores751.jpop;

import com.github.MartinFlores751.events.CurrentSongEvent;
import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class JMoeClient extends WebSocketClient {
    private final Logger logger = LoggerFactory.getLogger(JMoeClient.class);
    private final Timer heartbeatTimer = new Timer();

    private class HeartbeatTask extends TimerTask {
        @Override
        public void run() {
            JsonObject json = Json.createObjectBuilder()
                    .add("op", 9)
                    .build();
            send(json.toString());
        }
    }

    public JMoeClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
    }

    @Override
    public void onMessage(String message) {
        logger.debug(message);
        JsonObject obj = Json.createReader(new StringReader(message)).readObject();
        switch(obj.getInt("op")) {
            case 0: int heartbeat = obj.getJsonObject("d").getInt("heartbeat");
                heartbeatTimer.scheduleAtFixedRate(new HeartbeatTask(), heartbeat, heartbeat);
                break;
            case 1:
                EventBus.getDefault().post( new CurrentSongEvent(obj.getJsonObject("d").getJsonObject("song")));
            case 10: logger.debug("Good heartbeat");
                break;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        heartbeatTimer.cancel();
    }

    @Override
    public void onError(Exception ex) {
        logger.error(ex.toString());
    }
}
