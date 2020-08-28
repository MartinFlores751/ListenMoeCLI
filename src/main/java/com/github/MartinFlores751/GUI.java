package com.github.MartinFlores751;

import com.github.MartinFlores751.events.CurrentSongEvent;
import com.googlecode.lanterna.terminal.Terminal;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class GUI {
    private final Terminal terminal;

    public GUI(Terminal term) {
        terminal = term;
        EventBus.getDefault().register(this);
    }

    public void unsubscribe() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessageEvent(CurrentSongEvent e) {
        if (terminal != null) {
            try {
                terminal.setCursorPosition(0, 0);
                terminal.clearScreen();

                String nowPlaying = "Now playing: ";
                for (char c : nowPlaying.toCharArray())
                    terminal.putCharacter(c);

                for (char c : e.getTitle().toCharArray())
                    terminal.putCharacter(c);

                terminal.flush();

            } catch (IOException err) {
                err.printStackTrace();
            }
        }
    }
}
