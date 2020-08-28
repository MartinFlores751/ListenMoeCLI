package com.github.MartinFlores751.events;

import javax.json.JsonObject;

public class CurrentSongEvent {
    private final int id;
    private final String title;
    private final int duration;

    public CurrentSongEvent(JsonObject songObj) {
        id = songObj.getInt("id");
        title = songObj.getString("title");
        duration = songObj.getInt("duration");
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public int getDuration() {
        return duration;
    }
}
