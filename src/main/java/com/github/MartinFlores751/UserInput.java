package com.github.MartinFlores751;

import com.github.MartinFlores751.events.VolumeChangeEvent;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class UserInput implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(UserInput.class);
    private final InputProvider term;
    private boolean isQuit = false;

    UserInput(InputProvider term) {
        this.term = term;
    }

    /**
     * Helper function to get user input
     * @return Returns current user input, may be null
     */
    private KeyStroke getInput() {
        KeyStroke res;
        try {
            res = term.readInput();
        } catch (IOException e) {
            logger.warn("Failed to read user input!", e);
            return null;
        }
        return res;
    }

    @Override
    public void run() {
        while (!isQuit) {
            KeyStroke stroke = getInput();
            if (stroke == null) continue;
            // Check if input is a character type
            if (stroke.getKeyType() == KeyType.Character) {
                // Check input on several cases
                switch (stroke.getCharacter()) {
                    case 'q':
                    case 'Q':
                        isQuit = true;
                        break;
                    case 'u':
                        EventBus.getDefault().post(new VolumeChangeEvent(true));
                        break;
                    case 'd':
                        EventBus.getDefault().post(new VolumeChangeEvent(false));
                        break;
                }
            }
        }
    }
}
