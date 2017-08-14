package com.example.adarsh.studio7;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.adarsh.studio7.data.PlayerControl;

public class PlayScreen extends AppCompatActivity {

    PlayerControl playerControl;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_screen);

        setActionBar((Toolbar) findViewById(R.id.player_toolbar));

        findViewById(R.id.player_play_pause_button).setOnClickListener(new onClickListener());
        findViewById(R.id.player_play_pause_button).setTag(Boolean.FALSE);
        findViewById(R.id.player_previous_button).setOnClickListener(new onClickListener());
        findViewById(R.id.player_next_button).setOnClickListener(new onClickListener());
        findViewById(R.id.player_repeat_one_button).setOnClickListener(new onClickListener());
        findViewById(R.id.player_shuffle_button).setOnClickListener(new onClickListener());
    }

    @Override
    public void onStart() {
        super.onStart();

        String path = PlayerControl.getFilePath();

        if(getIntent().hasExtra("NEW_SONG")) {

            String[] projection = new String[]{
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DATA
            };

            Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    "_id IS " + getIntent().getStringExtra("Song ID"),
                    null,
                    null
            );
            c.moveToNext();

            path = c.getString(5);
            Log.i("Player Fragment", "Playing Song");

            PlayerControl.setName(c.getString(2));
            PlayerControl.setArtistName(c.getString(3));
            PlayerControl.setAlbumName(c.getString(4));

            ((TextView) findViewById(R.id.player_song_title)).setText(c.getString(2));
            ((TextView) findViewById(R.id.player_song_artist)).setText(c.getString(3));
            ((TextView) findViewById(R.id.player_song_album)).setText(c.getString(4));

            findViewById(R.id.player_song_title).setSelected(true);

            c = getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                    "_id IS " + c.getString(1),
                    null,
                    null
            );
            c.moveToNext();

            Bitmap bitmap = null;

            if (c != null) {
                bitmap = BitmapFactory.decodeFile(c.getString(1));
                c.close();
            }

            if (bitmap == null)
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_music);
            PlayerControl.setImage(bitmap);
        }

        ((TextView) findViewById(R.id.player_song_title)).setText(PlayerControl.getSongName());
        ((TextView) findViewById(R.id.player_song_album)).setText(PlayerControl.getAlbumName());
        ((TextView) findViewById(R.id.player_song_artist)).setText(PlayerControl.getArtistName());

        Bitmap bitmap = PlayerControl.getImage();

        ((ImageView) findViewById(R.id.player_album_art)).setImageBitmap(bitmap);
        bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() * 2 / 5, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        ((ImageView) findViewById(R.id.player_screen_background)).setImageBitmap(bitmap);

        findViewById(R.id.player_back_action).setOnClickListener(new onActionClickListener());
        findViewById(R.id.player_toggle_queue).setOnClickListener(new onActionClickListener());

        playerControl = new PlayerControl(this);

        if ((PlayerControl.getFilePath() == null || path.compareToIgnoreCase(PlayerControl.getFilePath()) != 0) ||
                !PlayerControl.isPlaying()){
            PlayerControl.stop();
            PlayerControl.release();

            PlayerControl.addMusicFile(getApplicationContext(), path);

            PlayerControl.play();

            ((ImageButton) findViewById(R.id.player_play_pause_button)).setImageResource(R.drawable.ic_pause);
            findViewById(R.id.player_play_pause_button).setTag(Boolean.TRUE);
        }
        else {
            PlayerControl.play();
            ((ImageButton) findViewById(R.id.player_play_pause_button)).setImageResource(R.drawable.ic_pause);
            findViewById(R.id.player_play_pause_button).setTag(Boolean.TRUE);
        }
    }

    private class onClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.player_play_pause_button:
                    if(v.findViewById(R.id.player_play_pause_button).getTag() == Boolean.FALSE && !PlayerControl.isPlaying()) {
                        ((ImageButton) v.findViewById(R.id.player_play_pause_button)).setImageResource(R.drawable.ic_pause);
                        v.findViewById(R.id.player_play_pause_button).setTag(Boolean.TRUE);
                        PlayerControl.play();
                    }
                    else{
                        ((ImageButton) v.findViewById(R.id.player_play_pause_button)).setImageResource(R.drawable.ic_play);
                        v.findViewById(R.id.player_play_pause_button).setTag(Boolean.FALSE);
                        PlayerControl.pause();
                    }
                    break;

                case R.id.player_previous_button:
                    break;

                case R.id.player_next_button:
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
                    getFragmentManager().popBackStack();
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
