package com.example.root.dto;

import android.content.Context;
import android.media.MediaPlayer;

public class Sound {
    private MediaPlayer mediaPlayer;
    private int currentPosition;

    public Sound(Context context, int id) {
        this.mediaPlayer = MediaPlayer.create(context, id);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
