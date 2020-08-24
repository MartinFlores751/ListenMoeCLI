package com.github.MartinFlores751;

import com.github.MartinFlores751.jpop.JVorbisStreamer;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class ListenMoeCLI {
    public static void main(String[] args) {
        // Create terminal factory, to choose most appropriate Terminal for the current use case
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();

        // Set terminal to null to there are no complaints on the 'finally' section
        Terminal terminal = null;

        try {
            // Perform all terminal actions here

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
            JVorbisStreamer japMusic = new JVorbisStreamer();
            Thread musicThread = new Thread(japMusic);

            // Key input and create thread for input
            UserInput input = new UserInput(terminal);
            Thread inputThread = new Thread(input);

            // Start threads to handle input and music
            inputThread.start();
            musicThread.start();

            // Await for input to signal exit and kill music streamer thereafter
            try {
                inputThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            japMusic.shutdown();
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
