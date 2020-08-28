package com.github.MartinFlores751;

import com.github.MartinFlores751.jpop.JMoeClient;
import com.github.MartinFlores751.jpop.JVorbisStreamer;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ListenMoeCLI {
    public static void main(String[] args) {
        // Create terminal factory, to choose most appropriate Terminal for the current use case
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();

        Terminal terminal = null;

        try {
            // Create terminal
            terminal = terminalFactory.createTerminal();

            // Enter private mode (full screen mode) and manually clear screen
            terminal.enterPrivateMode();
            terminal.clearScreen();

            // Hide cursor for fun
            terminal.setCursorVisible(false);

            String tempText = "Currently Streaming music\n";
            for (char c : tempText.toCharArray())
                terminal.putCharacter(c);
            terminal.flush();

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
                e.printStackTrace();
            }

            // Close program if client is bad
            if (client == null)
                System.exit(-1);

            GUI userGui = new GUI(terminal);

            // Async connect websocket
            client.connect();

            // Start threads to handle input and music
            inputThread.start();
            musicThread.start();

            // Await for input to signal exit and kill music streamer thereafter
            try {
                inputThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Close all streams
            client.close();
            jMusic.shutdown();
            userGui.unsubscribe();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (terminal != null) {
                try {
                    terminal.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
