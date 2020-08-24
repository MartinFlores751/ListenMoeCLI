package com.github.MartinFlores751;

import com.github.MartinFlores751.jpop.VolumeControl;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;

public class UserInput implements Runnable {
    private final InputProvider term;
    private final VolumeControl volume;
    private boolean isQuit = false;

    UserInput(InputProvider term, VolumeControl volume) {
        this.term = term;
        this.volume = volume;
    }

    private KeyStroke getInput() {
        KeyStroke res = null;
        try {
            res = term.readInput();
        } catch (IOException e) {
            e.printStackTrace();
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
                    case 'u': volume.incVolume();
                        break;
                    case 'd': volume.decVolume();
                        break;
                }
            }
        }
    }
}
