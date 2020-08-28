package com.github.MartinFlores751.events;

public class VolumeChangeEvent {
    private final boolean isVolUp;

    public VolumeChangeEvent(boolean isVolUp) {
        this.isVolUp = isVolUp;
    }

    public boolean isVolumeUp() {
        return isVolUp;
    }
}
