package com.github.MartinFlores751;

import com.github.MartinFlores751.jpop.JMoeClient;
import com.github.MartinFlores751.jpop.JVorbisStreamer;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ListenMoeCLI {
    private static final Logger logger = LoggerFactory.getLogger(ListenMoeCLI.class);

    public static void main(String[] args) {
        // Create terminal factory, to choose most appropriate Terminal for the current use case
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();

        try (
                Terminal terminal = terminalFactory.createTerminal();
                Screen screen = new TerminalScreen(terminal);
        ) {
            // Ensure the screen can be used
            screen.startScreen();

            // Create Jpop Music Streamer and create thread for music
            JVorbisStreamer jMusic = new JVorbisStreamer();
            Thread musicThread = new Thread(jMusic);

            // Key input and create thread for input
            UserInput input = new UserInput(terminal);
            Thread inputThread = new Thread(input);

            // Create Websocket
            JMoeClient client = null;
            try {
                client = new JMoeClient(new URI("wss://listen.moe/gateway_v2"));
            } catch (URISyntaxException e) {
                logger.warn("Failed to connect to WebSocket!", e);
            }

            // Create GUI manager
            GUI userGui = new GUI(terminal, screen);

            // Async connect websocket
            if (client != null)
                client.connect();

            // Start threads to handle input and music
            inputThread.start();
            musicThread.start();

            // Await for input to signal exit and kill music streamer thereafter
            try {
                inputThread.join();
            } catch (InterruptedException e) {
                logger.warn("Interrupted while waiting for program end!", e);
            }

            // Close WebSocket if open
            if (client != null)
                client.stop();

            // Close the music stream and remove userGUI from subscriptions
            jMusic.shutdown();
            userGui.stop();
        } catch (IOException e) {
            logger.error("Failed to open terminal!", e);
        }
    }
}
