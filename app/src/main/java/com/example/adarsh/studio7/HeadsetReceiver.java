package com.example.adarsh.studio7;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.example.adarsh.studio7.data.PlayerControl;

public class HeadsetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().compareTo(AudioManager.ACTION_AUDIO_BECOMING_NOISY) == 0)
            PlayerControl.pause();

        PlayerControl.updatePlayPauseIcon();
    }
}
