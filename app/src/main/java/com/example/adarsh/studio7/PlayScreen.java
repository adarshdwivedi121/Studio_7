package com.example.adarsh.studio7;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.example.adarsh.studio7.data.PlayerControl;
import com.example.adarsh.studio7.data.Song_Queue;

public class PlayScreen extends AppCompatActivity {

    private FrameLayout frame;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getFragmentManager().popBackStack();
        frame.setTag(Boolean.FALSE);
        findViewById(R.id.player_stats).setVisibility(View.VISIBLE);
        PlayerControl.updateScreen();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_screen);

        setActionBar((Toolbar) findViewById(R.id.player_toolbar));

        frame = (FrameLayout) findViewById(R.id.player_screen_container);

        new PlayerControl(this, PlayerControl.SCREEN_PLAY);

        if(getIntent().hasExtra("NEW_SONG")) {
            if(getIntent().getStringExtra("ID").compareTo("all") == 0)
                PlayerControl.updateSongList(getIntent().getStringExtra("ID"), null);
            else
                PlayerControl.updateSongList(getIntent().getStringExtra("ID"), getIntent().getStringExtra("LIST_ID"));
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

    @Override
    protected void onResume() {
        super.onResume();
        PlayerControl.updateScreen();
    }

    private class onClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.player_play_pause_button:
                    if(v.findViewById(R.id.player_play_pause_button).getTag() == Boolean.FALSE && !PlayerControl.isPlaying()) {
                        MainScreen.requestAudioFocus();
                        PlayerControl.play();
                    }
                    else {
                        MainScreen.abandonAudioFocus();
                        PlayerControl.pause();
                    }

                    PlayerControl.updateNotification_Icons();
                    break;

                case R.id.player_previous_button:
                    MainScreen.requestAudioFocus();
                    PlayerControl.playPrevious();
                    break;

                case R.id.player_next_button:
                    MainScreen.requestAudioFocus();
                    PlayerControl.playNext(true, PlayerControl.getPos());
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
                        getFragmentManager().popBackStack();
                        frame.setTag(Boolean.FALSE);
                        findViewById(R.id.player_stats).setVisibility(View.VISIBLE);
                        PlayerControl.updateScreen();
                    }
                    break;

            }
        }
    }
}

