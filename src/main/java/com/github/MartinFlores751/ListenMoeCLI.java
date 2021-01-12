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

    /**
     * Runs and manages all high level logic of ListenMoeCLI
     *
     * @param args Command line arguments. Currently ignored.
     * @throws IOException if terminal could not be created
     * @throws URISyntaxException if URL is invalid. Only possible if programmer made error.
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        // Create terminal
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        Screen screen = new TerminalScreen(terminal);

        // Ensure the screen can be used
        screen.startScreen();

        // Create Jpop Music Streamer and create thread for music
        JVorbisStreamer jMusic = new JVorbisStreamer();
        Thread musicThread = new Thread(jMusic);

        // Key input and create thread for input
        UserInput input = new UserInput(terminal);
        Thread inputThread = new Thread(input);

        // Create Websocket
        JMoeClient client = new JMoeClient(new URI("wss://listen.moe/gateway_v2"));

        // Create GUI manager
        GUI userGui = new GUI(terminal, screen);

        // Async connect websocket
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
        client.stop();

        // Close the music stream and remove userGUI from subscriptions
        jMusic.shutdown();
        userGui.stop();

        // Close the terminal
        terminal.close();
    }
}
