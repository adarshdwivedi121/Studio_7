package com.example.adarsh.studio7;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;

import com.example.adarsh.studio7.data.PagerFragment;
import com.example.adarsh.studio7.data.PlayerControl;

import java.util.ArrayList;

/**
 * Created by adarsh on 24/05/2017.
 */

public class MainScreen extends AppCompatActivity implements View.OnClickListener {

    private static AudioManager audioManager;
    private static OnAudioChangeListener audioChangeListener = new OnAudioChangeListener();
    private static int focus = 0;
    private static HeadsetReceiver headsetReceiver;
    private SharedPreferences pref;

    private static RemoteViews view;
    private static NotificationManager notificationManager;
    private static NotificationCompat.Builder notification;

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("Pos", PlayerControl.getPos());
        Cursor c = PlayerControl.getSongList();
        ArrayList<String> list = new ArrayList<>();
        for(int i=0; i<c.getCount(); i++) {
            c.moveToPosition(i);
            list.add(c.getString(0));
        }

        String data = list.toString();
        data = (data.replace('[', '(')).replace(']', ')');
        editor.putString("Prev_List", data);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headsetReceiver);
    }

    public static void requestAudioFocus(){
        focus = 1;
        audioManager.requestAudioFocus(audioChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public static void abandonAudioFocus(){
        focus = 0;
        audioManager.abandonAudioFocus(audioChangeListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences("Studio7", MODE_PRIVATE);

        setContentView(R.layout.activity_main_screen);
        setActionBar((android.widget.Toolbar) findViewById(R.id.toolbar));
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (shouldAskPermissions())
            askPermissions();

        if(!PlayerControl.hasSong() && pref.contains("Prev_List")){
            new PlayerControl(this, PlayerControl.SCREEN_MAIN);
            findViewById(R.id.main_screen_player_controls).setVisibility(View.VISIBLE);
            PlayerControl.lastSongList(pref.getString("Prev_List", ""), pref.getInt("Pos", 0));
            PlayerControl.updateScreen();
        }

        Log.i("Main Screen", "Content View Set.");

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        Log.i("FOCUS", String.valueOf(focus));

        headsetReceiver = new HeadsetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(headsetReceiver, intentFilter);

        findViewById(R.id.main_screen_play_pause_button).setOnClickListener(new onClickListener());
        findViewById(R.id.main_screen_previous_button).setOnClickListener(new onClickListener());
        findViewById(R.id.main_screen_next_button).setOnClickListener(new onClickListener());
        findViewById(R.id.main_screen_search).setOnClickListener(new onClickListener());

        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, new PagerFragment());
        ft.commit();
        Log.i("Main Screen", "Fragment Set.");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(PlayerControl.hasSong()) {
            findViewById(R.id.main_screen_player_controls).setVisibility(View.VISIBLE);

            new PlayerControl(this, PlayerControl.SCREEN_MAIN);
            PlayerControl.updateScreen();

            findViewById(R.id.main_screen_player_controls).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, PlayScreen.class);
        Pair<View, String> p1 = Pair.create(findViewById(R.id.main_screen_previous_button), "prev_button");
        Pair<View, String> p2 = Pair.create(findViewById(R.id.main_screen_play_pause_button), "play_button");
        Pair<View, String> p3 = Pair.create(findViewById(R.id.main_screen_next_button), "next_button");
        Pair<View, String> p4 = Pair.create(findViewById(R.id.main_screen_album_art), "album_art");
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2, p3, p4);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public static void setUpNotification(Context context){
        view = new RemoteViews(context.getPackageName(), R.layout.notification_player);
        view = PlayerControl.setUpNotification(view);

        Intent intent = new Intent(context, PlayScreen.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(    PlayScreen.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent play_pause;
        if(PlayerControl.isPlaying())
            play_pause = PendingIntent.getBroadcast(context, 1001, new Intent("PAUSE"), PendingIntent.FLAG_UPDATE_CURRENT);
        else
            play_pause = PendingIntent.getBroadcast(context, 1001, new Intent("PLAY"), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent previous = PendingIntent.getBroadcast(context, 1001, new Intent("PREVIOUS"), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent next = PendingIntent.getBroadcast(context, 1001, new Intent("NEXT"), PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(context).
                setContent(view).
                setContentIntent(pIntent).
                setSmallIcon(R.mipmap.ic_launcher_circle).
                setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)).
                setOngoing(true).
                setPriority(Notification.PRIORITY_MAX).
                setAutoCancel(false);

        notificationManager.notify(1001, notification.build());
    }

    public static boolean askedFocus() {
        return focus == 1;
    }

    private class NotificationReceiver extends BroadcastReceiver {
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
        }
    }

    private static class OnAudioChangeListener implements AudioManager.OnAudioFocusChangeListener{

        @Override
        public void onAudioFocusChange(int focusChange) {
            if(MainScreen.askedFocus() && focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                PlayerControl.play();
            else
                PlayerControl.pause();

            PlayerControl.updateNotification_Icons();
        }
    }

    private class HeadsetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().compareTo(AudioManager.ACTION_AUDIO_BECOMING_NOISY) == 0)
                PlayerControl.pause();

            PlayerControl.updateNotification_Icons();
        }
    }

    private class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_screen_play_pause_button:
                    if (v.findViewById(R.id.main_screen_play_pause_button).getTag() == Boolean.FALSE && !PlayerControl.isPlaying()) {
                        requestAudioFocus();
                        PlayerControl.play();
                    }

                    else {
                        abandonAudioFocus();
                        PlayerControl.pause();
                    }

                    PlayerControl.updateNotification_Icons();
                    break;

                case R.id.main_screen_previous_button:
                    requestAudioFocus();
                    PlayerControl.playPrevious();
                    break;

                case R.id.main_screen_next_button:
                    requestAudioFocus();
                    PlayerControl.playNext(true, PlayerControl.getPos());
                    break;
                case R.id.main_screen_search:
                    Log.e("Search", "Inside Listener");
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addSharedElement(findViewById(R.id.main_screen_search), "search_widget");
                    ft.addToBackStack("Main_Screen");
                    ft.replace(R.id.fragment_container, new SearchFragment());
                    ft.commit();
            }
        }
    }
}

