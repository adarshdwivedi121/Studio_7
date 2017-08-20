package com.example.adarsh.studio7;

import android.media.AudioManager;
import android.provider.MediaStore;

import com.example.adarsh.studio7.data.PlayerControl;

public class OnAudioChangeListener implements AudioManager.OnAudioFocusChangeListener{

    @Override
    public void onAudioFocusChange(int focusChange) {
        if(MainScreen.askedFocus() && focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            PlayerControl.play();
        else
            PlayerControl.pause();

        PlayerControl.updatePlayPauseIcon();
    }
}
