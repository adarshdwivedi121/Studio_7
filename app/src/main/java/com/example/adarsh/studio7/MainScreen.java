package com.example.adarsh.studio7;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.adarsh.studio7.data.PagerFragment;
import com.example.adarsh.studio7.data.PlayerControl;

/**
 * Created by adarsh on 24/05/2017.
 */

public class MainScreen extends AppCompatActivity{

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);
        setActionBar((android.widget.Toolbar) findViewById(R.id.toolbar));

        if (shouldAskPermissions()) {
            askPermissions();
        }

        Log.i("Main Screen", "Content View Set.");

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

            findViewById(R.id.main_screen_player_controls).setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), PlayScreen.class);
                startActivity(intent);
            });
        }
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

