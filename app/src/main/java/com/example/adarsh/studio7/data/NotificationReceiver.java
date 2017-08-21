package com.example.adarsh.studio7.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by adarsh on 22/08/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().compareTo("PLAY") == 0)
            PlayerControl.play();
        else if(intent.getAction().compareTo("PAUSE") == 0)
            PlayerControl.pause();
        else if(intent.getAction().compareTo("PREVIOUS") == 0)
            PlayerControl.playPrevious();
        else if(intent.getAction().compareTo("NEXT") == 0)
            PlayerControl.playNext(true, PlayerControl.getPos());

        PlayerControl.updateNotification_Icons();
    }
}
