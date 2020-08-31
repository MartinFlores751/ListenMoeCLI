package com.github.MartinFlores751;

import com.github.MartinFlores751.events.CurrentSongEvent;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GUI {
    private static final Logger logger = LoggerFactory.getLogger(GUI.class);

    private final Terminal terminal;

    private final Screen screen;
    private TerminalSize screenSize;

    private final WindowBasedTextGUI textGUI;
    private final Window mainWindow;
    private final Panel contentPanel;

    private final Label songLabel;

    public GUI(Terminal term, Screen screen) {
        // Get passed in arguments
        terminal = term;
        this.screen = screen;

        // Get current screen size
        screenSize = screen.getTerminalSize();

        // Create a window based text GUI
        textGUI = new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), this.screen);
        mainWindow = new BasicWindow("Listen.moe");

        // Create a grid layout for the GUI
        contentPanel = new Panel(new GridLayout(5));

        // Set grid spacing
        GridLayout gridLayout = (GridLayout)contentPanel.getLayoutManager();
        gridLayout.setHorizontalSpacing(3);

        // Create label for song
        songLabel = new Label("Loading song...");
        songLabel.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.BEGINNING,
                GridLayout.Alignment.BEGINNING,
                false,
                false,
                3,
                1
        ));

        // Add label to panel
        contentPanel.addComponent(songLabel);

        // Attach panel to window
        mainWindow.setComponent(contentPanel);

        // Add window
        textGUI.addWindow(mainWindow);

        // Start the GUI on a separate thread
        ((AsynchronousTextGUIThread) textGUI.getGUIThread()).start();

        // Register event
        EventBus.getDefault().register(this);
    }

    public void stop() {
        ((AsynchronousTextGUIThread) textGUI.getGUIThread()).stop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessageEvent(CurrentSongEvent e) {
        // Set song message
        String songMessage = "Now playing: " + e.getTitle();

        // Update label with current song
        songLabel.setText(songMessage);
    }
}
