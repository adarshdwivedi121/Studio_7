package com.example.adarsh.studio7;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toolbar;

import com.example.adarsh.studio7.data.PlayerControl;

public class PlayScreen extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_screen);

        setActionBar((Toolbar) findViewById(R.id.player_toolbar));

        new PlayerControl(this, PlayerControl.SCREEN_PLAY);

        if(getIntent().hasExtra("NEW_SONG")) {
            PlayerControl.updateSongList(getIntent().getStringExtra("ID"));
            PlayerControl.setPos(getIntent().getIntExtra("POS", -1), getIntent().getStringExtra("SONG_ID"));
        }

        PlayerControl.updateScreen();

        findViewById(R.id.player_toggle_queue).setOnClickListener(new onActionClickListener());
        findViewById(R.id.player_back_action).setOnClickListener(new onActionClickListener());

        findViewById(R.id.player_play_pause_button).setOnClickListener(new onClickListener());
        findViewById(R.id.player_play_pause_button).setTag(Boolean.FALSE);
        findViewById(R.id.player_previous_button).setOnClickListener(new onClickListener());
        findViewById(R.id.player_next_button).setOnClickListener(new onClickListener());
        findViewById(R.id.player_repeat_one_button).setOnClickListener(new onClickListener());
        findViewById(R.id.player_shuffle_button).setOnClickListener(new onClickListener());
    }

    private class onClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.player_play_pause_button:
                    if(v.findViewById(R.id.player_play_pause_button).getTag() == Boolean.FALSE && !PlayerControl.isPlaying())
                        PlayerControl.play();
                    else
                        PlayerControl.pause();

                    PlayerControl.updatePlayPauseIcon();
                    break;

                case R.id.player_previous_button:
                    PlayerControl.playPrevious();
                    break;

                case R.id.player_next_button:
                    PlayerControl.playNext();
                    break;

                case R.id.player_shuffle_button:
                    break;

                case R.id.player_repeat_one_button:
                    PlayerControl.repeat(true);
                    break;
            }
        }
    }

    private class onActionClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.player_back_action:
                        finish();
                    break;
                case R.id.player_toggle_queue:
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.replace(R.id.fragment_container, new AllSongs());
//                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    ft.addToBackStack("Song Queue");
//                    ft.commit();
                    break;

            }
        }
    }
}
