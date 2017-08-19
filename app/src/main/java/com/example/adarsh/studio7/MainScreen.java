package com.example.adarsh.studio7;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.adarsh.studio7.data.PagerFragment;
import com.example.adarsh.studio7.data.PlayerControl;

/**
 * Created by adarsh on 24/05/2017.
 */

public class MainScreen extends AppCompatActivity implements View.OnClickListener {

    static private AudioManager audioManager;
    static private int focus = 0;
    static private HeadsetReceiver headsetReceiver;

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
    protected void onDestroy() {
        super.onDestroy();
        audioManager.abandonAudioFocus(new onAudioChangeListener());
        unregisterReceiver(headsetReceiver);
        Log.i("FOCUS", String.valueOf(focus));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);
        setActionBar((android.widget.Toolbar) findViewById(R.id.toolbar));

        if (shouldAskPermissions()) {
            askPermissions();
        }

        Log.i("Main Screen", "Content View Set.");

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if(focus != 1)
            focus = audioManager.requestAudioFocus(new onAudioChangeListener(), AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        Log.i("FOCUS", String.valueOf(focus));

        headsetReceiver = new HeadsetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(headsetReceiver, intentFilter);

        findViewById(R.id.main_screen_play_pause_button).setOnClickListener(new onClickListener());
        findViewById(R.id.main_screen_previous_button).setOnClickListener(new onClickListener());
        findViewById(R.id.main_screen_next_button).setOnClickListener(new onClickListener());

        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, new PagerFragment());
        ft.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
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
        Intent intent = new Intent(getApplicationContext(), PlayScreen.class);
        Pair<View, String> p1 = Pair.create(findViewById(R.id.main_screen_previous_button), "prev_button");
        Pair<View, String> p2 = Pair.create(findViewById(R.id.main_screen_play_pause_button), "play_button");
        Pair<View, String> p3 = Pair.create(findViewById(R.id.main_screen_next_button), "next_button");
        Pair<View, String> p4 = Pair.create(findViewById(R.id.main_screen_album_art), "album_art");
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2, p3, p4);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    private class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_screen_play_pause_button:
                    if (v.findViewById(R.id.main_screen_play_pause_button).getTag() == Boolean.FALSE && !PlayerControl.isPlaying())
                        PlayerControl.play();
                    else
                        PlayerControl.pause();

                    PlayerControl.updatePlayPauseIcon();
                    break;

                case R.id.main_screen_previous_button:
                    PlayerControl.playPrevious();
                    break;

                case R.id.main_screen_next_button:
                    PlayerControl.playNext();
                    break;
            }
        }
    }
}

