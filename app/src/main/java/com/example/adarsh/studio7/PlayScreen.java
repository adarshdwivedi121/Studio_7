package com.example.adarsh.studio7;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.example.adarsh.studio7.data.PlayerControl;
import com.example.adarsh.studio7.data.Song_Queue;

public class PlayScreen extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findViewById(R.id.player_screen_container).setTag(Boolean.FALSE);
        getFragmentManager().popBackStack();
        findViewById(R.id.player_stats).setVisibility(View.VISIBLE);
        PlayerControl.updateScreen();
    }

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

        findViewById(R.id.player_screen_container).setTag(Boolean.FALSE);
        findViewById(R.id.player_toggle_queue).setOnClickListener(new onActionClickListener());
        findViewById(R.id.player_back_action).setOnClickListener(new onActionClickListener());

        findViewById(R.id.player_play_pause_button).setOnClickListener(new onClickListener());
        findViewById(R.id.player_play_pause_button).setTag(Boolean.FALSE);
        findViewById(R.id.player_previous_button).setOnClickListener(new onClickListener());
        findViewById(R.id.player_next_button).setOnClickListener(new onClickListener());
        findViewById(R.id.player_repeat_button).setOnClickListener(new onClickListener());
        findViewById(R.id.player_shuffle_button).setOnClickListener(new onClickListener());

        if(PlayerControl.shuffleStatus())
            ((ImageButton) findViewById(R.id.player_shuffle_button)).setColorFilter(Color.CYAN);

        ImageButton repeat = (ImageButton) findViewById(R.id.player_repeat_button);
        switch(PlayerControl.repeatStatus()){
            case PlayerControl.REPEAT_ALL:
                repeat.setColorFilter(Color.CYAN);
                break;
            case PlayerControl.REPEAT_ONE:
                repeat.setColorFilter(Color.CYAN);
                repeat.setImageResource(R.drawable.ic_repeat_one);
                break;
        }
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
                    PlayerControl.playNext(true);
                    break;

                case R.id.player_shuffle_button:
                    if(!PlayerControl.shuffleStatus()) {
                        PlayerControl.setShuffle(true);
                        ((ImageButton) findViewById(R.id.player_shuffle_button)).setColorFilter(Color.CYAN);
                    }
                    else{
                        PlayerControl.setShuffle(false);
                        ((ImageButton) findViewById(R.id.player_shuffle_button)).setColorFilter(Color.WHITE);
                    }
                    break;

                case R.id.player_repeat_button:
                    ImageButton repeat = (ImageButton) findViewById(R.id.player_repeat_button);
                    if(PlayerControl.repeatStatus() == PlayerControl.REPEAT_ALL) {
                        PlayerControl.setListLoop(false);
                        PlayerControl.repeat(true);
                        PlayerControl.setRepeatStatus(PlayerControl.REPEAT_ONE);
                        repeat.setImageResource(R.drawable.ic_repeat_one);
                    }
                    else if( PlayerControl.repeatStatus() == PlayerControl.REPEAT_ONE) {
                        PlayerControl.repeat(false);
                        PlayerControl.setRepeatStatus(PlayerControl.REPEAT_OFF);
                        repeat.setImageResource(R.drawable.ic_repeat_all);
                        repeat.setColorFilter(Color.WHITE);
                    }
                    else{
                        PlayerControl.setRepeatStatus(PlayerControl.REPEAT_ALL);
                        PlayerControl.setListLoop(true);
                        repeat.setColorFilter(Color.CYAN);
                    }
                    break;
            }
        }
    }

    private class onActionClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.player_back_action:
                    finishAfterTransition();
                    break;
                case R.id.player_toggle_queue:
                    if(!(Boolean)findViewById(R.id.player_screen_container).getTag()) {
                        findViewById(R.id.player_screen_container).setTag(Boolean.TRUE);
                        findViewById(R.id.player_stats).setVisibility(View.GONE);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.animator.slide_left, android.R.animator.fade_out, android.R.animator.fade_in, R.animator.slide_right);
                        ft.addToBackStack("");
                        ft.add(R.id.player_screen_container, new Song_Queue());
                        ft.commit();
                    }
                    else{
                        findViewById(R.id.player_screen_container).setTag(Boolean.FALSE);
                        getFragmentManager().popBackStack();
                        findViewById(R.id.player_stats).setVisibility(View.VISIBLE);
                        PlayerControl.updateScreen();
                    }
                    break;

            }
        }
    }
}

