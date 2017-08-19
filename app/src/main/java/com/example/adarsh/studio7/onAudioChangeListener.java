package com.example.adarsh.studio7;

import android.media.AudioManager;
import android.provider.MediaStore;

import com.example.adarsh.studio7.data.PlayerControl;

public class onAudioChangeListener implements AudioManager.OnAudioFocusChangeListener{

    @Override
    public void onAudioFocusChange(int focusChange) {
        if(focusChange != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            PlayerControl.pause();
        else
            PlayerControl.play();

        PlayerControl.updatePlayPauseIcon();
    }
}
